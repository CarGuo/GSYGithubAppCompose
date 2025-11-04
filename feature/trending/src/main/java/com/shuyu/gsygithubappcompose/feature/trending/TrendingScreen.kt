package com.shuyu.gsygithubappcompose.feature.trending

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shuyu.gsygithubappcompose.core.ui.components.GSYGeneralLoadState
import com.shuyu.gsygithubappcompose.core.ui.components.GSYPullRefresh
import com.shuyu.gsygithubappcompose.core.ui.components.RepositoryItem

@Composable
fun TrendingScreen(
    onImageClick: (String) -> Unit,
    viewModel: TrendingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.doInitialLoad()
    }

    GSYGeneralLoadState(
        isLoading = uiState.isPageLoading && uiState.repositories.isEmpty(),
        error = uiState.error,
        retry = { viewModel.refresh() }
    ) {
        GSYPullRefresh(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            isLoadMore = uiState.isLoadingMore,
            onLoadMore = { viewModel.loadMore() },
            hasMore = uiState.hasMore,
            itemCount = uiState.repositories.size,
            loadMoreError = uiState.loadMoreError,
            contentPadding = PaddingValues(5.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.repositories) { repo ->
                RepositoryItem(repository = repo, onImageClick = onImageClick)
            }
        }
    }
}
