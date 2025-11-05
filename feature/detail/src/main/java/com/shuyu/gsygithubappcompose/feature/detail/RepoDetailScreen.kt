package com.shuyu.gsygithubappcompose.feature.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.ui.components.GSYTopAppBar
import com.shuyu.gsygithubappcompose.feature.detail.file.RepoDetailFileScreen
import com.shuyu.gsygithubappcompose.feature.detail.info.RepoDetailInfoScreen
import com.shuyu.gsygithubappcompose.feature.detail.issue.RepoDetailIssueScreen
import com.shuyu.gsygithubappcompose.feature.detail.readme.RepoDetailReadmeScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

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
        }, modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState, modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (it) {
                0 -> RepoDetailInfoScreen(userName, repoName)
                1 -> RepoDetailReadmeScreen()
                2 -> RepoDetailIssueScreen()
                3 -> RepoDetailFileScreen(userName, repoName)
            }
        }
    }
}
