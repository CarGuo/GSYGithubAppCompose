package com.shuyu.gsygithubappcompose.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shuyu.gsygithubappcompose.core.database.entity.IssueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IssueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(issues: List<IssueEntity>)

    @Query("SELECT * FROM issue WHERE owner = :owner AND repo_name = :repoName AND page = :page ORDER BY created_at DESC")
    fun getIssues(owner: String, repoName: String, page: Int): Flow<List<IssueEntity>>

    @Query("DELETE FROM issue WHERE owner = :owner AND repo_name = :repoName")
    suspend fun clearIssues(owner: String, repoName: String)

    @Query("SELECT COUNT(*) FROM issue WHERE owner = :owner AND repo_name = :repoName")
    suspend fun getIssueCount(owner: String, repoName: String): Int
}
