package com.shuyu.gsygithubappcompose.core.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

/**
 * Navigator to make navigation easier
 */
class GSYNavigator(val navController: NavController) {

    fun back(
        route: String? = null,
        inclusive: Boolean = false,
    ) {
        if (route != null) {
            navController.popBackStack(route, inclusive)
        } else {
            navController.popBackStack()
        }
    }

    fun navigate(route: String, builder: NavOptionsBuilder.() -> Unit = {}) = navController.navigate(route, builder)

    /**
     * Navigates to a new root destination, clearing the back stack.
     */
    fun replace(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }
}

val LocalNavigator = compositionLocalOf<GSYNavigator> {
    error("No LocalNavigator given")
}
