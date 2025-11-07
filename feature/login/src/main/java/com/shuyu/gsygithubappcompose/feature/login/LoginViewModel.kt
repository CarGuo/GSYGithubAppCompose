package com.shuyu.gsygithubappcompose.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class LoginUiState(
    val token: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val showOAuthWebView: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()
    
    fun onTokenChange(token: String) {
        _uiState.update { it.copy(token = token, error = null) }
    }
    
    fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = userRepository.login(_uiState.value.token)
            
            result.fold(
                onSuccess = {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            error = null
                        )
                    }
                },
                onFailure = { exception ->
                    val errorMessage = exception.message ?: "Login failed"
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = errorMessage
                        )
                    }
                    _toastMessage.emit(errorMessage)
                }
            )
        }
    }
    
    fun startOAuthFlow() {
        _uiState.update { it.copy(showOAuthWebView = true, error = null) }
    }
    
    fun cancelOAuthFlow() {
        _uiState.update { it.copy(showOAuthWebView = false) }
    }
    
    fun handleOAuthCode(clientId: String, clientSecret: String, code: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showOAuthWebView = false, error = null) }
            
            val result = userRepository.loginWithOAuth(clientId, clientSecret, code)
            
            result.fold(
                onSuccess = {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            error = null
                        )
                    }
                },
                onFailure = { exception ->
                    val errorMessage = exception.message ?: "OAuth login failed"
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = errorMessage
                        )
                    }
                    _toastMessage.emit(errorMessage)
                }
            )
        }
    }
}
