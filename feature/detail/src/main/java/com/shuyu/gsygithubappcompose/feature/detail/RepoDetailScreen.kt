package com.shuyu.gsygithubappcompose.feature.detail

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.BottomAppBar
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.ui.components.GSYLoadingDialog
import com.shuyu.gsygithubappcompose.core.ui.components.GSYMarkdownInputDialog
import com.shuyu.gsygithubappcompose.core.ui.components.GSYTopAppBar
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseScreen
import com.shuyu.gsygithubappcompose.feature.detail.file.RepoDetailFileScreen
import com.shuyu.gsygithubappcompose.feature.detail.info.RepoDetailInfoScreen
import com.shuyu.gsygithubappcompose.feature.detail.info.RepoDetailInfoViewModel
import com.shuyu.gsygithubappcompose.feature.detail.issue.RepoDetailIssueScreen
import com.shuyu.gsygithubappcompose.feature.detail.readme.RepoDetailReadmeScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.shuyu.gsygithubappcompose.feature.detail.info.RepoDetailInfoEvent

@HiltViewModel
class RepoDetailViewModel @Inject constructor() : ViewModel() {
    // ViewModel logic will be added later
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RepoDetailScreen(
    modifier: Modifier = Modifier,
    repoDetailViewModel: RepoDetailViewModel = hiltViewModel(),
    repoDetailInfoViewModel: RepoDetailInfoViewModel = hiltViewModel(),
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
    val context = LocalContext.current

    val uiState by repoDetailInfoViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        repoDetailInfoViewModel.events.collect { event ->
            when (event) {
                is RepoDetailInfoEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is RepoDetailInfoEvent.NavigateToIssueScreenAndRefresh -> {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(2) // Navigate to Issue tab
                    }
                }
                is RepoDetailInfoEvent.DismissMarkdownDialog -> {
                    // Handled by uiState.showMarkdownDialog = false
                }
            }
        }
    }

    if (uiState.showMarkdownDialog) {
        GSYMarkdownInputDialog(
            dialogTitle = stringResource(id = R.string.create_issue),
            onDismissRequest = { repoDetailInfoViewModel.dismissMarkdownDialog() },
            onConfirm = { title, content ->
                repoDetailInfoViewModel.createIssue(userName, repoName, title, content)
            }
        )
    }

    if (uiState.isLoadingDialog) {
        GSYLoadingDialog()
    }

    BaseScreen(viewModel = repoDetailInfoViewModel) {
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
                        val starIcon =
                            if (uiState.repoDetail?.viewerHasStarred == true) Icons.Filled.Star else Icons.Outlined.StarBorder
                        val starText =
                            if (uiState.repoDetail?.viewerHasStarred == true) stringResource(R.string.repo_detail_action_unstar) else stringResource(
                                R.string.repo_detail_action_star
                            )

                        TextButton(
                            onClick = { repoDetailInfoViewModel.starRepo(userName, repoName) },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Row {
                                Text(
                                    starText,
                                    modifier = Modifier.padding(end = 5.dp)
                                )
                                Icon(
                                    starIcon,
                                    contentDescription = starText
                                )
                            }
                        }

                        val watchIcon =
                            if (uiState.repoDetail?.viewerSubscription == "SUBSCRIBED") Icons.Filled.Visibility else Icons.Outlined.VisibilityOff
                        val watchText =
                            if (uiState.repoDetail?.viewerSubscription == "SUBSCRIBED") stringResource(R.string.repo_detail_action_unwatch) else stringResource(
                                R.string.repo_detail_action_watch
                            )

                        TextButton(
                            onClick = { repoDetailInfoViewModel.watchRepo(userName, repoName) },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Row {
                                Text(
                                    watchText,
                                    modifier = Modifier.padding(end = 5.dp)
                                )
                                Icon(
                                    watchIcon,
                                    contentDescription = watchText
                                )
                            }
                        }
                        TextButton(
                            onClick = { repoDetailInfoViewModel.forkRepo(userName, repoName) },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Row {
                                Text(
                                    stringResource(R.string.repo_detail_action_fork),
                                    modifier = Modifier.padding(end = 5.dp)
                                )
                                Icon(
                                    Icons.AutoMirrored.Filled.CallSplit,
                                    contentDescription = stringResource(R.string.repo_detail_action_fork)
                                )
                            }
                        }
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        repoDetailInfoViewModel.showMarkdownDialog(true)
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
                    0 -> RepoDetailInfoScreen(userName, repoName, repoDetailInfoViewModel)
                    1 -> RepoDetailReadmeScreen(userName, repoName, null)
                    2 -> RepoDetailIssueScreen(userName, repoName)
                    3 -> RepoDetailFileScreen(userName, repoName)
                }
            }
        }
    }
}
