package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

/**
 * Repository license information
 */
data class License(
    @SerializedName("key")
    val key: String? = null,
    
    @SerializedName("name")
    val name: String? = null,
    
    @SerializedName("spdx_id")
    val spdxId: String? = null,
    
    @SerializedName("url")
    val url: String? = null,
    
    @SerializedName("node_id")
    val nodeId: String? = null
)
