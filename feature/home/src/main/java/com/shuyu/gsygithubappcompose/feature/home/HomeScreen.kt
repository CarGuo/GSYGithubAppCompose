package com.shuyu.gsygithubappcompose.feature.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.ui.LocalNavigator
import com.shuyu.gsygithubappcompose.core.ui.GSYNavigator
import com.shuyu.gsygithubappcompose.core.ui.components.GSYLoadingDialog
import com.shuyu.gsygithubappcompose.core.ui.components.GSYMarkdownInputDialog
import com.shuyu.gsygithubappcompose.core.ui.components.GSYTopAppBar
import com.shuyu.gsygithubappcompose.core.network.model.User
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseScreen
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
    versionName: String,
    dynamicContent: @Composable () -> Unit,
    trendingContent: @Composable () -> Unit,
    profileContent: @Composable () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val navigator = LocalNavigator.current
    val uiState by homeViewModel.uiState.collectAsState()
    var openAboutDialog by remember { mutableStateOf(false) }
    var openFeedbackDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current


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

    if (openAboutDialog) {
        AlertDialog(onDismissRequest = {
            openAboutDialog = false
        }, title = {
            Text(text = stringResource(id = R.string.app_name))
        }, text = {
            Text(text = "Version: $versionName")
        }, confirmButton = {
            Button(
                onClick = {
                    openAboutDialog = false
                }) {
                Text("OK")
            }
        })
    }

    if (openFeedbackDialog) {
        GSYMarkdownInputDialog(
            title = stringResource(id = R.string.menu_feedback),
            textHint = stringResource(id = R.string.feedback_hint),
            onDismiss = {
                openFeedbackDialog = false
            },
            onConfirm = { title, content ->
                coroutineScope.launch {
                    val success = homeViewModel.submitFeedback(title, content)
                    if (success) {
                        openFeedbackDialog = false
                    }
                }
            })
    }

    if (uiState.showUpdateDialog && uiState.latestRelease != null) {
        AlertDialog(onDismissRequest = {
            homeViewModel.dismissUpdateDialog()
        }, title = {
            Text(text = stringResource(id = R.string.update_dialog_title))
        }, text = {
            Text(
                text = stringResource(
                    id = R.string.update_dialog_message,
                    uiState.latestRelease?.tagName ?: "",
                    uiState.latestRelease?.body ?: ""
                )
            )
        }, confirmButton = {
            Button(
                onClick = {
                    uiState.latestRelease?.htmlUrl?.let { url ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                    homeViewModel.dismissUpdateDialog()
                }) {
                Text(stringResource(id = R.string.app_ok))
            }
        }, dismissButton = {
            Button(onClick = { homeViewModel.dismissUpdateDialog() }) {
                Text(stringResource(id = R.string.app_cancel))
            }
        })
    }

    if (uiState.isLoadingDialog) {
        GSYLoadingDialog()
    }

    BaseScreen(viewModel = homeViewModel) {
        ModalNavigationDrawer(
            drawerState = drawerState, drawerContent = {
                DrawerContent(user = uiState.user, onLogout = {
                    homeViewModel.logout()
                }, onItemSelected = {
                    when (it) {
                        "about" -> {
                            openAboutDialog = true
                        }

                        "feedback" -> {
                            openFeedbackDialog = true
                        }

                        "personal_info" -> {
                            // Handle personal info
                        }

                        "language" -> {
                            // Handle language
                        }

                        "check_update" -> {
                            homeViewModel.checkUpdate(versionName, true)
                        }
                    }
                    coroutineScope.launch {
                        drawerState.close()
                    }
                })
            }) {
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
                    })
            }, bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.primary) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = {
                            Icon(
                                item.icon,
                                contentDescription = stringResource(id = item.titleRes)
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
}

@Composable
fun DrawerContent(
    user: User?, onLogout: () -> Unit, onItemSelected: (String) -> Unit
) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.menu_feedback),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemSelected("feedback") }
                        .padding(16.dp))
                Text(
                    text = stringResource(id = R.string.menu_personal_info),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemSelected("personal_info") }
                        .padding(16.dp))
                Text(
                    text = stringResource(id = R.string.menu_language),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemSelected("language") }
                        .padding(16.dp))
                Text(
                    text = stringResource(id = R.string.menu_check_update),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemSelected("check_update") }
                        .padding(16.dp))
                Text(
                    text = stringResource(id = R.string.menu_about),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemSelected("about") }
                        .padding(16.dp))
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
