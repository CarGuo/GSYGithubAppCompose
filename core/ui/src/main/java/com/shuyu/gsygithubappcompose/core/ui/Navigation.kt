package com.shuyu.gsygithubappcompose.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

/**
 * Custom NavHost to make navigation easier
 */
@Composable
fun GSYNavHost(
    modifier: Modifier = Modifier,
    startDestination: String,
    navController: NavHostController = rememberNavController(),
    builder: NavGraphBuilder.() -> Unit
) {
    val navigator = GSYNavigator(navController)
    CompositionLocalProvider(
        LocalNavigator provides navigator
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier,
            builder = builder
        )
    }
}
