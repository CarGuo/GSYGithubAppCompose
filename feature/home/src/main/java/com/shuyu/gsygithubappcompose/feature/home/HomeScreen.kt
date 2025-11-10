package com.shuyu.gsygithubappcompose.feature.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.ui.LocalNavigator
import com.shuyu.gsygithubappcompose.core.ui.GSYNavigator
import com.shuyu.gsygithubappcompose.core.ui.components.GSYTopAppBar
import com.shuyu.gsygithubappcompose.core.network.model.User
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class BottomNavItem(
    val route: String, val icon: ImageVector, val titleRes: Int
) {
    object Dynamic : BottomNavItem("dynamic", Icons.Default.Timeline, R.string.nav_dynamic)
    object Trending : BottomNavItem("trending", Icons.Default.Star, R.string.nav_trending)
    object Profile : BottomNavItem("profile", Icons.Default.Person, R.string.nav_profile)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    dynamicContent: @Composable () -> Unit,
    trendingContent: @Composable () -> Unit,
    profileContent: @Composable () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val navigator = LocalNavigator.current
    val uiState by homeViewModel.uiState.collectAsState()

    val items = listOf(
        BottomNavItem.Dynamic, BottomNavItem.Trending, BottomNavItem.Profile
    )

    val systemUiController = rememberSystemUiController()
    val primaryColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(Unit) {
        homeViewModel.logoutEvent.collectLatest {
            navigator.replace("login")
        }
    }

    SideEffect {
        systemUiController.setStatusBarColor(
            color = primaryColor, darkIcons = false
        )
        systemUiController.setNavigationBarColor(color = primaryColor)
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                user = uiState.user,
                onLogout = {
                    homeViewModel.logout()
                },
                onNavigationTo = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                    navigator.navigate(it)
                }
            )
        }
    ) {
        Scaffold(topBar = {
            GSYTopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Drawer",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    SearchActionIcon(navigator = navigator)
                }
            )
        }, bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.primary) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                item.icon, contentDescription = stringResource(id = item.titleRes)
                            )
                        },
                        label = { Text(stringResource(id = item.titleRes)) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.White.copy(alpha = 0.6f),
                            indicatorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }) { innerPadding ->
            HorizontalPager(
                state = pagerState, modifier = Modifier.padding(innerPadding)
            ) { page ->
                when (page) {
                    0 -> dynamicContent()
                    1 -> trendingContent()
                    2 -> profileContent()
                }
            }
        }
    }
}

@Composable
fun DrawerContent(
    user: User?,
    onLogout: () -> Unit,
    onNavigationTo: (String) -> Unit
) {
    val drawerItems = listOf(
        DrawerItem(
            "notification_route",
            R.string.menu_notification,
            Icons.Default.Notifications
        ),
        DrawerItem("my_events_route", R.string.menu_events, Icons.Default.Event),
        DrawerItem("about_route", R.string.menu_about, Icons.Default.Info)
    )

    ModalDrawerSheet {
        Column(modifier = Modifier.fillMaxSize()) {
            // User Info Header
            user?.let {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = it.avatarUrl,
                        contentDescription = "User Avatar",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it.name ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = it.login ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
            HorizontalDivider()

            // Menu Items
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(drawerItems) { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(stringResource(item.titleRes)) },
                        selected = false,
                        onClick = { onNavigationTo(item.route) }
                    )
                }
            }

            // Logout Button
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = stringResource(id = R.string.logout), color = Color.White)
            }
        }
    }
}

data class DrawerItem(val route: String, val titleRes: Int, val icon: ImageVector)


@Composable
fun SearchActionIcon(navigator: GSYNavigator) {
    IconButton(onClick = { navigator.navigate("search_route") }) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = stringResource(id = R.string.nav_search),
            tint = Color.White, // Set icon color to white
            modifier = Modifier.size(28.dp) // Increase icon size
        )
    }
}
