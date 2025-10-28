package com.shuyu.gsygithubappcompose.feature.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String
) {
    object Dynamic : BottomNavItem("dynamic", Icons.Default.Timeline, "动态")
    object Trending : BottomNavItem("trending", Icons.Default.Star, "趋势")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "我的")
}

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
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> dynamicContent()
            1 -> trendingContent()
            2 -> profileContent()
        }
    }
}
