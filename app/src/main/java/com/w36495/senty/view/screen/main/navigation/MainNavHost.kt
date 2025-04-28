package com.w36495.senty.view.screen.main.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.w36495.senty.view.screen.friend.navigation.friendNavGraph
import com.w36495.senty.view.screen.gift.navigation.giftNavGraph
import com.w36495.senty.view.screen.home.navigation.homeNavGraph
import com.w36495.senty.view.screen.intro.navigation.introNavGraph
import com.w36495.senty.view.screen.login.navigation.authNavGraph
import com.w36495.senty.view.screen.main.MainBottomTab
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
        introNavGraph(
            navController = navigator.navController,
            moveToHome = { navigator.navigate(MainBottomTab.HOME) }
        )

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

        giftNavGraph(
            padding = padding,
            navController = navigator.navController,
            moveToHome = { navigator.navigate(MainBottomTab.HOME) },
        )

        settingNavGraph(
            padding = padding,
            navController = navigator.navController,
        )
    }
}