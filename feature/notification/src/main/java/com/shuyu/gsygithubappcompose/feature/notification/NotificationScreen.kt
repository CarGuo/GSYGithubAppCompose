package com.shuyu.gsygithubappcompose.feature.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.network.model.Notification
import com.shuyu.gsygithubappcompose.core.ui.LocalNavigator
import com.shuyu.gsygithubappcompose.core.ui.components.GSYCardItem
import com.shuyu.gsygithubappcompose.core.ui.components.GSYLoadingDialog
import com.shuyu.gsygithubappcompose.core.ui.components.GSYPullRefresh
import com.shuyu.gsygithubappcompose.core.ui.components.GSYTopAppBar
import com.shuyu.gsygithubappcompose.core.ui.components.RelativeTimeText
import com.shuyu.gsygithubappcompose.core.ui.components.SegmentedButton

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigator = LocalNavigator.current

    LaunchedEffect(Unit) {
        viewModel.doInitialLoad()
    }

    if (uiState.isDialogLoading) {
        GSYLoadingDialog()
    }

    Scaffold(
        topBar = {
            GSYTopAppBar(
                title = { Text(text = stringResource(R.string.notification)) },
                showBackButton = true,
                actions = {
                    IconButton(onClick = { viewModel.markAllAsRead() }) {
                        Icon(
                            Icons.Filled.DoneAll,
                            contentDescription = stringResource(R.string.mark_all_as_read)
                        )
                    }
                })
        }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (uiState.isPageLoading && uiState.notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null && uiState.notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.error ?: stringResource(id = R.string.error_unknown))
                }
            } else {
                Column {
                    SegmentedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp), items = listOf(
                            stringResource(R.string.notification_unread),
                            stringResource(R.string.notification_participating),
                            stringResource(R.string.notification_all)
                        ), selectedIndex = when (uiState.selectedItemType) {
                            NotificationItemType.UNREAD -> 0
                            NotificationItemType.PARTICIPATING -> 1
                            NotificationItemType.ALL -> 2
                        }, onItemSelected = { index ->
                            if (!uiState.isSwitchingItemType) {
                                when (index) {
                                    0 -> viewModel.setSelectedItemType(NotificationItemType.UNREAD)
                                    1 -> viewModel.setSelectedItemType(NotificationItemType.PARTICIPATING)
                                    2 -> viewModel.setSelectedItemType(NotificationItemType.ALL)
                                }
                            }
                        })
                    GSYPullRefresh(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = { viewModel.refresh() },
                        isLoadMore = uiState.isLoadingMore,
                        onLoadMore = { viewModel.loadMore() },
                        hasMore = uiState.hasMore,
                        itemCount = uiState.notifications.size,
                        loadMoreError = uiState.loadMoreError,
                        contentPadding = PaddingValues(5.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            uiState.notifications.size,
                            key = { uiState.notifications[it].id }) { index ->
                            val notification = uiState.notifications[index]
                            NotificationItem(notification = notification) {
                                viewModel.markNotificationAsRead(notification)
                                if (notification.subject.type == "Issue") {
                                    val url = notification.subject.url
                                    if (url != null) {
                                        val issueId = url.substring(url.lastIndexOf("/") + 1)
                                        val repoUrl = notification.repository.fullName

                                        val owner = repoUrl.split("/")[0]
                                        val repo = repoUrl.split("/")[1]
                                        navigator.navigate("issue_detail/$owner/$repo/$issueId")
                                    }
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
fun NotificationItem(
    notification: Notification, onClick: () -> Unit
) {
    GSYCardItem(
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = notification.repository.fullName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                RelativeTimeText(dateString = notification.updatedAt)
            }
            Text(
                text = notification.subject.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 5.dp)
            )
            val type = notification.subject.type
            val status =
                if (notification.unread) stringResource(R.string.notification_status_unread) else stringResource(
                    R.string.notification_status_read
                )
            Text(
                text = "${stringResource(R.string.notify_type)}: $type, ${stringResource(R.string.notify_status)}: $status",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 5.dp)
            )
        }
    }
}
