package com.w36495.senty.view.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.w36495.senty.view.screen.home.HomeScreen

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavigationItem.Home.route
    ) {
        composable(BottomNavigationItem.Home.route) {
            HomeScreen()
        }
        composable(BottomNavigationItem.Friend.route) {
//            FriendScreen()
        }
        composable(BottomNavigationItem.Anniversary.route) {

        }
        composable(BottomNavigationItem.Settings.route) {

        }
    }
}