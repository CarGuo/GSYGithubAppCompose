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

// Define the UI state for RepoDetailInfoViewModel
data class RepoDetailInfoUiState(
    val repoDetail: RepositoryDetailModel? = null,
    val repoDetailList: List<Any> = emptyList(), // Placeholder for related items like commits, issues, etc.
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
    }
) {

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

        if (owner == null || repoName == null) {
            // If owner or repoName are not set, we cannot load data.
            // This might happen if loadRepoDetailInfo hasn't been called yet.
            // Update the UI state to reflect an error or simply do nothing if it's an expected initial state.
            if (initialLoad) {
                // For initial load, we might want to show a specific message or state.
                // For now, we'll just log and return.
                // Or, if we want to show an error:
                updateErrorState(
                    Exception("Owner or repository name not provided for initial load."),
                    isLoadMore,
                    stringResourceProvider.getString(R.string.error_unknown)
                )
            }
            return
        }

        launchDataLoad(initialLoad, isRefresh, isLoadMore) {
            repositoryRepository.getRepositoryDetail(owner, repoName)
                .flowOn(Dispatchers.IO)
                .collectLatest { repositoryResult ->
                    repositoryResult.data.fold(
                        onSuccess = { fetchedRepoDetail ->
                            _uiState.update { currentState ->
                                currentState.copy(repoDetail = fetchedRepoDetail)
                            }
                            // If there are related lists to load (e.g., commits, issues),
                            // this is where you would call corresponding repository methods
                            // and update `repoDetailList` in the UI state.
                            // For now, `repoDetailList` remains empty as per the original code's intent.
                        },
                        onFailure = { exception ->
                            updateErrorState(
                                exception,
                                isLoadMore,
                                stringResourceProvider.getString(R.string.error_failed_to_load_repo_detail) // Assuming this string resource exists
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
        // This assumes that `repoDetailList` will eventually be populated via `loadData`
        // or a similar mechanism, and `hasMore` will be managed by `BaseViewModel`.
        // If `repoDetailList` is for a separate paginated API, you'd need to implement
        // `collectAndHandleListResult` for it within `loadData` or a dedicated function.
        if (!_uiState.value.isLoadingMore && _uiState.value.hasMore) {
            loadMore()
        }
    }
}
