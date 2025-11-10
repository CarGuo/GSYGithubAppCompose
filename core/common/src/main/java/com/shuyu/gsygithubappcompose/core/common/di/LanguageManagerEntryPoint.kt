package com.shuyu.gsygithubappcompose.core.common.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.shuyu.gsygithubappcompose.core.common.manager.LanguageManager

@EntryPoint
@InstallIn(SingletonComponent::class)
interface LanguageManagerEntryPoint {
    fun languageManager(): LanguageManager
}
