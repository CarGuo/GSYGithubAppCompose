package com.shuyu.gsygithubappcompose.feature.profile

import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.core.network.config.NetworkConfig
import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.core.network.model.User
import com.shuyu.gsygithubappcompose.data.repository.DataSource
import com.shuyu.gsygithubappcompose.data.repository.RepositoryResult
import com.shuyu.gsygithubappcompose.data.repository.UserRepository
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseUiState
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseViewModel
import kotlinx.coroutines.flow.Flow
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
    val notificationCount: Int = 0,
    val isFollowing: Boolean = false,
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
                    userRepoResult.data.fold(onSuccess = { fetchedUser ->
                        _uiState.update { currentState ->
                            currentState.copy(user = fetchedUser)
                        }

                        if (fetchedUser.type == "Organization") {
                            collectAndHandleListResult(
                                repoFlow = userRepository.getOrgMembers(
                                    fetchedUser.login, pageToLoad, NetworkConfig.PER_PAGE
                                ),
                                pageToLoad = pageToLoad,
                                isRefresh = isRefresh,
                                initialLoad = initialLoad,
                                isLoadMore = isLoadMore,
                                updateSuccess = { currentState, items, _, _, _, _ ->
                                    val currentItems = currentState.orgMembers.orEmpty()
                                    val updatedItems = if (isLoadMore) {
                                        currentItems + items
                                    } else {
                                        items
                                    }
                                    currentState.copy(orgMembers = updatedItems)
                                },
                                updateFailure = { currentState, errorMsg: String?, _ ->
                                    currentState.copy(
                                        orgMembers = if (isLoadMore) currentState.orgMembers else emptyList()
                                    )
                                },
                                updateFailureMessage = stringResourceProvider.getString(R.string.error_failed_to_load_org_members)
                            )
                        } else {
                            collectAndHandleListResult(
                                repoFlow = userRepository.getUserEvents(
                                    fetchedUser.login, pageToLoad, NetworkConfig.PER_PAGE
                                ),
                                pageToLoad = pageToLoad,
                                isRefresh = isRefresh,
                                initialLoad = initialLoad,
                                isLoadMore = isLoadMore,
                                updateSuccess = { currentState, items, _, _, _, _ ->
                                    val currentItems = currentState.userEvents.orEmpty()
                                    val updatedItems = if (isLoadMore) {
                                        currentItems + items
                                    } else {
                                        items
                                    }
                                    currentState.copy(userEvents = updatedItems)
                                },
                                updateFailure = { currentState, errorMsg: String?, _ ->
                                    currentState.copy(
                                        userEvents = if (isLoadMore) currentState.userEvents else emptyList()
                                    )
                                },
                                updateFailureMessage = stringResourceProvider.getString(R.string.error_failed_to_load_events)
                            )
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

    protected suspend fun <T> collectAndHandleListResult(
        repoFlow: Flow<RepositoryResult<List<T>>>,
        pageToLoad: Int,
        isRefresh: Boolean,
        initialLoad: Boolean,
        isLoadMore: Boolean,
        updateSuccess: (ProfileUiState, List<T>, Int, Boolean, Boolean, Boolean) -> ProfileUiState,
        updateFailure: (ProfileUiState, String?, Boolean) -> ProfileUiState,
        updateFailureMessage: String
    ) {
        repoFlow.collect { repoResult ->
            repoResult.data.fold(
                onSuccess = { newItems ->
                    handleResult(
                        newItems = newItems,
                        pageToLoad = pageToLoad,
                        isRefresh = isRefresh,
                        initialLoad = initialLoad,
                        isLoadMore = isLoadMore,
                        source = repoResult.dataSource,
                        isDbEmpty = repoResult.isDbEmpty,
                        updateSuccess = updateSuccess,
                        updateFailure = updateFailure
                    )
                },
                onFailure = { exception ->
                    updateErrorState(
                        exception,
                        isLoadMore,
                        updateFailureMessage
                    )
                }
            )
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
