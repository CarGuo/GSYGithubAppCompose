package com.shuyu.gsygithubappcompose.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.network.model.User
import com.shuyu.gsygithubappcompose.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile(initialLoad = true)
    }

    fun loadProfile(initialLoad: Boolean = false, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true, error = null) }
            } else if (initialLoad) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }

            val username = preferencesDataStore.username.first()
            if (username.isNullOrEmpty()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = "No username found"
                    )
                }
                return@launch
            }

            var emissionCount = 0
            userRepository.getUser(username).collect {
                emissionCount++
                it.fold(
                    onSuccess = { user ->
                        _uiState.update {
                            it.copy(
                                user = user,
                                // Only reset loading states on second emission (network result)
                                isLoading = if (emissionCount >= 2) false else it.isLoading,
                                isRefreshing = if (emissionCount >= 2) false else it.isRefreshing,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = exception.message ?: "Failed to load profile"
                            )
                        }
                    }
                )
            }
        }
    }

    fun refreshProfile() {
        loadProfile(isRefresh = true)
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}
