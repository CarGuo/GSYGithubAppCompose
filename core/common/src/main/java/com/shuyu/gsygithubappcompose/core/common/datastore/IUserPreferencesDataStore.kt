package com.shuyu.gsygithubappcompose.core.common.datastore

import kotlinx.coroutines.flow.Flow

interface IUserPreferencesDataStore {
    val authToken: Flow<String?>
    val username: Flow<String?>
    val userId: Flow<String?>

    suspend fun saveAuthToken(token: String)
    suspend fun clearAuthToken()
    suspend fun saveUsername(username: String)
    suspend fun saveUserId(userId: String)
    suspend fun clearAll()
}
