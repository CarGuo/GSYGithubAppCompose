package com.shuyu.gsygithubappcompose.feature.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.core.network.config.NetworkConfig
import com.shuyu.gsygithubappcompose.core.network.model.Notification
import com.shuyu.gsygithubappcompose.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val stringResourceProvider: StringResourceProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState = _uiState.asStateFlow()

    private var page = 1

    fun doInitialLoad() {
        load(false)
    }

    fun refresh() {
        load(true)
    }

    fun loadMore() {
        load(false)
    }

    private fun load(isRefresh: Boolean) {
        if (uiState.value.isLoadingMore || uiState.value.isRefreshing) {
            return
        }
        val loadPage = if (isRefresh) {
            1
        } else {
            page
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isPageLoading = loadPage == 1 && !isRefresh,
                    isRefreshing = isRefresh,
                    isLoadingMore = loadPage > 1,
                    loadMoreError = false,
                    error = null
                )
            }

            notificationRepository.getNotifications(
                page = loadPage,
                all = uiState.value.selectedItemType == NotificationItemType.ALL,
                participating = uiState.value.selectedItemType == NotificationItemType.PARTICIPATING
            ).onEach { result ->
                result.data.fold(
                    onSuccess = { notifications ->
                        val currentList = if (isRefresh) emptyList() else _uiState.value.notifications
                        val newList = currentList + notifications
                        page = loadPage + 1
                        _uiState.update {
                            it.copy(
                                notifications = newList,
                                hasMore = notifications.size >= NetworkConfig.PER_PAGE
                            )
                        }
                    },
                    onFailure = { throwable ->
                        val isLoadMoreError = loadPage > 1
                        _uiState.update {
                            it.copy(
                                error = if (isLoadMoreError) null else throwable.message,
                                loadMoreError = isLoadMoreError
                            )
                        }
                    }
                )
                _uiState.update {
                    it.copy(
                        isPageLoading = false, isRefreshing = false, isLoadingMore = false
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    fun setSelectedItemType(type: NotificationItemType) {
        if (uiState.value.selectedItemType == type || uiState.value.isSwitchingItemType) return
        viewModelScope.launch {
            _uiState.update { it.copy(selectedItemType = type, isSwitchingItemType = true) }
            // force refresh
            load(true)
            _uiState.update { it.copy(isSwitchingItemType = false) }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDialogLoading = true) }
            notificationRepository.markAllNotificationsAsRead().onEach { result ->
                result.data.fold(
                    onSuccess = {
                        _uiState.update { it.copy(isDialogLoading = false) }
                        refresh()
                    },
                    onFailure = { throwable ->
                        _uiState.update {
                            it.copy(
                                isDialogLoading = false,
                                error = throwable.message
                            )
                        }
                    }
                )
            }.launchIn(viewModelScope)
        }
    }
}

data class NotificationUiState(
    val notifications: List<Notification> = emptyList(),
    val selectedItemType: NotificationItemType = NotificationItemType.UNREAD,
    val isSwitchingItemType: Boolean = false,
    val hasMore: Boolean = true,
    val isPageLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val loadMoreError: Boolean = false,
    val isDialogLoading: Boolean = false
)
