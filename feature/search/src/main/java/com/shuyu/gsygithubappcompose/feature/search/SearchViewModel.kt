package com.shuyu.gsygithubappcompose.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.network.model.Repository
import com.shuyu.gsygithubappcompose.core.network.model.User
import com.shuyu.gsygithubappcompose.data.repository.RepositoryRepository
import com.shuyu.gsygithubappcompose.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val repositoryRepository: RepositoryRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchType = MutableStateFlow(SearchType.REPOSITORY)
    val searchType: StateFlow<SearchType> = _searchType.asStateFlow()

    private val _repositoryResults = MutableStateFlow<List<Repository>>(emptyList())
    val repositoryResults: StateFlow<List<Repository>> = _repositoryResults.asStateFlow()

    private val _userResults = MutableStateFlow<List<User>>(emptyList())
    val userResults: StateFlow<List<User>> = _userResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSearchTypeChanged(type: SearchType) {
        _searchType.value = type
        // Clear results when search type changes
        _repositoryResults.value = emptyList()
        _userResults.value = emptyList()
        _error.value = null
    }

    fun performSearch() {
        _error.value = null
        if (_searchQuery.value.isBlank()) {
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                when (_searchType.value) {
                    SearchType.REPOSITORY -> {
                        val response = repositoryRepository.searchRepositories(_searchQuery.value)
                        _repositoryResults.value = response.items
                    }
                    SearchType.USER -> {
                        val response = userRepository.searchUsers(_searchQuery.value)
                        _userResults.value = response.items
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
