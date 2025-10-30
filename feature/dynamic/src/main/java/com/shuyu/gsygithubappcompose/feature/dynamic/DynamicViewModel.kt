package com.shuyu.gsygithubappcompose.feature.dynamic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
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

const val PAGE_SIZE = 20 // Assuming a page size for loading more

data class DynamicUiState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = true
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

    fun loadEvents(initialLoad: Boolean = false, isRefresh: Boolean = false, isLoadMore: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true, error = null, currentPage = 1, hasMore = true) }
            } else if (isLoadMore) {
                _uiState.update { it.copy(isLoadingMore = true, error = null) }
            } else if (initialLoad) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }

            val username = preferencesDataStore.username.first()
            if (username.isNullOrEmpty()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        isLoadingMore = false,
                        error = "No username found"
                    )
                }
                return@launch
            }

            val pageToLoad = if (isRefresh) 1 else _uiState.value.currentPage

            eventRepository.getReceivedEvents(username, pageToLoad).collect {
                it.fold(
                    onSuccess = { newEvents ->
                        val currentEvents = if (isRefresh || initialLoad) emptyList() else _uiState.value.events
                        val updatedEvents = currentEvents + newEvents
                        _uiState.update {
                            it.copy(
                                events = updatedEvents,
                                isLoading = false,
                                isRefreshing = false,
                                isLoadingMore = false,
                                error = null,
                                currentPage = pageToLoad + 1,
                                hasMore = newEvents.size == PAGE_SIZE // Assuming if we get less than PAGE_SIZE, there are no more pages
                            )
                        }
                    },
                    onFailure = { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                isLoadingMore = false,
                                error = exception.message ?: "Failed to load events"
                            )
                        }
                    }
                )
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
