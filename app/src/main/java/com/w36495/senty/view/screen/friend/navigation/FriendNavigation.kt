package com.w36495.senty.view.screen.friend.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.w36495.senty.view.screen.friend.FriendAddRoute
import com.w36495.senty.view.screen.friendgroup.FriendGroupScreen
import com.w36495.senty.view.screen.friend.FriendRoute
import com.w36495.senty.view.screen.main.BottomTabRoute
import com.w36495.senty.view.screen.main.Route

fun NavController.navigateToFriend(navOption: NavOptions) {
    navigate(BottomTabRoute.Friend, navOption)
}

fun NavController.navigateToFriendAdd() {
    navigate(Route.FriendAdd)
}

fun NavController.navigateToFriendGroups() {
    navigate(Route.FriendGroups)
}

fun NavGraphBuilder.friendNavGraph(
    padding: PaddingValues,
    navController: NavController,
) {
    composable<BottomTabRoute.Friend> {
        FriendRoute(
            padding = padding,
            moveToFriendAdd = { navController.navigateToFriendAdd() },
            moveToFriendDetail = {},
            moveToFriendGroup = { navController.navigateToFriendGroups() },
        )
    }

    composable<Route.FriendAdd> {
        FriendAddRoute(
            moveToFriendGroups = { navController.navigateToFriendGroups() },
            onBackPressed = { navController.navigateUp() },
        )
    }

    composable<Route.FriendGroups> {
        FriendGroupScreen(
            onBackPressed = { navController.navigateUp() },
        )
    }
}