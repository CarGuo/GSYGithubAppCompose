package com.shuyu.gsygithubappcompose

import androidx.lifecycle.ViewModel
import com.shuyu.gsygithubappcompose.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    val isLoggedIn: Flow<Boolean> = userRepository.isLoggedIn()
}
