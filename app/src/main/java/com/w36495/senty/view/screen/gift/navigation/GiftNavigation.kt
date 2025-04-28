package com.w36495.senty.view.screen.gift.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.w36495.senty.view.screen.friend.navigation.navigateToFriendAdd
import com.w36495.senty.view.screen.gift.detail.GiftDetailRoute
import com.w36495.senty.view.screen.gift.edit.EditGiftRoute
import com.w36495.senty.view.screen.gift.list.GiftRoute
import com.w36495.senty.view.screen.main.BottomTabRoute
import com.w36495.senty.view.screen.main.Route
import com.w36495.senty.view.screen.setting.navigation.navigateToGiftCategories

fun NavController.navigateToGiftAdd(navOptions: NavOptions) {
    navigate(BottomTabRoute.GiftAdd, navOptions)
}

fun NavController.navigateToGifts() {
    navigate(Route.Gifts)
}

fun NavController.navigateToGiftDetail(giftId: String) {
    navigate(Route.GiftDetail(giftId))
}

fun NavController.navigateToGiftEdit(giftId: String) {
    navigate(Route.GiftEdit(giftId))
}

fun NavGraphBuilder.giftNavGraph(
    padding: PaddingValues,
    navController: NavController,
    moveToHome: () -> Unit,
) {
    composable<BottomTabRoute.GiftAdd> { 
        EditGiftRoute(
            padding = padding,
            moveToGiftCategories = { navController.navigateToGiftCategories() },
            moveToFriendAdd = { navController.navigateToFriendAdd() },
            moveToHome = { moveToHome() },
        )
    }

    composable<Route.Gifts> {
        GiftRoute(
            padding = padding,
            moveToGiftDetail = { navController.navigateToGiftDetail(it) },
            moveToGiftCategories = { navController.navigateToGiftCategories() },
            onBackPressed = { navController.popBackStack() },
        )
    }

    composable<Route.GiftDetail> { navBackStackEntry ->
        val giftId = navBackStackEntry.toRoute<Route.GiftDetail>().giftId

        GiftDetailRoute(
            padding = padding,
            giftId = giftId,
            moveToGiftEdit = { navController.navigateToGiftEdit(it) },
            onBackPressed = { navController.popBackStack() },
        )
    }

    composable<Route.GiftEdit> { navBackStackEntry ->
        val giftId = navBackStackEntry.toRoute<Route.GiftEdit>().giftId

        EditGiftRoute(
            padding = padding,
            giftId = giftId,
            moveToGiftCategories = { navController.navigateToGiftCategories() },
            moveToFriendAdd = { navController.navigateToFriendAdd() },
            moveToHome = { navController.popBackStack() },
        )
    }
}