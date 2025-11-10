package com.shuyu.gsygithubappcompose.core.common.util

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import com.shuyu.gsygithubappcompose.core.common.manager.LanguageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface StringResourceProvider {
    fun getString(@StringRes resId: Int): String
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
}

@Singleton
class StringResourceProviderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val languageManager: LanguageManager
) : StringResourceProvider {

    // Get a locale-aware context based on current language setting
    private fun getLocalizedContext(): Context {
        val currentLocale = languageManager.appLanguageToLocale(languageManager.getAppLanguageSync())
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(currentLocale)
        return context.createConfigurationContext(configuration)
    }

    override fun getString(@StringRes resId: Int): String {
        return getLocalizedContext().getString(resId)
    }

    override fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return getLocalizedContext().getString(resId, *formatArgs)
    }
}
