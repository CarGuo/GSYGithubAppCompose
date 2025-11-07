package com.shuyu.gsygithubappcompose.feature.detail.issue

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.ui.components.GSYGeneralLoadState
import com.shuyu.gsygithubappcompose.core.ui.components.GSYPullRefresh
import com.shuyu.gsygithubappcompose.core.ui.components.GSYSearchInput
import com.shuyu.gsygithubappcompose.core.ui.components.IssueItem
import com.shuyu.gsygithubappcompose.core.ui.components.SegmentedButton
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseScreen
import kotlinx.coroutines.flow.collectLatest
import com.shuyu.gsygithubappcompose.feature.detail.LocalRepoOwner
import com.shuyu.gsygithubappcompose.feature.detail.LocalRepoName
import com.shuyu.gsygithubappcompose.feature.detail.LocalRepoDetailIssueViewModel
import com.shuyu.gsygithubappcompose.core.ui.LocalNavigator

@Composable
fun RepoDetailIssueScreen(
) {
    val viewModel = LocalRepoDetailIssueViewModel.current
    val uiState by viewModel.uiState.collectAsState()
    val owner = LocalRepoOwner.current
    val repoName = LocalRepoName.current
    val navigator = LocalNavigator.current

    LaunchedEffect(owner, repoName) {
        viewModel.setRepoInfo(owner, repoName)
        viewModel.doInitialLoad()
    }

    LaunchedEffect(Unit) {
        viewModel.refreshTrigger.collectLatest { // Use collectLatest to handle rapid emissions
            viewModel.doInitialLoad()
        }
    }

    BaseScreen(viewModel = viewModel) {
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
                    })

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
                        IssueItem(issue = issue, onClick = {
                            navigator.navigate("issue_detail/${owner}/${repoName}/${issue.number}")
                        })
                    }
                }
            }
        }
    }
}
