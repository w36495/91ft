package com.w36495.senty.view.screen.setting.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.w36495.senty.view.screen.gift.category.GiftCategoriesRoute
import com.w36495.senty.view.screen.login.navigation.navigateToLogin
import com.w36495.senty.view.screen.main.BottomTabRoute
import com.w36495.senty.view.screen.main.Route
import com.w36495.senty.view.screen.setting.SettingsRoute

fun NavController.navigateToSettings(navOptions: NavOptions) {
    navigate(BottomTabRoute.Settings, navOptions)
}

fun NavController.navigateToGiftCategories() {
    navigate(Route.GiftCategories)
}

fun NavGraphBuilder.settingNavGraph(
    padding: PaddingValues,
    navController: NavController,
) {
    composable<BottomTabRoute.Settings> {
        SettingsRoute(
            padding = padding,
            moveToLogin = {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(
                        route = BottomTabRoute.Home,
                        inclusive = true
                    ).build()

                navController.navigateToLogin(navOptions)
            },
            moveToGiftCategories = { navController.navigateToGiftCategories() },
        )
    }

    composable<Route.GiftCategories> {
        GiftCategoriesRoute(
            onBackPressed = { navController.popBackStack() },
        )
    }
}