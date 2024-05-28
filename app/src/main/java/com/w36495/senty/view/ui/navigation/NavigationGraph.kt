package com.w36495.senty.view.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.w36495.senty.view.screen.MainScreen
import com.w36495.senty.view.screen.home.AnniversaryScreen
import com.w36495.senty.view.screen.home.HomeScreen
import com.w36495.senty.view.screen.home.SettingScreen

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavigationItem.MAIN.name,
    ) {
        composable(BottomNavigationItem.MAIN.name) {
            MainScreen(
                onLoginClick = { navController.navigate(AccountNavigationItem.LOGIN.name) },
                onSignUpClick = { navController.navigate(AccountNavigationItem.SIGNUP.name) }
            )
        }

        nestedAccountGraph(navController)

        composable(BottomNavigationItem.HOME.name) {
            HomeScreen()
        }

        nestedFriendGraph(navController)

        composable(BottomNavigationItem.ANNIVERSARY.name) {

        }
        composable(BottomNavigationItem.SETTINGS.name) {
            SettingScreen(
                onClickGiftCategorySetting = {
                    navController.navigate(GiftNavigationItem.GIFT_CATEGORY.name)
                }
            )
        }

        nestedGiftGraph(navController)
    }
}