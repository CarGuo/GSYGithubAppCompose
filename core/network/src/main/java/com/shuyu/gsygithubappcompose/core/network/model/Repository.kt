package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

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
