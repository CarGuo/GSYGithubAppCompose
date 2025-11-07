package com.shuyu.gsygithubappcompose.feature.detail.info

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shuyu.gsygithubappcompose.core.ui.components.GSYGeneralLoadState
import com.shuyu.gsygithubappcompose.core.ui.components.GSYPullRefresh
import com.shuyu.gsygithubappcompose.core.ui.components.CommitItem
import com.shuyu.gsygithubappcompose.core.ui.components.EventItem
import com.shuyu.gsygithubappcompose.core.ui.components.GSYLoadingDialog
import com.shuyu.gsygithubappcompose.core.ui.components.SegmentedButton
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseScreen

@Composable
fun RepoDetailInfoScreen(
    owner: String,
    name: String,
    viewModel: RepoDetailInfoViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(owner, name) {
        viewModel.loadRepoDetailInfo(owner, name)
    }

    if (uiState.isLoadingDialog) {
        GSYLoadingDialog()
    }

    BaseScreen(viewModel = viewModel) {
        GSYGeneralLoadState(
            isLoading = uiState.isPageLoading && uiState.repoDetail == null,
            error = uiState.error,
            retry = { viewModel.refreshRepoDetailInfo() }
        ) {
            GSYPullRefresh(
                modifier = Modifier.fillMaxSize(),
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.refreshRepoDetailInfo() },
                isLoadMore = uiState.isLoadingMore,
                onLoadMore = { viewModel.loadMoreRepoDetailList() },
                hasMore = uiState.hasMore,
                itemCount = uiState.repoDetailList.size,
                loadMoreError = uiState.loadMoreError
            ) {
                uiState.repoDetail?.let { headerData ->
                    item {
                        RepositoryDetailInfoHeader(
                            repositoryDetailModel = headerData
                        )
                    }
                }

                item {
                    SegmentedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        items = listOf(
                            stringResource(R.string.events),
                            stringResource(R.string.commits)
                        ),
                        selectedIndex = when (uiState.selectedItemType) {
                            RepoDetailItemType.EVENT -> 0
                            RepoDetailItemType.COMMIT -> 1
                        },
                        onItemSelected = { index ->
                            when (index) {
                                0 -> viewModel.setSelectedItemType(RepoDetailItemType.EVENT)
                                1 -> viewModel.setSelectedItemType(RepoDetailItemType.COMMIT)
                            }
                        }
                    )
                }

                items(uiState.repoDetailList) { item ->
                    when (item) {
                        is RepoDetailListItem.EventItem -> {
                            EventItem(event = item.event)
                        }
                        is RepoDetailListItem.CommitItem -> {
                            CommitItem(commit = item.commit)
                        }
                    }
                }
            }
        }
    }
}
