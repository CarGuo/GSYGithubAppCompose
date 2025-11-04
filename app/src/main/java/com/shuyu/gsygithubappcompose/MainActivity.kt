package com.shuyu.gsygithubappcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shuyu.gsygithubappcompose.core.ui.theme.GSYGithubAppComposeTheme
import com.shuyu.gsygithubappcompose.feature.dynamic.DynamicScreen
import com.shuyu.gsygithubappcompose.feature.home.HomeScreen
import com.shuyu.gsygithubappcompose.feature.login.LoginScreen
import com.shuyu.gsygithubappcompose.feature.profile.PersonScreen
import com.shuyu.gsygithubappcompose.feature.profile.ProfileScreen
import com.shuyu.gsygithubappcompose.feature.trending.TrendingScreen
import com.shuyu.gsygithubappcompose.feature.welcome.WelcomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GSYGithubAppComposeTheme {
                val navController = rememberNavController()
                val viewModel: MainViewModel = hiltViewModel()
                val isLoggedIn by viewModel.isLoggedIn.collectAsState(initial = false)

                NavHost(
                    navController = navController,
                    startDestination = "welcome"
                ) {
                    composable("welcome") {
                        WelcomeScreen(
                            onNavigateToLogin = {
                                navController.navigate("login") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                            },
                            onNavigateToHome = {
                                navController.navigate("home") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                            },
                            isLoggedIn = isLoggedIn
                        )
                    }

                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("home") {
                        HomeScreen(
                            dynamicContent = { DynamicScreen(onImageClick = { navController.navigate("person/$it") }) },
                            trendingContent = { TrendingScreen(onImageClick = { navController.navigate("person/$it") }) },
                            profileContent = {
                                ProfileScreen(
                                    onLogout = {
                                        navController.navigate("login") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    },
                                    onImageClick = {
                                        navController.navigate("person/$it")
                                    }
                                )
                            }
                        )
                    }

                    composable("person/{username}") { backStackEntry ->
                        val username = backStackEntry.arguments?.getString("username") ?: ""
                        PersonScreen(
                            username = username,
                            onBack = { navController.popBackStack() },
                            onImageClick = {
                                navController.navigate("person/$it")
                            }
                        )
                    }
                }
            }
        }
    }
}