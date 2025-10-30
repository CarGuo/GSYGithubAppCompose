package com.shuyu.gsygithubappcompose.feature.trending

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shuyu.gsygithubappcompose.core.network.model.Repository
import com.shuyu.gsygithubappcompose.core.ui.components.AvatarImage
import com.shuyu.gsygithubappcompose.core.ui.components.GSYGeneralLoadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingScreen(
    viewModel: TrendingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()

    GSYGeneralLoadState(
        isLoading = uiState.isLoading && uiState.repositories.isEmpty(),
        error = uiState.error,
        retry = { viewModel.loadTrendingRepositories(initialLoad = true) }
    ) {
        PullToRefreshBox(
            state = pullRefreshState,
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refreshTrendingRepositories() }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(5.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.repositories) { repo ->
                    RepositoryItem(repository = repo)
                }
            }
        }
    }
}

@Composable
fun RepositoryItem(repository: Repository) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 8.dp)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarImage(
                    url = repository.owner.avatarUrl,
                    size = 40.dp
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = repository.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    repository.language?.let { lang ->
                        Text(
                            text = lang,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            repository.description?.let { desc ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "⭐",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = repository.stargazersCount.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "🔱",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = repository.forksCount.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
