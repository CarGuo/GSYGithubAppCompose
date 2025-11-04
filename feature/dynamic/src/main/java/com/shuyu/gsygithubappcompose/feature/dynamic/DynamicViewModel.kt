package com.shuyu.gsygithubappcompose.feature.dynamic

import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.data.repository.EventRepository
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseUiState
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.shuyu.gsygithubappcompose.core.common.R

data class DynamicUiState(
    val events: List<Event> = emptyList(),
    override val isPageLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
    override val error: String? = null,
    override val currentPage: Int = 1,
    override val hasMore: Boolean = false,
    override val loadMoreError: Boolean = false
) : BaseUiState

@HiltViewModel
class DynamicViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    preferencesDataStore: UserPreferencesDataStore,
    private val stringResourceProvider: StringResourceProvider
) : BaseViewModel<DynamicUiState>(
    initialUiState = DynamicUiState(),
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
        launchDataLoadWithUser(initialLoad, isRefresh, isLoadMore) { user, pageToLoad ->
            eventRepository.getReceivedEvents(user, pageToLoad, true).collect { repoResult ->
                repoResult.data.fold(
                    onSuccess = { newEvents ->
                        handleResult(
                            newItems = newEvents,
                            pageToLoad = pageToLoad,
                            isRefresh = isRefresh,
                            initialLoad = initialLoad,
                            isLoadMore = isLoadMore,
                            source = repoResult.dataSource,
                            isDbEmpty = repoResult.isDbEmpty,
                            updateSuccess = { currentState, items, _, _, _, _ ->
                                val currentEvents = currentState.events
                                val updatedEvents = if (isLoadMore) {
                                    currentEvents + items
                                } else {
                                    items
                                }
                                currentState.copy(events = updatedEvents)
                            },
                            updateFailure = { currentState, _, _ ->
                                currentState.copy(
                                    events = if (isLoadMore) currentState.events else emptyList()
                                )
                            }
                        )
                    },
                    onFailure = { exception ->
                        updateErrorState(exception, isLoadMore, stringResourceProvider.getString(R.string.error_failed_to_load_events))
                    }
                )
            }
        }
    }

    fun refreshEvents() {
        refresh()
    }

    fun loadMoreEvents() {
        loadMore()
    }
}
