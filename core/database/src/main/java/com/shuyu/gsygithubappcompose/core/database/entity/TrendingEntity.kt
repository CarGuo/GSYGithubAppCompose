package com.shuyu.gsygithubappcompose.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trending_repo")
data class TrendingEntity(
    @PrimaryKey
    val fullName: String,
    val url: String?,
    val description: String?,
    val language: String?,
    val meta: String?,
    val contributors: String?, // Store as a comma-separated string or use a TypeConverter
    val contributorsUrl: String?,
    val starCount: String?,
    val forkCount: String?,
    val name: String?,
    val reposName: String?
)
