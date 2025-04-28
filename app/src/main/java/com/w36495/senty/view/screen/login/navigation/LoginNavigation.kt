package com.w36495.senty.view.screen.login.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.w36495.senty.view.screen.home.navigation.navigateToHome
import com.w36495.senty.view.screen.login.LoginRoute
import com.w36495.senty.view.screen.main.Route
import com.w36495.senty.view.screen.signup.SignUpRoute

fun NavController.navigateToLogin() {
    navigate(Route.Login)
}
fun NavController.navigateToSignUp() {
    navigate(Route.SignUp)
}

fun NavGraphBuilder.authNavGraph(
    padding: PaddingValues,
    navController: NavController,
) {
    composable<Route.Login> {
        LoginRoute(
            moveToHome = {
                val navOptions = NavOptions.Builder().apply {
                    this.setPopUpTo<Route.Login>(true)
                }.build()

                navController.navigateToHome(navOptions)
            },
            moveToSignUp = { navController.navigateToSignUp() },
        )
    }

    composable<Route.SignUp> {
        SignUpRoute(
            padding = padding,
            moveToLogin = { navController.navigateToLogin() },
            onBackPressed = { navController.popBackStack() },
        )
    }
}