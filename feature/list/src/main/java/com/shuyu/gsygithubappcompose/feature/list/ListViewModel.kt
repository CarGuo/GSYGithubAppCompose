package com.shuyu.gsygithubappcompose.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

@HiltViewModel
class ListViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val reposRepository: RepositoryRepository,
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
                CommonListDataType.FOLLOWER -> {
                    userRepository.getFollowers(userName!!, loadPage)
                }
                CommonListDataType.FOLLOWED -> {
                    userRepository.getFollowing(userName!!, loadPage)
                }
                CommonListDataType.USER_REPOS -> {
                    reposRepository.getUserRepos(userName!!, loadPage, "pushed")
                }
                CommonListDataType.REPO_STAR -> {
                    reposRepository.getRepoStargazers(userName!!, repoName!!, loadPage)
                }
                CommonListDataType.USER_STAR -> {
                    reposRepository.getUserStaredRepos(userName!!, loadPage, "updated")
                }
                CommonListDataType.REPO_WATCHER -> {
                    reposRepository.getRepoWatchers(userName!!, repoName!!, loadPage)
                }
                CommonListDataType.REPO_FORK -> {
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
                        CommonListDataType.USER_REPOS,
                        CommonListDataType.USER_STAR,
                        CommonListDataType.REPO_FORK,
                        CommonListDataType.TOPICS -> data.map { (it as? com.shuyu.gsygithubappcompose.core.network.model.Repository)?.toRepositoryDisplayData() }
                        else -> data
                    }

                    val currentList = if (isRefresh) emptyList() else _uiState.value.list
                    val newList = currentList + mappedData.filterNotNull()

                    page = loadPage + 1

                    val title = when (listType) {
                        CommonListDataType.FOLLOWER -> "$userName followers"
                        CommonListDataType.FOLLOWED -> "$userName following"
                        CommonListDataType.USER_REPOS -> "$userName repos"
                        CommonListDataType.REPO_STAR -> "$repoName stagers"
                        CommonListDataType.USER_STAR -> "$userName star"
                        CommonListDataType.REPO_WATCHER -> "$repoName watchers"
                        CommonListDataType.REPO_FORK -> "$repoName forks"
                        CommonListDataType.TOPICS -> "$userName topics"
                        CommonListDataType.USER_ORGS -> "$userName orgs"
                        else -> ""
                    }

                    _uiState.value = _uiState.value.copy(
                        list = newList,
                        hasMore = data.size == NetworkConfig.PER_PAGE,
                        title = title
                    )
                }
                result.onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = e.message
                    )
                }
                _uiState.value = _uiState.value.copy(
                    isPageLoading = false,
                    isRefreshing = false,
                    isLoadingMore = false
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
