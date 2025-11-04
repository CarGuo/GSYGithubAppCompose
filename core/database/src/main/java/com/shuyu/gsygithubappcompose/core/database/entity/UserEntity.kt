package com.shuyu.gsygithubappcompose.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users", primaryKeys = ["id", "orgLogin"])
data class UserEntity(
    val id: Long,
    val login: String,
    val nodeId: String?,
    val avatarUrl: String,
    val gravatarId: String?,
    val url: String?,
    val htmlUrl: String?,
    val followersUrl: String?,
    val followingUrl: String?,
    val gistsUrl: String?,
    val starredUrl: String?,
    val subscriptionsUrl: String?,
    val organizationsUrl: String?,
    val reposUrl: String?,
    val eventsUrl: String?,
    val receivedEventsUrl: String?,
    val type: String?,
    val siteAdmin: Boolean?,
    val name: String?,
    val company: String?,
    val blog: String?,
    val location: String?,
    val email: String?,
    val starred: String?,
    val bio: String?,
    val publicRepos: Int?,
    val publicGists: Int?,
    val followers: Int?,
    val following: Int?,
    val createdAt: String?,
    val updatedAt: String?,
    val privateGists: Int?,
    val totalPrivateRepos: Int?,
    val ownedPrivateRepos: Int?,
    val diskUsage: Int?,
    val collaborators: Int?,
    val twoFactorAuthentication: Boolean?,
    val orgLogin: String = "" // Changed to non-nullable String with default empty string
)
