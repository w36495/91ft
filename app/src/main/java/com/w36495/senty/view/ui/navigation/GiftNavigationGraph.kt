package com.w36495.senty.view.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.w36495.senty.view.entity.gift.GiftCategory
import com.w36495.senty.view.entity.gift.GiftDetail
import com.w36495.senty.view.screen.gift.GiftAddScreen
import com.w36495.senty.view.screen.gift.GiftCategoryAddDialogScreen
import com.w36495.senty.view.screen.gift.GiftCategoryScreen
import com.w36495.senty.view.screen.gift.GiftDetailScreen
import com.w36495.senty.view.screen.gift.GiftScreen
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun NavGraphBuilder.nestedGiftGraph(navController: NavController) {
    navigation(
        startDestination = GiftNavigationItem.GIFT_ADD.name.plus("/{null}"),
        route = GiftNavigationItem.GIFT.name
    ) {
        composable(GiftNavigationItem.GIFT_LIST.name) {
            GiftScreen(
                onBackPressed = { navController.navigateUp() },
                onClickGiftCategory = { navController.navigate(GiftNavigationItem.GIFT_CATEGORY.name) },
            )
        }

        composable(
            route = GiftNavigationItem.GIFT_ADD.name.plus("/{giftDetail}"),
            arguments = listOf(navArgument("giftDetail") {
                nullable = true
                type = NavType.StringType
            })
        ) {backStackEntry ->
            val resultArgs = backStackEntry.arguments?.getString("giftDetail")
            val giftDetail: GiftDetail? = resultArgs?.let { Json.decodeFromString<GiftDetail>(it) }

            GiftAddScreen(
                giftDetail = giftDetail,
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
        ) {backStackEntry ->
            val giftId = requireNotNull(backStackEntry.arguments).getString("giftId")!!

            GiftDetailScreen(
                giftId = giftId,
                onBackPressed = { navController.navigateUp() },
                onClickEdit = { giftDetail ->
                    val jsonGiftDetail = Json.encodeToString(giftDetail)

                    navController.navigate(GiftNavigationItem.GIFT_ADD.name.plus("/$jsonGiftDetail"))
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