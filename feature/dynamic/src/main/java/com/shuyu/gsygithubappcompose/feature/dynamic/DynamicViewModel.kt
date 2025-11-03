package com.shuyu.gsygithubappcompose.feature.dynamic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.network.config.NetworkConfig
import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DynamicUiState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = true,
    val loadMoreError: Boolean = false
)

@HiltViewModel
class DynamicViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val preferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(DynamicUiState())
    val uiState: StateFlow<DynamicUiState> = _uiState.asStateFlow()

    init {
        loadEvents(initialLoad = true)
    }

    fun loadEvents(
        initialLoad: Boolean = false, isRefresh: Boolean = false, isLoadMore: Boolean = false
    ) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.update {
                    it.copy(
                        isRefreshing = true,
                        error = null,
                        currentPage = 1,
                        hasMore = true,
                        loadMoreError = false
                    )
                }
            } else if (isLoadMore) {
                _uiState.update {
                    it.copy(
                        isLoadingMore = true, error = null, loadMoreError = false
                    )
                }
            } else if (initialLoad) {
                _uiState.update { it.copy(isLoading = true, error = null, loadMoreError = false) }
            }

            val username = preferencesDataStore.username.first()
            if (username.isNullOrEmpty()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        isLoadingMore = false,
                        error = "No username found",
                        loadMoreError = false
                    )
                }
                return@launch
            }

            val pageToLoad = if (isRefresh) 1 else _uiState.value.currentPage

            var emissionCount = 0
            eventRepository.getReceivedEvents(username, pageToLoad, true).collect {
                emissionCount++
                it.fold(onSuccess = { newEvents ->
                    val currentEvents =
                        if (isRefresh || initialLoad) emptyList() else _uiState.value.events
                    val updatedEvents = currentEvents + newEvents
                    _uiState.update { it ->
                        it.copy(
                            events = updatedEvents,
                            // Only reset loading states on second emission (network result)
                            isLoading = if (emissionCount >= 2) false else it.isLoading,
                            isRefreshing = if (emissionCount >= 2) false else it.isRefreshing,
                            isLoadingMore = if (emissionCount >= 2) false else it.isLoadingMore,
                            error = null,
                            currentPage = if (emissionCount >= 2) pageToLoad + 1 else it.currentPage,
                            hasMore = if (emissionCount >= 2) newEvents.size == NetworkConfig.PER_PAGE else it.hasMore,
                            loadMoreError = false
                        )
                    }
                }, onFailure = { exception ->
                    _uiState.update { it ->
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            isLoadingMore = false,
                            error = exception.message ?: "Failed to load events",
                            loadMoreError = isLoadMore // Set loadMoreError to true only if it was a loadMore operation
                        )
                    }
                })
            }
        }
    }

    fun refreshEvents() {
        loadEvents(isRefresh = true)
    }

    fun loadMoreEvents() {
        if (!_uiState.value.isLoadingMore && _uiState.value.hasMore) {
            loadEvents(isLoadMore = true)
        }
    }
}
