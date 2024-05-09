package com.w36495.senty.view.ui.navigation

import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.w36495.senty.view.ui.theme.Green40

@Composable
fun BottomNavigation(navController: NavHostController) {
    val bottomNavigationItem = listOf(
        BottomNavigationItem.Home,
        BottomNavigationItem.Friend,
        BottomNavigationItem.Anniversary,
        BottomNavigationItem.Settings
    )

    androidx.compose.material.BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Green40
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        bottomNavigationItem.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                selectedContentColor = Green40,
                unselectedContentColor = Color.Gray,
                alwaysShowLabel = true
            )
        }
    }
}