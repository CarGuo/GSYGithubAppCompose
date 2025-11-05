package com.shuyu.gsygithubappcompose.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.shuyu.gsygithubappcompose.core.database.entity.CommitEntity

@Dao
interface CommitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(commits: List<CommitEntity>)

    @Query("SELECT * FROM commits WHERE repo_owner_login = :owner AND repo_name = :repoName")
    suspend fun getRepoCommits(owner: String, repoName: String): List<CommitEntity>

    @Query("DELETE FROM commits WHERE repo_owner_login = :owner AND repo_name = :repoName")
    suspend fun clearRepoCommits(owner: String, repoName: String)

    @Transaction
    suspend fun clearAndInsertRepoCommits(owner: String, repoName: String, commits: List<CommitEntity>) {
        clearRepoCommits(owner, repoName)
        insert(commits)
    }

    @Query("DELETE FROM commits")
    suspend fun clearAll()
}
