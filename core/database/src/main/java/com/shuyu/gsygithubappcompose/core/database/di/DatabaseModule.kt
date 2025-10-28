package com.shuyu.gsygithubappcompose.core.database.di

import android.content.Context
import androidx.room.Room
import com.shuyu.gsygithubappcompose.core.database.AppDatabase
import com.shuyu.gsygithubappcompose.core.database.dao.RepositoryDao
import com.shuyu.gsygithubappcompose.core.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "gsy_github_db"
        ).build()
    }
    
    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
    
    @Provides
    @Singleton
    fun provideRepositoryDao(database: AppDatabase): RepositoryDao {
        return database.repositoryDao()
    }
}
