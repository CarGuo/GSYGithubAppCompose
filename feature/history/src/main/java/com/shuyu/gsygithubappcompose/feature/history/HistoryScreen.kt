package com.shuyu.gsygithubappcompose.feature.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.ui.components.GSYTopAppBar
import com.shuyu.gsygithubappcompose.core.ui.components.RepositoryItem
import com.shuyu.gsygithubappcompose.core.ui.components.toRepositoryDisplayData

@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel = hiltViewModel()
) {
    val lazyPagingItems = historyViewModel.historyList.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            GSYTopAppBar(
                title = { Text(text = stringResource(id = R.string.history)) },
                showBackButton = true,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(5.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    count = lazyPagingItems.itemCount,
                    key = { lazyPagingItems.peek(it)?.id ?: "" }
                ) { it ->
                    lazyPagingItems[it]?.let {
                        RepositoryItem(
                            repoItem = it.toRepositoryDisplayData()
                        )
                    }
                }
            }
        }
    }
}
