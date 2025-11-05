package com.shuyu.gsygithubappcompose.feature.detail.issue

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.network.model.Issue
import com.shuyu.gsygithubappcompose.core.ui.components.GSYGeneralLoadState
import com.shuyu.gsygithubappcompose.core.ui.components.GSYPullRefresh
import com.shuyu.gsygithubappcompose.core.ui.components.GSYSearchInput
import com.shuyu.gsygithubappcompose.core.ui.components.SegmentedButton

@Composable
fun RepoDetailIssueScreen(
    userName: String, repoName: String, viewModel: RepoDetailIssueViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userName, repoName) {
        viewModel.setRepoInfo(userName, repoName)
        viewModel.doInitialLoad()
    }

    GSYGeneralLoadState(
        isLoading = uiState.isPageLoading && uiState.issues.isEmpty(),
        error = uiState.error,
        retry = { viewModel.doInitialLoad() }) {
        Column(Modifier.fillMaxSize()) {
            GSYSearchInput(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                onSearch = { viewModel.refresh() },
                label = stringResource(id = R.string.search_issue),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            SegmentedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                items = listOf(
                    stringResource(R.string.all),
                    stringResource(R.string.open),
                    stringResource(R.string.closed)
                ),
                selectedIndex = when (uiState.issueState) {
                    "all" -> 0
                    "open" -> 1
                    "closed" -> 2
                    else -> 0
                },
                onItemSelected = { index ->
                    val newState = when (index) {
                        0 -> "all"
                        1 -> "open"
                        2 -> "closed"
                        else -> "all"
                    }
                    viewModel.onIssueStateChanged(newState)
                }
            )

            GSYPullRefresh(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.refresh() },
                isLoadMore = uiState.isLoadingMore,
                onLoadMore = { viewModel.loadMore() },
                hasMore = uiState.hasMore,
                itemCount = uiState.issues.size,
                loadMoreError = uiState.loadMoreError
            ) {
                items(uiState.issues) { issue ->
                    IssueItem(issue = issue)
                }
            }
        }
    }
}

@Composable
fun IssueItem(issue: Issue) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { /* TODO: Navigate to issue detail */ },
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.BugReport,
                    contentDescription = "Issue",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "#${issue.number} ${issue.title}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            issue.body?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                issue.user?.login.let {
                    Text(
                        text = "Opened by $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                issue.createdAt.let {
                    Text(
                        text = it.substringBefore("T"), // Simple date format for now
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
