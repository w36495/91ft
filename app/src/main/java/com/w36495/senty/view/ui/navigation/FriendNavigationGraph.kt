package com.w36495.senty.view.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.w36495.senty.view.entity.FriendGroup
import com.w36495.senty.view.screen.friend.FriendAddScreen
import com.w36495.senty.view.screen.friend.FriendDetailScreen
import com.w36495.senty.view.screen.friend.FriendGroupDialogScreen
import com.w36495.senty.view.screen.home.FriendScreen
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun NavGraphBuilder.nestedFriendGraph(navController: NavController) {
    navigation(
        route = BottomNavigationItem.Friend.route,
        startDestination = FriendNavigationItem.FRIEND_LIST.name,
    ) {
        composable(FriendNavigationItem.FRIEND_LIST.name) {
            FriendScreen(
                onClickAddFriend = {
                    val emptyGroup = Json.encodeToString(FriendGroup())
                    navController.navigate("${FriendNavigationItem.FRIEND_ADD.name}/$emptyGroup")
                },
                onClickGroupSetting = {
                    navController.navigate(FriendNavigationItem.FRIEND_GROUP_SEETING.name)
                },
                onClickFriend = { friendId ->
                    navController.navigate("${FriendNavigationItem.FRIEND_DETAIL.name}/$friendId")
                }
            )
        }
        composable(
            route = "${FriendNavigationItem.FRIEND_ADD.name}/{group}",
            arguments = listOf(navArgument("group") {
                nullable = false
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val group = backStackEntry.arguments?.getString("group")?.let {
                FriendGroup.decodeToObject(it)
            }

            FriendAddScreen(
                group = group!!,
                onBackPressed = { navController.navigateUp() },
                onFriendGroupClick = {
                    navController.navigate(FriendNavigationItem.FRIEND_GROUP_DIALOG.name)
                },
                onBirthdayClick = {
                },
                onMoveFriendList = {
                    navController.navigate(FriendNavigationItem.FRIEND_LIST.name) {
                        launchSingleTop = true

                        popUpTo(FriendNavigationItem.FRIEND_LIST.name) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        dialog(
            route = FriendNavigationItem.FRIEND_GROUP_DIALOG.name
        ) {
            FriendGroupDialogScreen(
                onDismiss = {
                    navController.navigateUp()
                },
                onGroupSelected = { group ->
                    val jsonGroup = FriendGroup.encodeToJson(group)
                    navController.currentBackStackEntry?.arguments?.putString("group", jsonGroup)

                    navController.navigate(route = "${FriendNavigationItem.FRIEND_ADD.name}/$jsonGroup")
                },
                onEditClick = {

                }
            )
        }
        composable(
            route = "${FriendNavigationItem.FRIEND_DETAIL.name}/{friendId}",
            arguments = listOf(navArgument("friendId") {
                nullable = false
                type = NavType.StringType
            })
        ) {backStackEntry ->
            val friendId = requireNotNull(backStackEntry.arguments).getString("friendId")

            FriendDetailScreen(
                friendId = friendId.toString(),
                onBackPressed = { navController.navigateUp() },
                onClickEdit = {},
                onClickDelete = {}
            )
        }

    }
}

enum class FriendNavigationItem {
    FRIEND_LIST, FRIEND_DETAIL, FRIEND_GROUP_DIALOG, FRIEND_ADD, FRIEND_GROUP_SEETING, BIRTHDAY
}