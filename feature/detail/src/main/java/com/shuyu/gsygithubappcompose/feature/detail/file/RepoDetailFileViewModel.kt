package com.shuyu.gsygithubappcompose.feature.detail.file

import androidx.lifecycle.SavedStateHandle
import com.shuyu.gsygithubappcompose.core.network.model.FileContent
import com.shuyu.gsygithubappcompose.data.repository.FileContentRepository
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseUiState
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import kotlinx.coroutines.flow.update

data class RepoDetailFileUiState(
    val owner: String? = null,
    val repoName: String? = null,
    val branch: String? = null,
    val defaultBranch: String? = null, // Added defaultBranch parameter
    val currentPath: String = "",
    val pathSegments: List<String> = emptyList(),
    val fileContents: List<FileContent> = emptyList(),
    override val isPageLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
    override val error: String? = null,
    override val currentPage: Int = 1,
    override val hasMore: Boolean = false,
    override val loadMoreError: Boolean = false
) : BaseUiState

@HiltViewModel
class RepoDetailFileViewModel @Inject constructor(
    private val fileContentRepository: FileContentRepository,
    savedStateHandle: SavedStateHandle,
    stringResourceProvider: StringResourceProvider // Inject StringResourceProvider
) : BaseViewModel<RepoDetailFileUiState>(
    initialUiState = RepoDetailFileUiState(),
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

    private val _owner = savedStateHandle.getStateFlow<String?>("userName", null)
    private val _repoName = savedStateHandle.getStateFlow<String?>("repoName", null)

    init {
        _owner.value?.let { owner ->
            _repoName.value?.let { repoName ->
                _uiState.update { it.copy(owner = owner, repoName = repoName) }
                doInitialLoad() // Use public method
            }
        }
    }

    override fun loadData(initialLoad: Boolean, isRefresh: Boolean, isLoadMore: Boolean) {
        val currentOwner = uiState.value.owner ?: return
        val currentRepoName = uiState.value.repoName ?: return
        val currentBranch = uiState.value.branch
        val currentDefaultBranch = uiState.value.defaultBranch
        val currentPath = uiState.value.currentPath

        launchDataLoad(initialLoad, isRefresh, isLoadMore) { pageToLoad ->
            fileContentRepository.getRepositoryContents(currentOwner, currentRepoName, currentPath, currentBranch, currentDefaultBranch)
                .collect {
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
                            pageToLoad = pageToLoad, // Pass current page
                            isRefresh = isRefresh,
                            initialLoad = initialLoad,
                            isLoadMore = isLoadMore,
                            source = it.dataSource,
                            isDbEmpty = data.isEmpty(), // Assuming data.isEmpty() implies db is empty for this context
                            updateSuccess = { currentState, newItems, _, _, _, _ ->
                                currentState.copy(fileContents = newItems)
                            },
                            updateFailure = { currentState, errorMsg, _ ->
                                currentState.copy(error = errorMsg)
                            }
                        )
                    }.onFailure { throwable ->
                        updateErrorState(throwable, isLoadMore) // Pass throwable directly
                    }
                }
        }
    }

    fun navigateToPath(path: String) {
        _uiState.update { uiState ->
            val newPathSegments = if (path.isEmpty()) {
                emptyList()
            } else {
                path.split("/")
            }
            uiState.copy(currentPath = path, pathSegments = newPathSegments, fileContents = emptyList())
        }
        refresh() // Use public method
    }

    fun navigateUp() {
        val currentPath = uiState.value.currentPath
        val lastSlashIndex = currentPath.lastIndexOf('/')
        val newPath = if (lastSlashIndex > 0) {
            currentPath.substring(0, lastSlashIndex)
        } else if (lastSlashIndex == 0) {
            "" // Go to root
        } else {
            "" // Already at root
        }
        navigateToPath(newPath)
    }

    fun setRepoInfo(owner: String, repoName: String, branch: String?, defaultBranch: String?) {
        _uiState.update { it.copy(owner = owner, repoName = repoName, branch = branch, defaultBranch = defaultBranch) }
    }
}
