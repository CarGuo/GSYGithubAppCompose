package com.shuyu.gsygithubappcompose.feature.trending


import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.core.network.model.Repository
import com.shuyu.gsygithubappcompose.data.repository.RepositoryRepository
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseUiState
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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
        launchDataLoadWithUser(initialLoad, isRefresh, isLoadMore) { _, pageToLoad ->
            repositoryRepository.getTrendingRepositories(page = pageToLoad).collect { repoResult ->
                repoResult.result.fold(
                    onSuccess = { newRepos ->
                        handleResult(
                            newItems = newRepos,
                            pageToLoad = pageToLoad,
                            isRefresh = isRefresh,
                            initialLoad = initialLoad,
                            isLoadMore = isLoadMore,
                            source = repoResult.source,
                            isDbEmpty = repoResult.isDbEmpty,
                            updateSuccess = { currentState, items, _, _, _, _ ->
                                val currentRepos = currentState.repositories
                                val updatedRepos = if (isLoadMore) {
                                    currentRepos + items
                                } else {
                                    items
                                }
                                currentState.copy(repositories = updatedRepos)
                            },
                            updateFailure = { currentState, _, _ ->
                                currentState.copy(
                                    repositories = if (isLoadMore) currentState.repositories else emptyList()
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
