package com.shuyu.gsygithubappcompose.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.shuyu.gsygithubappcompose.core.database.entity.IssueCommentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IssueCommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(comments: List<IssueCommentEntity>)

    @Query("SELECT * FROM issue_comments WHERE issueId = :issueId ORDER BY createdAt ASC")
    fun getIssueComments(issueId: Long): Flow<List<IssueCommentEntity>>

    @Query("DELETE FROM issue_comments WHERE issueId = :issueId")
    suspend fun clearIssueComments(issueId: Long)

    @Transaction
    suspend fun clearAndInsertIssueComments(issueId: Long, comments: List<IssueCommentEntity>) {
        clearIssueComments(issueId)
        insertAll(comments)
    }

    @Query("DELETE FROM issue_comments")
    suspend fun clearAll()
}
