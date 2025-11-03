package com.shuyu.gsygithubappcompose.core.ui.components

import androidx.compose.foundation.background
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
    content: LazyListScope.() -> Unit
) {
    PullToRefreshBox(
        modifier = modifier, isRefreshing = isRefreshing, onRefresh = onRefresh
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

        ///判断滚动位置: 代码中有一个 shouldLoadMore 的状态，它会持续观察列表的滚动状态：
        ///当用户滚动列表，即将看到倒数第二个 item 时，shouldLoadMore 的值就会变为 true。
        val shouldLoadMore by remember {
            derivedStateOf {
                val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                if (lastVisibleItem == null || listState.layoutInfo.totalItemsCount == 0) {
                    false
                } else {
                    lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 2
                }
            }
        }

        /// LaunchedEffect 会观察 shouldLoadMore 的变化：
        ///当 shouldLoadMore 变为 true 时，这个 LaunchedEffect 会执行。
        // 它会进行一系列判断：◦shouldLoadMore: 是否滚动到了底部？◦
        // !isLoadMore: 当前是否没有正在加载更多？（防止重复触发）◦
        // !isRefreshing: 当前是否没有在下拉刷新？（防止冲突）◦
        // hasMore: 是否还有更多数据可供加载？
        // 如果所有条件都满足，就会调用您传入的 onLoadMore() 回调函数，从而触发加载更多的逻辑。
        LaunchedEffect(shouldLoadMore) {
            if (shouldLoadMore && !isLoadMore && !isRefreshing && hasMore) {
                onLoadMore()
            }
        }
    }
}