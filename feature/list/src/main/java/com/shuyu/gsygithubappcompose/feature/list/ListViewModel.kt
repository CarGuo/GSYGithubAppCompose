package com.shuyu.gsygithubappcompose.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.core.network.config.NetworkConfig
import com.shuyu.gsygithubappcompose.core.ui.components.toRepositoryDisplayData
import com.shuyu.gsygithubappcompose.data.repository.RepositoryRepository
import com.shuyu.gsygithubappcompose.data.repository.UserRepository
import com.shuyu.gsygithubappcompose.data.repository.mapper.toUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.shuyu.gsygithubappcompose.core.common.R

@HiltViewModel
class ListViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val reposRepository: RepositoryRepository,
    private val stringResourceProvider: StringResourceProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListUIState())
    val uiState = _uiState.asStateFlow()

    private var userName: String? = null
    private var repoName: String? = null
    private var listType: CommonListDataType? = null
    private var page = 1

    fun loadData(userName: String, repoName: String, listType: String) {
        if (this.listType != null) {
            return
        }
        this.userName = userName
        this.repoName = repoName
        this.listType = CommonListDataType.valueOf(listType.uppercase())
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
            _uiState.value = _uiState.value.copy(
                isPageLoading = loadPage == 1 && !isRefresh,
                isRefreshing = isRefresh,
                isLoadingMore = loadPage > 1
            )

            val resultFlow = when (listType) {
                CommonListDataType.REPOSITORIES -> {
                    reposRepository.getUserRepos(userName!!, loadPage, "pushed")
                }

                CommonListDataType.FOLLOWER -> {
                    userRepository.getFollowers(userName!!, loadPage)
                }

                CommonListDataType.FOLLOWING, CommonListDataType.FOLLOWED -> {
                    userRepository.getFollowing(userName!!, loadPage)
                }

                CommonListDataType.USER_REPOS -> {
                    reposRepository.getUserRepos(userName!!, loadPage, "pushed")
                }

                CommonListDataType.STARGAZERS, CommonListDataType.REPO_STAR -> {
                    reposRepository.getRepoStargazers(userName!!, repoName!!, loadPage)
                }

                CommonListDataType.USER_STAR -> {
                    reposRepository.getUserStaredRepos(userName!!, loadPage, "updated")
                }

                CommonListDataType.WATCHERS, CommonListDataType.REPO_WATCHER -> {
                    reposRepository.getRepoWatchers(userName!!, repoName!!, loadPage)
                }

                CommonListDataType.FORKS, CommonListDataType.REPO_FORK -> {
                    reposRepository.getRepoForks(userName!!, repoName!!, loadPage)
                }

                CommonListDataType.TOPICS -> {
                    reposRepository.searchRepos("topic:${userName}", loadPage, "stars", "desc")
                }

                CommonListDataType.USER_ORGS -> {
                    userRepository.getOrgs(userName!!, loadPage)
                }

                else -> {
                    null
                }
            }

            resultFlow?.onEach { result ->
                result.onSuccess { data ->
                    val mappedData = when (listType) {
                        CommonListDataType.USER_ORGS -> data.map { (it as? com.shuyu.gsygithubappcompose.core.network.model.Organization)?.toUser() }
                        CommonListDataType.REPOSITORIES, CommonListDataType.USER_REPOS, CommonListDataType.USER_STAR, CommonListDataType.FORKS, CommonListDataType.REPO_FORK, CommonListDataType.TOPICS -> data.map { (it as? com.shuyu.gsygithubappcompose.core.network.model.Repository)?.toRepositoryDisplayData() }

                        else -> data
                    }

                    val currentList = if (isRefresh) emptyList() else _uiState.value.list
                    val newList = currentList + mappedData.filterNotNull()

                    page = loadPage + 1

                    val title = when (listType) {
                        CommonListDataType.REPOSITORIES -> stringResourceProvider.getString(
                            R.string.list_repositories, userName ?: ""
                        )

                        CommonListDataType.FOLLOWER -> stringResourceProvider.getString(
                            R.string.list_followers, userName ?: ""
                        )

                        CommonListDataType.FOLLOWING, CommonListDataType.FOLLOWED -> stringResourceProvider.getString(
                            R.string.list_following, userName ?: ""
                        )

                        CommonListDataType.USER_REPOS -> stringResourceProvider.getString(
                            R.string.list_repos, userName ?: ""
                        )

                        CommonListDataType.STARGAZERS, CommonListDataType.REPO_STAR -> stringResourceProvider.getString(
                            R.string.list_stargazers, repoName ?: ""
                        )

                        CommonListDataType.USER_STAR -> stringResourceProvider.getString(
                            R.string.list_star, userName ?: ""
                        )

                        CommonListDataType.WATCHERS, CommonListDataType.REPO_WATCHER -> stringResourceProvider.getString(
                            R.string.list_watchers, repoName ?: ""
                        )

                        CommonListDataType.FORKS, CommonListDataType.REPO_FORK -> stringResourceProvider.getString(
                            R.string.list_forks, repoName ?: ""
                        )

                        CommonListDataType.TOPICS -> stringResourceProvider.getString(
                            R.string.list_topics, userName ?: ""
                        )

                        CommonListDataType.USER_ORGS -> stringResourceProvider.getString(
                            R.string.list_orgs, userName ?: ""
                        )

                        else -> ""
                    }

                    _uiState.value = _uiState.value.copy(
                        list = newList, hasMore = data.size == NetworkConfig.PER_PAGE, title = title
                    )
                }
                result.onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = e.message
                    )
                }
                _uiState.value = _uiState.value.copy(
                    isPageLoading = false, isRefreshing = false, isLoadingMore = false
                )
            }?.launchIn(viewModelScope)
        }
    }
}

data class ListUIState(
    val list: List<Any> = emptyList(),
    val listType: CommonListDataType? = null,
    val title: String = "",
    val hasMore: Boolean = true,
    val isPageLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val loadMoreError: Boolean = false
)
