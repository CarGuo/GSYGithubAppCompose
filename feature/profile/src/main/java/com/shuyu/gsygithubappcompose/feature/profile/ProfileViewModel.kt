package com.shuyu.gsygithubappcompose.feature.profile

import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.core.ui.GSYNavigator
import com.shuyu.gsygithubappcompose.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferencesDataStore: UserPreferencesDataStore,
    stringResourceProvider: StringResourceProvider
) : BaseProfileViewModel(userRepository, preferencesDataStore, stringResourceProvider) {

    override fun getUserLogin(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val username = preferencesDataStore.username.first()
            if (username != null) {
                onSuccess(username)
            }
        }
    }

    fun logout(navigator: GSYNavigator) {
        viewModelScope.launch {
            userRepository.logout()
            navigator.replace("login")
        }
    }
}