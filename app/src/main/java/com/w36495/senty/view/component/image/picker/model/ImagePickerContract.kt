package com.w36495.senty.view.component.image.picker.model

import android.net.Uri
import androidx.compose.foundation.ScrollState
import androidx.compose.ui.unit.IntSize

sealed interface ImagePickerContract {
    data class State(
        val images: List<Uri> = emptyList(),
        val currentFolderName: String = "",
        val galleryFolders: List<GalleryFolderUiModel> = emptyList(),
        val selectedImageUris: List<Uri> = emptyList(),
        val editedImageUris: List<Uri> = emptyList(),
        val initializedPages: Set<Int> = emptySet(),
        val scrollStates: Map<Int, ScrollState> = emptyMap(),
        val viewportSize: IntSize = IntSize.Zero,
        val showGalleryGroupBottomSheet: Boolean = false,
    ) : ImagePickerContract

    sealed interface Effect : ImagePickerContract {
        data class NavigateToImagePreview(val editedImageUris: List<Uri>) : Effect
        data object NavigateToBack : Effect
    }
}