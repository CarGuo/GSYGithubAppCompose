package com.shuyu.gsygithubappcompose.feature.dynamic

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shuyu.gsygithubappcompose.core.ui.components.EventItem
import com.shuyu.gsygithubappcompose.core.ui.components.GSYGeneralLoadState
import com.shuyu.gsygithubappcompose.core.ui.components.GSYPullRefresh
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseScreen

@Composable
fun DynamicScreen(
    viewModel: DynamicViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.doInitialLoad()
    }

    BaseScreen(viewModel = viewModel) {
        GSYGeneralLoadState(
            isLoading = uiState.isPageLoading,
            error = uiState.error,
            retry = { viewModel.refresh() }
        ) {
            GSYPullRefresh(
                listState = listState,
                onRefresh = { viewModel.refresh() },
                onLoadMore = { viewModel.loadMore() },
                isRefreshing = uiState.isRefreshing,
                isLoadMore = uiState.isLoadingMore,
                hasMore = uiState.hasMore,
                itemCount = uiState.events.size,
                loadMoreError = uiState.loadMoreError
            ) {
                items(uiState.events) { event ->
                    EventItem(event = event)
                }
            }
        }
    }
}
