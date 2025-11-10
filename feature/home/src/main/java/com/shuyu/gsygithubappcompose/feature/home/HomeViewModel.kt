package com.shuyu.gsygithubappcompose.feature.home

import androidx.lifecycle.viewModelScope
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.common.util.StringResourceProvider
import com.shuyu.gsygithubappcompose.core.network.model.Release
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

    fun checkUpdate(currentVersionName: String, showTip: Boolean = false) {
        viewModelScope.launch {
            repositoryRepository.getRepositoryReleases("CarGuo", "GSYGithubAppCompose")
                .collectLatest { result ->
                    result.data.fold(onSuccess = { releases ->
                        val latestRelease = releases.firstOrNull()
                        if (latestRelease != null) {
                            val latestVersion = parseVersion(latestRelease.tagName)
                            val currentVersion = parseVersion(currentVersionName)

                            if (latestVersion > currentVersion) {
                                _uiState.update {
                                    it.copy(
                                        latestRelease = latestRelease, showUpdateDialog = true
                                    )
                                }
                            } else if (showTip) {
                                showToast(stringResourceProvider.getString(R.string.app_not_new_version))
                            }
                        } else if (showTip) {
                            showToast(stringResourceProvider.getString(R.string.error_no_data_found))
                        }
                    }, onFailure = { e ->
                        if (showTip) {
                            showToast(
                                e.message
                                    ?: stringResourceProvider.getString(R.string.error_unknown)
                            )
                        }
                    })
                }
        }
    }

    fun dismissUpdateDialog() {
        _uiState.update { it.copy(showUpdateDialog = false) }
    }

    private fun parseVersion(versionString: String): Version {
        // Remove any non-numeric or leading 'v' characters
        val cleanedVersion = versionString.replace(Regex("[^\\d.]"), "")
        val parts = cleanedVersion.split(".").map { it.toIntOrNull() ?: 0 }
        return Version(
            major = parts.getOrElse(0) { 0 },
            minor = parts.getOrElse(1) { 0 },
            patch = parts.getOrElse(2) { 0 })
    }
}

data class Version(
    val major: Int, val minor: Int, val patch: Int
) : Comparable<Version> {
    override fun compareTo(other: Version): Int {
        if (major != other.major) return major.compareTo(other.major)
        if (minor != other.minor) return minor.compareTo(other.minor)
        return patch.compareTo(other.patch)
    }
}

data class HomeUiState(
    val user: User? = null,
    val isLoadingDialog: Boolean = false,
    val latestRelease: Release? = null,
    val showUpdateDialog: Boolean = false,
    override val isPageLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val isLoadingMore: Boolean = false,
    override val error: String? = null,
    override val currentPage: Int = 1,
    override val hasMore: Boolean = false,
    override val loadMoreError: Boolean = false
) : BaseUiState
