package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

data class TrendingRepoModel(
    @SerializedName("fullName")
    val fullName: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("language")
    val language: String?,
    @SerializedName("meta")
    val meta: String?,
    @SerializedName("contributors")
    val contributors: List<String>?,
    @SerializedName("contributorsUrl")
    val contributorsUrl: String?,
    @SerializedName("starCount")
    val starCount: String?,
    @SerializedName("forkCount")
    val forkCount: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("reposName")
    val reposName: String?
)
