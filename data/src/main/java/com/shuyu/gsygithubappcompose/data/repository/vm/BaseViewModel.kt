package com.shuyu.gsygithubappcompose.data.repository.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.core.network.config.NetworkConfig
import com.shuyu.gsygithubappcompose.data.repository.DataSource
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
    private val stringResourceProvider: StringResourceProvider,
    private val commonStateUpdater: (
        currentState: UiState, isPageLoading: Boolean, isRefreshing: Boolean, isLoadingMore: Boolean, error: String?, currentPage: Int, hasMore: Boolean, loadMoreError: Boolean
    ) -> UiState
) : ViewModel() {

    constructor(
        initialUiState: UiState,
        stringResourceProvider: StringResourceProvider,
        commonStateUpdater: (
            currentState: UiState, isPageLoading: Boolean, isRefreshing: Boolean, isLoadingMore: Boolean, error: String?, currentPage: Int, hasMore: Boolean, loadMoreError: Boolean
        ) -> UiState
    ) : this(initialUiState, null, stringResourceProvider, commonStateUpdater)

    protected val _uiState: MutableStateFlow<UiState> = MutableStateFlow(initialUiState)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var isInitialLoadStarted = false

    protected abstract fun loadData(
        initialLoad: Boolean = false, isRefresh: Boolean = false, isLoadMore: Boolean = false
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
                        error = stringResourceProvider.getString(R.string.error_datastore_not_provided),
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
                        error = stringResourceProvider.getString(R.string.error_no_username_found),
                        loadMoreError = false
                    )
                }
                return@launch
            }

            val pageToLoad = if (isRefresh) 1 else _uiState.value.currentPage
            dataLoad(username, pageToLoad)
        }
    }


    protected fun launchDataLoad(
        initialLoad: Boolean,
        isRefresh: Boolean,
        isLoadMore: Boolean,
        dataLoad: suspend (pageToLoad: Int) -> Unit
    ) {
        viewModelScope.launch {
            updateLoadingState(initialLoad, isRefresh, isLoadMore)
            val pageToLoad = if (isRefresh) 1 else _uiState.value.currentPage
            dataLoad(pageToLoad)
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
                    it, isLoadingMore = true, error = null, loadMoreError = false
                )
            } else if (initialLoad) {
                updateUiStateWithCommonProperties(
                    it, isPageLoading = true, error = null, loadMoreError = false
                )
            } else it
        }
    }

    protected fun <T> handleResult(
        newItems: List<T>,
        pageToLoad: Int,
        isRefresh: Boolean,
        initialLoad: Boolean,
        isLoadMore: Boolean,
        source: DataSource,
        isDbEmpty: Boolean,
        updateSuccess: (UiState, List<T>, Int, Boolean, Boolean, Boolean) -> UiState,
        updateFailure: (UiState, String?, Boolean) -> UiState
    ) {
        _uiState.update { currentState ->
            val updatedState = if (newItems.isNotEmpty()) {
                updateSuccess(
                    currentState, newItems, pageToLoad, isRefresh, initialLoad, isLoadMore
                )
            } else {
                updateFailure(
                    currentState,
                    stringResourceProvider.getString(R.string.error_no_data_found),
                    isLoadMore
                )
            }

            // The final emission is when data comes from the network.
            // This ensures loading indicators are dismissed and pagination states are updated only after network data is received.
            val isFinalEmission = source == DataSource.NETWORK

            updateUiStateWithCommonProperties(
                updatedState,
                isPageLoading = false, // Any data arrival should immediately hide the full-screen loading indicator.
                isRefreshing = if (isFinalEmission) false else updatedState.isRefreshing,
                isLoadingMore = if (isFinalEmission) false else updatedState.isLoadingMore,
                hasMore = if (isFinalEmission) newItems.size == NetworkConfig.PER_PAGE else updatedState.hasMore,
                currentPage = if (isFinalEmission) pageToLoad + 1 else updatedState.currentPage,
                error = if (isFinalEmission) null else updatedState.error,
                loadMoreError = if (isFinalEmission) false else updatedState.loadMoreError
            )
        }
    }

    protected fun updateErrorState(
        exception: Throwable?,
        isLoadMore: Boolean,
        defaultMessage: String = stringResourceProvider.getString(R.string.error_unknown)
    ) {
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


    /**
     * Hilt 注入时机注意:
     * 1、Hilt 创建一个 @HiltViewModel 实例时，它首先调用该类的构造函数，并将所有依赖项（如 userRepository）作为参数传入。
     * 2.Kotlin 构造顺序: 在 Kotlin 中，当一个子类（ProfileViewModel）被实例化时，其构造过程遵循以下顺序：
     *  ◦子类的构造函数参数被求值。
     *  ◦父类（BaseProfileViewModel）的 init 代码块和构造函数被执行。
     *  ◦子类（ProfileViewModel）的 init 代码块和属性初始化器被执行。
     * 3.问题根源:
     *  ◦ ProfileViewModel 将 userRepository 和 preferencesDataStore 传递给了父类 BaseProfileViewModel 的构造函数。
     *  ◦如果 BaseProfileViewModel 的构造函数或其 init 块直接或间接地调用了 getUserLogin 方法，此时就会出现问题。
     *  ◦因为 getUserLogin 是一个被 ProfileViewModel 覆写的方法，所以在父类构造期间，调用的将是子类的实现。
     *  ◦然而，在父类构造函数执行完毕之前，子类 ProfileViewModel 自身的属性（包括从构造函数参数初始化的 userRepository 和 preferencesDataStore）尚未被初始化。它们仍然是 null。
     *  ◦因此，当 ProfileViewModel 的 getUserLogin 方法被过早调用时，它尝试访问的 userRepository 和 preferencesDataStore 自然就是 null，从而导致 NullPointerException。
     * */

    fun doInitialLoad() {
        if (!isInitialLoadStarted) {
            isInitialLoadStarted = true
            loadData(initialLoad = true)
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
