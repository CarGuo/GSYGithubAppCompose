package com.shuyu.gsygithubappcompose.core.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shuyu.gsygithubappcompose.core.ui.LocalNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GSYTopAppBar(
    modifier: Modifier = Modifier, title: @Composable () -> Unit, showBackButton: Boolean = false
) {
    val navigator = LocalNavigator.current
    TopAppBar(
        title = title, navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = { navigator.back() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        }, modifier = modifier, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
