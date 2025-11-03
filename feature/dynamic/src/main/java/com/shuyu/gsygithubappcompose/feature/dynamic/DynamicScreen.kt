package com.shuyu.gsygithubappcompose.feature.dynamic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shuyu.gsygithubappcompose.core.ui.components.EventItem
import com.shuyu.gsygithubappcompose.core.ui.components.GSYGeneralLoadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicScreen(
    viewModel: DynamicViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()
    val listState = rememberLazyListState()

    GSYGeneralLoadState(
        isLoading = uiState.isLoading && uiState.events.isEmpty(),
        error = uiState.error,
        retry = { viewModel.loadEvents(initialLoad = true) }
    ) {
        PullToRefreshBox(
            state = pullRefreshState,
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refreshEvents() }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.8f)),
                state = listState
            ) {
                items(uiState.events) { event ->
                    EventItem(event = event)
                }
                if (uiState.isLoadingMore) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }
            }

            // Load more logic
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isNotEmpty() && layoutInfo.totalItemsCount > 0) {
                val lastVisibleItem = visibleItemsInfo.last()
                if (lastVisibleItem.index == layoutInfo.totalItemsCount - 1 && !uiState.isLoadingMore && uiState.hasMore) {
                    LaunchedEffect(lastVisibleItem) {
                        viewModel.loadMoreEvents()
                    }
                }
            }
        }
    }
}
