package com.w36495.senty.view.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.w36495.senty.view.screen.LoginScreen
import com.w36495.senty.view.screen.SignupScreen

fun NavGraphBuilder.nestedAccountGraph(navController: NavController) {
    navigation(
        startDestination = AccountNavigationItem.LOGIN.name,
        route = AccountNavigationItem.ACCOUNT.name
    ) {
        composable(AccountNavigationItem.LOGIN.name) {
            LoginScreen(
                onBackPressed = { navController.navigateUp() },
                onSuccessLogin = { navController.navigate(BottomNavigationItem.HOME.name) },
            )
        }

        composable(AccountNavigationItem.SIGNUP.name) {
            SignupScreen(
                onBackPressed = { navController.navigateUp() },
                onSuccessSignup = { navController.navigate(AccountNavigationItem.LOGIN.name) },
            )
        }
    }
}

enum class AccountNavigationItem {
    ACCOUNT, LOGIN, SIGNUP
}