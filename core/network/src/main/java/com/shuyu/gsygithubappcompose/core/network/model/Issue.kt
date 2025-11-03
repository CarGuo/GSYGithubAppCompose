package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

data class Issue(
    val id: Long,
    @SerializedName("node_id")
    val nodeId: String?,
    val number: Int,
    val title: String,
    val user: User,
    val labels: List<IssueLabel>?,
    val state: String,
    val locked: Boolean?,
    val assignee: User?,
    val assignees: List<User>?,
    val comments: Int?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("closed_at")
    val closedAt: String?,
    val body: String?,
    @SerializedName("body_html")
    val bodyHtml: String?,
    @SerializedName("html_url")
    val htmlUrl: String?,
    @SerializedName("repository_url")
    val repositoryUrl: String?
)

data class IssueLabel(
    val id: Long,
    @SerializedName("node_id")
    val nodeId: String?,
    val url: String?,
    val name: String,
    val color: String,
    val default: Boolean?
)
