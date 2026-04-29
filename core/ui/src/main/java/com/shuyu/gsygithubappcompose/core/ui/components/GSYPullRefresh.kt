package com.shuyu.gsygithubappcompose.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shuyu.gsygithubappcompose.core.common.R
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * 下拉刷新和上拉加载更多
 *
 * @param modifier           修饰符
 * @param listState          LazyListState
 * @param contentPadding     LazyColumn的contentPadding
 * @param verticalArrangement LazyColumn的verticalArrangement
 * @param onRefresh          刷新回调
 * @param onLoadMore         加载更多回调
 * @param isRefreshing       是否正在刷新
 * @param isLoadMore         是否正在加载更多
 * @param hasMore            是否还有更多数据
 * @param itemCount          列表数据量
 * @param loadMoreError      加载更多是否失败
 * @param content            列表内容
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GSYPullRefresh(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    isRefreshing: Boolean,
    isLoadMore: Boolean,
    hasMore: Boolean,
    itemCount: Int,
    loadMoreError: Boolean = false,
    content: LazyListScope.() -> Unit
) {
    val currentOnLoadMore by rememberUpdatedState(onLoadMore)
    val currentItemCount by rememberUpdatedState(itemCount)
    val currentIsLoadMore by rememberUpdatedState(isLoadMore)
    val currentIsRefreshing by rememberUpdatedState(isRefreshing)
    val currentHasMore by rememberUpdatedState(hasMore)
    val currentLoadMoreError by rememberUpdatedState(loadMoreError)

    PullToRefreshBox(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = {
            if (!isLoadMore) { // Prevent refresh if loading more
                onRefresh()
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.8f)),
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement
        ) {
            content()
            if (itemCount > 0 && !isRefreshing) { // Only show load more indicators if there's data
                if (isLoadMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (loadMoreError) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { currentOnLoadMore() }, // Click to retry
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(id = R.string.load_failed_click_to_retry)) // Assuming you'll add this string resource
                        }
                    }
                } else if (hasMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(id = R.string.loading_more))
                        }
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(id = R.string.no_more_data))
                        }
                    }
                }
            }
        }

        ///判断滚动位置: 代码中有一个 shouldLoadMore 的状态，它会持续观察列表的滚动状态：
        ///当用户滚动列表，即将看到倒数第二个 item 时，shouldLoadMore 的值就会变为 true。

        LaunchedEffect(listState) {
            snapshotFlow {
                val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                val shouldLoadMore = if (lastVisibleItem == null || listState.layoutInfo.totalItemsCount == 0) {
                    false
                } else {
                    lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 2
                }
                LoadMoreSnapshot(
                    shouldLoadMore = shouldLoadMore,
                    itemCount = currentItemCount,
                    isLoadMore = currentIsLoadMore,
                    isRefreshing = currentIsRefreshing,
                    hasMore = currentHasMore,
                    loadMoreError = currentLoadMoreError
                )
            }
                .distinctUntilChanged()
                .collect { snapshot ->
                    if (
                        snapshot.itemCount > 0 &&
                        snapshot.shouldLoadMore &&
                        !snapshot.isLoadMore &&
                        !snapshot.isRefreshing &&
                        snapshot.hasMore &&
                        !snapshot.loadMoreError
                    ) {
                        currentOnLoadMore()
                    }
                }
        }
    }
}

private data class LoadMoreSnapshot(
    val shouldLoadMore: Boolean,
    val itemCount: Int,
    val isLoadMore: Boolean,
    val isRefreshing: Boolean,
    val hasMore: Boolean,
    val loadMoreError: Boolean
)
