package com.shuyu.gsygithubappcompose.core.common.di

import com.shuyu.gsygithubappcompose.core.common.datastore.IUserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Binds
    abstract fun bindUserPreferencesDataStore(impl: UserPreferencesDataStore): IUserPreferencesDataStore
}
