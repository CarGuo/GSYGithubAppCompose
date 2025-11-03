package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

/**
 * Issue timeline event (comments, state changes, etc.)
 */
data class IssueEvent(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("user")
    val user: User? = null,
    
    @SerializedName("created_at")
    val createdAt: String? = null,
    
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    
    @SerializedName("author_association")
    val authorAssociation: String? = null,
    
    @SerializedName("body")
    val body: String? = null,
    
    @SerializedName("body_html")
    val bodyHtml: String? = null,
    
    @SerializedName("event")
    val eventType: String? = null,
    
    @SerializedName("html_url")
    val htmlUrl: String? = null
)
