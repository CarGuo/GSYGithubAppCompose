package com.shuyu.gsygithubappcompose.feature.dynamic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.core.ui.components.AvatarImage
import com.shuyu.gsygithubappcompose.core.ui.components.GSYGeneralLoadState
import java.text.SimpleDateFormat
import java.util.*

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
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {
                items(uiState.events) { event ->
                    EventItem(event = event)
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
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

@Composable
fun EventItem(event: Event) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar
        AvatarImage(
            url = event.actor.avatarUrl,
            size = 40.dp,
            modifier = Modifier.clip(CircleShape)
        )

        // Content
        Column(modifier = Modifier.weight(1f)) {
            // Username
            Text(
                text = event.actor.login,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Action description
            val actionText = when {
                event.type.contains("Push") -> "pushed to"
                event.type.contains("Create") -> "created"
                event.type.contains("Fork") -> "forked"
                event.type.contains("Star") -> "starred"
                event.type.contains("Watch") -> "started watching"
                event.type.contains("Issue") -> "created issue in"
                event.type.contains("PullRequest") -> "created pull request in"
                else -> event.type.replace("Event", "").lowercase()
            }

            Text(
                text = actionText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Repository name
            Text(
                text = event.repo.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Time
            val timeText = formatEventTime(event.createdAt)
            Text(
                text = timeText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun formatEventTime(createdAt: String): String {
    return try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = dateFormat.parse(createdAt)

        if (date != null) {
            val now = Date()
            val diffInMillis = now.time - date.time
            val diffInMinutes = diffInMillis / (1000 * 60)
            val diffInHours = diffInMinutes / 60
            val diffInDays = diffInHours / 24

            when {
                diffInMinutes < 1 -> "just now"
                diffInMinutes < 60 -> "${diffInMinutes}m ago"
                diffInHours < 24 -> "${diffInHours}h ago"
                diffInDays < 7 -> "${diffInDays}d ago"
                else -> {
                    val outputFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                    outputFormat.format(date)
                }
            }
        } else {
            createdAt.split("T")[0]
        }
    } catch (e: Exception) {
        createdAt.split("T")[0]
    }
}
