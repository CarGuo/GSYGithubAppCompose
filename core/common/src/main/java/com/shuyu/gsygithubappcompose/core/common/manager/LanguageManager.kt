package com.shuyu.gsygithubappcompose.core.common.manager

import com.shuyu.gsygithubappcompose.core.common.datastore.AppLanguage
import com.shuyu.gsygithubappcompose.core.common.datastore.LanguageDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageManager @Inject constructor(
    private val languageDataStore: LanguageDataStore
) {
    // 1. 增加一个私有的内存缓存变量
    private var inMemoryLanguage: AppLanguage? = null

    val appLanguage: Flow<AppLanguage> = languageDataStore.appLanguage

    suspend fun setAppLanguage(appLanguage: AppLanguage) {
        // 2. 保存语言时，同时更新内存缓存和DataStore
        inMemoryLanguage = appLanguage
        languageDataStore.saveAppLanguage(appLanguage)
    }

    // Synchronous method to get the current AppLanguage, primarily for attachBaseContext
    fun getAppLanguageSync(): AppLanguage {
        // 3. 同步获取时，优先从内存缓存读取
        inMemoryLanguage?.let {
            return it
        }
        // 如果内存中没有，再从DataStore同步读取（适用于应用首次启动的场景）
        return runBlocking {
            languageDataStore.appLanguage.first()
        }
    }

    // Helper to convert AppLanguage to Locale for internal use if needed, though setAppLanguage handles it.
    fun appLanguageToLocale(appLanguage: AppLanguage): Locale {
        return when (appLanguage) {
            AppLanguage.SYSTEM -> Locale.getDefault()
            AppLanguage.CHINESE -> Locale.CHINESE
            AppLanguage.ENGLISH -> Locale.ENGLISH
        }
    }
}