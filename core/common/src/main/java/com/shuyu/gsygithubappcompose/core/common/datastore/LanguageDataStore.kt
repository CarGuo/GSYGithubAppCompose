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

private val Context.languageDataStore: DataStore<Preferences> by preferencesDataStore(name = "language_preferences")

@Singleton
class LanguageDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val APP_LANGUAGE = stringPreferencesKey("app_language")
    }

    val appLanguage: Flow<AppLanguage> = context.languageDataStore.data.map { preferences ->
        AppLanguage.fromLanguageCode(preferences[PreferencesKeys.APP_LANGUAGE])
    }

    suspend fun saveAppLanguage(language: AppLanguage) {
        context.languageDataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_LANGUAGE] = language.languageCode
        }
    }
}
