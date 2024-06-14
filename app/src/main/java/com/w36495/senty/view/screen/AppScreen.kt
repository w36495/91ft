package com.w36495.senty.view.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.w36495.senty.view.ui.navigation.BottomNavigation
import com.w36495.senty.view.ui.navigation.BottomNavigationItem
import com.w36495.senty.view.ui.navigation.GiftNavigationItem
import com.w36495.senty.view.ui.navigation.NavigationGraph
import com.w36495.senty.view.ui.theme.Green40

@Composable
fun AppScreen() {
    val bottomNavState = rememberBottomNavigationState()
    val navController = bottomNavState.navHostController

    Scaffold(
        bottomBar = {
            if (bottomNavState.shouldShowBottomBar) {
                BottomAppBar(
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    cutoutShape = CircleShape,
                    backgroundColor = Color.Transparent
                ) {
                    BottomNavigation(navController = navController)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            if (bottomNavState.shouldShowBottomBar) {
                FloatingActionButton(
                    shape = CircleShape,
                    onClick = {
                        navController.navigate(GiftNavigationItem.GIFT_ADD.name.plus("/null")) {
                            launchSingleTop = true
                            restoreState = true

                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                        }
                    },
                    contentColor = Color.White,
                    containerColor = Green40
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
            }
        }
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