package com.shuyu.gsygithubappcompose.core.network.model

import java.util.Date

data class RepositoryDetailModel(
    val id: String,
    val name: String,
    val owner: String,
    val ownerAvatarUrl: String?,
    val license: String?,
    val forkCount: Int,
    val stargazersCount: Int,
    val watchersCount: Int,
    val hasIssuesEnabled: Boolean,
    val viewerHasStarred: Boolean,
    val viewerSubscription: String?,
    val defaultBranchRef: String?,
    val isFork: Boolean,
    val languages: List<String?>?,
    val createdAt: Date,
    val pushedAt: Date?,
    val sshUrl: String?,
    val url: String?,
    val shortDescriptionHTML: String?,
    val topics: List<String?>?,
    val issuesClosed: Int,
    val issuesOpen: Int,
    val issuesTotal: Int,
    val nameWithOwner: String,
    val parentNameWithOwner: String?,
    val parentFullName: String?,
    val size: Int?,
)
