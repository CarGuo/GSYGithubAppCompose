package com.shuyu.gsygithubappcompose.feature.info

import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.core.network.model.User
import com.shuyu.gsygithubappcompose.data.repository.UserRepository
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseUiState
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.shuyu.gsygithubappcompose.core.common.R

data class InfoUiState(
    val user: User? = null,
    override val isPageLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
    override val error: String? = null,
    override val currentPage: Int = 1,
    override val hasMore: Boolean = false,
    override val loadMoreError: Boolean = false
) : BaseUiState

abstract class BaseInfoViewModel(
    private val userRepository: UserRepository,
    private val preferencesDataStore: UserPreferencesDataStore,
    private val stringResourceProvider: StringResourceProvider
) : BaseViewModel<InfoUiState>(
    initialUiState = InfoUiState(),
    preferencesDataStore = preferencesDataStore,
    stringResourceProvider = stringResourceProvider,
    commonStateUpdater = { currentState: InfoUiState, isPageLoading, isRefreshing, isLoadingMore, error, currentPage, hasMore, loadMoreError ->
        currentState.copy(
            isPageLoading = isPageLoading,
            isRefreshing = isRefreshing,
            isLoadingMore = isLoadingMore,
            error = error,
            currentPage = currentPage,
            hasMore = hasMore,
            loadMoreError = loadMoreError
        )
    }) {

    override fun loadData(initialLoad: Boolean, isRefresh: Boolean, isLoadMore: Boolean) {
        launchDataLoadWithUser(initialLoad, isRefresh, isLoadMore) { username, _ ->
            userRepository.getUser(username).collect { repoResult ->
                repoResult.data.fold(
                    onSuccess = { user ->
                        _uiState.update {
                            it.copy(user = user)
                        }
                    },
                    onFailure = { throwable ->
                        updateErrorState(throwable, isLoadMore)
                    }
                )
            }
        }
    }

    fun updateUser(userInfo: Map<String, String>) {
        viewModelScope.launch {
            userRepository.updateUserInfo(userInfo)
                .onSuccess { updatedUser ->
                    _uiState.update { it.copy(user = updatedUser) }
                    showToast(stringResourceProvider.getString(R.string.user_info_update_success))
                }
                .onFailure { throwable ->
                    updateErrorState(throwable, false, stringResourceProvider.getString(R.string.user_info_update_failed))
                }
        }
    }
}
