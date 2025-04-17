package com.w36495.senty.view.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.w36495.senty.view.screen.account.LoginScreen
import com.w36495.senty.view.screen.signup.SignUpRoute
import com.w36495.senty.view.screen.home.AnniversaryScreen
import com.w36495.senty.view.screen.home.HomeScreen
import com.w36495.senty.view.screen.home.SettingScreen

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AccountNavigationItem.LOGIN.name,
    ) {
        composable(AccountNavigationItem.LOGIN.name) {
            LoginScreen(
                onSuccessLogin = { navController.navigate(BottomNavigationItem.HOME.name) },
                onClickSignUp = { navController.navigate(AccountNavigationItem.SIGNUP.name) }
            )
        }

        composable(AccountNavigationItem.SIGNUP.name) {
            SignUpRoute(
                padding = PaddingValues(0.dp),
                moveToLogin = { navController.navigate(AccountNavigationItem.LOGIN.name) },
                onBackPressed = { navController.navigateUp() },
            )
        }

        composable(BottomNavigationItem.HOME.name) {
            HomeScreen(
                onClickGiftButton = {
                    navController.navigate(GiftNavigationItem.GIFT_LIST.name)
                },
                onClickGiftDetail = { giftId ->
                    navController.navigate(GiftNavigationItem.GIFT_DETAIL.name.plus("/$giftId"))
                }
            )
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
                    navController.navigate(AccountNavigationItem.LOGIN.name) {
                        launchSingleTop = true

                        popUpTo(AccountNavigationItem.LOGIN.name) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        nestedGiftGraph(navController)
    }
}

enum class AccountNavigationItem {
    LOGIN, SIGNUP,
}