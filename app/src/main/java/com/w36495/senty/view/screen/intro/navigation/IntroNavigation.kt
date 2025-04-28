package com.w36495.senty.view.screen.intro.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.w36495.senty.view.screen.intro.IntroRoute
import com.w36495.senty.view.screen.login.navigation.navigateToLogin
import com.w36495.senty.view.screen.main.Route

fun NavGraphBuilder.introNavGraph(
    navController: NavController,
    moveToHome: () -> Unit,
) {
    composable<Route.Intro> {
        IntroRoute(
            moveToHome = moveToHome,
            moveToLogin = { navController.navigateToLogin() },
        )
    }
}