package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

data class PullRequest(
    val id: Long,
    @SerializedName("node_id")
    val nodeId: String?,
    val number: Int,
    val state: String,
    val locked: Boolean?,
    val title: String,
    val user: User,
    val body: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("closed_at")
    val closedAt: String?,
    @SerializedName("merged_at")
    val mergedAt: String?,
    @SerializedName("merge_commit_sha")
    val mergeCommitSha: String?,
    val assignee: User?,
    val assignees: List<User>?,
    @SerializedName("requested_reviewers")
    val requestedReviewers: List<User>?,
    val labels: List<IssueLabel>?,
    val draft: Boolean?,
    val head: PullRequestBranch?,
    val base: PullRequestBranch?,
    @SerializedName("html_url")
    val htmlUrl: String?,
    @SerializedName("diff_url")
    val diffUrl: String?,
    @SerializedName("patch_url")
    val patchUrl: String?,
    @SerializedName("commits_url")
    val commitsUrl: String?,
    val merged: Boolean?,
    val mergeable: Boolean?,
    @SerializedName("mergeable_state")
    val mergeableState: String?,
    @SerializedName("merged_by")
    val mergedBy: User?,
    val comments: Int?,
    @SerializedName("review_comments")
    val reviewComments: Int?,
    val commits: Int?,
    val additions: Int?,
    val deletions: Int?,
    @SerializedName("changed_files")
    val changedFiles: Int?
)

data class PullRequestBranch(
    val label: String?,
    val ref: String?,
    val sha: String?,
    val user: User?,
    val repo: Repository?
)
