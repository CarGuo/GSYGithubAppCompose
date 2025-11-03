package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

data class Release(
    val id: Long,
    @SerializedName("node_id")
    val nodeId: String?,
    @SerializedName("tag_name")
    val tagName: String,
    @SerializedName("target_commitish")
    val targetCommitish: String?,
    val name: String?,
    val body: String?,
    val draft: Boolean,
    val prerelease: Boolean,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("published_at")
    val publishedAt: String?,
    val author: User?,
    val assets: List<ReleaseAsset>?,
    @SerializedName("tarball_url")
    val tarballUrl: String?,
    @SerializedName("zipball_url")
    val zipballUrl: String?,
    @SerializedName("html_url")
    val htmlUrl: String?,
    val url: String?
)

data class ReleaseAsset(
    val id: Long,
    @SerializedName("node_id")
    val nodeId: String?,
    val name: String,
    val label: String?,
    val uploader: User?,
    @SerializedName("content_type")
    val contentType: String?,
    val state: String?,
    val size: Long?,
    @SerializedName("download_count")
    val downloadCount: Int?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("browser_download_url")
    val browserDownloadUrl: String?,
    val url: String?
)
