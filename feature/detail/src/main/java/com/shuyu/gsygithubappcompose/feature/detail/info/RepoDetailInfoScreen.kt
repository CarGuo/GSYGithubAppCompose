package com.shuyu.gsygithubappcompose.feature.detail.info

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shuyu.gsygithubappcompose.core.ui.components.GSYGeneralLoadState
import com.shuyu.gsygithubappcompose.core.ui.components.GSYPullRefresh

@Composable
fun RepoDetailInfoScreen(
    owner: String,
    name: String,
    viewModel: RepoDetailInfoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(owner, name) {
        viewModel.loadRepoDetailInfo(owner, name)
    }

    GSYGeneralLoadState(
        isLoading = uiState.isPageLoading && uiState.repoDetail == null,
        error = uiState.error,
        retry = { viewModel.refreshRepoDetailInfo() }
    ) {
        GSYPullRefresh(
            modifier = Modifier.fillMaxSize(),
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refreshRepoDetailInfo() },
            isLoadMore = uiState.isLoadingMore,
            onLoadMore = { viewModel.loadMoreRepoDetailList() },
            hasMore = uiState.hasMore,
            itemCount = uiState.repoDetailList.size,
            loadMoreError = uiState.loadMoreError
        ) {
            uiState.repoDetail?.let { headerData ->
                item {
                    RepositoryDetailInfoHeader(repositoryDetailModel = headerData)
                }
            }
            uiState.repoDetailList.let { listItems ->
                items(listItems) { item ->
                    // Replace this Text composable with your actual item composable
                    Text(text = item.toString())
                }
            }
        }
    }
}
