package com.shuyu.gsygithubappcompose.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "file_content")
data class FileContentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val repoOwner: String,
    val repoName: String,
    val type: String,
    val name: String,
    val path: String,
    val sha: String,
    val url: String?,
    val gitUrl: String?,
    val htmlUrl: String?,
    val downloadUrl: String?,
)
