package com.shuyu.gsygithubappcompose.core.common.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) : IUserPreferencesDataStore {
    private object PreferencesKeys {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USERNAME = stringPreferencesKey("username")
        val USER_ID = stringPreferencesKey("user_id")
    }

    override val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.AUTH_TOKEN]
    }

    override val username: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USERNAME]
    }

    override val userId: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_ID]
    }

    override suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTH_TOKEN] = token
        }
    }

    override suspend fun clearAuthToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.AUTH_TOKEN)
        }
    }

    override suspend fun saveUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USERNAME] = username
        }
    }

    override suspend fun saveUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userId
        }
    }

    override suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
