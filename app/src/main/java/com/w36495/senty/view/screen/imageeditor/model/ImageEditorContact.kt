package com.w36495.senty.view.screen.imageeditor.model

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

sealed interface ImageEditorContact {
    data class State (
        val isLoading: Boolean = true,
        val editImageState: EditImageUiState = EditImageUiState(),
        val originalImageUris: List<Uri> = emptyList(),
        val editedImages: List<Bitmap> = emptyList(),
        val currentEditIndex: Int = 0,
        val isFlippedVertical: Boolean = false,
        val isFlippedHorizontal: Boolean = false,
        val isEditing: Boolean = true,
        val showSaveDialog: Boolean = false,
    )

    data class EditImageUiState(
        val offset: Offset = Offset.Zero,
        val size: IntSize = IntSize.Zero,
    )

    sealed interface Event {
        data object OnClickFlipVertical : Event
        data object OnClickFlipHorizontal : Event
        data object OnClickSave : Event
        data object OnClickReset : Event
        data object OnClickPrev : Event
        data object OnClickNext : Event
        data object OnDismissSaveDialog : Event
        data class OnClickComplete(val context: Context) : Event
        data class OnClickSaveDialog(val isComplete: Boolean) : Event
        data object ShowSaveDialog : Event
        data class UpdateEditImage(val image: Bitmap) : Event
        data class UpdateEditImageSpec(val offset: Offset, val size: IntSize) : Event
    }

    sealed interface Effect {
        data class ShowSnackBar(val message: String) : Effect
        data class ShowError(val throwable: Throwable?) : Effect
        data object NavigateToBack : Effect
        data class NavigateToImageEditorPreview(val imageFileUris: List<String>) : Effect
    }
}