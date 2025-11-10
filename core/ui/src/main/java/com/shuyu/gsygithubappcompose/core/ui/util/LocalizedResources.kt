package com.shuyu.gsygithubappcompose.core.ui.util

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * A ContextWrapper that provides localized resources while preserving the base context type.
 * This is crucial for Hilt to recognize the Activity context for ViewModel creation.
 */
private class LocalizedContextWrapper(
    base: Context,
    private val locale: Locale
) : ContextWrapper(base) {

    private val localizedResources: Resources by lazy {
        val config = Configuration(baseContext.resources.configuration)
        config.setLocale(locale)
        baseContext.createConfigurationContext(config).resources
    }

    override fun getResources(): Resources {
        return localizedResources
    }
}

/**
 * Provides localized resources while preserving the Activity context for Hilt.
 * Uses ContextWrapper to wrap the Activity context, ensuring:
 * 1. stringResource() uses the app-selected language
 * 2. Hilt can still access the underlying Activity context for ViewModel creation
 */
@Composable
fun ProvideLocalizedResources(
    locale: Locale,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val localizedContext = remember(context, locale) {
        LocalizedContextWrapper(context, locale)
    }

    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalAppLocale provides locale,
        content = content
    )
}


