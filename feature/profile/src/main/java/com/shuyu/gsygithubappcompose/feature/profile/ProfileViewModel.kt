package com.shuyu.gsygithubappcompose.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.core.network.config.NetworkConfig
import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.core.network.model.User
import com.shuyu.gsygithubappcompose.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.shuyu.gsygithubappcompose.core.common.R

data class ProfileUiState(
    val user: User? = null,
    val isPageLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val orgMembers: List<User>? = null,
    val userEvents: List<Event>? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = true,
    val loadMoreError: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferencesDataStore: UserPreferencesDataStore,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile(initialLoad = true)
    }

    fun loadProfile(
        initialLoad: Boolean = false, isRefresh: Boolean = false, isLoadMore: Boolean = false
    ) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.update {
                    it.copy(
                        isRefreshing = true,
                        error = null,
                        currentPage = 1,
                        hasMore = true,
                        loadMoreError = false
                    )
                }
            } else if (isLoadMore) {
                _uiState.update {
                    it.copy(
                        isLoadingMore = true, error = null, loadMoreError = false
                    )
                }
            } else if (initialLoad) {
                _uiState.update {
                    it.copy(
                        isPageLoading = true, error = null, loadMoreError = false
                    )
                }
            }

            val username = preferencesDataStore.username.first()
            if (username.isNullOrEmpty()) {
                _uiState.update {
                    it.copy(
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

            var emissionCount = 0
            userRepository.getUser(username).collect {
                emissionCount++
                it.fold(onSuccess = { user ->
                    _uiState.update { it ->
                        it.copy(
                            user = user,
                            // Only reset loading states on second emission (network result)
                            isPageLoading = if (emissionCount >= 2) false else it.isPageLoading,
                            isRefreshing = if (emissionCount >= 2) false else it.isRefreshing,
                            error = null
                        )
                    }
                    if (user.type == "Organization") {
                        userRepository.getOrgMembers(user.login).collect { it ->
                            it.fold(onSuccess = { members ->
                                _uiState.update { it.copy(orgMembers = members) }
                            }, onFailure = { exception ->
                                _uiState.update { it.copy(error = exception.message) }
                            })
                        }
                    } else {
                        userRepository.getUserEvents(
                            user.login, pageToLoad, NetworkConfig.PER_PAGE
                        ).collect { it ->
                            it.fold(onSuccess = { newEvents ->
                                val currentEvents =
                                    if (isRefresh || initialLoad) emptyList() else _uiState.value.userEvents.orEmpty()
                                val updatedEvents = currentEvents + newEvents
                                _uiState.update { it ->
                                    it.copy(
                                        userEvents = updatedEvents,
                                        isPageLoading = if (emissionCount >= 1) false else it.isPageLoading,
                                        isRefreshing = if (emissionCount >= 2) false else it.isRefreshing,
                                        isLoadingMore = if (emissionCount >= 2) false else it.isLoadingMore,
                                        error = null,
                                        currentPage = if (emissionCount >= 2) pageToLoad + 1 else it.currentPage,
                                        hasMore = if (emissionCount >= 2) newEvents.size == NetworkConfig.PER_PAGE else it.hasMore,
                                        loadMoreError = false
                                    )
                                }
                            }, onFailure = { exception ->
                                _uiState.update { it ->
                                    it.copy(
                                        isPageLoading = false,
                                        isRefreshing = false,
                                        isLoadingMore = false,
                                        error = exception.message
                                            ?: stringResourceProvider.getString(R.string.error_failed_to_load_events),
                                        loadMoreError = isLoadMore
                                    )
                                }
                            })
                        }
                    }
                }, onFailure = { exception ->
                    _uiState.update { it ->
                        it.copy(
                            isPageLoading = false,
                            isRefreshing = false,
                            isLoadingMore = false,
                            error = exception.message
                                ?: stringResourceProvider.getString(R.string.error_failed_to_load_profile),
                            loadMoreError = isLoadMore
                        )
                    }
                })
            }
        }
    }

    fun refreshProfile() {
        loadProfile(isRefresh = true)
    }

    fun loadMoreUserEvents() {
        if (!_uiState.value.isLoadingMore && _uiState.value.hasMore && _uiState.value.user?.type != "Organization") {
            loadProfile(isLoadMore = true)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}
