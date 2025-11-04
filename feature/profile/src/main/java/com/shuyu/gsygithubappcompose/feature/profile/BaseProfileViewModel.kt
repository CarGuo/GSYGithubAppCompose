package com.shuyu.gsygithubappcompose.feature.profile

import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.core.network.config.NetworkConfig
import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.core.network.model.User
import com.shuyu.gsygithubappcompose.data.repository.UserRepository
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseUiState
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseViewModel
import kotlinx.coroutines.flow.update
import com.shuyu.gsygithubappcompose.core.common.R

data class ProfileUiState(
    val user: User? = null,
    override val isPageLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
    override val error: String? = null,
    val orgMembers: List<User>? = null,
    val userEvents: List<Event>? = null,
    override val currentPage: Int = 1,
    override val hasMore: Boolean = false,
    override val loadMoreError: Boolean = false
) : BaseUiState

abstract class BaseProfileViewModel(
    private val userRepository: UserRepository,
    preferencesDataStore: UserPreferencesDataStore,
    private val stringResourceProvider: StringResourceProvider
) : BaseViewModel<ProfileUiState>(
    initialUiState = ProfileUiState(),
    preferencesDataStore = preferencesDataStore,
    stringResourceProvider = stringResourceProvider,
    commonStateUpdater = { currentState, isPageLoading, isRefreshing, isLoadingMore, error, currentPage, hasMore, loadMoreError ->
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

    protected abstract fun getUserLogin(onSuccess: (String) -> Unit)

    override fun loadData(
        initialLoad: Boolean, isRefresh: Boolean, isLoadMore: Boolean
    ) {
        getUserLogin { userLogin ->
            launchDataLoadWithUser(initialLoad, isRefresh, isLoadMore) { _, pageToLoad ->
                var emissionCount = 0
                userRepository.getUser(userLogin).collect {
                    emissionCount++
                    it.fold(onSuccess = { fetchedUser ->
                        _uiState.update { currentState ->
                            currentState.copy(
                                user = fetchedUser
                            )
                        }

                        if (fetchedUser.type == "Organization") {
                            userRepository.getOrgMembers(fetchedUser.login).collect { orgResult ->
                                orgResult.fold(onSuccess = { members ->
                                    _uiState.update { it.copy(orgMembers = members) }
                                }, onFailure = { exception ->
                                    updateErrorState(
                                        exception,
                                        isLoadMore,
                                        stringResourceProvider.getString(R.string.error_failed_to_load_org_members)
                                    )
                                })
                            }
                        } else {
                            userRepository.getUserEvents(
                                fetchedUser.login, pageToLoad, NetworkConfig.PER_PAGE
                            ).collect { eventResult ->
                                eventResult.fold(onSuccess = { newEvents ->
                                    val currentEvents =
                                        if (isRefresh || initialLoad) emptyList() else _uiState.value.userEvents.orEmpty()
                                    val updatedEvents = currentEvents + newEvents
                                    handleResult(
                                        emissionCount,
                                        newEvents,
                                        pageToLoad,
                                        isRefresh,
                                        initialLoad,
                                        isLoadMore,
                                        updateSuccess = { currentState, _, _, _, _, _ ->
                                            currentState.copy(
                                                userEvents = updatedEvents
                                            )
                                        },
                                        updateFailure = { currentState, _, _ ->
                                            currentState.copy(
                                                userEvents = emptyList() // Clear events on failure if no data found
                                            )
                                        })
                                }, onFailure = { exception ->
                                    updateErrorState(
                                        exception,
                                        isLoadMore,
                                        stringResourceProvider.getString(R.string.error_failed_to_load_events)
                                    )
                                })
                            }
                        }
                    }, onFailure = { exception ->
                        updateErrorState(
                            exception,
                            isLoadMore,
                            stringResourceProvider.getString(R.string.error_failed_to_load_profile)
                        )
                    })
                }
            }
        }
    }

    fun refreshProfile() {
        refresh()
    }

    fun loadMoreUserEvents() {
        if (!_uiState.value.isLoadingMore && _uiState.value.hasMore && _uiState.value.user?.type != "Organization") {
            loadMore()
        }
    }
}