package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

/**
 * Commits comparison model for comparing two commits
 */
data class CommitsComparison(
    @SerializedName("url")
    val url: String? = null,
    
    @SerializedName("html_url")
    val htmlUrl: String? = null,
    
    @SerializedName("base_commit")
    val baseCommit: RepoCommit? = null,
    
    @SerializedName("merge_base_commit")
    val mergeBaseCommit: RepoCommit? = null,
    
    @SerializedName("status")
    val status: String? = null,
    
    @SerializedName("total_commits")
    val totalCommits: Int? = null,
    
    @SerializedName("commits")
    val commits: List<RepoCommit>? = null,
    
    @SerializedName("files")
    val files: List<CommitFile>? = null
)
