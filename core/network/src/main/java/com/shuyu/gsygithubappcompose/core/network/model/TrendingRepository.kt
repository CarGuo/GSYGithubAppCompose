package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

data class TrendingRepository(
    val author: String?,
    val name: String,
    val avatar: String?,
    val url: String?,
    val description: String?,
    val language: String?,
    @SerializedName("languageColor")
    val languageColor: String?,
    val stars: Int?,
    val forks: Int?,
    @SerializedName("currentPeriodStars")
    val currentPeriodStars: Int?,
    @SerializedName("builtBy")
    val builtBy: List<TrendingUser>?
)

data class TrendingUser(
    val username: String?,
    val href: String?,
    val avatar: String?
)
