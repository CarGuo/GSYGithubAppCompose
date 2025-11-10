package com.shuyu.gsygithubappcompose.data.repository

import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.database.AppDatabase
import com.shuyu.gsygithubappcompose.core.database.dao.EventDao
import com.shuyu.gsygithubappcompose.core.database.dao.UserDao
import com.shuyu.gsygithubappcompose.core.database.entity.UserEntity
import com.shuyu.gsygithubappcompose.core.network.api.GitHubApiService
import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.core.network.model.Organization
import com.shuyu.gsygithubappcompose.core.network.model.User
import com.shuyu.gsygithubappcompose.core.network.model.UserSearchResponse
import com.shuyu.gsygithubappcompose.data.repository.mapper.toEntity
import com.shuyu.gsygithubappcompose.data.repository.mapper.toEvent
import com.shuyu.gsygithubappcompose.data.repository.mapper.toUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.net.HttpURLConnection
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

    fun getUser(username: String): Flow<RepositoryResult<User>> =
        getFromCacheAndNetwork(
            cacheFlow = userDao.getUserByLogin(username),
            networkCall = { apiService.getUser(username) },
            cacheUpdate = { user -> userDao.insertUser(user.toEntity()) },
            toDomain = { userEntity -> userEntity.toUser() }
        )

    fun getCachedUser(login: String): Flow<UserEntity?> {
        return userDao.getUserByLogin(login)
    }

    fun getOrgMembers(org: String, page: Int, perPage: Int): Flow<RepositoryResult<List<User>>> =
        getPaginatedFromCacheAndNetwork(
            page = page,
            cacheFetch = { userDao.getOrgMembers(org).first() },
            networkFetch = { apiService.getOrgMembers(org, page, perPage) },
            cacheUpdateForPage1 = { networkMembers -> userDao.insertUsers(networkMembers.map { it.toEntity(org) }) },
            toDomain = { it.toUser() },
            clearCacheForPage1 = { userDao.clearOrgMembers(org) }
        )

    fun getUserEvents(
        username: String, page: Int, perPage: Int
    ): Flow<RepositoryResult<List<Event>>> =
        getPaginatedFromCacheAndNetwork(
            page = page,
            cacheFetch = { eventDao.getEventsByUserLogin(username).map { it.toEvent() } },
            networkFetch = { apiService.getUserEvents(username, page, perPage) },
            cacheUpdateForPage1 = { networkEvents ->
                eventDao.clearAndInsertUserEvents(username, networkEvents.map { it.toEntity(false, username) })
            },
            toDomain = { it },
            clearCacheForPage1 = { /* clearAndInsertUserEvents handles clearing */ }
        )

    suspend fun searchUsers(query: String, page: Int = 1): UserSearchResponse {
        return apiService.searchUsers(query, page = page)
    }

    fun getFollowers(userName: String, page: Int): Flow<Result<List<User>>> = flow {
        try {
            emit(Result.success(apiService.getUserFollowers(userName, page)))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getFollowing(userName: String, page: Int): Flow<Result<List<User>>> = flow {
        try {
            emit(Result.success(apiService.getUserFollowing(userName, page)))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun checkFollowing(userName: String): Flow<RepositoryResult<Boolean>> = flow {
        try {
            apiService.checkFollowingUser(userName)
            emit(RepositoryResult(Result.success(true), DataSource.NETWORK, true))
        } catch (e: Exception) {
            if (e is HttpException && e.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                emit(RepositoryResult(Result.success(false), DataSource.NETWORK, true))
            } else {
                emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, true))
            }
        }
    }

    fun followUser(userName: String): Flow<RepositoryResult<Boolean>> = flow {
        try {
            apiService.followUser(userName)
            emit(RepositoryResult(Result.success(true), DataSource.NETWORK, true))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, true))
        }
    }

    fun unFollowUser(userName: String): Flow<RepositoryResult<Boolean>> = flow {
        try {
            apiService.unfollowUser(userName)
            emit(RepositoryResult(Result.success(true), DataSource.NETWORK, true))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, true))
        }
    }


    fun getOrgs(userName: String, page: Int): Flow<Result<List<Organization>>> = flow {
        try {
            emit(Result.success(apiService.getUserOrganizations(userName, page)))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun <T, R> getFromCacheAndNetwork(
        cacheFlow: Flow<T?>,
        networkCall: suspend () -> R,
        cacheUpdate: suspend (R) -> Unit,
        toDomain: (T) -> R
    ): Flow<RepositoryResult<R>> = flow {
        val cachedData = cacheFlow.first()
        val isDbEmpty = cachedData == null
        if (cachedData != null) {
            emit(RepositoryResult(Result.success(toDomain(cachedData)), DataSource.CACHE, isDbEmpty))
        }

        try {
            val networkData = networkCall()
            cacheUpdate(networkData)
            emit(RepositoryResult(Result.success(networkData), DataSource.NETWORK, isDbEmpty))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, isDbEmpty))
        }
    }

    private fun <T, R> getPaginatedFromCacheAndNetwork(
        page: Int,
        cacheFetch: suspend () -> List<T>, // For page 1
        networkFetch: suspend () -> List<R>,
        cacheUpdateForPage1: suspend (List<R>) -> Unit, // For page 1
        toDomain: (T) -> R,
        clearCacheForPage1: suspend () -> Unit // For page 1
    ): Flow<RepositoryResult<List<R>>> = flow {
        var isDbEmpty = false
        if (page == 1) {
            val cachedData = cacheFetch()
            isDbEmpty = cachedData.isEmpty()
            if (cachedData.isNotEmpty()) {
                emit(RepositoryResult(Result.success(cachedData.map { toDomain(it) }), DataSource.CACHE, isDbEmpty))
            }
        }

        try {
            val networkData = networkFetch()
            if (page == 1) {
                clearCacheForPage1()
                cacheUpdateForPage1(networkData)
            }
            emit(RepositoryResult(Result.success(networkData), DataSource.NETWORK, isDbEmpty))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, isDbEmpty))
        }
    }
}
