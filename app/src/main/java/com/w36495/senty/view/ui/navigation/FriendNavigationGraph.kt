package com.w36495.senty.view.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.w36495.senty.view.entity.FriendDetail
import com.w36495.senty.view.screen.friend.FriendAddScreen
import com.w36495.senty.view.screen.friend.FriendDeleteDialogScreen
import com.w36495.senty.view.screen.friend.FriendDetailScreen
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
                    navController.navigate(FriendNavigationItem.FRIEND_GROUP_SEETING.name)
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
                }
            )
        }
        composable(
            route = "${FriendNavigationItem.FRIEND_DETAIL.name}/{friendId}",
            arguments = listOf(navArgument("friendId") {
                nullable = false
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val friendId = requireNotNull(backStackEntry.arguments).getString("friendId")

            FriendDetailScreen(
                friendId = friendId.toString(),
                onBackPressed = { navController.navigateUp() },
                onClickEdit = { friendEntity ->
                    val friend = Json.encodeToString<FriendDetail>(friendEntity)

                    navController.navigate(FriendNavigationItem.FRIEND_ADD.name.plus("/$friend"))
                },
                onClickDelete = {
                    navController.navigate("${FriendNavigationItem.FRIEND_DELETE_DIALOG.name}/$friendId")
                }
            )
        }

        dialog(
            route = "${FriendNavigationItem.FRIEND_DELETE_DIALOG.name}/{friendId}",
            arguments = listOf(navArgument("friendId") {
                nullable = false
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val friendId = requireNotNull(backStackEntry.arguments).getString("friendId")

            FriendDeleteDialogScreen(
                friendId = friendId.toString(),
                onComplete = {
                    navController.navigate(FriendNavigationItem.FRIEND_LIST.name) {
                        launchSingleTop = true

                        popUpTo(FriendNavigationItem.FRIEND_LIST.name) {
                            inclusive = true
                        }
                    }
                },
                onDismiss = {
                    navController.navigateUp()
                }
            )
        }

    }
}

enum class FriendNavigationItem {
    FRIEND_LIST, FRIEND_DETAIL, FRIEND_ADD, FRIEND_GROUP_SEETING, FRIEND_DELETE_DIALOG,
}