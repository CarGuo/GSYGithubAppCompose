package com.shuyu.gsygithubappcompose.feature.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.network.model.User
import com.shuyu.gsygithubappcompose.core.ui.LocalNavigator
import com.shuyu.gsygithubappcompose.core.ui.components.GSYPullRefresh
import com.shuyu.gsygithubappcompose.core.ui.components.GSYTopAppBar
import com.shuyu.gsygithubappcompose.core.ui.components.RepoItemDisplayData
import com.shuyu.gsygithubappcompose.core.ui.components.RepositoryItem
import com.shuyu.gsygithubappcompose.core.ui.components.UserItem

@Composable
fun ListScreen(
    userName: String,
    repoName: String,
    listType: String,
    listViewModel: ListViewModel = hiltViewModel()
) {
    val uiState by listViewModel.uiState.collectAsState()
    val navigator = LocalNavigator.current

    listViewModel.loadData(userName, repoName, listType)

    Scaffold(
        topBar = {
            GSYTopAppBar(
                title = { Text(text = uiState.title) },
                showBackButton = true,
            )
        }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (uiState.isPageLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.error ?: stringResource(id = R.string.error_unknown))
                }
            } else {
                GSYPullRefresh(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = {
                        listViewModel.refresh()
                    },
                    isLoadMore = uiState.isLoadingMore,
                    onLoadMore = {
                        listViewModel.loadMore()
                    },
                    hasMore = uiState.hasMore,
                    itemCount = uiState.list.size,
                    contentPadding = PaddingValues(5.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.list.size) { index ->
                        val item = uiState.list[index]
                        when (item) {
                            is RepoItemDisplayData -> {
                                RepositoryItem(
                                    repoItem = item
                                )
                            }

                            is User -> {
                                UserItem(
                                    user = item
                                ) {
                                    navigator.navigate("person/${item.login}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
