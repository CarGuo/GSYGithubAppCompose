package com.shuyu.gsygithubappcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.composable
import com.shuyu.gsygithubappcompose.core.ui.GSYNavHost
import com.shuyu.gsygithubappcompose.core.ui.theme.GSYGithubAppComposeTheme
import com.shuyu.gsygithubappcompose.feature.dynamic.DynamicScreen
import com.shuyu.gsygithubappcompose.feature.home.HomeScreen
import com.shuyu.gsygithubappcompose.feature.login.LoginScreen
import com.shuyu.gsygithubappcompose.feature.profile.PersonScreen
import com.shuyu.gsygithubappcompose.feature.profile.ProfileScreen
import com.shuyu.gsygithubappcompose.feature.search.SearchScreen
import com.shuyu.gsygithubappcompose.feature.trending.TrendingScreen
import com.shuyu.gsygithubappcompose.feature.welcome.WelcomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_GSYGithubAppCompose)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GSYGithubAppComposeTheme {
                val viewModel: MainViewModel = hiltViewModel()
                val isLoggedIn by viewModel.isLoggedIn.collectAsState(initial = false)

                GSYNavHost(
                    startDestination = "welcome"
                ) {
                    composable("welcome") {
                        WelcomeScreen(isLoggedIn = isLoggedIn)
                    }

                    composable("login") {
                        LoginScreen()
                    }

                    composable("home") {
                        HomeScreen(
                            dynamicContent = { DynamicScreen() },
                            trendingContent = { TrendingScreen() },
                            profileContent = { ProfileScreen() }
                        )
                    }

                    composable("search_route") {
                        SearchScreen()
                    }

                    composable("person/{username}") { backStackEntry ->
                        val username = backStackEntry.arguments?.getString("username") ?: ""
                        PersonScreen(
                            username = username
                        )
                    }
                }
            }
        }
    }
}
