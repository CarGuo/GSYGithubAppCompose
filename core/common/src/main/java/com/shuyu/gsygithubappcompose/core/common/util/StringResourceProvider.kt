package com.shuyu.gsygithubappcompose.core.common.util

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface StringResourceProvider {
    fun getString(@StringRes resId: Int): String
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
}

@Singleton
class StringResourceProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : StringResourceProvider {
    override fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    override fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}
