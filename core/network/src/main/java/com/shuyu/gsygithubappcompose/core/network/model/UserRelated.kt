package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

data class UserOrganization(
    val login: String,
    val id: Long,
    @SerializedName("node_id")
    val nodeId: String?,
    val url: String?,
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    val description: String?
)

data class FollowUser(
    val login: String,
    val id: Long,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    @SerializedName("html_url")
    val htmlUrl: String?,
    val type: String?
)
