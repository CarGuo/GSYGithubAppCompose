package com.shuyu.gsygithubappcompose.core.database.di

import android.content.Context
import androidx.room.Room
import com.shuyu.gsygithubappcompose.core.database.AppDatabase
import com.shuyu.gsygithubappcompose.core.database.dao.CommitDao
import com.shuyu.gsygithubappcompose.core.database.dao.EventDao
import com.shuyu.gsygithubappcompose.core.database.dao.FileContentDao
import com.shuyu.gsygithubappcompose.core.database.dao.IssueCommentDao
import com.shuyu.gsygithubappcompose.core.database.dao.IssueDao
import com.shuyu.gsygithubappcompose.core.database.dao.ReadmeDao
import com.shuyu.gsygithubappcompose.core.database.dao.RepositoryDao
import com.shuyu.gsygithubappcompose.core.database.dao.RepositoryDetailDao
import com.shuyu.gsygithubappcompose.core.database.dao.SearchHistoryDao
import com.shuyu.gsygithubappcompose.core.database.dao.TrendingDao
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
            context, AppDatabase::class.java, "gsy_github_db"
        ).fallbackToDestructiveMigration(true).build()
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

    @Provides
    @Singleton
    fun provideRepositoryDetailDao(database: AppDatabase): RepositoryDetailDao {
        return database.repositoryDetailDao()
    }

    @Provides
    @Singleton
    fun provideEventDao(database: AppDatabase): EventDao {
        return database.eventDao()
    }

    @Provides
    @Singleton
    fun provideTrendingDao(database: AppDatabase): TrendingDao {
        return database.trendingDao()
    }

    @Provides
    @Singleton
    fun provideSearchHistory(database: AppDatabase): SearchHistoryDao {
        return database.searchHistoryDao()
    }

    @Provides
    @Singleton
    fun provideCommitDao(database: AppDatabase): CommitDao {
        return database.commitDao()
    }

    @Provides
    @Singleton
    fun provideFileContentDao(database: AppDatabase): FileContentDao {
        return database.fileContentDao()
    }

    @Provides
    @Singleton
    fun provideIssueDao(database: AppDatabase): IssueDao {
        return database.issueDao()
    }

    @Provides
    @Singleton
    fun provideReadmeDao(database: AppDatabase): ReadmeDao {
        return database.readmeDao()
    }

    @Provides
    @Singleton
    fun provideIssueCommentDao(database: AppDatabase): IssueCommentDao {
        return database.issueCommentDao()
    }
}
