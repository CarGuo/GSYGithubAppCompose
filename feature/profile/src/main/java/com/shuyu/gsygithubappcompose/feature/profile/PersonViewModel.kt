package com.shuyu.gsygithubappcompose.feature.profile

import androidx.lifecycle.SavedStateHandle
import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PersonViewModel @Inject constructor(
    userRepository: UserRepository,
    preferencesDataStore: UserPreferencesDataStore,
    stringResourceProvider: StringResourceProvider,
    private val savedStateHandle: SavedStateHandle
) : BaseProfileViewModel(userRepository, preferencesDataStore, stringResourceProvider) {

    override fun getUserLogin(onSuccess: (String) -> Unit) {
        val username = savedStateHandle.get<String>("username")
        if (username != null) {
            onSuccess(username)
        }
    }
}