package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

/**
 * Repository template model
 */
data class Template(
    @SerializedName("name")
    val name: String? = null,
    
    @SerializedName("id")
    val id: Int? = null,
    
    @SerializedName("push_id")
    val pushId: Int? = null
)
