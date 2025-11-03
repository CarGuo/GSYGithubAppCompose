package com.shuyu.gsygithubappcompose.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repositories")
data class RepositoryEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val ownerId: Long,
    val ownerLogin: String,
    val ownerAvatarUrl: String,
    val isPrivate: Boolean,
    val htmlUrl: String,
    val language: String?,
    val stargazersCount: Int,
    val watchersCount: Int,
    val forksCount: Int,
    val openIssuesCount: Int,
    val createdAt: String,
    val updatedAt: String
)
