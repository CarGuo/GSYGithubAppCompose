package com.shuyu.gsygithubappcompose.feature.detail.readme

import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.data.repository.ReadmeRepository
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseUiState
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class RepoDetailReadmeUiState(
    val owner: String = "",
    val repo: String = "",
    val branch: String? = "main",
    val defaultBranch: String? = null, // Added defaultBranch parameter
    val readme: String? = null,
    override val isPageLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
    override val error: String? = null,
    override val currentPage: Int = 1,
    override val hasMore: Boolean = false,
    override val loadMoreError: Boolean = false
) : BaseUiState

@HiltViewModel
class RepoDetailReadmeViewModel @Inject constructor(
    private val readmeRepository: ReadmeRepository,
    stringResourceProvider: StringResourceProvider
) : BaseViewModel<RepoDetailReadmeUiState>(
    initialUiState = RepoDetailReadmeUiState(),
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

    fun loadReadme(owner: String, repo: String, branch: String?, defaultBranch: String?) {
        _uiState.update {
            it.copy(owner = owner, repo = repo, branch = branch, defaultBranch = defaultBranch)
        }
        doInitialLoad()
    }

    override fun loadData(initialLoad: Boolean, isRefresh: Boolean, isLoadMore: Boolean) {
        // Update loading state first
        _uiState.update {
            when {
                isRefresh -> it.copy(isRefreshing = true, error = null)
                initialLoad -> it.copy(isPageLoading = true, error = null)
                else -> it
            }
        }

        readmeRepository.getReadme(uiState.value.owner, uiState.value.repo, uiState.value.branch, uiState.value.defaultBranch)
            .onEach { result ->
                _uiState.update {
                    if (result.data.isSuccess) {
                        it.copy(
                            readme = result.data.getOrNull(),
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
