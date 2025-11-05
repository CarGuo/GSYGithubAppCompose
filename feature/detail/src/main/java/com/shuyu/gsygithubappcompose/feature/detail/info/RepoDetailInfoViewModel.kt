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
import com.shuyu.gsygithubappcompose.data.repository.DataSource
import com.shuyu.gsygithubappcompose.data.repository.EventRepository
import com.shuyu.gsygithubappcompose.core.network.config.NetworkConfig
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class RepoDetailItemType {
    EVENT,
    COMMIT
}

// Define the UI state for RepoDetailInfoViewModel
data class RepoDetailInfoUiState(
    val repoDetail: RepositoryDetailModel? = null,
    val repoDetailList: List<RepoDetailListItem> = emptyList(), // Placeholder for related items like commits, issues, etc.
    val owner: String? = null,
    val repoName: String? = null,
    val selectedItemType: RepoDetailItemType = RepoDetailItemType.EVENT, // New state for selected item type
    val isSwitchingItemType: Boolean = false, // New state to prevent multiple requests when switching
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

    fun setSelectedItemType(itemType: RepoDetailItemType) {
        if (_uiState.value.selectedItemType != itemType && !_uiState.value.isSwitchingItemType) {
            _uiState.update { it.copy(selectedItemType = itemType, isSwitchingItemType = true) }
            refresh() // Refresh data when item type changes
        }
    }

    override fun loadData(initialLoad: Boolean, isRefresh: Boolean, isLoadMore: Boolean) {
        val owner = _uiState.value.owner
        val repoName = _uiState.value.repoName
        val currentPage = _uiState.value.currentPage
        val selectedItemType = _uiState.value.selectedItemType

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
                        // After fetching repo detail, fetch events or commits based on selectedItemType
                        scopeForAsync.launch {
                            fetchItems(
                                owner, repoName, currentPage, isLoadMore, selectedItemType
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

    private suspend fun fetchItems(
        owner: String,
        repoName: String,
        page: Int,
        isLoadMore: Boolean,
        itemType: RepoDetailItemType
    ) {
        val currentList = if (isLoadMore) _uiState.value.repoDetailList.toMutableList() else mutableListOf()
        var hasMoreItems = false

        when (itemType) {
            RepoDetailItemType.EVENT -> {
                eventRepository.getRepositoryEvents(owner, repoName, page)
                    .flowOn(Dispatchers.IO + CoroutineName("RepoEventsFlow"))
                    .collectLatest { eventsResult ->
                        eventsResult.data.fold(onSuccess = { events ->
                            if (!isLoadMore) {
                                currentList.clear()
                            }
                            currentList.addAll(events.map { RepoDetailListItem.EventItem(it) })
                            hasMoreItems = events.size == NetworkConfig.PER_PAGE

                            _uiState.update { currentState ->
                                currentState.copy(
                                    repoDetailList = currentList,
                                    hasMore = hasMoreItems,
                                    isSwitchingItemType = false // Reset switching state
                                )
                            }
                        }, onFailure = { exception ->
                            updateErrorState(
                                exception,
                                isLoadMore,
                                stringResourceProvider.getString(R.string.error_failed_to_load_events)
                            )
                            _uiState.update { it.copy(isSwitchingItemType = false) }
                        })
                    }
            }
            RepoDetailItemType.COMMIT -> {
                repositoryRepository.getRepoCommits(owner, repoName, page)
                    .flowOn(Dispatchers.IO + CoroutineName("RepoCommitsFlow"))
                    .collectLatest { commitsResult ->
                        commitsResult.data.fold(onSuccess = { commits ->
                            if (!isLoadMore) {
                                currentList.clear()
                            }
                            currentList.addAll(commits.map { RepoDetailListItem.CommitItem(it) })
                            hasMoreItems = commits.size == NetworkConfig.PER_PAGE

                            _uiState.update { currentState ->
                                currentState.copy(
                                    repoDetailList = currentList,
                                    hasMore = hasMoreItems,
                                    isSwitchingItemType = false // Reset switching state
                                )
                            }
                        }, onFailure = { exception ->
                            updateErrorState(
                                exception,
                                isLoadMore,
                                stringResourceProvider.getString(R.string.error_failed_to_load_commits)
                            )
                            _uiState.update { it.copy(isSwitchingItemType = false) }
                        })
                    }
            }
        }
    }

    // Public functions to trigger refresh and load more, leveraging BaseViewModel's functionality.
    fun refreshRepoDetailInfo() {
        refresh()
    }

    fun loadMoreRepoDetailList() {
        if (!_uiState.value.isLoadingMore && _uiState.value.hasMore && !_uiState.value.isSwitchingItemType) {
            loadMore()
        }
    }
}
