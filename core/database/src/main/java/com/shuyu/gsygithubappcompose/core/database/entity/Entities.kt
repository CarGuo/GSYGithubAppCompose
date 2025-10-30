package com.shuyu.gsygithubappcompose.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Long,
    val login: String,
    val name: String?,
    val avatarUrl: String,
    val bio: String?,
    val company: String?,
    val blog: String?,
    val location: String?,
    val email: String?,
    val publicRepos: Int,
    val followers: Int,
    val following: Int,
    val createdAt: String,
    val updatedAt: String
)

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

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey
    val id: String,
    val type: String,
    val actorId: Long,
    val actorLogin: String,
    val actorAvatarUrl: String,
    val repoId: Long,
    val repoName: String,
    val payload: String,
    val createdAt: String
)
