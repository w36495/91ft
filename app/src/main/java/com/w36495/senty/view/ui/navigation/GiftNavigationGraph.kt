package com.w36495.senty.view.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.w36495.senty.view.entity.gift.GiftCategory
import com.w36495.senty.view.screen.gift.GiftAddScreen
import com.w36495.senty.view.screen.gift.GiftCategoryAddDialogScreen
import com.w36495.senty.view.screen.gift.GiftCategoryScreen
import com.w36495.senty.view.screen.gift.GiftDetailScreen
import com.w36495.senty.view.screen.gift.GiftScreen
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun NavGraphBuilder.nestedGiftGraph(navController: NavController) {
    navigation(
        startDestination = BottomNavigationItem.GIFT_ADD.name.plus("/${null}"),
        route = GiftNavigationItem.GIFT.name
    ) {
        composable(GiftNavigationItem.GIFT_LIST.name) {
            GiftScreen(
                onBackPressed = { navController.navigateUp() },
                onClickGiftCategory = { navController.navigate(GiftNavigationItem.GIFT_CATEGORY.name) },
                onClickGiftDetail = { giftId ->
                    navController.navigate(GiftNavigationItem.GIFT_DETAIL.name.plus("/$giftId")) {
                        launchSingleTop = true

                        popUpTo(GiftNavigationItem.GIFT_DETAIL.name) {
                            saveState = true
                        }
                    }
                }
            )
        }

        composable(
            route = BottomNavigationItem.GIFT_ADD.name.plus("/{giftId}"),
            arguments = listOf(navArgument("giftId") {
                nullable = true
                type = NavType.StringType
            })
        ) {backStackEntry ->
            val giftId = backStackEntry.arguments?.getString("giftId")

            GiftAddScreen(
                giftId = giftId,
                onPressedBack = {
                    navController.navigateUp()
                },
                onComplete = {
                    navController.navigate(BottomNavigationItem.HOME.name) {
                        launchSingleTop = true

                        popUpTo(BottomNavigationItem.HOME.name) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(GiftNavigationItem.GIFT_CATEGORY.name) {
            GiftCategoryScreen(
                onPressedBack = { navController.navigateUp() },
                onShowGiftCategoryDialog = {
                    navController.navigate(GiftNavigationItem.GIFT_CATEGORY_DIALOG.name.plus("/${null}"))
                },
                onClickEditCategory = { giftCategory ->
                    val formattedJson = Json { encodeDefaults = true }
                    val jsonGiftCategory = formattedJson.encodeToString(giftCategory)

                    navController.navigate(GiftNavigationItem.GIFT_CATEGORY_DIALOG.name.plus("/$jsonGiftCategory"))
                }
            )
        }

        composable(
            route = GiftNavigationItem.GIFT_DETAIL.name.plus("/{giftId}"),
            arguments = listOf(navArgument("giftId") {
                nullable = true
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val giftId = requireNotNull(backStackEntry.arguments).getString("giftId")!!

            GiftDetailScreen(
                giftId = giftId,
                onBackPressed = { navController.navigateUp() },
                onClickEdit = { editGiftId ->

                    navController.navigate(BottomNavigationItem.GIFT_ADD.name.plus("/$editGiftId")) {
                        launchSingleTop = true

                        popUpTo(BottomNavigationItem.GIFT_ADD.name) {
                            saveState = true
                        }
                    }
                }
            )
        }

        dialog(
            route = GiftNavigationItem.GIFT_CATEGORY_DIALOG.name.plus("/{giftCategory}"),
            arguments = listOf(navArgument("giftCategory") {
                nullable = true
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("giftCategory")
            val categoryEntity = category?.let { Json.decodeFromString<GiftCategory>(it) }

            GiftCategoryAddDialogScreen(
                giftCategory = categoryEntity,
                onDismiss = { navController.navigateUp() },
                onComplete = {
                    navController.navigate(GiftNavigationItem.GIFT_CATEGORY.name) {
                        launchSingleTop = true

                        popUpTo(GiftNavigationItem.GIFT_CATEGORY.name) {
                            inclusive = true
                        }
                    }
                },
            )
        }
    }
}

enum class GiftNavigationItem {
    GIFT, GIFT_LIST, GIFT_ADD, GIFT_CATEGORY, GIFT_DETAIL, GIFT_CATEGORY_DIALOG,
}