package com.shuyu.gsygithubappcompose.feature.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shuyu.gsygithubappcompose.core.ui.components.GSYTopAppBar
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseScreen

@Composable
fun PersonScreen(
    modifier: Modifier = Modifier,
    username: String,
    viewModel: PersonViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.doInitialLoad()
    }

    BaseScreen(viewModel = viewModel) {
        Scaffold(
            modifier = modifier, topBar = {
                GSYTopAppBar(
                    title = { Text(text = username) },
                    showBackButton = true
                )
            }) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                ProfileContent(
                    uiState = uiState,
                    onRefresh = { viewModel.refresh() },
                    onLoadMore = { viewModel.loadMore() }
                )
            }
        }
    }
}
