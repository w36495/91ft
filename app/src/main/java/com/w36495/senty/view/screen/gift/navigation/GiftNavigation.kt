package com.w36495.senty.view.screen.gift.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.w36495.senty.view.screen.friend.navigation.navigateToFriendAdd
import com.w36495.senty.view.screen.gift.edit.EditGiftRoute
import com.w36495.senty.view.screen.home.navigation.navigateToHome
import com.w36495.senty.view.screen.main.BottomTabRoute
import com.w36495.senty.view.screen.setting.navigation.navigateToGiftCategories

fun NavController.navigateToGiftAdd(navOptions: NavOptions) {
    navigate(BottomTabRoute.GiftAdd, navOptions)
}

fun NavGraphBuilder.giftNavGraph(
    padding: PaddingValues,
    navController: NavController,
) {
    composable<BottomTabRoute.GiftAdd> { 
        EditGiftRoute(
            padding = padding,
            moveToGiftCategories = { navController.navigateToGiftCategories() },
            moveToFriendAdd = { navController.navigateToFriendAdd() },
            moveToHome = {
                val navOption = NavOptions.Builder().apply {
                    setPopUpTo(
                        route = navController.graph.findStartDestination().id,
                        inclusive = false
                    )
                    setLaunchSingleTop(true)
                }.build()
                navController.navigateToHome(navOption)
             },
        )
    }
}