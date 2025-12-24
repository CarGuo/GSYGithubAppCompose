package com.shuyu.gsygithubappcompose.feature.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _navigationDestination = MutableStateFlow<String?>(null)
    val navigationDestination: StateFlow<String?> = _navigationDestination.asStateFlow()

    init {
        viewModelScope.launch {
            // Start a timer for the minimum splash screen duration
            val minDurationJob = launch { delay(2000) }

            // Fetch the login status. first() will suspend until the DataStore emits a value.
            val isLoggedIn = userRepository.isLoggedIn().first()

            // Wait for the minimum duration to pass
            minDurationJob.join()

            // Navigate based on login status
            if (isLoggedIn) {
                _navigationDestination.value = "home"
            } else {
                _navigationDestination.value = "login"
            }
        }
    }
}
