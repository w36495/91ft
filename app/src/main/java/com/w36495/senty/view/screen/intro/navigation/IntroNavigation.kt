package com.w36495.senty.view.screen.intro.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.w36495.senty.view.screen.home.navigation.navigateToHome
import com.w36495.senty.view.screen.intro.IntroRoute
import com.w36495.senty.view.screen.login.navigation.navigateToLogin
import com.w36495.senty.view.screen.main.Route

fun NavGraphBuilder.introNavGraph(
    navController: NavController,
) {
    composable<Route.Intro> {
        IntroRoute(
            moveToHome = {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(
                        route = Route.Intro,
                        inclusive = true
                    )
                    .build()
                navController.navigateToHome(navOptions)
            },
            moveToLogin = {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(
                        route = Route.Intro,
                        inclusive = true
                    )
                    .build()

                navController.navigateToLogin(navOptions)
            },
        )
    }
}