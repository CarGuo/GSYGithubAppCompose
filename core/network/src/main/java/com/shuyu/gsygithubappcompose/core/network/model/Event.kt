package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

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
