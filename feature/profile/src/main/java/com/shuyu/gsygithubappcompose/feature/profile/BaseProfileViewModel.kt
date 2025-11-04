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
import com.shuyu.gsygithubappcompose.data.repository.DataSource

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
                userRepository.getUser(userLogin).collect { userRepoResult ->
                    userRepoResult.result.fold(onSuccess = { fetchedUser ->
                        _uiState.update { currentState ->
                            currentState.copy(user = fetchedUser)
                        }

                        if (fetchedUser.type == "Organization") {
                            userRepository.getOrgMembers(fetchedUser.login).collect { orgMembersRepoResult ->
                                orgMembersRepoResult.result.fold(onSuccess = { members ->
                                    _uiState.update {
                                        val stateWithMembers = it.copy(orgMembers = members)
                                        if (orgMembersRepoResult.source == DataSource.NETWORK) {
                                            stateWithMembers.copy(isPageLoading = false, isRefreshing = false, error = null)
                                        } else {
                                            stateWithMembers
                                        }
                                    }
                                }, onFailure = { exception ->
                                    if (orgMembersRepoResult.source == DataSource.NETWORK) {
                                        updateErrorState(
                                            exception,
                                            isLoadMore,
                                            stringResourceProvider.getString(R.string.error_failed_to_load_org_members)
                                        )
                                    }
                                })
                            }
                        } else {
                            userRepository.getUserEvents(
                                fetchedUser.login, pageToLoad, NetworkConfig.PER_PAGE
                            ).collect { eventRepoResult ->
                                eventRepoResult.result.fold(onSuccess = { newEvents ->
                                    handleResult(
                                        newItems = newEvents,
                                        pageToLoad = pageToLoad,
                                        isRefresh = isRefresh,
                                        initialLoad = initialLoad,
                                        isLoadMore = isLoadMore,
                                        source = eventRepoResult.source,
                                        isDbEmpty = eventRepoResult.isDbEmpty,
                                        updateSuccess = { currentState, items, _, _, _, _ ->
                                            val currentEvents = currentState.userEvents.orEmpty()
                                            val updatedEvents = if (isLoadMore) {
                                                currentEvents + items
                                            } else {
                                                items
                                            }
                                            currentState.copy(userEvents = updatedEvents)
                                        },
                                        updateFailure = { currentState, _, _ ->
                                            currentState.copy(
                                                userEvents = if (isLoadMore) currentState.userEvents else emptyList()
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
