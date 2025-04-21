package com.w36495.senty.view.screen.main

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import com.w36495.senty.R

enum class MainBottomTab(
    val title: String,
    @DrawableRes val icon: Int,
    val route: BottomTabRoute,
) {
    HOME(
        "홈",
        R.drawable.ic_round_home_24,
        BottomTabRoute.Home,
    ),
    FRIEND(
        "친구",
        R.drawable.ic_round_person_outline_24,
        BottomTabRoute.Friend,
    ),
    GIFT_ADD(
        "선물",
        R.drawable.ic_outline_add_box_24,
        BottomTabRoute.GiftAdd,
    ),
    ANNIVERSARY(
        "기념일",
        R.drawable.ic_round_calendar_month_24,
        BottomTabRoute.Anniversary,
    ),
    SETTINGS(
        "설정",
        R.drawable.ic_round_settings_24,
        BottomTabRoute.Settings,
    );

    companion object {
        @Composable
        fun find(predicate: @Composable (BottomTabRoute) -> Boolean): MainBottomTab? {
            return entries.find { predicate(it.route) }
        }

        @Composable
        fun contains(predicate: @Composable (Route) -> Boolean): Boolean {
            return entries.map { it.route }.any { predicate(it) }
        }
    }
}