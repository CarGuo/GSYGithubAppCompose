package com.shuyu.gsygithubappcompose.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shuyu.gsygithubappcompose.core.database.entity.PushCommitEntity

@Dao
interface PushCommitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPushCommit(pushCommit: PushCommitEntity)

    @Query("SELECT * FROM push_commits WHERE sha = :sha")
    suspend fun getPushCommit(sha: String): PushCommitEntity?

    @Query("DELETE FROM push_commits")
    suspend fun clearPushCommits()
}
