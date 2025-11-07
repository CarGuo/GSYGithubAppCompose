package com.shuyu.gsygithubappcompose.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "issue_comments")
data class IssueCommentEntity(
    @PrimaryKey
    val id: Long,
    val issueId: Long, // Foreign key to link to IssueEntity
    val nodeId: String?,
    val url: String?,
    val htmlUrl: String?,
    val issueUrl: String?,
    val body: String,
    val bodyHtml: String?,
    val bodyText: String?,
    val userLogin: String,
    val userAvatarUrl: String?,
    val userId: Long?,
    val createdAt: String,
    val updatedAt: String?,
    val authorAssociation: String?
)
