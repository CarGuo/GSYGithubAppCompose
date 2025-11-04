package com.shuyu.gsygithubappcompose.data.repository

import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.database.AppDatabase
import com.shuyu.gsygithubappcompose.core.database.dao.EventDao
import com.shuyu.gsygithubappcompose.core.database.dao.UserDao
import com.shuyu.gsygithubappcompose.core.database.entity.UserEntity
import com.shuyu.gsygithubappcompose.core.network.api.GitHubApiService
import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.core.network.model.User
import com.shuyu.gsygithubappcompose.core.network.model.UserSearchResponse
import com.shuyu.gsygithubappcompose.data.repository.mapper.toEntity
import com.shuyu.gsygithubappcompose.data.repository.mapper.toEvent
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
    private val eventDao: EventDao,
    private val preferencesDataStore: UserPreferencesDataStore,
    private val appDatabase: AppDatabase
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
        appDatabase.clearAllData()
    }

    fun isLoggedIn(): Flow<Boolean> {
        return preferencesDataStore.authToken.map { it?.isNotEmpty() == true }
    }

    fun getUser(username: String): Flow<RepositoryResult<User>> = flow {
        // 1. Emit data from database
        val cachedUser = userDao.getUserByLogin(username).first()
        val isDbEmpty = cachedUser == null
        if (cachedUser != null) {
            emit(RepositoryResult(Result.success(cachedUser.toUser()), DataSource.CACHE, isDbEmpty))
        }

        // 2. Fetch from network
        try {
            val networkUser = apiService.getUser(username)
            userDao.insertUser(networkUser.toEntity())
            // After inserting, we could re-query, but for simplicity we\'ll just emit the network response
            emit(RepositoryResult(Result.success(networkUser), DataSource.NETWORK, isDbEmpty))
        } catch (e: Exception) {
            // If network fails, and we have no cached user, we should emit the failure.
            // If we had a cached user, the UI already has data, and this failure can be handled differently (e.g., a toast).
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, isDbEmpty))
        }
    }

    fun getCachedUser(login: String): Flow<UserEntity?> {
        return userDao.getUserByLogin(login)
    }

    fun getOrgMembers(org: String): Flow<RepositoryResult<List<User>>> = flow {
        // 1. Emit data from database if available
        val cachedMembers = userDao.getOrgMembers(org).first()
        val isDbEmpty = cachedMembers.isEmpty()
        if (cachedMembers.isNotEmpty()) {
            emit(RepositoryResult(Result.success(cachedMembers.map { it.toUser() }), DataSource.CACHE, isDbEmpty))
        }

        // 2. Fetch from network
        try {
            val networkMembers = apiService.getOrgMembers(org)
            // 3. Update the database
            userDao.clearOrgMembers(org)
            userDao.insertUsers(networkMembers.map { it.toEntity(org) })
            // 4. Emit network data
            emit(RepositoryResult(Result.success(networkMembers), DataSource.NETWORK, isDbEmpty))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, isDbEmpty))
        }
    }

    fun getUserEvents(username: String, page: Int, perPage: Int): Flow<RepositoryResult<List<Event>>> = flow {
        var isDbEmpty = false
        // For paginated data, we only check the DB on the first page.
        if (page == 1) {
            val cachedEvents = eventDao.getEventsByUserLogin(username).map { it.toEvent() }
            isDbEmpty = cachedEvents.isEmpty()
            if (cachedEvents.isNotEmpty()) {
                emit(RepositoryResult(Result.success(cachedEvents), DataSource.CACHE, isDbEmpty))
            }
        }

        // Fetch from network
        try {
            val networkEvents = apiService.getUserEvents(username, page, perPage)
            // If it\'s the first page, update the database
            if (page == 1) {
                eventDao.clearAndInsertUserEvents(username, networkEvents.map { it.toEntity(false, username) })
            }
            // Emit network data
            emit(RepositoryResult(Result.success(networkEvents), DataSource.NETWORK, isDbEmpty))
        } catch (e: Exception) {
            // For load more (page > 1), we need to inform the UI about the failure.
            // For page 1, this follows the (optional) cached emission.
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, isDbEmpty))
        }
    }

    suspend fun searchUsers(query: String, page: Int = 1): UserSearchResponse {
        return apiService.searchUsers(query, page = page)
    }
}
