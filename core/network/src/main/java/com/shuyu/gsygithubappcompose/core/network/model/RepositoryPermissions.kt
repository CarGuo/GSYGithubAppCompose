package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

/**
 * Repository permissions for current user
 */
data class RepositoryPermissions(
    @SerializedName("admin")
    val admin: Boolean? = null,
    
    @SerializedName("push")
    val push: Boolean? = null,
    
    @SerializedName("pull")
    val pull: Boolean? = null
)
