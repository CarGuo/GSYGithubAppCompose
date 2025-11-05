package com.shuyu.gsygithubappcompose.feature.detail.info

import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.core.network.model.RepositoryDetailModel
import com.shuyu.gsygithubappcompose.data.repository.RepositoryRepository
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseViewModel
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.data.repository.EventRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import com.shuyu.gsygithubappcompose.core.network.config.NetworkConfig
import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.core.network.model.RepoCommit
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineName

// Define the UI state for RepoDetailInfoViewModel
data class RepoDetailInfoUiState(
    val repoDetail: RepositoryDetailModel? = null,
    val repoDetailList: List<RepoDetailListItem> = emptyList(), // Placeholder for related items like commits, issues, etc.
    val owner: String? = null,
    val repoName: String? = null,
    override val isPageLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
    override val error: String? = null,
    override val currentPage: Int = 1,
    override val hasMore: Boolean = false,
    override val loadMoreError: Boolean = false
) : BaseUiState

@HiltViewModel
class RepoDetailInfoViewModel @Inject constructor(
    private val repositoryRepository: RepositoryRepository,
    private val eventRepository: EventRepository, // Inject EventRepository
    preferencesDataStore: UserPreferencesDataStore,
    private val stringResourceProvider: StringResourceProvider
) : BaseViewModel<RepoDetailInfoUiState>(
    initialUiState = RepoDetailInfoUiState(),
    preferencesDataStore = preferencesDataStore,
    stringResourceProvider = stringResourceProvider,
    commonStateUpdater = { currentState, isPageLoading, isRefreshing, isLoadingMore, error, currentPage, hasMore, loadMoreError ->
        currentState.copy(
            isPageLoading = isPageLoading,
            isRefreshing = isRefreshing,
            isLoadingMore = isLoadingMore,
            error = error,
            currentPage = currentPage,
            hasMore = hasMore,
            loadMoreError = loadMoreError
        )
    }) {

    // This function is now responsible for setting the owner and repoName, then triggering a data load.
    fun loadRepoDetailInfo(owner: String, repoName: String) {
        // Only trigger a refresh if the owner or repoName has changed, or if it's an initial load
        if (_uiState.value.owner != owner || _uiState.value.repoName != repoName || _uiState.value.repoDetail == null) {
            _uiState.update { currentState ->
                currentState.copy(owner = owner, repoName = repoName)
            }
            refresh() // Trigger data loading through BaseViewModel's refresh mechanism
        }
    }

    override fun loadData(initialLoad: Boolean, isRefresh: Boolean, isLoadMore: Boolean) {
        val owner = _uiState.value.owner
        val repoName = _uiState.value.repoName
        val currentPage = _uiState.value.currentPage

        if (owner == null || repoName == null) {
            if (initialLoad) {
                updateErrorState(
                    Exception("Owner or repository name not provided for initial load."),
                    isLoadMore,
                    stringResourceProvider.getString(R.string.error_unknown)
                )
            }
            return
        }

        launchDataLoad(initialLoad, isRefresh, isLoadMore) {
            // This 'this' refers to the CoroutineScope provided by launchDataLoad
            val scopeForAsync = this

            // Fetch repository detail
            repositoryRepository.getRepositoryDetail(owner, repoName)
                .flowOn(Dispatchers.IO + CoroutineName("RepoDetailFlow"))
                .collectLatest { repositoryResult ->
                    repositoryResult.data.fold(onSuccess = { fetchedRepoDetail ->
                        _uiState.update { currentState ->
                            currentState.copy(repoDetail = fetchedRepoDetail)
                        }
                        // After fetching repo detail, fetch events and commits
                        // Launch fetchEventsAndCommits within the correct scope
                        scopeForAsync.launch {
                            fetchEventsAndCommits(
                                scopeForAsync, owner, repoName, currentPage, isLoadMore
                            )
                        }
                    }, onFailure = { exception ->
                        updateErrorState(
                            exception,
                            isLoadMore,
                            stringResourceProvider.getString(R.string.error_failed_to_load_repo_detail)
                        )
                    })
                }
        }
    }

    private suspend fun fetchEventsAndCommits(
        coroutineScope: CoroutineScope,
        owner: String,
        repoName: String,
        page: Int,
        isLoadMore: Boolean
    ) {
        val eventsDeferred: Deferred<Result<List<Event>>> =
            (coroutineScope as CoroutineScope).async(Dispatchers.IO + CoroutineName("EventsFetch")) {
                eventRepository.getRepositoryEvents(owner, repoName, page)
            }
        val commitsDeferred: Deferred<Result<List<RepoCommit>>> =
            (coroutineScope as CoroutineScope).async(Dispatchers.IO + CoroutineName("CommitsFetch")) {
                repositoryRepository.getRepoCommits(owner, repoName, page)
            }

        val deferredResults = awaitAll(eventsDeferred, commitsDeferred)
        val eventsResult = deferredResults[0] as Result<List<Event>>
        val commitsResult = deferredResults[1] as Result<List<RepoCommit>>

        val currentList =
            if (isLoadMore) _uiState.value.repoDetailList.toMutableList() else mutableListOf()
        var hasMoreEvents = false
        var hasMoreCommits = false

        eventsResult.onSuccess { events ->
            currentList.addAll(events.map { RepoDetailListItem.EventItem(it) })
            hasMoreEvents = events.size == NetworkConfig.PER_PAGE
        }.onFailure { exception ->
            updateErrorState(
                exception,
                isLoadMore,
                stringResourceProvider.getString(R.string.error_failed_to_load_events)
            )
        }

        commitsResult.onSuccess { commits ->
            currentList.addAll(commits.map { RepoDetailListItem.CommitItem(it) })
            hasMoreCommits = commits.size == NetworkConfig.PER_PAGE
        }.onFailure { exception ->
            updateErrorState(
                exception,
                isLoadMore,
                stringResourceProvider.getString(R.string.error_failed_to_load_commits)
            )
        }

        // Sort the combined list by date if possible, or maintain insertion order
        // For now, we'll just maintain insertion order (events then commits)
        // If a specific sort order is needed, it should be implemented here.

        _uiState.update { currentState ->
            currentState.copy(
                repoDetailList = currentList,
                hasMore = hasMoreEvents || hasMoreCommits // If either has more, then overall has more
            )
        }
    }

    // Public functions to trigger refresh and load more, leveraging BaseViewModel's functionality.
    fun refreshRepoDetailInfo() {
        refresh()
    }

    fun loadMoreRepoDetailList() {
        if (!_uiState.value.isLoadingMore && _uiState.value.hasMore) {
            loadMore()
        }
    }
}
