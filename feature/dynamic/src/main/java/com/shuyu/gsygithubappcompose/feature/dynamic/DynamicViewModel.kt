package com.shuyu.gsygithubappcompose.feature.dynamic

import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.network.config.NetworkConfig
import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.data.repository.EventRepository
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseUiState
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DynamicUiState(
    val events: List<Event> = emptyList(),
    override val isPageLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
    override val error: String? = null,
    override val currentPage: Int = 1,
    override val hasMore: Boolean = true,
    override val loadMoreError: Boolean = false
) : BaseUiState

@HiltViewModel
class DynamicViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    preferencesDataStore: UserPreferencesDataStore
) : BaseViewModel<DynamicUiState>(
    initialUiState = DynamicUiState(),
    preferencesDataStore = preferencesDataStore,
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
            var emissionCount = 0
            eventRepository.getReceivedEvents(user, pageToLoad, true).collect {
                emissionCount++
                it.fold(onSuccess = { newEvents ->
                    val currentEvents =
                        if (isRefresh || initialLoad) emptyList() else _uiState.value.events
                    val updatedEvents = currentEvents + newEvents
                    handleResult(
                        emissionCount,
                        newEvents,
                        pageToLoad,
                        isRefresh,
                        initialLoad,
                        isLoadMore,
                        updateSuccess = { currentState, items, page, isR, initialL, isLM ->
                            currentState.copy(
                                events = updatedEvents
                            )
                        },
                        updateFailure = { currentState, errorMessage, isLM ->
                            currentState.copy(
                                events = emptyList() // Clear events on failure if no data found
                            )
                        }
                    )
                }, onFailure = { exception ->
                    updateErrorState(exception, isLoadMore, "Failed to load events")
                })
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
