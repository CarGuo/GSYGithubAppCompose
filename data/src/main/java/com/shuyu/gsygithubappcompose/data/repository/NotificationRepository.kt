package com.shuyu.gsygithubappcompose.data.repository

import com.shuyu.gsygithubappcompose.core.network.api.GitHubApiService
import com.shuyu.gsygithubappcompose.core.network.model.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val githubApiService: GitHubApiService
) {

    fun getNotifications(page: Int, all: Boolean, participating: Boolean): Flow<RepositoryResult<List<Notification>>> = flow {
        try {
            val response = githubApiService.getNotifications(page = page, all = all, participating = participating)
            emit(RepositoryResult(Result.success(response), DataSource.NETWORK, true))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, true))
        }
    }

    fun markAllNotificationsAsRead(): Flow<RepositoryResult<Boolean>> = flow {
        try {
            githubApiService.markAllNotificationsAsRead()
            emit(RepositoryResult(Result.success(true), DataSource.NETWORK, true))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, true))
        }
    }
}
