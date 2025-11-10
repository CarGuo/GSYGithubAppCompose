package com.shuyu.gsygithubappcompose.feature.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonViewModel @Inject constructor(
    private val userRepository: UserRepository,
    preferencesDataStore: UserPreferencesDataStore,
    stringResourceProvider: StringResourceProvider,
    private val savedStateHandle: SavedStateHandle
) : BaseProfileViewModel(userRepository, preferencesDataStore, stringResourceProvider) {

    override fun getUserLogin(onSuccess: (String) -> Unit) {
        val username = savedStateHandle.get<String>("username")
        if (username != null) {
            onSuccess(username)
            checkFollowStatus(username)
        }
    }

    private fun checkFollowStatus(username: String) {
        viewModelScope.launch {
            userRepository.checkFollowing(username).onEach { result ->
                result.data.fold(
                    onSuccess = {
                        _uiState.update { currentState ->
                            currentState.copy(isFollowing = it)
                        }
                    },
                    onFailure = {
                        //ignore
                    }
                )
            }.launchIn(viewModelScope)
        }
    }

    fun changeFollowStatus() {
        val username = savedStateHandle.get<String>("username") ?: return
        viewModelScope.launch {
            val currentStatus = uiState.value.isFollowing
            val flow = if (currentStatus) {
                userRepository.unFollowUser(username)
            } else {
                userRepository.followUser(username)
            }
            flow.onEach { result ->
                result.data.fold(
                    onSuccess = {
                        _uiState.update { currentState ->
                            currentState.copy(isFollowing = !currentStatus)
                        }
                    },
                    onFailure = {
                        //ignore
                    }
                )
            }.launchIn(viewModelScope)
        }
    }
}
