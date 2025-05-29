package com.w36495.senty.view.component.image.picker.model

import android.net.Uri
import androidx.compose.foundation.ScrollState
import androidx.compose.ui.unit.IntSize
import com.w36495.senty.domain.error.SentyError

sealed interface ImagePickerContract {
    data class State(
        val isLoading: Boolean = false,
        val galleryFolders: List<GalleryFolderUiModel> = emptyList(),
        val selectedImages: List<GalleryImageUiModel> = emptyList(),
        val editedImageUris: List<Uri> = emptyList(),
        val initializedPages: Set<Int> = emptySet(),
        val scrollStates: Map<Int, ScrollState> = emptyMap(),
        val viewportSize: IntSize = IntSize.Zero,
        val showGalleryGroupBottomSheet: Boolean = false,
    ) : ImagePickerContract

    sealed interface Effect : ImagePickerContract {
        data class ShowError(val errorType: SentyError) : Effect
        data class NavigateToImagePreview(val editedImageUris: List<Uri>) : Effect
        data object NavigateToBack : Effect
    }
}