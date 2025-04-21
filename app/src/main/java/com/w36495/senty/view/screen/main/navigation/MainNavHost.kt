package com.w36495.senty.view.screen.main.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.w36495.senty.view.screen.friend.navigation.friendNavGraph
import com.w36495.senty.view.screen.home.navigation.homeNavGraph
import com.w36495.senty.view.screen.login.navigation.authNavGraph
import com.w36495.senty.view.screen.main.MainNavigator
import com.w36495.senty.view.screen.setting.navigation.settingNavGraph

@Composable
fun MainNavHost(
    navigator: MainNavigator,
    padding: PaddingValues,
) {
    NavHost(
        navController = navigator.navController,
        startDestination = navigator.startDestination,
    ) {
        authNavGraph(
            padding = padding,
            navController = navigator.navController,
        )

        homeNavGraph(
            padding = padding,
            navController = navigator.navController,
        )

        friendNavGraph(
            padding = padding,
            navController = navigator.navController,
        )

        settingNavGraph(
            padding = padding,
            navController = navigator.navController,
        )
    }
}