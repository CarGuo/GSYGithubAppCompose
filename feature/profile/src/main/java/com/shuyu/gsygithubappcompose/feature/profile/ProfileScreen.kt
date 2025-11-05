package com.shuyu.gsygithubappcompose.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shuyu.gsygithubappcompose.core.ui.LocalNavigator

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigator = LocalNavigator.current

    LaunchedEffect(Unit) {
        viewModel.doInitialLoad()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ProfileContent(
            uiState = uiState,
            onRefresh = { viewModel.refresh() },
            onLoadMore = { viewModel.loadMore() }
        )
        Button(
            onClick = { viewModel.logout(navigator) },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Logout")
        }
    }
}
