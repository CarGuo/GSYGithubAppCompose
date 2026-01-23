package com.shuyu.gsygithubappcompose.feature.push

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.network.model.CommitFile
import com.shuyu.gsygithubappcompose.core.network.model.PushCommit
import com.shuyu.gsygithubappcompose.core.ui.LocalNavigator
import com.shuyu.gsygithubappcompose.core.ui.components.GSYCardItem
import com.shuyu.gsygithubappcompose.core.ui.components.GSYGeneralLoadState
import com.shuyu.gsygithubappcompose.core.ui.components.GSYPullRefresh
import com.shuyu.gsygithubappcompose.core.ui.components.GSYTopAppBar
import com.shuyu.gsygithubappcompose.core.ui.components.RelativeTimeText
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseScreen

@Composable
fun PushDetailScreen(pushDetailViewModel: PushDetailViewModel = hiltViewModel()) {
    val uiState by pushDetailViewModel.uiState.collectAsState()

    BaseScreen(
        viewModel = pushDetailViewModel,
    ) {
        Scaffold(
            topBar = {
                GSYTopAppBar(
                    title = { Text(text = "${uiState.owner}/${uiState.repoName}") },
                    showBackButton = true
                )
            }) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                GSYGeneralLoadState(
                    isLoading = uiState.isPageLoading && uiState.pushCommit == null,
                    error = uiState.error,
                    retry = { pushDetailViewModel.refresh() }) {
                    uiState.pushCommit?.let { pushCommit ->
                        GSYPullRefresh(
                            isRefreshing = uiState.isRefreshing,
                            onRefresh = { pushDetailViewModel.refresh() },
                            isLoadMore = uiState.isLoadingMore,
                            onLoadMore = { pushDetailViewModel.loadMore() },
                            hasMore = uiState.hasMore,
                            itemCount = pushCommit.files?.size ?: 0,
                            contentPadding = PaddingValues(5.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                PushDetailHeader(pushCommit = pushCommit)
                            }
                            pushCommit.files?.let { files ->
                                items(files) { file ->
                                    CommitFileItem(
                                        file = file,
                                        owner = uiState.owner ?: "",
                                        repoName = uiState.repoName ?: "",
                                        sha = uiState.sha ?: ""
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PushDetailHeader(pushCommit: PushCommit) {
    GSYCardItem(
        backgroundColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = pushCommit.author?.avatarUrl,
                    contentDescription = stringResource(R.string.user_avatar),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        pushCommit.stats?.let { stats ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(R.string.files_changed),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "${stats.total ?: 0}",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 4.dp, end = 8.dp)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(R.string.lines_added),
                                    tint = Color.Green, // Specific color for additions
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "${stats.additions ?: 0}",
                                    color = Color.Green, // Specific color for additions
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 4.dp, end = 8.dp)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.lines_deleted),
                                    tint = Color.Red, // Specific color for deletions
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "${stats.deletions ?: 0}",
                                    color = Color.Red, // Specific color for deletions
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                    pushCommit.commit?.author?.date?.let { date ->
                        RelativeTimeText(
                            dateString = date,
                            modifier = Modifier.padding(top = 4.dp),
                            color = Color.White
                        )
                    }
                }
            }
            pushCommit.commit?.message?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}

@Composable
fun CommitFileItem(file: CommitFile, owner: String, repoName: String, sha: String) {
    val navigator = LocalNavigator.current
    val fullPath = file.filename ?: stringResource(R.string.unknown_file)
    val lastSlashIndex = fullPath.lastIndexOf('/')
    val path = if (lastSlashIndex > -1) fullPath.substring(0, lastSlashIndex) else ""
    val name = if (lastSlashIndex > -1) fullPath.substring(lastSlashIndex + 1) else fullPath

    Column(modifier = Modifier.fillMaxWidth()) {
        if (path.isNotEmpty()) {
            Text(
                text = path,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 2.dp)
            )
        }
        GSYCardItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (sha.isNotEmpty()) {
                        val encodedPath = Uri.encode(fullPath)
                        navigator.navigate("file_code/$owner/$repoName/$encodedPath?sha=$sha")
                    }
                },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Edit, // Placeholder icon
                    contentDescription = stringResource(R.string.file_icon),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = name,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }
}
