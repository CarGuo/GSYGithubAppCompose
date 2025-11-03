package com.shuyu.gsygithubappcompose.data.repository

import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.database.dao.UserDao
import com.shuyu.gsygithubappcompose.core.database.entity.UserEntity
import com.shuyu.gsygithubappcompose.core.network.api.GitHubApiService
import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.core.network.model.User
import com.shuyu.gsygithubappcompose.data.repository.mapper.toEntity
import com.shuyu.gsygithubappcompose.data.repository.mapper.toUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: GitHubApiService,
    private val userDao: UserDao,
    private val preferencesDataStore: UserPreferencesDataStore
) {

    suspend fun login(token: String): Result<User> {
        return try {
            val user = apiService.getAuthenticatedUser("token $token")
            preferencesDataStore.saveAuthToken(token)
            preferencesDataStore.saveUsername(user.login)
            preferencesDataStore.saveUserId(user.id.toString())

            // Cache user in database
            userDao.insertUser(user.toEntity())

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithOAuth(clientId: String, clientSecret: String, code: String): Result<User> {
        return try {
            // Exchange code for access token
            val tokenResponse = apiService.getAccessToken(clientId, clientSecret, code)
            val accessToken = tokenResponse.accessToken

            // Get user info with the access token
            val user = apiService.getAuthenticatedUser("token $accessToken")

            // Save credentials
            preferencesDataStore.saveAuthToken(accessToken)
            preferencesDataStore.saveUsername(user.login)
            preferencesDataStore.saveUserId(user.id.toString())

            // Cache user in database
            userDao.insertUser(user.toEntity())

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        preferencesDataStore.clearAll()
    }

    fun isLoggedIn(): Flow<Boolean> {
        return preferencesDataStore.authToken.map { it?.isNotEmpty() == true }
    }

    fun getUser(username: String): Flow<Result<User>> = flow {
        // 1. Emit data from database
        val cachedUser = userDao.getUserByLogin(username).first()
        if (cachedUser != null) {
            emit(Result.success(cachedUser.toUser()))
        }

        // 2. Fetch from network
        try {
            val networkUser = apiService.getUser(username)
            userDao.insertUser(networkUser.toEntity())
            emit(Result.success(networkUser))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getCachedUser(login: String): Flow<UserEntity?> {
        return userDao.getUserByLogin(login)
    }

    fun getOrgMembers(org: String): Flow<Result<List<User>>> = flow {
        try {
            val members = apiService.getOrgMembers(org)
            emit(Result.success(members))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getUserEvents(username: String, page: Int, perPage: Int): Flow<Result<List<Event>>> = flow {
        try {
            val events = apiService.getUserEvents(username, page, perPage)
            emit(Result.success(events))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
