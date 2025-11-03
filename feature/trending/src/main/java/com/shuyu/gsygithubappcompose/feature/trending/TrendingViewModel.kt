package com.shuyu.gsygithubappcompose.feature.trending

import android.nfc.tech.MifareUltralight.PAGE_SIZE
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.network.model.Repository
import com.shuyu.gsygithubappcompose.data.repository.RepositoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrendingUiState(
    val repositories: List<Repository> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = true,
    val loadMoreError: Boolean = false
)

@HiltViewModel
class TrendingViewModel @Inject constructor(
    private val repositoryRepository: RepositoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrendingUiState())
    val uiState: StateFlow<TrendingUiState> = _uiState.asStateFlow()

    init {
        loadTrendingRepositories(initialLoad = true)
    }

    fun loadTrendingRepositories(initialLoad: Boolean = false, isRefresh: Boolean = false, isLoadMore: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true, error = null, currentPage = 1, hasMore = true, loadMoreError = false) }
            } else if (isLoadMore) {
                _uiState.update { it.copy(isLoadingMore = true, error = null, loadMoreError = false) }
            } else if (initialLoad) {
                _uiState.update { it.copy(isLoading = true, error = null, loadMoreError = false) }
            }

            val pageToLoad = if (isRefresh) 1 else _uiState.value.currentPage

            var emissionCount = 0
            repositoryRepository.getTrendingRepositories().collect {
                emissionCount++
                it.fold(
                    onSuccess = { newRepos ->
                        val currentRepos =
                            if (isRefresh || initialLoad) emptyList() else _uiState.value.repositories
                        val updatedRepos = currentRepos + newRepos
                        _uiState.update { it ->
                            it.copy(
                                repositories = updatedRepos,
                                // Only reset loading states on second emission (network result)
                                isLoading = if (emissionCount >= 2) false else it.isLoading,
                                isRefreshing = if (emissionCount >= 2) false else it.isRefreshing,
                                isLoadingMore = if (emissionCount >= 2) false else it.isLoadingMore,
                                error = null,
                                currentPage = if (emissionCount >= 2) pageToLoad + 1 else it.currentPage,
                                hasMore = if (emissionCount >= 2) newRepos.size == PAGE_SIZE else it.hasMore,
                                loadMoreError = false
                            )
                        }
                    },
                    onFailure = { exception ->
                        _uiState.update { it ->
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                isLoadingMore = false,
                                error = exception.message ?: "Failed to load repositories",
                                loadMoreError = isLoadMore
                            )
                        }
                    }
                )
            }
        }
    }

    fun refreshTrendingRepositories() {
        loadTrendingRepositories(isRefresh = true)
    }

    fun loadMoreTrendingRepositories() {
        if (!_uiState.value.isLoadingMore && _uiState.value.hasMore) {
            loadTrendingRepositories(isLoadMore = true)
        }
    }
}
