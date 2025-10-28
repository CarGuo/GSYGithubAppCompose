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
    val error: String? = null
)

@HiltViewModel
class TrendingViewModel @Inject constructor(
    private val repositoryRepository: RepositoryRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TrendingUiState())
    val uiState: StateFlow<TrendingUiState> = _uiState.asStateFlow()
    
    fun loadTrendingRepositories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = repositoryRepository.getTrendingRepositories()
            
            result.fold(
                onSuccess = { repos ->
                    _uiState.update { 
                        it.copy(
                            repositories = repos,
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load repositories"
                        )
                    }
                }
            )
        }
    }
}
