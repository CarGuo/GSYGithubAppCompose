package com.shuyu.gsygithubappcompose.feature.issue

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.shuyu.gsygithubappcompose.core.network.model.Comment
import com.shuyu.gsygithubappcompose.core.network.model.Issue
import com.shuyu.gsygithubappcompose.core.common.R // Corrected R import
import com.shuyu.gsygithubappcompose.core.ui.components.GSYCardItem
import com.shuyu.gsygithubappcompose.core.ui.components.GSYGeneralLoadState
import com.shuyu.gsygithubappcompose.core.ui.components.GSYMarkdownText
import com.shuyu.gsygithubappcompose.core.ui.components.GSYPullRefresh
import com.shuyu.gsygithubappcompose.core.ui.components.GSYTopAppBar
import com.shuyu.gsygithubappcompose.core.ui.components.RelativeTimeText
import com.shuyu.gsygithubappcompose.core.ui.components.getRelativeTimeSpanString

@Composable
fun IssueScreen(
    owner: String, repoName: String, issueNumber: Int, viewModel: IssueViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(owner, repoName, issueNumber) {
        viewModel.doInitialLoad()
    }

    Scaffold(
        topBar = {
            GSYTopAppBar(
                title = { Text("#${issueNumber} ${uiState.issue?.title ?: ""}", maxLines = 1) },
                showBackButton = true
            )
        }) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            GSYGeneralLoadState(
                isLoading = uiState.isPageLoading && uiState.issue == null,
                error = uiState.error,
                retry = { viewModel.refresh() }) {
                GSYPullRefresh(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { viewModel.refresh() },
                    isLoadMore = uiState.isLoadingMore,
                    onLoadMore = { viewModel.loadMore() },
                    hasMore = uiState.hasMore,
                    itemCount = uiState.comments.size,
                    loadMoreError = uiState.loadMoreError,
                    contentPadding = PaddingValues(5.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.issue?.let { issue ->
                        item {
                            IssueHeader(issue = issue)
                        }
                    }
                    items(uiState.comments) { comment ->
                        IssueCommentItem(comment = comment)
                    }
                }
            }
        }
    }
}

@Composable
fun IssueHeader(issue: Issue) {
    GSYCardItem {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = issue.user?.avatarUrl,
                    contentDescription = stringResource(R.string.user_avatar),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = issue.user?.login ?: "", fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val stateColor = if (issue.state == "open") Color.Green else Color.Red
                        Text(
                            text = issue.state,
                            color = stateColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "#${issue.number}", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        issue.comments?.let { comments ->
                            Text(
                                text = stringResource(R.string.comments_count, comments),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                RelativeTimeText(dateString = issue.createdAt)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = issue.title, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            IssueBody(issue = issue)
            issue.closedAt?.let { closedAt ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = stringResource(
                            R.string.closed_by_at,
                            issue.user?.login ?: "",
                            getRelativeTimeSpanString(closedAt)
                        ), fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun IssueBody(issue: Issue) {
    issue.body?.let { body ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            GSYMarkdownText(markdown = body)
        }
    }
}

@Composable
fun IssueCommentItem(comment: Comment) {
    GSYCardItem {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = comment.user.avatarUrl,
                contentDescription = stringResource(R.string.user_avatar),
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = comment.user.login, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    RelativeTimeText(dateString = comment.createdAt)
                }
                Spacer(modifier = Modifier.height(4.dp))
                GSYMarkdownText(markdown = comment.body)
            }
        }
    }
}
