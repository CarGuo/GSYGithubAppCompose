package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

/**
 * Commit comment model
 */
data class CommitComment(
    @SerializedName("id")
    val id: Int? = null,
    
    @SerializedName("body")
    val body: String? = null,
    
    @SerializedName("path")
    val path: String? = null,
    
    @SerializedName("position")
    val position: Int? = null,
    
    @SerializedName("line")
    val line: Int? = null,
    
    @SerializedName("commit_id")
    val commitId: String? = null,
    
    @SerializedName("created_at")
    val createdAt: String? = null,
    
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    
    @SerializedName("html_url")
    val htmlUrl: String? = null,
    
    @SerializedName("url")
    val url: String? = null,
    
    @SerializedName("user")
    val user: User? = null
)
