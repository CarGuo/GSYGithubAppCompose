package com.shuyu.gsygithubappcompose.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shuyu.gsygithubappcompose.core.database.entity.TrendingEntity

@Dao
interface TrendingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<TrendingEntity>)

    @Query("SELECT * FROM trending_repo")
    suspend fun getAllTrendingRepos(): List<TrendingEntity>

    @Query("DELETE FROM trending_repo")
    suspend fun clearTrendingRepos()
}
