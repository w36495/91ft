package com.w36495.senty.view.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.w36495.senty.view.screen.LoginScreen
import com.w36495.senty.view.screen.SignupScreen
import com.w36495.senty.view.screen.home.AnniversaryScreen
import com.w36495.senty.view.screen.home.HomeScreen
import com.w36495.senty.view.screen.home.SettingScreen

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavigationItem.LOGIN.name,
    ) {
        composable(BottomNavigationItem.LOGIN.name) {
            LoginScreen(
                onSuccessLogin = { navController.navigate(BottomNavigationItem.HOME.name) },
                onClickSignUp = { navController.navigate(AccountNavigationItem.SIGNUP.name) }
            )
        }

        composable(AccountNavigationItem.SIGNUP.name) {
            SignupScreen(
                onBackPressed = { navController.navigateUp() },
                onSuccessSignup = { navController.navigate(AccountNavigationItem.LOGIN.name) },
            )
        }

//        nestedAccountGraph(navController)

        composable(BottomNavigationItem.HOME.name) {
            HomeScreen()
        }

        nestedFriendGraph(navController)

        composable(BottomNavigationItem.ANNIVERSARY.name) {
            AnniversaryScreen()
        }
        composable(BottomNavigationItem.SETTINGS.name) {
            SettingScreen(
                onClickGiftCategorySetting = {
                    navController.navigate(GiftNavigationItem.GIFT_CATEGORY.name)
                },
                onSuccessLogout = {
                    navController.navigate(BottomNavigationItem.LOGIN.name) {
                        launchSingleTop = true
                        popUpTo(BottomNavigationItem.LOGIN.name) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        nestedGiftGraph(navController)
    }
}