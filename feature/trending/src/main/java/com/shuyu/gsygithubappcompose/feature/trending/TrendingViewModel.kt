package com.shuyu.gsygithubappcompose.feature.trending

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
    val error: String? = null
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

    fun loadTrendingRepositories(initialLoad: Boolean = false, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true, error = null) }
            } else if (initialLoad) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }

            repositoryRepository.getTrendingRepositories().collect {
                it.fold(
                    onSuccess = { repos ->
                        _uiState.update {
                            it.copy(
                                repositories = repos,
                                isLoading = false,
                                isRefreshing = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = exception.message ?: "Failed to load repositories"
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
}
