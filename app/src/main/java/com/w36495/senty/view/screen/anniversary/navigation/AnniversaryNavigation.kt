package com.w36495.senty.view.screen.anniversary.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.w36495.senty.view.screen.anniversary.AnniversaryRoute
import com.w36495.senty.view.screen.main.BottomTabRoute

fun NavController.navigateToAnniversary(navOptions: NavOptions) {
    navigate(BottomTabRoute.Anniversary, navOptions)
}

fun NavGraphBuilder.anniversaryNavGraph(
    padding: PaddingValues,
    navController: NavController,
    onShowGlobalErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
    composable<BottomTabRoute.Anniversary> {
        AnniversaryRoute(
            padding = padding,
            onShowGlobalErrorSnackBar = onShowGlobalErrorSnackBar,
        )
    }
}