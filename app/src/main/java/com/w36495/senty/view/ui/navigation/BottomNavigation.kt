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
        BottomNavigationItem.HOME,
        BottomNavigationItem.FRIEND,
        BottomNavigationItem.GIFT_ADD,
        BottomNavigationItem.ANNIVERSARY,
        BottomNavigationItem.SETTINGS
    )

    androidx.compose.material.BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Green40,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        bottomNavigationItem.forEachIndexed { _, item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    if (item.name != BottomNavigationItem.GIFT_ADD.name) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                selected = currentRoute == item.name,
                onClick = {
                    navController.navigate(
                        if (item.name == BottomNavigationItem.GIFT_ADD.name) GiftNavigationItem.GIFT_ADD.name.plus("/null")
                        else item.name
                    ) {
                        navController.graph.startDestinationRoute?.let { route ->
                            launchSingleTop = true
                            restoreState = true

                            popUpTo(route) {
                                saveState = true
                            }
                        }
                    }
                },
                selectedContentColor = Green40,
                unselectedContentColor = Color.Gray,
                alwaysShowLabel = false
            )
        }
    }
}