package com.shuyu.gsygithubappcompose.feature.detail.info

import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.CoroutineName
import com.shuyu.gsygithubappcompose.data.repository.RepositoryResult
import kotlinx.coroutines.flow.Flow


enum class RepoDetailItemType {
    EVENT, COMMIT
}

// Define the UI state for RepoDetailInfoViewModel
data class RepoDetailInfoUiState(
    val repoDetail: RepositoryDetailModel? = null,
    val repoDetailList: List<RepoDetailListItem> = emptyList(), // Placeholder for related items like commits, issues, etc.
    val owner: String? = null,
    val repoName: String? = null,
    val selectedItemType: RepoDetailItemType = RepoDetailItemType.EVENT, // New state for selected item type
    val isSwitchingItemType: Boolean = false, // New state to prevent multiple requests when switching
    val isLoadingDialog: Boolean = false,
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

        launchDataLoad(initialLoad, isRefresh, isLoadMore) { pageToLoad ->
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
                                owner, repoName, pageToLoad, isLoadMore, selectedItemType
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
        when (itemType) {
            RepoDetailItemType.EVENT -> {
                collectAndHandleRepoDetailListResult(
                    repoFlow = eventRepository.getRepositoryEvents(owner, repoName, page),
                    pageToLoad = page,
                    isRefresh = !isLoadMore, // If not load more, it's a refresh or initial load
                    initialLoad = false, // Handled by loadData
                    isLoadMore = isLoadMore,
                    updateSuccess = { currentState, items, _, _, _, _ ->
                        val currentItems =
                            if (isLoadMore) currentState.repoDetailList else emptyList()
                        val updatedItems =
                            currentItems + items.map { RepoDetailListItem.EventItem(it) }
                        currentState.copy(
                            repoDetailList = updatedItems, isSwitchingItemType = false
                        )
                    },
                    updateFailure = { currentState, _, _ ->
                        currentState.copy(
                            repoDetailList = if (isLoadMore) currentState.repoDetailList else emptyList(),
                            isSwitchingItemType = false
                        )
                    },
                    updateFailureMessage = stringResourceProvider.getString(R.string.error_failed_to_load_events)
                )
            }

            RepoDetailItemType.COMMIT -> {
                collectAndHandleRepoDetailListResult(
                    repoFlow = repositoryRepository.getRepoCommits(owner, repoName, page),
                    pageToLoad = page,
                    isRefresh = !isLoadMore, // If not load more, it's a refresh or initial load
                    initialLoad = false, // Handled by loadData
                    isLoadMore = isLoadMore,
                    updateSuccess = { currentState, items, _, _, _, _ ->
                        val currentItems =
                            if (isLoadMore) currentState.repoDetailList else emptyList()
                        val updatedItems =
                            currentItems + items.map { RepoDetailListItem.CommitItem(it) }
                        currentState.copy(
                            repoDetailList = updatedItems, isSwitchingItemType = false
                        )
                    },
                    updateFailure = { currentState, _, _ ->
                        currentState.copy(
                            repoDetailList = if (isLoadMore) currentState.repoDetailList else emptyList(),
                            isSwitchingItemType = false
                        )
                    },
                    updateFailureMessage = stringResourceProvider.getString(R.string.error_failed_to_load_commits)
                )
            }
        }
    }

    private suspend fun <T> collectAndHandleRepoDetailListResult(
        repoFlow: Flow<RepositoryResult<List<T>>>,
        pageToLoad: Int,
        isRefresh: Boolean,
        initialLoad: Boolean,
        isLoadMore: Boolean,
        updateSuccess: (RepoDetailInfoUiState, List<T>, Int, Boolean, Boolean, Boolean) -> RepoDetailInfoUiState,
        updateFailure: (RepoDetailInfoUiState, String?, Boolean) -> RepoDetailInfoUiState,
        updateFailureMessage: String
    ) {
        repoFlow.flowOn(Dispatchers.IO).collectLatest { repoResult ->
            repoResult.data.fold(onSuccess = { newItems ->
                handleResult(
                    newItems = newItems,
                    pageToLoad = pageToLoad,
                    isRefresh = isRefresh,
                    initialLoad = initialLoad,
                    isLoadMore = isLoadMore,
                    source = repoResult.dataSource,
                    isDbEmpty = repoResult.isDbEmpty,
                    updateSuccess = updateSuccess,
                    updateFailure = updateFailure
                )
            }, onFailure = { exception ->
                updateErrorState(
                    exception, isLoadMore, updateFailureMessage
                )
                _uiState.update { it.copy(isSwitchingItemType = false) }
            })
        }
    }

    fun starRepo(owner: String, repo: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDialog = true) }
            val isStarred = _uiState.value.repoDetail?.viewerHasStarred ?: false
            repositoryRepository.starRepo(owner, repo, !isStarred)
                .flowOn(Dispatchers.IO)
                .collectLatest {
                    _uiState.update { it.copy(isLoadingDialog = false) }
                    if (it) {
                        refresh()
                    }
                }
        }
    }

    fun forkRepo(owner: String, repo: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDialog = true) }
            repositoryRepository.forkRepo(owner, repo)
                .flowOn(Dispatchers.IO)
                .collectLatest {
                    _uiState.update { it.copy(isLoadingDialog = false) }
                    if (it.data.isSuccess) {
                        refresh()
                    }
                }
        }
    }

    fun watchRepo(owner: String, repo: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDialog = true) }
            val isWatched = _uiState.value.repoDetail?.viewerSubscription == "SUBSCRIBED"
            repositoryRepository.watchRepo(owner, repo, !isWatched)
                .flowOn(Dispatchers.IO)
                .collectLatest {
                    _uiState.update { it.copy(isLoadingDialog = false) }
                    if (it) {
                        refresh()
                    }
                }
        }
    }

    fun createIssue(owner: String, repo: String, title: String, body: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDialog = true) }
            repositoryRepository.createIssue(owner, repo, title, body)
                .flowOn(Dispatchers.IO)
                .collectLatest { result ->
                    result.data.fold(
                        onSuccess = {
                            _uiState.update { it.copy(isLoadingDialog = false) }
                            refresh()
                        },
                        onFailure = {
                            _uiState.update { it.copy(isLoadingDialog = false) }
                            updateErrorState(
                                Exception("Failed to create issue"),
                                false,
                                stringResourceProvider.getString(R.string.error_unknown)
                            )
                        }
                    )
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
