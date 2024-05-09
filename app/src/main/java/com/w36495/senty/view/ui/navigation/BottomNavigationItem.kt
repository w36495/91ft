package com.w36495.senty.view.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    data object Home : BottomNavigationItem("홈", Icons.Filled.Home, "HOME")
    data object Friend : BottomNavigationItem("친구", Icons.Filled.PersonOutline, "FRIEND")
    data object Anniversary : BottomNavigationItem("기념일", Icons.Filled.CalendarMonth, "ANNIVERSARY")
    data object Settings : BottomNavigationItem("설정", Icons.Filled.Settings, "SETTINGS")
}