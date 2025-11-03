package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

data class RepoCommit(
    val sha: String,
    @SerializedName("node_id")
    val nodeId: String?,
    val commit: CommitDetail,
    val url: String?,
    @SerializedName("html_url")
    val htmlUrl: String?,
    @SerializedName("comments_url")
    val commentsUrl: String?,
    val author: User?,
    val committer: User?,
    val parents: List<CommitParent>?
)

data class CommitDetail(
    val author: CommitUser?,
    val committer: CommitUser?,
    val message: String,
    val tree: CommitTree?,
    val url: String?,
    @SerializedName("comment_count")
    val commentCount: Int?,
    val verification: CommitVerification?
)

data class CommitUser(
    val name: String?,
    val email: String?,
    val date: String?
)

data class CommitTree(
    val sha: String,
    val url: String?
)

data class CommitParent(
    val sha: String,
    val url: String?,
    @SerializedName("html_url")
    val htmlUrl: String?
)

data class CommitVerification(
    val verified: Boolean,
    val reason: String?,
    val signature: String?,
    val payload: String?
)
