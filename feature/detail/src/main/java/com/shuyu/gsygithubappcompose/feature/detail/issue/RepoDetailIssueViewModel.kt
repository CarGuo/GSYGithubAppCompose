package com.shuyu.gsygithubappcompose.feature.detail.issue

import androidx.lifecycle.SavedStateHandle
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.core.network.model.Issue
import com.shuyu.gsygithubappcompose.data.repository.IssueRepository
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseUiState
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class RepoDetailIssueUiState(
    val owner: String? = null,
    val repoName: String? = null,
    val searchQuery: String = "",
    val issueState: String = "all", // "all", "open", "closed"
    val issues: List<Issue> = emptyList(),
    override val isPageLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
    override val error: String? = null,
    override val currentPage: Int = 1,
    override val hasMore: Boolean = false,
    override val loadMoreError: Boolean = false
) : BaseUiState

@HiltViewModel
class RepoDetailIssueViewModel @Inject constructor(
    private val issueRepository: IssueRepository,
    savedStateHandle: SavedStateHandle,
    stringResourceProvider: StringResourceProvider
) : BaseViewModel<RepoDetailIssueUiState>(
    initialUiState = RepoDetailIssueUiState(),
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

    fun setRepoInfo(owner: String, repoName: String) {
        _uiState.update { it.copy(owner = owner, repoName = repoName) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onIssueStateChanged(state: String) {
        _uiState.update { it.copy(issueState = state) }
        refresh()
    }

    override fun loadData(initialLoad: Boolean, isRefresh: Boolean, isLoadMore: Boolean) {
        val currentOwner = uiState.value.owner ?: return
        val currentRepoName = uiState.value.repoName ?: return
        val currentIssueState = uiState.value.issueState
        val currentSearchQuery = uiState.value.searchQuery

        launchDataLoad(initialLoad, isRefresh, isLoadMore) { pageToLoad ->
            issueRepository.getRepositoryIssues(
                owner = currentOwner,
                repoName = currentRepoName,
                state = currentIssueState,
                query = currentSearchQuery,
                page = pageToLoad
            ).collect {
                _uiState.update { uiState ->
                    uiState.copy(
                        isPageLoading = initialLoad,
                        isRefreshing = isRefresh,
                        isLoadingMore = isLoadMore
                    )
                }
                it.data.onSuccess { data ->
                    handleResult(
                        newItems = data,
                        pageToLoad = pageToLoad,
                        isRefresh = isRefresh,
                        initialLoad = initialLoad,
                        isLoadMore = isLoadMore,
                        source = it.dataSource,
                        isDbEmpty = data.isEmpty(),
                        updateSuccess = { currentState, newItems, _, _, _, _ ->
                            val updatedIssues = if (isLoadMore) {
                                currentState.issues + newItems
                            } else {
                                newItems
                            }
                            currentState.copy(issues = updatedIssues)
                        },
                        updateFailure = { currentState, errorMsg, _ ->
                            currentState.copy(error = errorMsg, issues = if (isLoadMore) currentState.issues else emptyList())
                        }
                    )
                }.onFailure { throwable ->
                    updateErrorState(throwable, isLoadMore)
                }
            }
        }
    }
}
