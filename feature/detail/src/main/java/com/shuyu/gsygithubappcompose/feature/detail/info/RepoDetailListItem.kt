package com.shuyu.gsygithubappcompose.feature.detail.info

import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.core.network.model.RepoCommit

sealed interface RepoDetailListItem {
    data class EventItem(val event: Event) : RepoDetailListItem
    data class CommitItem(val commit: RepoCommit) : RepoDetailListItem
}
