package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

data class AccessToken(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    val scope: String?
)

data class User(
    val id: Long,
    val login: String,
    val name: String?,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    val bio: String?,
    val company: String?,
    val blog: String?,
    val location: String?,
    val email: String?,
    @SerializedName("public_repos")
    val publicRepos: Int,
    val followers: Int,
    val following: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class Repository(
    val id: Long,
    val name: String,
    @SerializedName("full_name")
    val fullName: String,
    val description: String?,
    val owner: User,
    val private: Boolean,
    @SerializedName("html_url")
    val htmlUrl: String,
    val language: String?,
    @SerializedName("stargazers_count")
    val stargazersCount: Int,
    @SerializedName("watchers_count")
    val watchersCount: Int,
    @SerializedName("forks_count")
    val forksCount: Int,
    @SerializedName("open_issues_count")
    val openIssuesCount: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("pushed_at")
    val pushedAt: String?
)

data class Event(
    val id: String,
    val type: String,
    val actor: User,
    val repo: EventRepo,
    val payload: EventPayload?,
    @SerializedName("created_at")
    val createdAt: String
)

data class EventRepo(
    val id: Long,
    val name: String,
    val url: String
)

data class EventPayload(
    val action: String?,
    @SerializedName("ref_type")
    val refType: String?,
    @SerializedName("master_branch")
    val masterBranch: String?,
    val description: String?,
    @SerializedName("pusher_type")
    val pusherType: String?
)
