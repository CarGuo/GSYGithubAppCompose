package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

data class PushCommit(
    val files: List<CommitFile>?,
    val stats: CommitStats?,
    val sha: String?,
    val url: String?,
    @SerializedName("html_url")
    val htmlUrl: String?,
    @SerializedName("comments_url")
    val commentsUrl: String?,
    val commit: CommitDetail?,
    val author: User?,
    val committer: User?,
    val parents: List<RepoCommit>?
)
