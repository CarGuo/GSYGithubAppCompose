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
import com.shuyu.gsygithubappcompose.feature.code.FileCodeViewScreen
import com.shuyu.gsygithubappcompose.feature.detail.RepoDetailScreen
import com.shuyu.gsygithubappcompose.feature.dynamic.DynamicScreen
import com.shuyu.gsygithubappcompose.feature.home.HomeScreen
import com.shuyu.gsygithubappcompose.feature.issue.IssueScreen
import com.shuyu.gsygithubappcompose.feature.list.ListScreen
import com.shuyu.gsygithubappcompose.feature.login.LoginScreen
import com.shuyu.gsygithubappcompose.feature.notification.NotificationScreen // Import NotificationScreen
import com.shuyu.gsygithubappcompose.feature.profile.PersonScreen
import com.shuyu.gsygithubappcompose.feature.profile.ProfileScreen
import com.shuyu.gsygithubappcompose.feature.push.PushDetailScreen
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
                            versionName = BuildConfig.VERSION_NAME,
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

                    composable("repo_detail/{userName}/{repoName}") { backStackEntry ->
                        val userName = backStackEntry.arguments?.getString("userName") ?: ""
                        val repoName = backStackEntry.arguments?.getString("repoName") ?: ""
                        RepoDetailScreen(
                            userName = userName,
                            repoName = repoName
                        )
                    }

                    composable("file_code/{owner}/{repo}/{path}?sha={sha}&branch={branch}") { backStackEntry ->
                        val owner = backStackEntry.arguments?.getString("owner") ?: ""
                        val repo = backStackEntry.arguments?.getString("repo") ?: ""
                        val sha = backStackEntry.arguments?.getString("sha")
                        val branch = backStackEntry.arguments?.getString("branch")
                        val path = backStackEntry.arguments?.getString("path") ?: ""
                        FileCodeViewScreen(
                            owner = owner,
                            repo = repo,
                            path = path,
                            sha = sha,
                            branch = branch,
                        )
                    }

                    composable("issue_detail/{owner}/{repoName}/{issueNumber}") { backStackEntry ->
                        val owner = backStackEntry.arguments?.getString("owner") ?: ""
                        val repoName = backStackEntry.arguments?.getString("repoName") ?: ""
                        val issueNumber = backStackEntry.arguments?.getString("issueNumber")?.toIntOrNull() ?: 0
                        IssueScreen(
                            owner = owner,
                            repoName = repoName,
                            issueNumber = issueNumber
                        )
                    }

                    composable("push_detail/{owner}/{repoName}/{sha}") { backStackEntry ->
                        val owner = backStackEntry.arguments?.getString("owner") ?: ""
                        val repoName = backStackEntry.arguments?.getString("repoName") ?: ""
                        val sha = backStackEntry.arguments?.getString("sha") ?: ""
                        PushDetailScreen()
                    }

                    composable("list_screen/{listType}/{username}/{repoName}") { backStackEntry ->
                        val listType = backStackEntry.arguments?.getString("listType") ?: ""
                        val username = backStackEntry.arguments?.getString("username") ?: ""
                        val repoName = backStackEntry.arguments?.getString("repoName") ?: ""
                        ListScreen(
                            listType = listType,
                            userName = username,
                            repoName = repoName
                        )
                    }

                    // Add NotificationScreen route
                    composable("notification") {
                        NotificationScreen()
                    }
                }
            }
        }
    }
}
