package com.shuyu.gsygithubappcompose.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.database.entity.SearchHistoryEntity
import com.shuyu.gsygithubappcompose.core.network.config.NetworkConfig
import com.shuyu.gsygithubappcompose.core.network.model.Repository
import com.shuyu.gsygithubappcompose.core.network.model.User
import com.shuyu.gsygithubappcompose.data.repository.RepositoryRepository
import com.shuyu.gsygithubappcompose.data.repository.SearchHistoryRepository
import com.shuyu.gsygithubappcompose.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val repositoryRepository: RepositoryRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchType = MutableStateFlow(SearchType.REPOSITORY)
    val searchType: StateFlow<SearchType> = _searchType.asStateFlow()

    private val _repositoryResults = MutableStateFlow<List<Repository>>(emptyList())
    val repositoryResults: StateFlow<List<Repository>> = _repositoryResults.asStateFlow()

    private val _userResults = MutableStateFlow<List<User>>(emptyList())
    val userResults: StateFlow<List<User>> = _userResults.asStateFlow()

    private val _isPageLoading = MutableStateFlow(false)
    val isPageLoading: StateFlow<Boolean> = _isPageLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _repoCurrentPage = MutableStateFlow(1)
    private val _userCurrentPage = MutableStateFlow(1)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _hasMoreRepo = MutableStateFlow(true)
    val hasMoreRepo: StateFlow<Boolean> = _hasMoreRepo.asStateFlow()

    private val _hasMoreUser = MutableStateFlow(true)
    val hasMoreUser: StateFlow<Boolean> = _hasMoreUser.asStateFlow()

    private val _loadMoreErrorRepo = MutableStateFlow(false)
    val loadMoreErrorRepo: StateFlow<Boolean> = _loadMoreErrorRepo.asStateFlow()

    private val _loadMoreErrorUser = MutableStateFlow(false)
    val loadMoreErrorUser: StateFlow<Boolean> = _loadMoreErrorUser.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<SearchHistoryEntity>>(emptyList<SearchHistoryEntity>()) // Corrected type inference
    val searchHistory: StateFlow<List<SearchHistoryEntity>> = _searchHistory.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    init {
        searchHistoryRepository.getSearchHistory()
            .onEach { _searchHistory.value = it }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSearchTypeChanged(type: SearchType) {
        _searchType.value = type
        // Clear results and reset pagination when search type changes
        _repositoryResults.value = emptyList()
        _userResults.value = emptyList()
        _repoCurrentPage.value = 1
        _userCurrentPage.value = 1
        _hasMoreRepo.value = true
        _hasMoreUser.value = true
        _loadMoreErrorRepo.value = false
        _loadMoreErrorUser.value = false
        _error.value = null
    }

    fun performSearch(page: Int = 1, isRefresh: Boolean = false) {
        _error.value = null
        if (_searchQuery.value.isBlank()) {
            return
        }

        if (isRefresh) {
            _isRefreshing.value = true
        } else if (page == 1) {
            _isPageLoading.value = true
        } else {
            _isLoadingMore.value = true
        }

        viewModelScope.launch {
            try {
                when (_searchType.value) {
                    SearchType.REPOSITORY -> {
                        _loadMoreErrorRepo.value = false
                        val response =
                            repositoryRepository.searchRepositories(_searchQuery.value, page)
                        if (page == 1) {
                            _repositoryResults.value = response.items
                        } else {
                            _repositoryResults.value = _repositoryResults.value + response.items
                        }
                        _hasMoreRepo.value = response.items.size == NetworkConfig.PER_PAGE
                    }

                    SearchType.USER -> {
                        _loadMoreErrorUser.value = false
                        val response = userRepository.searchUsers(_searchQuery.value, page)
                        if (page == 1) {
                            _userResults.value = response.items
                        } else {
                            _userResults.value = _userResults.value + response.items
                        }
                        _hasMoreUser.value = response.items.size == NetworkConfig.PER_PAGE
                    }
                }
                // Save search query to history only on successful search and if it's the first page
                if (page == 1) {
                    saveSearchQuery(_searchQuery.value)
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown search error"
                _error.value = errorMessage
                _toastMessage.emit(errorMessage)
                if (page > 1) {
                    when (_searchType.value) {
                        SearchType.REPOSITORY -> _loadMoreErrorRepo.value = true
                        SearchType.USER -> _loadMoreErrorUser.value = true
                    }
                }
            } finally {
                _isPageLoading.value = false
                _isRefreshing.value = false
                _isLoadingMore.value = false
            }
        }
    }

    fun refreshSearch() {
        when (_searchType.value) {
            SearchType.REPOSITORY -> _repoCurrentPage.value = 1
            SearchType.USER -> _userCurrentPage.value = 1
        }
        performSearch(page = 1, isRefresh = true)
    }

    fun loadNextPage() {
        when (_searchType.value) {
            SearchType.REPOSITORY -> {
                _repoCurrentPage.value++
                performSearch(page = _repoCurrentPage.value)
            }

            SearchType.USER -> {
                _userCurrentPage.value++
                performSearch(page = _userCurrentPage.value)
            }
        }
    }

    private fun saveSearchQuery(query: String) {
        viewModelScope.launch {
            searchHistoryRepository.saveSearchQuery(query)
        }
    }
}