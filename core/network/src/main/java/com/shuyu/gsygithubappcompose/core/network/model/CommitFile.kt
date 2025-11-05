package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

/**
 * File changed in a commit
 */
data class CommitFile(
    @SerializedName("sha")
    val sha: String? = null,
    
    @SerializedName("filename")
    val filename: String? = null,
    
    @SerializedName("status")
    val status: String? = null,
    
    @SerializedName("additions")
    val additions: Int? = null,
    
    @SerializedName("deletions")
    val deletions: Int? = null,
    
    @SerializedName("changes")
    val changes: Int? = null,
    
    @SerializedName("blob_url")
    val blobUrl: String? = null,
    
    @SerializedName("raw_url")
    val rawUrl: String? = null,
    
    @SerializedName("contents_url")
    val contentsUrl: String? = null,
    
    @SerializedName("patch")
    val patch: String? = null
)
