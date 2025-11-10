package com.shuyu.gsygithubappcompose.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

data class CommitUserEntity(
    val name: String?,
    val email: String?,
    val date: String?
)

@Entity(tableName = "commits", primaryKeys = ["sha", "repo_owner_login", "repo_name"])
data class CommitEntity(
    val sha: String,
    val message: String?,
    val author: String?,
    val committer: String?,
    @ColumnInfo(name = "repo_owner_login")
    val repoOwnerLogin: String,
    @ColumnInfo(name = "repo_name")
    val repoName: String,
    @ColumnInfo(name = "commit_detail")
    val commitDetail: String
)
