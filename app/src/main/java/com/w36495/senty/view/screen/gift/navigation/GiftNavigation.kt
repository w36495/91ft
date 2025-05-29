package com.w36495.senty.view.screen.gift.navigation

import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.w36495.senty.view.component.image.editor.ImageEditorPreviewRoute
import com.w36495.senty.view.component.image.picker.ImagePickerRoute
import com.w36495.senty.view.screen.friend.navigation.navigateToFriendAdd
import com.w36495.senty.view.screen.gift.detail.GiftDetailRoute
import com.w36495.senty.view.screen.gift.edit.EditGiftRoute
import com.w36495.senty.view.screen.gift.list.GiftRoute
import com.w36495.senty.view.screen.home.navigation.navigateToHome
import com.w36495.senty.view.screen.imageeditor.ImageEditorRoute
import com.w36495.senty.view.screen.main.BottomTabRoute
import com.w36495.senty.view.screen.main.Route
import com.w36495.senty.view.screen.setting.navigation.navigateToGiftCategories

fun NavController.navigateToGiftAdd(navOptions: NavOptions) {
    navigate(BottomTabRoute.GiftAdd, navOptions)
}

fun NavController.navigateToGifts() {
    navigate(Route.Gifts)
}

fun NavController.navigateToGiftDetail(giftId: String) {
    navigate(Route.GiftDetail(giftId))
}

fun NavController.navigateToGiftEdit(giftId: String) {
    navigate(Route.GiftEdit(giftId))
}

fun NavController.navigateToImagePicker(count: Int) {
    navigate(Route.ImagePicker(count))
}

fun NavController.navigateToImageEditor(imageUris: List<Uri>) {
    navigate(Route.ImageEditor(imageUris.map { it.toString() }))
}

fun NavController.navigateToImageEditorPreview(editedImageFileUris: List<String>) {
    navigate(Route.ImageEditorPreview(editedImageFileUris))
}

fun NavGraphBuilder.giftNavGraph(
    padding: PaddingValues,
    navController: NavController,
    onShowGlobalErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
    composable<BottomTabRoute.GiftAdd> { 
        EditGiftRoute(
            padding = padding,
            savedStateHandle = navController.currentBackStackEntry?.savedStateHandle,
            moveToGiftCategories = { navController.navigateToGiftCategories() },
            moveToFriendAdd = { navController.navigateToFriendAdd() },
            moveToHome = {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(
                        route = BottomTabRoute.Home,
                        inclusive = true
                    )
                    .build()

                navController.navigateToHome(navOptions)
            },
            moveToImagePicker = { navController.navigateToImagePicker(it) },
            onShowGlobalErrorSnackBar = onShowGlobalErrorSnackBar,
        )
    }

    composable<Route.Gifts> {
        GiftRoute(
            padding = padding,
            moveToGiftDetail = { navController.navigateToGiftDetail(it) },
            moveToGiftCategories = { navController.navigateToGiftCategories() },
            onBackPressed = { navController.popBackStack() },
            onShowGlobalErrorSnackBar = onShowGlobalErrorSnackBar,
        )
    }

    composable<Route.GiftDetail> { navBackStackEntry ->
        val giftId = navBackStackEntry.toRoute<Route.GiftDetail>().giftId

        GiftDetailRoute(
            padding = padding,
            giftId = giftId,
            moveToGiftEdit = { navController.navigateToGiftEdit(it) },
            onBackPressed = { navController.popBackStack() },
            onShowGlobalErrorSnackBar = onShowGlobalErrorSnackBar,
        )
    }

    composable<Route.GiftEdit> { navBackStackEntry ->
        val giftId = navBackStackEntry.toRoute<Route.GiftEdit>().giftId

        EditGiftRoute(
            padding = padding,
            giftId = giftId,
            savedStateHandle = navController.currentBackStackEntry?.savedStateHandle,
            moveToGiftCategories = { navController.navigateToGiftCategories() },
            moveToFriendAdd = { navController.navigateToFriendAdd() },
            moveToHome = { navController.popBackStack() },
            moveToImagePicker = { navController.navigateToImagePicker(it) },
            onShowGlobalErrorSnackBar = onShowGlobalErrorSnackBar,
        )
    }

    composable<Route.ImagePicker> {
        val imageCount = it.toRoute<Route.ImagePicker>().originalImageCount

        ImagePickerRoute(
            originalImageCount = imageCount,
            moveToImagePreview = { uri -> navController.navigateToImageEditorPreview(uri) },
            onBackPressed = { navController.popBackStack() },
        )
    }

    composable<Route.ImageEditor> {navBackStackEntry ->
        val imageUriStrings = navBackStackEntry.toRoute<Route.ImageEditor>().imageUris
        val imageUris = imageUriStrings.map { uriString -> Uri.parse(uriString) }

        ImageEditorRoute(
            imageUris = imageUris,
            moveToEditPreview = { navController.navigateToImageEditorPreview(it) },
            onBackPressed = { navController.popBackStack() },
            onShowGlobalErrorSnackBar = onShowGlobalErrorSnackBar,
        )
    }

    composable<Route.ImageEditorPreview> { navBackStackEntry ->
        val imageFiles = navBackStackEntry.toRoute<Route.ImageEditorPreview>().editedImageFileUris
        val fileUris = imageFiles.map { Uri.parse(it) }

        ImageEditorPreviewRoute(
            uris = fileUris,
            moveToEditGift = {
                navController.getBackStackEntry(BottomTabRoute.GiftAdd).savedStateHandle
                    .set(
                        key = "imageUri",
                        value = it
                    )

                navController.popBackStack(
                    route = BottomTabRoute.GiftAdd,
                    inclusive = false,
                )
            },
            onBackPressed = { navController.popBackStack() },
        )
    }
}