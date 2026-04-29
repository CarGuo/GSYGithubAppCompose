package com.shuyu.gsygithubappcompose.feature.trending

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shuyu.gsygithubappcompose.core.ui.LocalNavigator
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseScreen
import com.shuyu.gsygithubappcompose.core.ui.components.GSYGeneralLoadState
import com.shuyu.gsygithubappcompose.core.ui.components.GSYPullRefresh
import com.shuyu.gsygithubappcompose.core.ui.components.RepositoryItem
import com.shuyu.gsygithubappcompose.core.ui.components.toTrendingDisplayData

@Composable
fun TrendingScreen(
    viewModel: TrendingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current

    LaunchedEffect(Unit) {
        viewModel.doInitialLoad()
    }

    BaseScreen(viewModel = viewModel) {
        GSYGeneralLoadState(
            isLoading = uiState.isPageLoading && uiState.repositories.isEmpty(),
            error = uiState.error,
            retry = { viewModel.refresh() }
        ) {
            GSYPullRefresh(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.refresh() },
                isLoadMore = false, // Trending API does not support load more
                onLoadMore = { /* No-op */ }, // Trending API does not support load more
                hasMore = false, // Trending API does not support load more
                itemCount = uiState.repositories.size,
                loadMoreError = false, // Trending API does not support load more
                contentPadding = PaddingValues(5.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(
                    items = uiState.repositories,
                    key = { index, repo -> repo.url ?: repo.fullName ?: "${repo.name}/${repo.reposName}:$index" }
                ) { _, repo ->
                    val repoItem = repo.toTrendingDisplayData()
                    RepositoryItem(
                        repoItem = repoItem,
                        onClick = {
                            navigator.navigate("repo_detail/${repoItem.ownerName}/${repoItem.name}")
                        }
                    )
                }
            }
        }
    }
}
