package com.w36495.senty.view.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.w36495.senty.view.screen.friend.navigation.navigateToFriend
import com.w36495.senty.view.screen.home.navigation.navigateToHome
import com.w36495.senty.view.screen.setting.navigation.navigateToSettings
import kotlinx.serialization.Serializable

class MainNavigator(
    val navController: NavHostController,
) {
    private val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val startDestination = Route.Login

    val currentTab: MainBottomTab?
        @Composable get() = MainBottomTab.find { tab ->
            currentDestination?.hasRoute(tab::class) == true
        }

    fun navigate(bottomTab: MainBottomTab) {
        val navOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (bottomTab) {
            MainBottomTab.HOME -> navController.navigateToHome(navOptions)
            MainBottomTab.FRIEND -> navController.navigateToFriend(navOptions)
            MainBottomTab.GIFT_ADD -> {
//                navController.navigateToPocketTab(navOptions)
            }
            MainBottomTab.ANNIVERSARY -> {
//                navController.navigateToLoungeTab(navOptions)
            }
            MainBottomTab.SETTINGS -> navController.navigateToSettings(navOptions)
        }
    }

    @Composable
    fun showBottomBar() = MainBottomTab.contains {
        currentDestination?.hasRoute(it::class) == true
    }
}

sealed interface Route {
    @Serializable data object Intro : Route

    @Serializable data object Login : Route
    @Serializable data object SignUp : Route

    // Friend
    @Serializable data class EditFriend(val friendId: String? = null) : Route
    @Serializable data class FriendDetail(val friendId: String) : Route
    @Serializable data object FriendGroups : Route

    // Settings

}

sealed interface BottomTabRoute : Route {
    @Serializable data object Home : BottomTabRoute
    @Serializable data object Friend : BottomTabRoute
    @Serializable data object GiftAdd : BottomTabRoute
    @Serializable data object Anniversary : BottomTabRoute
    @Serializable data object Settings : BottomTabRoute
}

@Composable
fun rememberMainNavigator(
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}