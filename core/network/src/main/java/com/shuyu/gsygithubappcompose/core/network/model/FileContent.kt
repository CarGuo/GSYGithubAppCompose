package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

data class FileContent(
    val type: String,
    val encoding: String?,
    val size: Long,
    val name: String,
    val path: String,
    val content: String?,
    val sha: String,
    val url: String?,
    @SerializedName("git_url")
    val gitUrl: String?,
    @SerializedName("html_url")
    val htmlUrl: String?,
    @SerializedName("download_url")
    val downloadUrl: String?,
    @SerializedName("_links")
    val links: FileLinks?
)

data class FileLinks(
    val self: String?,
    val git: String?,
    val html: String?
)
