package com.shuyu.gsygithubappcompose.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shuyu.gsygithubappcompose.core.ui.LocalNavigator
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseScreen

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigator = LocalNavigator.current

    LaunchedEffect(Unit) {
        viewModel.doInitialLoad()
    }

    BaseScreen(viewModel = viewModel) {
        Column(modifier = Modifier.fillMaxSize()) {
            ProfileContent(
                uiState = uiState,
                onRefresh = { viewModel.refresh() },
                onLoadMore = { viewModel.loadMore() },
                notificationAction = {
                    IconButton(onClick = {
                        navigator.navigate("notification")

                    }, modifier = Modifier.padding(bottom = 15.dp)) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notification",
                            tint = if (uiState.notificationCount > 0) {
                                Color.Blue
                            } else {
                                Color.White
                            }
                        )
                    }
                }
            )
            Button(
                onClick = { viewModel.logout(navigator) },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Logout")
            }
        }
    }
}
