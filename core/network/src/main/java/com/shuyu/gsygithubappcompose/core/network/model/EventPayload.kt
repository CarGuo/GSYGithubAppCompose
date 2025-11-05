package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

/**
 * Payload details for GitHub events
 */
data class EventPayload(
    @SerializedName("push_id")
    val pushId: Long? = null,
    
    @SerializedName("size")
    val size: Int? = null,
    
    @SerializedName("distinct_size")
    val distinctSize: Int? = null,
    
    @SerializedName("ref")
    val ref: String? = null,
    
    @SerializedName("head")
    val head: String? = null,
    
    @SerializedName("before")
    val before: String? = null,
    
    @SerializedName("commits")
    val commits: List<PushEventCommit>? = null,
    
    @SerializedName("action")
    val action: String? = null,
    
    @SerializedName("ref_type")
    val refType: String? = null,
    
    @SerializedName("master_branch")
    val masterBranch: String? = null,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("pusher_type")
    val pusherType: String? = null,
    
    @SerializedName("release")
    val release: Release? = null,
    
    @SerializedName("issue")
    val issue: Issue? = null,
    
    @SerializedName("comment")
    val comment: IssueEvent? = null
)

/**
 * Commit in a push event
 */
data class PushEventCommit(
    @SerializedName("sha")
    val sha: String? = null,
    
    @SerializedName("message")
    val message: String? = null,
    
    @SerializedName("author")
    val author: CommitUser? = null,
    
    @SerializedName("url")
    val url: String? = null,
    
    @SerializedName("distinct")
    val distinct: Boolean? = null
)
