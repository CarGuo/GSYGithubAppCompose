package com.shuyu.gsygithubappcompose.feature.home

import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.core.network.model.User
import com.shuyu.gsygithubappcompose.data.repository.IssueRepository
import com.shuyu.gsygithubappcompose.data.repository.RepositoryRepository
import com.shuyu.gsygithubappcompose.data.repository.UserRepository
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseUiState
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val repositoryRepository: RepositoryRepository,
    private val stringResourceProvider: StringResourceProvider,
    userPreferencesDataStore: UserPreferencesDataStore
) : BaseViewModel<HomeUiState>(
    initialUiState = HomeUiState(),
    preferencesDataStore = userPreferencesDataStore,
    stringResourceProvider = stringResourceProvider,
    commonStateUpdater = { currentState, isPageLoading, isRefreshing, isLoadingMore, error, _, _, _ ->
        currentState.copy(
            isPageLoading = isPageLoading,
            isRefreshing = isRefreshing,
            isLoadingMore = isLoadingMore,
            error = error,
        )
    }) {
    init {
        viewModelScope.launch {
            doInitialLoad()
        }
    }

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()

    override fun loadData(initialLoad: Boolean, isRefresh: Boolean, isLoadMore: Boolean) {
        launchDataLoadWithUser(initialLoad, isRefresh, isLoadMore) { user, _ ->
            userRepository.getUser(user).collect {
                it.data.fold(onSuccess = { user ->
                    _uiState.update { currentState ->
                        currentState.copy(user = user)
                    }
                }, onFailure = { e ->
                    updateErrorState(e, isLoadMore, "")
                })
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _logoutEvent.emit(Unit)
        }
    }

    suspend fun submitFeedback(title: String, content: String): Boolean {
        _uiState.update { it.copy(isLoadingDialog = true) }
        var success = false
        repositoryRepository.createIssue(
            owner = "CarGuo", repo = "GSYGithubAppCompose", title = title, body = content
        ).flowOn(Dispatchers.IO).collectLatest { it ->
            it.data.fold(onSuccess = {
                _uiState.update { it -> it.copy(isLoadingDialog = false) }
                showToast(stringResourceProvider.getString(R.string.issue_submit_success))
                success = true
            }, onFailure = { e ->
                _uiState.update { it -> it.copy(isLoadingDialog = false) }
                showToast(
                    e.message ?: stringResourceProvider.getString(R.string.issue_submit_fail)
                )
                success = false
            })
        }
        return success
    }
}

data class HomeUiState(
    val user: User? = null,
    val isLoadingDialog: Boolean = false,
    override val isPageLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
    override val error: String? = null,
    override val currentPage: Int = 1,
    override val hasMore: Boolean = false,
    override val loadMoreError: Boolean = false
) : BaseUiState
