package com.shuyu.gsygithubappcompose.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shuyu.gsygithubappcompose.core.database.entity.RepositoryDetailEntity

@Dao
interface RepositoryDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(repositoryDetail: RepositoryDetailEntity)

    @Query("SELECT * FROM repository_detail WHERE name_with_owner = :nameWithOwner")
    suspend fun getRepositoryDetail(nameWithOwner: String): RepositoryDetailEntity?

}
