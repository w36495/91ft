package com.w36495.senty.view.screen.setting.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.w36495.senty.view.screen.main.BottomTabRoute
import com.w36495.senty.view.screen.setting.SettingsRoute

fun NavController.navigateToSettings(navOptions: NavOptions) {
    navigate(BottomTabRoute.Settings, navOptions)
}

fun NavGraphBuilder.settingNavGraph(
    padding: PaddingValues,
    navController: NavController,
) {
    composable<BottomTabRoute.Settings> {
        SettingsRoute(
            padding = padding,
            moveToGiftCategories = {},
        )
    }
}