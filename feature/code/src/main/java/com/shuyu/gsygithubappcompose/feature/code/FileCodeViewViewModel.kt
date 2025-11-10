package com.shuyu.gsygithubappcompose.feature.code

import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.data.repository.RepositoryRepository
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseUiState
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class FileCodeViewUiState(
    val owner: String = "",
    val repo: String = "",
    val path: String = "",
    val branch: String? = "main",
    val content: String? = null,
    override val isPageLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
    override val error: String? = null,
    override val hasMore: Boolean = false,
    override val currentPage: Int = 1,
    override val loadMoreError: Boolean = false
) : BaseUiState

@HiltViewModel
class FileCodeViewViewModel @Inject constructor(
    private val repositoryRepository: RepositoryRepository,
    stringResourceProvider: StringResourceProvider
) : BaseViewModel<FileCodeViewUiState>(
    initialUiState = FileCodeViewUiState(),
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

    fun loadFile(owner: String, repo: String, path: String, branch: String?) {
        _uiState.update {
            it.copy(owner = owner, repo = repo, path = path, branch = branch)
        }
        doInitialLoad()
    }

    fun setPatchContent(patchText: String) {
        _uiState.update {
            it.copy(
                content = patchText,
                isPageLoading = false,
                isRefreshing = false,
                error = null
            )
        }
    }

    override fun loadData(initialLoad: Boolean, isRefresh: Boolean, isLoadMore: Boolean) {
        // If content is already set by patchText, no need to load from repository
        if (_uiState.value.content != null && _uiState.value.path.isEmpty()) {
            return
        }

        // Update loading state first
        _uiState.update {
            when {
                isRefresh -> it.copy(isRefreshing = true, error = null)
                initialLoad -> it.copy(isPageLoading = true, error = null)
                else -> it
            }
        }

        repositoryRepository.getFileContents(uiState.value.owner, uiState.value.repo, uiState.value.path, uiState.value.branch)
            .onEach { result ->
                _uiState.update {
                    if (result.data.isSuccess) {
                        it.copy(
                            content = result.data.getOrNull(),
                            isPageLoading = false,
                            isRefreshing = false,
                            error = null
                        )
                    } else {
                        it.copy(
                            isPageLoading = false,
                            isRefreshing = false,
                            error = result.data.exceptionOrNull()?.message
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}
