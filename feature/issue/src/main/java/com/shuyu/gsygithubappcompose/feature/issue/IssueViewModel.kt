package com.shuyu.gsygithubappcompose.feature.issue

import androidx.lifecycle.SavedStateHandle
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.data.repository.IssueRepository
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseUiState
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseViewModel
import com.shuyu.gsygithubappcompose.core.network.model.Comment
import com.shuyu.gsygithubappcompose.core.network.model.Issue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.shuyu.gsygithubappcompose.core.network.config.NetworkConfig


data class IssueUiState(
    val owner: String? = null,
    val repoName: String? = null,
    val issueNumber: Int? = null,
    val issue: Issue? = null,
    val comments: List<Comment> = emptyList(),
    override val isPageLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
    override val error: String? = null,
    override val currentPage: Int = 1,
    override val hasMore: Boolean = false,
    override val loadMoreError: Boolean = false
) : BaseUiState

@HiltViewModel
class IssueViewModel @Inject constructor(
    private val issueRepository: IssueRepository,
    stringResourceProvider: StringResourceProvider,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<IssueUiState>(
    initialUiState = IssueUiState(),
    preferencesDataStore = null, // IssueViewModel doesn't use UserPreferencesDataStore directly
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
    init {
        savedStateHandle.get<String>("owner")?.let { owner ->
            _uiState.update { it.copy(owner = owner) }
        }
        savedStateHandle.get<String>("repoName")?.let { repoName ->
            _uiState.update { it.copy(repoName = repoName) }
        }
        savedStateHandle.get<String>("issueNumber")?.toIntOrNull().let { issueNumber ->
            _uiState.update { it.copy(issueNumber = issueNumber) }
        }
        doInitialLoad()
    }

    override fun loadData(initialLoad: Boolean, isRefresh: Boolean, isLoadMore: Boolean) {
        val currentOwner = _uiState.value.owner
        val currentRepoName = _uiState.value.repoName
        val currentIssueNumber = _uiState.value.issueNumber

        if (currentOwner == null || currentRepoName == null || currentIssueNumber == null) {
            _uiState.update { it.copy(error = "Missing owner, repoName or issueNumber") }
            return
        }

        if (initialLoad || isRefresh) {
            fetchIssueInfo(currentOwner, currentRepoName, currentIssueNumber, isRefresh)
        }
        fetchIssueComments(currentOwner, currentRepoName, currentIssueNumber, isLoadMore)
    }

    private fun fetchIssueInfo(owner: String, repoName: String, issueNumber: Int, isRefresh: Boolean) {
        launchDataLoad(initialLoad = false, isRefresh = isRefresh, isLoadMore = false) { pageToLoad -> // pageToLoad is not used for single item fetch, but launchDataLoad requires it
            issueRepository.getIssueInfo(owner, repoName, issueNumber, isRefresh).collect { result ->
                result.data.fold(
                    onSuccess = { issue ->
                        _uiState.update {
                            it.copy(issue = issue, isPageLoading = false, isRefreshing = false, error = null)
                        }
                    },
                    onFailure = { throwable ->
                        _uiState.update {
                            it.copy(error = throwable.message, isPageLoading = false, isRefreshing = false)
                        }
                    }
                )
            }
        }
    }

    private fun fetchIssueComments(owner: String, repoName: String, issueNumber: Int, isLoadMore: Boolean) {
        launchDataLoad(initialLoad = false, isRefresh = false, isLoadMore = isLoadMore) { pageToLoad ->
            issueRepository.getIssueComments(owner, repoName, issueNumber, pageToLoad).collect { result ->
                result.data.fold(
                    onSuccess = { newComments ->
                        _uiState.update { currentState ->
                            val updatedComments = if (pageToLoad == 1) newComments else currentState.comments + newComments
                            currentState.copy(
                                comments = updatedComments,
                                isPageLoading = false,
                                isLoadingMore = false,
                                error = null,
                                currentPage = pageToLoad + 1,
                                hasMore = newComments.size == NetworkConfig.PER_PAGE
                            )
                        }
                    },
                    onFailure = { throwable ->
                        _uiState.update { currentState ->
                            currentState.copy(
                                error = throwable.message,
                                isPageLoading = false,
                                isLoadingMore = false,
                                loadMoreError = true
                            )
                        }
                    }
                )
            }
        }
    }
}
