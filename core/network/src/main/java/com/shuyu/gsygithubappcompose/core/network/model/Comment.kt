package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

data class Comment(
    val id: Long,
    @SerializedName("node_id")
    val nodeId: String?,
    val url: String?,
    @SerializedName("html_url")
    val htmlUrl: String?,
    val body: String,
    @SerializedName("body_html")
    val bodyHtml: String?,
    @SerializedName("body_text")
    val bodyText: String?,
    val user: User,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("author_association")
    val authorAssociation: String?
)

data class IssueComment(
    val id: Long,
    @SerializedName("node_id")
    val nodeId: String?,
    val url: String?,
    @SerializedName("html_url")
    val htmlUrl: String?,
    @SerializedName("issue_url")
    val issueUrl: String?,
    val body: String,
    @SerializedName("body_html")
    val bodyHtml: String?,
    @SerializedName("body_text")
    val bodyText: String?,
    val user: User,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("author_association")
    val authorAssociation: String?
)
