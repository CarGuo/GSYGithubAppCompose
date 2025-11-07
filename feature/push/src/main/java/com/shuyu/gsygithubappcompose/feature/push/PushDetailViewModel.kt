package com.shuyu.gsygithubappcompose.feature.push

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.data.repository.PushRepository
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseUiState
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseViewModel
import com.shuyu.gsygithubappcompose.core.network.model.PushCommit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PushDetailUiState(
    val owner: String? = null,
    val repoName: String? = null,
    val sha: String? = null,
    val pushCommit: PushCommit? = null,
    override val isPageLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
    override val error: String? = null,
    override val currentPage: Int = 1,
    override val hasMore: Boolean = false,
    override val loadMoreError: Boolean = false
) : BaseUiState

@HiltViewModel
class PushDetailViewModel @Inject constructor(
    private val pushRepository: PushRepository,
    private val preferencesDataStore: UserPreferencesDataStore,
    private val stringResourceProvider: StringResourceProvider,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<PushDetailUiState>(
    initialUiState = PushDetailUiState(),
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

    init {
        val owner = savedStateHandle.get<String>("owner")
        val repoName = savedStateHandle.get<String>("repoName")
        val sha = savedStateHandle.get<String>("sha")
        _uiState.update {
            it.copy(owner = owner, repoName = repoName, sha = sha)
        }
        viewModelScope.launch {
            doInitialLoad()
        }
    }

    override fun loadData(initialLoad: Boolean, isRefresh: Boolean, isLoadMore: Boolean) {
        val currentOwner = uiState.value.owner ?: return
        val currentRepoName = uiState.value.repoName ?: return
        val currentSha = uiState.value.sha ?: return

        launchDataLoad(initialLoad, isRefresh, isLoadMore) { pageToLoad ->
            pushRepository.getRepositoryCommitInfo(currentOwner, currentRepoName, currentSha).collect { repoResult ->
                repoResult.data.fold(
                    onSuccess = { pushCommit ->
                        _uiState.update {
                            it.copy(
                                pushCommit = pushCommit,
                                isPageLoading = false,
                                isRefreshing = false
                            )
                        }
                    },
                    onFailure = { throwable ->
                        _uiState.update {
                            it.copy(
                                error = throwable.message,
                                isPageLoading = false,
                                isRefreshing = false
                            )
                        }
                    }
                )
            }
        }
    }
}
