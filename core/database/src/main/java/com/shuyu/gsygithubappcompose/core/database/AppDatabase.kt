package com.shuyu.gsygithubappcompose.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shuyu.gsygithubappcompose.core.database.dao.RepositoryDao
import com.shuyu.gsygithubappcompose.core.database.dao.UserDao
import com.shuyu.gsygithubappcompose.core.database.entity.RepositoryEntity
import com.shuyu.gsygithubappcompose.core.database.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        RepositoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun repositoryDao(): RepositoryDao
}
