package com.shuyu.gsygithubappcompose.feature.trending


import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.core.network.model.TrendingRepoModel
import com.shuyu.gsygithubappcompose.data.repository.TrendingRepository
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseUiState
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.shuyu.gsygithubappcompose.core.common.R

data class TrendingUiState(
    val repositories: List<TrendingRepoModel> = emptyList(),
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
    private val trendingRepository: TrendingRepository,
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
        // Trending API does not support pagination, so we always load the first page.
        // isLoadMore will effectively be ignored for this API.
        launchDataLoadWithUser(initialLoad, isRefresh, isLoadMore) { _, _ ->
            trendingRepository.getTrendingRepositories(since = "daily", languageType = null).collect { repoResult ->
                repoResult.data.fold(
                    onSuccess = { newRepos ->
                        handleResult(
                            newItems = newRepos,
                            pageToLoad = 1, // Always page 1 for trending
                            isRefresh = isRefresh,
                            initialLoad = initialLoad,
                            isLoadMore = false, // No load more for trending
                            source = repoResult.dataSource,
                            isDbEmpty = repoResult.isDbEmpty,
                            updateSuccess = { currentState, items, _, _, _, _ ->
                                currentState.copy(repositories = items, hasMore = false) // No more pages
                            },
                            updateFailure = { currentState, _, _ ->
                                currentState.copy(repositories = emptyList(), hasMore = false)
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

    // No loadMoreTrendingRepositories as trending API does not support pagination
}
