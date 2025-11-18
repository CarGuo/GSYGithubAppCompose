package com.shuyu.gsygithubappcompose.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "full_name")
    val fullName: String?,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "owner")
    val owner: String?,
    @ColumnInfo(name = "owner_name")
    val ownerName: String?,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "language")
    val language: String?,
    @ColumnInfo(name = "stargazers_count")
    val starCount: Int,
    @ColumnInfo(name = "forks_count")
    val forkCount: Int,
    @ColumnInfo(name = "watchers_count")
    val watcherCount: Int,
    @ColumnInfo(name = "open_issues_count")
    val openIssuesCount: Int,
    @ColumnInfo(name = "subscribers_count")
    val subscribersCount: Int,
    @ColumnInfo(name = "pushed_at")
    val pushAt: Date?,
    @ColumnInfo(name = "created_at")
    val createAt: Date?,
    @ColumnInfo(name = "updated_at")
    val updateAt: Date?,
    @ColumnInfo(name = "license")
    val license: String?,
    @ColumnInfo(name = "fork")
    val fork: Boolean,
    @ColumnInfo(name = "topics")
    val topics: List<String?>?,
    @ColumnInfo(name = "data")
    val data: String?,
    @ColumnInfo(name = "insert_date")
    val insertDate: Long
)
