package com.shuyu.gsygithubappcompose.feature.trending


import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.core.network.config.NetworkConfig
import com.shuyu.gsygithubappcompose.core.network.model.Repository
import com.shuyu.gsygithubappcompose.data.repository.RepositoryRepository
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseUiState
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.shuyu.gsygithubappcompose.core.common.R

data class TrendingUiState(
    val repositories: List<Repository> = emptyList(),
    override val isPageLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
    override val error: String? = null,
    override val currentPage: Int = 1,
    override val hasMore: Boolean = false,
    override val loadMoreError: Boolean = false
) : BaseUiState

@HiltViewModel
class TrendingViewModel @Inject constructor(
    private val repositoryRepository: RepositoryRepository,
    preferencesDataStore: UserPreferencesDataStore,
    private val stringResourceProvider: StringResourceProvider
) : BaseViewModel<TrendingUiState>(
    initialUiState = TrendingUiState(),
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

    override fun loadData(
        initialLoad: Boolean,
        isRefresh: Boolean,
        isLoadMore: Boolean
    ) {
        launchDataLoadWithUser(initialLoad, isRefresh, isLoadMore) { user,pageToLoad ->
            var emissionCount = 0
            repositoryRepository.getTrendingRepositories().collect {
                emissionCount++
                it.fold(
                    onSuccess = { newRepos ->
                        val currentRepos =
                            if (isRefresh || initialLoad) emptyList() else _uiState.value.repositories
                        val updatedRepos = currentRepos + newRepos
                        handleResult(
                            emissionCount,
                            newRepos,
                            pageToLoad,
                            isRefresh,
                            initialLoad,
                            isLoadMore,
                            updateSuccess = { currentState, items, page, isR, initialL, isLM ->
                                currentState.copy(
                                    repositories = updatedRepos
                                )
                            },
                            updateFailure = { currentState, errorMessage, isLM ->
                                currentState.copy(
                                    repositories = emptyList() // Clear repositories on failure if no data found
                                )
                            }
                        )
                    },
                    onFailure = { exception ->
                        updateErrorState(exception, isLoadMore, stringResourceProvider.getString(R.string.error_failed_to_load_repositories))
                    }
                )
            }
        }
    }

    fun refreshTrendingRepositories() {
        refresh()
    }

    fun loadMoreTrendingRepositories() {
        loadMore()
    }
}
