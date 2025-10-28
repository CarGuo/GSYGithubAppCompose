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

data class DynamicUiState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DynamicViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val preferencesDataStore: UserPreferencesDataStore
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DynamicUiState())
    val uiState: StateFlow<DynamicUiState> = _uiState.asStateFlow()
    
    fun loadEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val username = preferencesDataStore.username.first()
            if (username.isNullOrEmpty()) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "No username found"
                    )
                }
                return@launch
            }
            
            val result = eventRepository.getReceivedEvents(username)
            
            result.fold(
                onSuccess = { events ->
                    _uiState.update { 
                        it.copy(
                            events = events,
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load events"
                        )
                    }
                }
            )
        }
    }
}
