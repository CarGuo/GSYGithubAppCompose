package com.shuyu.gsygithubappcompose.core.common.di

import android.content.Context
import com.shuyu.gsygithubappcompose.core.common.datastore.IUserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.common.datastore.LanguageDataStore
import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Binds
    abstract fun bindUserPreferencesDataStore(impl: UserPreferencesDataStore): IUserPreferencesDataStore

    companion object {
        @Provides
        @Singleton
        fun provideLanguageDataStore(@ApplicationContext context: Context): LanguageDataStore {
            return LanguageDataStore(context)
        }
    }
}
