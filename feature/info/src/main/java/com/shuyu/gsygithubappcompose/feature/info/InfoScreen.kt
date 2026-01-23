
package com.shuyu.gsygithubappcompose.feature.info

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shuyu.gsygithubappcompose.core.ui.components.GSYTopAppBar
import com.shuyu.gsygithubappcompose.core.ui.components.GSYCardItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Group
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import com.shuyu.gsygithubappcompose.core.ui.components.GSYPullRefresh
import com.shuyu.gsygithubappcompose.core.ui.components.GSYGeneralLoadState
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseScreen
import com.shuyu.gsygithubappcompose.core.common.R

@Composable
fun InfoScreen(viewModel: InfoViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var editingField by remember { mutableStateOf("") }
    var editingValue by remember { mutableStateOf("") }
    var dialogTitle by remember { mutableStateOf("") }

    // Pre-resolve string resources for dialog titles
    val nameTitle = stringResource(id = R.string.user_profile_name)
    val emailTitle = stringResource(id = R.string.user_profile_email)
    val linkTitle = stringResource(id = R.string.user_profile_link)
    val orgTitle = stringResource(id = R.string.user_profile_org)
    val locationTitle = stringResource(id = R.string.user_profile_location)
    val bioTitle = stringResource(id = R.string.user_profile_info)
    val confirmText = stringResource(id = R.string.confirm)
    val cancelText = stringResource(id = R.string.cancel)
    val editFieldText = stringResource(id = R.string.edit_field, dialogTitle)


    BaseScreen(viewModel = viewModel) {
        Scaffold(
            topBar = {
                GSYTopAppBar(
                    title = { Text(text = stringResource(id = R.string.home_user_info)) },
                    showBackButton = true
                )
            }
        ) { paddingValues ->
            GSYGeneralLoadState(
                isLoading = uiState.isPageLoading && uiState.user == null,
                error = uiState.error,
                retry = { viewModel.refresh() }
            ) {
                GSYPullRefresh(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { viewModel.refresh() },
                    isLoadMore = false,
                    onLoadMore = { },
                    hasMore = false,
                    itemCount = if (uiState.user != null) 1 else 0,
                    loadMoreError = false,
                    contentPadding = PaddingValues(5.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(paddingValues)
                ) {
                    item {
                        uiState.user?.let { user ->
                            Column {
                                InfoItem(
                                    leftIcon = Icons.Filled.Info,
                                    title = nameTitle,
                                    value = user.name ?: "---",
                                    onClick = {
                                        editingField = "name"
                                        editingValue = user.name ?: ""
                                        dialogTitle = nameTitle
                                        showEditDialog = true
                                    }
                                )
                                InfoItem(
                                    leftIcon = Icons.Filled.Email,
                                    title = emailTitle,
                                    value = user.email ?: "---",
                                    onClick = {
                                        editingField = "email"
                                        editingValue = user.email ?: ""
                                        dialogTitle = emailTitle
                                        showEditDialog = true
                                    }
                                )
                                InfoItem(
                                    leftIcon = Icons.Filled.Link,
                                    title = linkTitle,
                                    value = user.blog ?: "---",
                                    onClick = {
                                        editingField = "blog"
                                        editingValue = user.blog ?: ""
                                        dialogTitle = linkTitle
                                        showEditDialog = true
                                    }
                                )
                                InfoItem(
                                    leftIcon = Icons.Filled.Group,
                                    title = orgTitle,
                                    value = user.company ?: "---",
                                    onClick = {
                                        editingField = "company"
                                        editingValue = user.company ?: ""
                                        dialogTitle = orgTitle
                                        showEditDialog = true
                                    }
                                )
                                InfoItem(
                                    leftIcon = Icons.Filled.LocationOn,
                                    title = locationTitle,
                                    value = user.location ?: "---",
                                    onClick = {
                                        editingField = "location"
                                        editingValue = user.location ?: ""
                                        dialogTitle = locationTitle
                                        showEditDialog = true
                                    }
                                )
                                InfoItem(
                                    leftIcon = Icons.Filled.Message,
                                    title = bioTitle,
                                    value = user.bio ?: "---",
                                    onClick = {
                                        editingField = "bio"
                                        editingValue = user.bio ?: ""
                                        dialogTitle = bioTitle
                                        showEditDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (showEditDialog) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text(text = stringResource(id = R.string.edit_field, dialogTitle)) },
                    text = {
                        TextField(
                            value = editingValue,
                            onValueChange = { editingValue = it },
                            label = { Text(dialogTitle) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.updateUser(mapOf(editingField to editingValue))
                                showEditDialog = false
                            }
                        ) {
                            Text(confirmText)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showEditDialog = false }
                        ) {
                            Text(cancelText)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun InfoItem(leftIcon: ImageVector, title: String, value: String, onClick: () -> Unit) {
    GSYCardItem(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Icon(imageVector = leftIcon, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = title)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = value)
        }
    }
}
