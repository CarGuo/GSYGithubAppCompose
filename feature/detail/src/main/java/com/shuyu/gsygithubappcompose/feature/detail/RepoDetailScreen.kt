package com.shuyu.gsygithubappcompose.feature.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.ui.components.GSYTopAppBar
import com.shuyu.gsygithubappcompose.feature.detail.file.RepoDetailFileScreen
import com.shuyu.gsygithubappcompose.feature.detail.info.RepoDetailInfoScreen
import com.shuyu.gsygithubappcompose.feature.detail.issue.RepoDetailIssueScreen
import com.shuyu.gsygithubappcompose.feature.detail.readme.RepoDetailReadmeScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class RepoDetailViewModel @Inject constructor() : ViewModel() {
    // ViewModel logic will be added later
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RepoDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: RepoDetailViewModel = hiltViewModel(),
    userName: String,
    repoName: String
) {
    val tabs = listOf(
        stringResource(id = R.string.repo_detail_tab_info),
        stringResource(id = R.string.repo_detail_tab_readme),
        stringResource(id = R.string.repo_detail_tab_issue),
        stringResource(id = R.string.repo_detail_tab_file)
    )
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Column {
                GSYTopAppBar(title = { Text("$userName/$repoName") }, showBackButton = true)
                SecondaryTabRow(selectedTabIndex = pagerState.currentPage) {
                    tabs.forEachIndexed { index, title ->
                        Tab(selected = pagerState.currentPage == index, onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }, text = { Text(text = title) })
                    }
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White, contentColor = Color.White,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp, Alignment.Start
                    ), // Group to the left with spacing
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RepoDetailActionButton(
                        text = stringResource(R.string.repo_detail_action_star),
                        icon = Icons.Filled.Star,
                        onClick = { /* TODO: Handle star click */ }
                    )
                    RepoDetailActionButton(
                        text = stringResource(R.string.repo_detail_action_watch),
                        icon = Icons.Filled.Visibility,
                        onClick = { /* TODO: Handle watch click */ }
                    )
                    RepoDetailActionButton(
                        text = stringResource(R.string.repo_detail_action_fork),
                        icon = Icons.Filled.CallSplit,
                        onClick = { /* TODO: Handle fork click */ }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    /* TODO: Handle create issue click */
                }) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState, modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (it) {
                0 -> RepoDetailInfoScreen(userName, repoName)
                1 -> RepoDetailReadmeScreen(userName, repoName, null)
                2 -> RepoDetailIssueScreen(userName, repoName)
                3 -> RepoDetailFileScreen(userName, repoName)
            }
        }
    }
}

@Composable
private fun RepoDetailActionButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text,
                modifier = Modifier.padding(end = 5.dp)
            )
            Icon(
                icon,
                contentDescription = text
            )
        }
    }
}
