package com.shuyu.gsygithubappcompose.data.repository.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.network.config.NetworkConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface BaseUiState {
    val isPageLoading: Boolean
    val isRefreshing: Boolean
    val isLoadingMore: Boolean
    val error: String?
    val currentPage: Int
    val hasMore: Boolean
    val loadMoreError: Boolean
}

abstract class BaseViewModel<UiState : BaseUiState>(
    initialUiState: UiState,
    private val preferencesDataStore: UserPreferencesDataStore? = null,
    private val commonStateUpdater: (
        currentState: UiState,
        isPageLoading: Boolean,
        isRefreshing: Boolean,
        isLoadingMore: Boolean,
        error: String?,
        currentPage: Int,
        hasMore: Boolean,
        loadMoreError: Boolean
    ) -> UiState
) : ViewModel() {

    constructor(
        initialUiState: UiState,
        commonStateUpdater: (
            currentState: UiState,
            isPageLoading: Boolean,
            isRefreshing: Boolean,
            isLoadingMore: Boolean,
            error: String?,
            currentPage: Int,
            hasMore: Boolean,
            loadMoreError: Boolean
        ) -> UiState
    ) : this(initialUiState, null, commonStateUpdater)

    protected val _uiState: MutableStateFlow<UiState> = MutableStateFlow(initialUiState)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadData(initialLoad = true)
    }

    protected abstract fun loadData(
        initialLoad: Boolean = false,
        isRefresh: Boolean = false,
        isLoadMore: Boolean = false
    )

    private fun updateUiStateWithCommonProperties(
        currentState: UiState,
        isPageLoading: Boolean = currentState.isPageLoading,
        isRefreshing: Boolean = currentState.isRefreshing,
        isLoadingMore: Boolean = currentState.isLoadingMore,
        error: String? = currentState.error,
        currentPage: Int = currentState.currentPage,
        hasMore: Boolean = currentState.hasMore,
        loadMoreError: Boolean = currentState.loadMoreError
    ): UiState {
        return commonStateUpdater(
            currentState,
            isPageLoading,
            isRefreshing,
            isLoadingMore,
            error,
            currentPage,
            hasMore,
            loadMoreError
        )
    }

    protected fun launchDataLoadWithUser(
        initialLoad: Boolean,
        isRefresh: Boolean,
        isLoadMore: Boolean,
        dataLoad: suspend (username: String, pageToLoad: Int) -> Unit
    ) {
        viewModelScope.launch {
            updateLoadingState(initialLoad, isRefresh, isLoadMore)

            if (preferencesDataStore == null) {
                _uiState.update {
                    updateUiStateWithCommonProperties(
                        it,
                        isPageLoading = false,
                        isRefreshing = false,
                        isLoadingMore = false,
                        error = "UserPreferencesDataStore not provided for username-dependent operation", // TODO: Use StringResourceProvider
                        loadMoreError = false
                    )
                }
                return@launch
            }

            val username = preferencesDataStore.username.first()
            if (username.isNullOrEmpty()) {
                _uiState.update {
                    updateUiStateWithCommonProperties(
                        it,
                        isPageLoading = false,
                        isRefreshing = false,
                        isLoadingMore = false,
                        error = "No username found", // TODO: Use StringResourceProvider
                        loadMoreError = false
                    )
                }
                return@launch
            }

            val pageToLoad = if (isRefresh) 1 else _uiState.value.currentPage
            dataLoad(username, pageToLoad)
        }
    }

    private fun updateLoadingState(initialLoad: Boolean, isRefresh: Boolean, isLoadMore: Boolean) {
        _uiState.update {
            if (isRefresh) {
                updateUiStateWithCommonProperties(
                    it,
                    isRefreshing = true,
                    error = null,
                    currentPage = 1,
                    hasMore = true,
                    loadMoreError = false
                )
            } else if (isLoadMore) {
                updateUiStateWithCommonProperties(
                    it,
                    isLoadingMore = true, error = null, loadMoreError = false
                )
            } else if (initialLoad) {
                updateUiStateWithCommonProperties(
                    it,
                    isPageLoading = true, error = null, loadMoreError = false
                )
            } else it
        }
    }

    protected fun <T> handleResult(
        emissionCount: Int,
        newItems: List<T>,
        pageToLoad: Int,
        isRefresh: Boolean,
        initialLoad: Boolean,
        isLoadMore: Boolean,
        updateSuccess: (UiState, List<T>, Int, Boolean, Boolean, Boolean) -> UiState,
        updateFailure: (UiState, String?, Boolean) -> UiState
    ) {
        _uiState.update { currentState ->
            val updatedState = if (newItems.isNotEmpty()) {
                updateSuccess(
                    currentState,
                    newItems,
                    pageToLoad,
                    isRefresh,
                    initialLoad,
                    isLoadMore
                )
            } else {
                updateFailure(
                    currentState,
                    "No data found", // TODO: Use StringResourceProvider
                    isLoadMore
                )
            }
            updateUiStateWithCommonProperties(
                updatedState,
                isPageLoading = if (emissionCount >= 1) false else updatedState.isPageLoading,
                isRefreshing = if (emissionCount >= 2) false else updatedState.isRefreshing,
                isLoadingMore = if (emissionCount >= 2) false else updatedState.isLoadingMore,
                hasMore = if (emissionCount >= 2) newItems.size == NetworkConfig.PER_PAGE else updatedState.hasMore,
                currentPage = if (emissionCount >= 2) pageToLoad + 1 else updatedState.currentPage,
                error = if (emissionCount >= 2) null else updatedState.error,
                loadMoreError = if (emissionCount >= 2) false else updatedState.loadMoreError
            )
        }
    }

    protected fun updateErrorState(exception: Throwable?, isLoadMore: Boolean, defaultMessage: String = "Unknown error") {
        _uiState.update {
            updateUiStateWithCommonProperties(
                it,
                isPageLoading = false,
                isRefreshing = false,
                isLoadingMore = false,
                error = exception?.message ?: defaultMessage,
                loadMoreError = isLoadMore
            )
        }
    }

    fun refresh() {
        loadData(isRefresh = true)
    }

    fun loadMore() {
        if (!_uiState.value.isLoadingMore && _uiState.value.hasMore) {
            loadData(isLoadMore = true)
        }
    }
}
