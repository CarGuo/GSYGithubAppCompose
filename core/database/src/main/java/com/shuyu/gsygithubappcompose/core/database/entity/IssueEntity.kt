package com.shuyu.gsygithubappcompose.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "issues")
data class IssueEntity(
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "node_id")
    val nodeId: String?,
    @ColumnInfo(name = "number")
    val number: Int,
    @ColumnInfo(name = "title")
    val title: String,

    @Embedded(prefix = "user_")
    val user: UserEntity?,

    @ColumnInfo(name = "labels")
    val labels: String?, // Comma-separated label names

    @ColumnInfo(name = "state")
    val state: String,
    @ColumnInfo(name = "locked")
    val locked: Boolean?,

    @Embedded(prefix = "assignee_")
    val assignee: UserEntity?,

    @ColumnInfo(name = "comments")
    val comments: Int?,
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String?,
    @ColumnInfo(name = "closed_at")
    val closedAt: String?,
    @ColumnInfo(name = "body")
    val body: String?,
    @ColumnInfo(name = "body_html")
    val bodyHtml: String?,
    @ColumnInfo(name = "html_url")
    val htmlUrl: String?,
    @ColumnInfo(name = "repository_url")
    val repositoryUrl: String?,

    // Fields for local caching logic
    @ColumnInfo(name = "owner")
    val owner: String,
    @ColumnInfo(name = "repo_name")
    val repoName: String,
)
