package com.shuyu.gsygithubappcompose.core.ui.util

import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

/**
 * CompositionLocal for providing the current application Locale.
 * This allows Composables to react to locale changes and update their string resources.
 */
val LocalAppLocale = staticCompositionLocalOf { Locale.getDefault() }
