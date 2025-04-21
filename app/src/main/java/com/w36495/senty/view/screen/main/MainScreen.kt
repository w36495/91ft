package com.w36495.senty.view.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.w36495.senty.view.screen.main.component.MainBottomTabBar
import com.w36495.senty.view.screen.main.navigation.MainNavHost

@Composable
fun MainScreen(
    navigator: MainNavigator = rememberMainNavigator(),
) {
    Scaffold(
        bottomBar = {
            MainBottomTabBar(
                isVisible = navigator.showBottomBar(),
                tabs = MainBottomTab.entries.toList(),
                currentTab = navigator.currentTab,
                onTabSelected = { navigator.navigate(it) },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            MainNavHost(
                navigator = navigator,
                padding = innerPadding,
            )
        }
    }
}