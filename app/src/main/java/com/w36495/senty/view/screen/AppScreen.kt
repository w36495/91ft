package com.w36495.senty.view.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.w36495.senty.view.ui.navigation.BottomNavigation
import com.w36495.senty.view.ui.navigation.BottomNavigationItem
import com.w36495.senty.view.ui.navigation.NavigationGraph

@Composable
fun AppScreen() {
    val bottomNavState = rememberBottomNavigationState()
    val navController = bottomNavState.navHostController

    Scaffold(
        bottomBar = {
            if (bottomNavState.shouldShowBottomBar) {
                BottomAppBar(
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    backgroundColor = Color.Transparent
                ) {
                    BottomNavigation(navController = navController)
                }
            }
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            NavigationGraph(navController = navController)
        }
    }
}

@Composable
fun rememberBottomNavigationState(
    navHostController: NavHostController = rememberNavController()
) = remember(navHostController) {
    BottomNavigationState(navHostController)
}

@Stable
class BottomNavigationState(
    val navHostController: NavHostController
) {
    private val bottomNavRoutes = BottomNavigationItem.entries.map { it.name }

    val shouldShowBottomBar: Boolean
        @Composable get() = navHostController.currentBackStackEntryAsState().value?.destination?.route in bottomNavRoutes
}