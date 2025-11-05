package com.shuyu.gsygithubappcompose.feature.detail.file

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.ui.components.GSYGeneralLoadState
import com.shuyu.gsygithubappcompose.core.ui.components.GSYPullRefresh

@Composable
fun RepoDetailFileScreen(
    userName: String, repoName: String, viewModel: RepoDetailFileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userName, repoName) {
        viewModel.setRepoInfo(userName, repoName)
        viewModel.doInitialLoad()
    }

    GSYGeneralLoadState(
        isLoading = uiState.isPageLoading && uiState.fileContents.isEmpty(),
        error = uiState.error,
        retry = { viewModel.doInitialLoad() }) {
        Column(Modifier.fillMaxSize()) {
            PathNavigator(
                pathSegments = uiState.pathSegments,
                onSegmentClick = { index ->
                    val path = uiState.pathSegments.subList(0, index + 1).joinToString("/")
                    viewModel.navigateToPath(path)
                },
                onNavigateUp = { viewModel.navigateUp() },
                isLoading = uiState.isPageLoading || uiState.isRefreshing || uiState.isLoadingMore
            )

            GSYPullRefresh(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.refresh() },
                isLoadMore = false,
                onLoadMore = { },
                hasMore = false,
                itemCount = uiState.fileContents.size,
                loadMoreError = false
            ) {
                items(uiState.fileContents.size) { index ->
                    val file = uiState.fileContents[index]
                    FileItem(file = file, onClick = {
                        if (file.type == "dir") {
                            viewModel.navigateToPath(file.path)
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun PathNavigator(
    pathSegments: List<String>,
    onSegmentClick: (Int) -> Unit,
    onNavigateUp: () -> Unit,
    isLoading: Boolean
) {
    Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 2.dp) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onNavigateUp() }, enabled = !isLoading) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
                LazyRow(Modifier.padding(8.dp), state = rememberLazyListState()) {
                    item {
                        Text(
                            text = ".",
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clickable(enabled = !isLoading) { onSegmentClick(-1) }, // -1 to represent root
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (pathSegments.isNotEmpty()) {
                            Text(text = ">")
                        }
                    }
                    items(pathSegments.size) { index ->
                        val segment = pathSegments[index]
                        Text(
                            text = segment,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clickable(enabled = !isLoading) { onSegmentClick(index) },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (index < pathSegments.size - 1) {
                            Text(text = ">")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FileItem(
    file: com.shuyu.gsygithubappcompose.core.network.model.FileContent, onClick: () -> Unit
) {
    val isDirectory = file.type == "dir"
    Surface(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable(enabled = isDirectory) { onClick() },
        tonalElevation = 2.dp
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isDirectory) Icons.Default.Folder else Icons.Default.Description,
                contentDescription = if (isDirectory) "Folder" else "File",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = file.name)
        }
    }
}
