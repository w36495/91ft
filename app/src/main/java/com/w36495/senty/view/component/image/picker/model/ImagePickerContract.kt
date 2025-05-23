package com.w36495.senty.view.component.image.picker.model

import android.net.Uri
import androidx.compose.ui.unit.IntSize

sealed interface ImagePickerContract {
    data class State(
        val images: List<Uri> = emptyList(),
        val galleryFolders: List<GalleryFolderUiModel> = emptyList(),
        val selectedImageUris: List<Uri> = emptyList(),
        val editedImageUris: List<Uri> = emptyList(),
        val initializedPages: Set<Int> = emptySet(),
        val viewportSize: IntSize = IntSize.Zero,
    ) : ImagePickerContract

    sealed interface Effect : ImagePickerContract {
        data class NavigateToImagePreview(val editedImageUris: List<Uri>) : Effect
        data object NavigateToBack : Effect
    }
}