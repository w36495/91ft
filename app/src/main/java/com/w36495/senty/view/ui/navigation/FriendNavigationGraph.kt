package com.w36495.senty.view.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.w36495.senty.view.entity.FriendDetail
import com.w36495.senty.view.screen.friend.FriendAddScreen
import com.w36495.senty.view.screen.friend.FriendDeleteDialogScreen
import com.w36495.senty.view.screen.friend.FriendDetailScreen
import com.w36495.senty.view.screen.friend.FriendEditRoute
import com.w36495.senty.view.screen.friend.FriendGroupScreen
import com.w36495.senty.view.screen.home.FriendScreen

fun NavGraphBuilder.nestedFriendGraph(navController: NavController) {
    navigation(
        route = BottomNavigationItem.Friend.route,
        startDestination = FriendNavigationItem.FRIEND_LIST.name,
    ) {
        composable(FriendNavigationItem.FRIEND_LIST.name) {
            FriendScreen(
                onClickAddFriend = {
                    navController.navigate(FriendNavigationItem.FRIEND_ADD.name)
                },
                onClickGroupSetting = {
                    navController.navigate(FriendNavigationItem.FRIEND_GROUP_SETTINGS.name)
                },
                onClickFriend = { friendId ->
                    navController.navigate("${FriendNavigationItem.FRIEND_DETAIL.name}/$friendId")
                }
            )
        }
        composable(FriendNavigationItem.FRIEND_ADD.name) {
            FriendAddScreen(
                friend = friendEntity,
                onBackPressed = { navController.navigateUp() },
                onMoveFriendList = {
                    navController.navigate(FriendNavigationItem.FRIEND_LIST.name) {
                        launchSingleTop = true
                    }
                },
                onClickGroupEdit = { navController.navigate(FriendNavigationItem.FRIEND_GROUP_SETTINGS.name) }
            )
        }
        composable(FriendNavigationItem.FRIEND_EDIT.name.plus("/{friendId}"),
            arguments = listOf(navArgument("friendId") {
                nullable = true
                type = NavType.StringType
            })
        ) {backStackEntry ->
            val friendId = backStackEntry.arguments?.getString("friendId")

            if (friendId != null) {
                FriendEditRoute(
                    friendId = friendId,
                    onBackPressed = { navController.navigateUp() },
                    onMoveFriendList = {
                        navController.navigate(FriendNavigationItem.FRIEND_LIST.name) {
                            launchSingleTop = true
                        }
                    },
                    onClickGroupEdit = { navController.navigate(FriendNavigationItem.FRIEND_GROUP_SETTINGS.name) }
                )
            }
        }
        composable(
            route = FriendNavigationItem.FRIEND_DETAIL.name.plus("/{friendId}"),
            arguments = listOf(navArgument("friendId") {
                nullable = false
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val friendId = requireNotNull(backStackEntry.arguments).getString("friendId")

            FriendDetailScreen(
                friendId = friendId.toString(),
                onBackPressed = { navController.navigateUp() },
                onClickEdit = { friendId ->
                    navController.navigate(FriendNavigationItem.FRIEND_EDIT.name.plus("/${friendId}"))
                },
                onClickGiftDetail = { giftId ->
                    navController.navigate(GiftNavigationItem.GIFT_DETAIL.name.plus("/$giftId")) {
                        launchSingleTop = true

                        popUpTo(GiftNavigationItem.GIFT_DETAIL.name) {
                            saveState = true
                        }
                    }
                },
                onCompleteDelete = {
                    navController.navigate(FriendNavigationItem.FRIEND_LIST.name) {
                        launchSingleTop = true

                        popUpTo(FriendNavigationItem.FRIEND_LIST.name) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(FriendNavigationItem.FRIEND_GROUP_SETTINGS.name) {
            FriendGroupScreen(
                onBackPressed = { navController.navigateUp() },
            )
        }

    }
}

enum class FriendNavigationItem {
    FRIEND_LIST, FRIEND_DETAIL, FRIEND_ADD, FRIEND_EDIT, FRIEND_GROUP_SETTINGS,
}