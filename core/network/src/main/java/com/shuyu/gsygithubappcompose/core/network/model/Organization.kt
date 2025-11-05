package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

data class Organization(
    val login: String,
    val id: Long,
    @SerializedName("node_id")
    val nodeId: String?,
    val url: String?,
    @SerializedName("repos_url")
    val reposUrl: String?,
    @SerializedName("events_url")
    val eventsUrl: String?,
    @SerializedName("hooks_url")
    val hooksUrl: String?,
    @SerializedName("issues_url")
    val issuesUrl: String?,
    @SerializedName("members_url")
    val membersUrl: String?,
    @SerializedName("public_members_url")
    val publicMembersUrl: String?,
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    val description: String?,
    val name: String?,
    val company: String?,
    val blog: String?,
    val location: String?,
    val email: String?,
    @SerializedName("is_verified")
    val isVerified: Boolean?,
    @SerializedName("has_organization_projects")
    val hasOrganizationProjects: Boolean?,
    @SerializedName("has_repository_projects")
    val hasRepositoryProjects: Boolean?,
    @SerializedName("public_repos")
    val publicRepos: Int?,
    @SerializedName("public_gists")
    val publicGists: Int?,
    val followers: Int?,
    val following: Int?,
    @SerializedName("html_url")
    val htmlUrl: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    val type: String?
)
