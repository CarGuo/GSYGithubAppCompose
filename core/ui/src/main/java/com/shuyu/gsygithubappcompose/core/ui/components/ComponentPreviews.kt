package com.shuyu.gsygithubappcompose.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shuyu.gsygithubappcompose.core.network.model.Issue
import com.shuyu.gsygithubappcompose.core.network.model.User
import com.shuyu.gsygithubappcompose.core.ui.theme.GSYGithubAppComposeTheme

@Preview(showBackground = true)
@Composable
private fun RepositoryItemPreview() {
    GSYGithubAppComposeTheme {
        RepositoryItem(
            repoItem = RepositoryDisplayData(
                fullName = "CarGuo/GSYGithubAppCompose",
                description = "A GitHub Android client built with Jetpack Compose.",
                language = "Kotlin",
                starCount = "1200",
                forkCount = "240",
                avatarUrl = "",
                ownerName = "CarGuo",
                name = "GSYGithubAppCompose"
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UserItemPreview() {
    GSYGithubAppComposeTheme {
        UserItem(
            user = sampleUser(),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun IssueItemPreview() {
    GSYGithubAppComposeTheme {
        IssueItem(
            issue = Issue(
                id = 1L,
                nodeId = "node",
                number = 42,
                title = "Improve Compose stability",
                user = sampleUser(),
                labels = emptyList(),
                state = "open",
                locked = false,
                assignee = null,
                assignees = emptyList(),
                comments = 3,
                createdAt = "2026-04-29T08:00:00Z",
                updatedAt = null,
                closedAt = null,
                body = "Reduce unstable parameters and keep list identity stable.",
                bodyHtml = null,
                htmlUrl = null,
                repositoryUrl = null
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GSYTopAppBarPreview() {
    GSYGithubAppComposeTheme {
        GSYTopAppBar(
            title = { Text(text = "Preview") },
            showBackButton = true,
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GSYGeneralLoadStatePreview() {
    GSYGithubAppComposeTheme {
        GSYGeneralLoadState(
            isLoading = false,
            error = null,
            retry = {}
        ) {
            Text(text = "Loaded content")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GSYPullRefreshPreview() {
    GSYGithubAppComposeTheme {
        GSYPullRefresh(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            onRefresh = {},
            onLoadMore = {},
            isRefreshing = false,
            isLoadMore = false,
            hasMore = false,
            itemCount = 1
        ) {
            item {
                Text(text = "Pull refresh content")
            }
        }
    }
}

private fun sampleUser(): User {
    return User.toMiniUserModel(
        login = "CarGuo",
        id = 1L,
        avatarUrl = ""
    )
}
