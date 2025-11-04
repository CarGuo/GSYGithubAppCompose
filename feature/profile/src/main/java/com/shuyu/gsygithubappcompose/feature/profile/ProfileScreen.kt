package com.shuyu.gsygithubappcompose.feature.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onImageClick: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.doInitialLoad()
    }

    ProfileContent(
        uiState = uiState,
        onRefresh = { viewModel.refresh() },
        onLoadMore = { viewModel.loadMore() },
        onImageClick = onImageClick
    )
}
