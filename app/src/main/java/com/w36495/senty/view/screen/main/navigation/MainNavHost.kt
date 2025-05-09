package com.w36495.senty.view.screen.main.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.w36495.senty.view.screen.anniversary.navigation.anniversaryNavGraph
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
    onShowGlobalErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
    NavHost(
        navController = navigator.navController,
        startDestination = navigator.startDestination,
    ) {
        introNavGraph(
            navController = navigator.navController,
        )

        authNavGraph(
            padding = padding,
            navController = navigator.navController,
            onShowGlobalErrorSnackBar = onShowGlobalErrorSnackBar,
        )

        homeNavGraph(
            padding = padding,
            navController = navigator.navController,
            onShowGlobalErrorSnackBar = onShowGlobalErrorSnackBar,
        )

        friendNavGraph(
            padding = padding,
            navController = navigator.navController,
            onShowGlobalErrorSnackBar = onShowGlobalErrorSnackBar,
        )

        giftNavGraph(
            padding = padding,
            navController = navigator.navController,
            onShowGlobalErrorSnackBar = onShowGlobalErrorSnackBar,
        )

        anniversaryNavGraph(
            padding = padding,
            navController = navigator.navController,
            onShowGlobalErrorSnackBar = onShowGlobalErrorSnackBar,
        )

        settingNavGraph(
            padding = padding,
            navController = navigator.navController,
            onShowGlobalErrorSnackBar = onShowGlobalErrorSnackBar,
        )
    }
}