package com.shuyu.gsygithubappcompose.core.common.di

import android.content.Context
import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {
    
    @Provides
    @Singleton
    fun provideUserPreferencesDataStore(
        @ApplicationContext context: Context
    ): UserPreferencesDataStore {
        return UserPreferencesDataStore(context)
    }
}
