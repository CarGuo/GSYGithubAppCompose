package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

data class Notification(
    val id: String,
    val repository: Repository,
    val subject: NotificationSubject,
    val reason: String,
    val unread: Boolean,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("last_read_at")
    val lastReadAt: String?,
    val url: String?,
    @SerializedName("subscription_url")
    val subscriptionUrl: String?
)

data class NotificationSubject(
    val title: String,
    val url: String?,
    @SerializedName("latest_comment_url")
    val latestCommentUrl: String?,
    val type: String
)
