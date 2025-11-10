package com.shuyu.gsygithubappcompose.feature.issue

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.common.R
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
import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class IssueUiState(
    val owner: String? = null,
    val repoName: String? = null,
    val issueNumber: Int? = null,
    val issue: Issue? = null,
    val comments: List<Comment> = emptyList(),
    val currentUserLogin: String? = null,
    val isRepoOwner: Boolean = false,
    val isIssueCreator: Boolean = false,
    val showReplyDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val showEditCommentDialog: Boolean = false,
    val canReply: Boolean = false,
    val canEdit: Boolean = false,
    val canOpenClose: Boolean = false,
    val canLockUnlock: Boolean = false,
    val editIssueTitle: String = "",
    val editIssueBody: String = "",
    val editComment: String = "",
    val replyComment: String = "",
    val isIssueLocked: Boolean = false,
    val isActionLoading: Boolean = false,
    val showOptionDialog: Boolean = false,
    val selectedComment: Comment? = null,
    val optionDialogOptions: List<String> = emptyList(),
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
    private val preferencesDataStore: UserPreferencesDataStore,
    private val stringResourceProvider: StringResourceProvider,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<IssueUiState>(
    initialUiState = IssueUiState(),
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

        viewModelScope.launch {
            preferencesDataStore.username.first().let { username ->
                _uiState.update { it.copy(currentUserLogin = username) }
            }
            doInitialLoad()
        }
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

    private fun fetchIssueInfo(
        owner: String, repoName: String, issueNumber: Int, isRefresh: Boolean
    ) {
        launchDataLoad(initialLoad = false, isRefresh = isRefresh, isLoadMore = false) { _ ->
            issueRepository.getIssueInfo(owner, repoName, issueNumber).collect { result ->
                result.data.fold(onSuccess = { issue ->
                    _uiState.update { currentState ->
                        val currentUserLogin = currentState.currentUserLogin
                        val isIssueCreator = currentUserLogin == issue.user?.login
                        // Directly compare current user login with the repo owner from the issue context
                        val isRepoOwner = currentUserLogin == owner

                        val isIssueLocked = issue.locked ?: false

                        val canReply = isRepoOwner || isIssueCreator || !isIssueLocked
                        val canEdit = isRepoOwner || (isIssueCreator && !isIssueLocked)
                        val canOpenClose = isRepoOwner || isIssueCreator
                        val canLockUnlock = isRepoOwner

                        currentState.copy(
                            issue = issue,
                            isPageLoading = false,
                            isRefreshing = false,
                            error = null,
                            isIssueCreator = isIssueCreator,
                            isRepoOwner = isRepoOwner,
                            isIssueLocked = isIssueLocked,
                            canReply = canReply,
                            canEdit = canEdit,
                            canOpenClose = canOpenClose,
                            canLockUnlock = canLockUnlock,
                            editIssueTitle = issue.title,
                            editIssueBody = issue.body ?: ""
                        )
                    }
                }, onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            error = throwable.message, isPageLoading = false, isRefreshing = false
                        )
                    }
                })
            }
        }
    }

    private fun fetchIssueComments(
        owner: String, repoName: String, issueNumber: Int, isLoadMore: Boolean
    ) {
        launchDataLoad(
            initialLoad = false, isRefresh = false, isLoadMore = isLoadMore
        ) { pageToLoad ->
            issueRepository.getIssueComments(owner, repoName, issueNumber, pageToLoad)
                .collect { result ->
                    result.data.fold(onSuccess = { newComments ->
                        _uiState.update { currentState ->
                            val updatedComments =
                                if (pageToLoad == 1) newComments else currentState.comments + newComments
                            currentState.copy(
                                comments = updatedComments,
                                isPageLoading = false,
                                isLoadingMore = false,
                                error = null,
                                currentPage = pageToLoad + 1,
                                hasMore = newComments.size == NetworkConfig.PER_PAGE
                            )
                        }
                    }, onFailure = { throwable ->
                        _uiState.update { currentState ->
                            currentState.copy(
                                error = throwable.message,
                                isPageLoading = false,
                                isLoadingMore = false,
                                loadMoreError = true
                            )
                        }
                    })
                }
        }
    }

    fun showReplyDialog(show: Boolean) {
        _uiState.update { it.copy(showReplyDialog = show) }
    }

    fun showEditDialog(show: Boolean) {
        _uiState.update { it.copy(showEditDialog = show) }
    }

    fun showEditCommentDialog(show: Boolean) {
        _uiState.update { it.copy(showEditCommentDialog = show) }
    }

    fun updateEditIssueTitle(title: String) {
        _uiState.update { it.copy(editIssueTitle = title) }
    }

    fun updateEditIssueBody(body: String) {
        _uiState.update { it.copy(editIssueBody = body) }
    }

    fun updateReplyComment(comment: String) {
        _uiState.update { it.copy(replyComment = comment) }
    }

    fun updateEditComment(comment: String) {
        _uiState.update { it.copy(editComment = comment) }
    }

    fun showCopySuccessToast() {
        showToast(stringResourceProvider.getString(R.string.copy_success))
    }

    fun addComment() {
        val currentOwner = _uiState.value.owner ?: return
        val currentRepoName = _uiState.value.repoName ?: return
        val currentIssueNumber = _uiState.value.issueNumber ?: return
        val commentBody = _uiState.value.replyComment

        if (commentBody.isBlank()) {
            showToast("Comment cannot be empty")
            return
        }

        _uiState.update { it.copy(isActionLoading = true) }
        launchDataLoad(initialLoad = false, isRefresh = false, isLoadMore = false) {
            issueRepository.addIssueComment(
                currentOwner, currentRepoName, currentIssueNumber, commentBody
            ).collect { result ->
                result.data.fold(onSuccess = {
                    showToast("Comment added successfully")
                    showReplyDialog(false)
                    updateReplyComment("")
                    _uiState.update { it.copy(isActionLoading = false) }
                    refresh() // Refresh comments
                }, onFailure = { throwable ->
                    showToast(throwable.message ?: "Failed to add comment")
                    _uiState.update { it.copy(isActionLoading = false) }
                })
            }
        }
    }

    fun editIssue() {
        val currentOwner = _uiState.value.owner ?: return
        val currentRepoName = _uiState.value.repoName ?: return
        val currentIssueNumber = _uiState.value.issueNumber ?: return
        val newTitle = _uiState.value.editIssueTitle
        val newBody = _uiState.value.editIssueBody

        if (newTitle.isBlank()) {
            showToast("Title cannot be empty")
            return
        }

        _uiState.update { it.copy(isActionLoading = true) }
        launchDataLoad(initialLoad = false, isRefresh = false, isLoadMore = false) {
            issueRepository.editIssue(
                currentOwner, currentRepoName, currentIssueNumber, newTitle, newBody
            ).collect { result ->
                result.data.fold(onSuccess = {
                    showToast("Issue updated successfully")
                    showEditDialog(false)
                    _uiState.update { it.copy(isActionLoading = false) }
                    refresh() // Refresh issue info
                }, onFailure = { throwable ->
                    showToast(throwable.message ?: "Failed to update issue")
                    _uiState.update { it.copy(isActionLoading = false) }
                })
            }
        }
    }

    fun toggleIssueState() {
        val currentOwner = _uiState.value.owner ?: return
        val currentRepoName = _uiState.value.repoName ?: return
        val currentIssueNumber = _uiState.value.issueNumber ?: return
        val currentState = _uiState.value.issue?.state ?: return

        val newState = if (currentState == "open") "closed" else "open"

        _uiState.update { it.copy(isActionLoading = true) }
        launchDataLoad(initialLoad = false, isRefresh = false, isLoadMore = false) {
            issueRepository.editIssue(
                currentOwner,
                currentRepoName,
                currentIssueNumber,
                _uiState.value.editIssueTitle,
                _uiState.value.editIssueBody,
                newState
            ).collect { result ->
                result.data.fold(onSuccess = {
                    showToast("Issue state changed to $newState")
                    _uiState.update { it.copy(isActionLoading = false) }
                    refresh() // Refresh issue info
                }, onFailure = { throwable ->
                    showToast(throwable.message ?: "Failed to change issue state")
                    _uiState.update { it.copy(isActionLoading = false) }
                })
            }
        }
    }

    fun toggleIssueLock() {
        val currentOwner = _uiState.value.owner ?: return
        val currentRepoName = _uiState.value.repoName ?: return
        val currentIssueNumber = _uiState.value.issueNumber ?: return
        val isLocked = _uiState.value.isIssueLocked

        _uiState.update { it.copy(isActionLoading = true) }
        launchDataLoad(initialLoad = false, isRefresh = false, isLoadMore = false) {
            if (isLocked) {
                issueRepository.unlockIssue(currentOwner, currentRepoName, currentIssueNumber)
                    .collect { result ->
                        result.data.fold(onSuccess = {
                            showToast("Issue unlocked successfully")
                            _uiState.update { it.copy(isActionLoading = false) }
                            refresh()
                        }, onFailure = { throwable ->
                            showToast(throwable.message ?: "Failed to unlock issue")
                            _uiState.update { it.copy(isActionLoading = false) }
                        })
                    }
            } else {
                issueRepository.lockIssue(currentOwner, currentRepoName, currentIssueNumber)
                    .collect { result ->
                        result.data.fold(onSuccess = {
                            showToast("Issue locked successfully")
                            _uiState.update { it.copy(isActionLoading = false) }
                            refresh()
                        }, onFailure = { throwable ->
                            showToast(throwable.message ?: "Failed to lock issue")
                            _uiState.update { it.copy(isActionLoading = false) }
                        })
                    }
            }
        }
    }

    fun showOptionDialog(show: Boolean, comment: Comment?) {
        val options = mutableListOf<String>()
        val isCommentAuthor = _uiState.value.currentUserLogin == comment?.user?.login
        if (_uiState.value.isRepoOwner || isCommentAuthor) {
            options.add(stringResourceProvider.getString(R.string.edit))
            options.add(stringResourceProvider.getString(R.string.delete))
        }

        if (options.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    showOptionDialog = show,
                    selectedComment = comment,
                    optionDialogOptions = options,
                    editComment = comment?.body ?: ""
                )
            }
        }
    }

    fun hideOptionDialog(cleanSelect: Boolean) {
        if (cleanSelect) {
            _uiState.update {
                it.copy(
                    showOptionDialog = false,
                    selectedComment = null,
                    optionDialogOptions = emptyList()
                )
            }
        } else {

            _uiState.update {
                it.copy(
                    showOptionDialog = false, optionDialogOptions = emptyList()
                )
            }
        }

    }

    fun onOptionSelected(option: String) {
        when (option) {
            stringResourceProvider.getString(R.string.edit) -> {
                showEditCommentDialog(true)
                hideOptionDialog(false)
            }

            stringResourceProvider.getString(R.string.delete) -> {
                deleteComment()
                hideOptionDialog(true)
            }
        }
    }

    fun editComment() {
        val currentOwner = _uiState.value.owner ?: return
        val currentRepoName = _uiState.value.repoName ?: return
        val commentId = _uiState.value.selectedComment?.id ?: return
        val commentBody = _uiState.value.editComment

        if (commentBody.isBlank()) {
            showToast("Comment cannot be empty")
            return
        }

        _uiState.update { it.copy(isActionLoading = true) }
        launchDataLoad(initialLoad = false, isRefresh = false, isLoadMore = false) {
            issueRepository.editComment(currentOwner, currentRepoName, commentId, commentBody)
                .collect { result ->
                    result.data.fold(onSuccess = {
                        showToast("Comment updated successfully")
                        showEditCommentDialog(false)
                        _uiState.update { it.copy(isActionLoading = false, selectedComment = null) }
                        refresh() // Refresh comments
                    }, onFailure = { throwable ->
                        showToast(throwable.message ?: "Failed to update comment")
                        _uiState.update { it.copy(isActionLoading = false) }
                    })
                }
        }
    }

    fun deleteComment() {
        val currentOwner = _uiState.value.owner ?: return
        val currentRepoName = _uiState.value.repoName ?: return
        val commentId = _uiState.value.selectedComment?.id ?: return

        _uiState.update { it.copy(isActionLoading = true) }
        launchDataLoad(initialLoad = false, isRefresh = false, isLoadMore = false) {
            issueRepository.deleteComment(currentOwner, currentRepoName, commentId)
                .collect { result ->
                    result.data.fold(onSuccess = {
                        showToast("Comment deleted successfully")
                        _uiState.update { it.copy(isActionLoading = false) }
                        refresh() // Refresh comments
                    }, onFailure = { throwable ->
                        showToast(throwable.message ?: "Failed to delete comment")
                        _uiState.update { it.copy(isActionLoading = false) }
                    })
                }
        }
    }
}
