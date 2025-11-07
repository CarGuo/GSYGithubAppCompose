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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIssue(issue: IssueEntity)

    @Query("SELECT * FROM issues WHERE id = :issueId")
    fun getIssueById(issueId: Long): Flow<IssueEntity?>

    @Query("SELECT * FROM issues WHERE owner = :owner AND repo_name = :repoName  ORDER BY number DESC")
    fun getIssues(owner: String, repoName: String): Flow<List<IssueEntity>>

    @Query("DELETE FROM issues WHERE owner = :owner AND repo_name = :repoName")
    suspend fun clearIssues(owner: String, repoName: String)

    @Query("SELECT COUNT(*) FROM issues WHERE owner = :owner AND repo_name = :repoName")
    suspend fun getIssueCount(owner: String, repoName: String): Int
}
