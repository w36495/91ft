package com.w36495.senty.view.screen.home.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.w36495.senty.view.screen.gift.navigation.navigateToGiftDetail
import com.w36495.senty.view.screen.gift.navigation.navigateToGifts
import com.w36495.senty.view.screen.home.HomeRoute
import com.w36495.senty.view.screen.main.BottomTabRoute

fun NavController.navigateToHome(navOption: NavOptions) {
    navigate(BottomTabRoute.Home, navOption)
}

fun NavGraphBuilder.homeNavGraph(
    padding: PaddingValues,
    navController: NavController,
) {
    composable<BottomTabRoute.Home> {
        HomeRoute(
            padding = padding,
            moveToGifts = { navController.navigateToGifts() },
            moveToGiftDetail = { navController.navigateToGiftDetail(it) },
        )
    }
}