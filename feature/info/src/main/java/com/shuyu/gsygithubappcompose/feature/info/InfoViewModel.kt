package com.shuyu.gsygithubappcompose.feature.info

import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    userRepository: UserRepository,
    preferencesDataStore: UserPreferencesDataStore,
    stringResourceProvider: StringResourceProvider
) : BaseInfoViewModel(userRepository, preferencesDataStore, stringResourceProvider) {

    init {
        loadData(true)
    }
}
