package com.shuyu.gsygithubappcompose.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shuyu.gsygithubappcompose.core.database.dao.CommitDao
import com.shuyu.gsygithubappcompose.core.database.dao.EventDao
import com.shuyu.gsygithubappcompose.core.database.dao.FileContentDao
import com.shuyu.gsygithubappcompose.core.database.dao.IssueDao
import com.shuyu.gsygithubappcompose.core.database.dao.RepositoryDao
import com.shuyu.gsygithubappcompose.core.database.dao.RepositoryDetailDao
import com.shuyu.gsygithubappcompose.core.database.dao.SearchHistoryDao
import com.shuyu.gsygithubappcompose.core.database.dao.TrendingDao
import com.shuyu.gsygithubappcompose.core.database.dao.UserDao
import com.shuyu.gsygithubappcompose.core.database.entity.CommitEntity
import com.shuyu.gsygithubappcompose.core.database.entity.EventEntity
import com.shuyu.gsygithubappcompose.core.database.entity.FileContentEntity
import com.shuyu.gsygithubappcompose.core.database.entity.IssueEntity
import com.shuyu.gsygithubappcompose.core.database.entity.RepositoryDetailEntity
import com.shuyu.gsygithubappcompose.core.database.entity.RepositoryEntity
import com.shuyu.gsygithubappcompose.core.database.entity.SearchHistoryEntity
import com.shuyu.gsygithubappcompose.core.database.entity.TrendingEntity
import com.shuyu.gsygithubappcompose.core.database.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        RepositoryEntity::class,
        EventEntity::class,
        CommitEntity::class,
        TrendingEntity::class,
        SearchHistoryEntity::class,
        RepositoryDetailEntity::class,
        FileContentEntity::class,
        IssueEntity::class
    ],
    version = 47,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun repositoryDao(): RepositoryDao
    abstract fun eventDao(): EventDao
    abstract fun commitDao(): CommitDao
    abstract fun trendingDao(): TrendingDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun repositoryDetailDao(): RepositoryDetailDao
    abstract fun fileContentDao(): FileContentDao
    abstract fun issueDao(): IssueDao

    fun clearAllData() {
        clearAllTables()
    }
}
