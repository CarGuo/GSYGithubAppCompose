package com.shuyu.gsygithubappcompose.feature.dynamic

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shuyu.gsygithubappcompose.core.ui.components.EventItem
import com.shuyu.gsygithubappcompose.core.ui.components.GSYGeneralLoadState
import com.shuyu.gsygithubappcompose.core.ui.components.GSYPullRefresh

@Composable
fun DynamicScreen(
    viewModel: DynamicViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    GSYGeneralLoadState(
        isLoading = uiState.isPageLoading,
        error = uiState.error,
        retry = { viewModel.loadEvents(initialLoad = true) }
    ) {
        GSYPullRefresh(
            listState = listState,
            onRefresh = { viewModel.refreshEvents() },
            onLoadMore = { viewModel.loadMoreEvents() },
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
