package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

/**
 * Commit statistics model
 */
data class CommitStats(
    @SerializedName("total")
    val total: Int? = null,
    
    @SerializedName("additions")
    val additions: Int? = null,
    
    @SerializedName("deletions")
    val deletions: Int? = null
)
