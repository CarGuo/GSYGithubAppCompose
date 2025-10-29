package com.shuyu.gsygithubappcompose.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val titleRes: Int
) {
    object Dynamic : BottomNavItem("dynamic", Icons.Default.Timeline, com.shuyu.gsygithubappcompose.R.string.nav_dynamic)
    object Trending : BottomNavItem("trending", Icons.Default.Star, com.shuyu.gsygithubappcompose.R.string.nav_trending)
    object Profile : BottomNavItem("profile", Icons.Default.Person, com.shuyu.gsygithubappcompose.R.string.nav_profile)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    dynamicContent: @Composable () -> Unit,
    trendingContent: @Composable () -> Unit,
    profileContent: @Composable () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    val items = listOf(
        BottomNavItem.Dynamic,
        BottomNavItem.Trending,
        BottomNavItem.Profile
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = com.shuyu.gsygithubappcompose.R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = stringResource(id = item.titleRes)) },
                        label = { Text(stringResource(id = item.titleRes)) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> dynamicContent()
                1 -> trendingContent()
                2 -> profileContent()
            }
        }
    }
}
