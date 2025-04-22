package com.w36495.senty.view.screen.friend.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.w36495.senty.view.screen.friend.edit.EditFriendRoute
import com.w36495.senty.view.screen.friend.detail.FriendDetailRoute
import com.w36495.senty.view.screen.friend.list.FriendRoute
import com.w36495.senty.view.screen.friendgroup.FriendGroupScreen
import com.w36495.senty.view.screen.gift.navigation.navigateToGiftDetail
import com.w36495.senty.view.screen.main.BottomTabRoute
import com.w36495.senty.view.screen.main.Route

fun NavController.navigateToFriend(navOption: NavOptions) {
    navigate(BottomTabRoute.Friend, navOption)
}

fun NavController.navigateToFriendAdd() {
    navigate(Route.EditFriend(null))
}

fun NavController.navigateToFriendEdit(friendId: String) {
    navigate(Route.EditFriend(friendId))
}

fun NavController.navigateToFriendDetail(friendId: String) {
    navigate(Route.FriendDetail(friendId))
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
            moveToFriendDetail = { navController.navigateToFriendDetail(it) },
            moveToFriendGroup = { navController.navigateToFriendGroups() },
        )
    }

    // 친구 정보 등록/수정 화면
    composable<Route.EditFriend> {navBackStackEntry ->
        val friendId = navBackStackEntry.toRoute<Route.EditFriend>().friendId

        EditFriendRoute(
            padding = padding,
            friendId = friendId,
            moveToFriendGroups = { navController.navigateToFriendGroups() },
            onBackPressed = { navController.navigateUp() },
        )
    }

    // 친구 정보 조회 화면
    composable<Route.FriendDetail> { navBackStackEntry ->
        val friendId = navBackStackEntry.toRoute<Route.FriendDetail>().friendId

        FriendDetailRoute(
            friendId = friendId,
            onBackPressed = { navController.popBackStack() },
            moveToGiftDetail = { navController.navigateToGiftDetail(it) },
            moveToEditFriend = { navController.navigateToFriendEdit(it) },
        )
    }

    // 친구 그룹 목록 화면
    composable<Route.FriendGroups> {
        FriendGroupScreen(
            onBackPressed = { navController.navigateUp() },
        )
    }
}