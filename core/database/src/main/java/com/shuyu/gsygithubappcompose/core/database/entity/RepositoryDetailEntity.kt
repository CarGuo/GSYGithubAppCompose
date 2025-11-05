package com.shuyu.gsygithubappcompose.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "repository_detail")
data class RepositoryDetailEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "owner")
    val owner: String,
    @ColumnInfo(name = "owner_avatar_url")
    val ownerAvatarUrl: String?,
    @ColumnInfo(name = "license")
    val license: String?,
    @ColumnInfo(name = "fork_count")
    val forkCount: Int,
    @ColumnInfo(name = "stargazers_count")
    val stargazersCount: Int,
    @ColumnInfo(name = "watchers_count")
    val watchersCount: Int,
    @ColumnInfo(name = "has_issues_enabled")
    val hasIssuesEnabled: Boolean,
    @ColumnInfo(name = "viewer_has_starred")
    val viewerHasStarred: Boolean,
    @ColumnInfo(name = "viewer_subscription")
    val viewerSubscription: String?,
    @ColumnInfo(name = "default_branch_ref")
    val defaultBranchRef: String?,
    @ColumnInfo(name = "is_fork")
    val isFork: Boolean,
    @ColumnInfo(name = "languages")
    val languages: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: Date,
    @ColumnInfo(name = "pushed_at")
    val pushedAt: Date?,
    @ColumnInfo(name = "ssh_url")
    val sshUrl: String?,
    @ColumnInfo(name = "url")
    val url: String?,
    @ColumnInfo(name = "short_description_html")
    val shortDescriptionHTML: String?,
    @ColumnInfo(name = "topics")
    val topics: String?,
    @ColumnInfo(name = "issues_closed")
    val issuesClosed: Int,
    @ColumnInfo(name = "issues_open")
    val issuesOpen: Int,
    @ColumnInfo(name = "issues_total")
    val issuesTotal: Int,
    @ColumnInfo(name = "name_with_owner")
    val nameWithOwner: String,
    @ColumnInfo(name = "parent_name_with_owner")
    val parentNameWithOwner: String?,
    @ColumnInfo(name = "parent_full_name")
    val parentFullName: String?,
    @ColumnInfo(name = "size")
    val size: Int?,
)
