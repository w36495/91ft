package com.w36495.senty.view.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavigationItem(
    val title: String,
    val icon: ImageVector,
) {
    HOME("홈", Icons.Filled.Home),
    FRIEND("친구", Icons.Filled.PersonOutline),
    ANNIVERSARY("기념일", Icons.Filled.CalendarMonth),
    SETTINGS("설정", Icons.Filled.Settings),
}