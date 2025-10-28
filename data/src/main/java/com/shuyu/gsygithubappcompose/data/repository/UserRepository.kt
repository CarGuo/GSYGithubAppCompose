package com.shuyu.gsygithubappcompose.data.repository

import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.database.dao.UserDao
import com.shuyu.gsygithubappcompose.core.database.entity.UserEntity
import com.shuyu.gsygithubappcompose.core.network.api.GitHubApiService
import com.shuyu.gsygithubappcompose.core.network.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
    
    suspend fun logout() {
        preferencesDataStore.clearAll()
    }
    
    fun isLoggedIn(): Flow<Boolean> {
        return preferencesDataStore.authToken.let { tokenFlow ->
            kotlinx.coroutines.flow.flow {
                emit(tokenFlow.first()?.isNotEmpty() == true)
            }
        }
    }
    
    suspend fun getUser(username: String): Result<User> {
        return try {
            val user = apiService.getUser(username)
            userDao.insertUser(user.toEntity())
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getCachedUser(login: String): Flow<UserEntity?> {
        return userDao.getUserByLogin(login)
    }
    
    private fun User.toEntity() = UserEntity(
        id = id,
        login = login,
        name = name,
        avatarUrl = avatarUrl,
        bio = bio,
        company = company,
        blog = blog,
        location = location,
        email = email,
        publicRepos = publicRepos,
        followers = followers,
        following = following,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
