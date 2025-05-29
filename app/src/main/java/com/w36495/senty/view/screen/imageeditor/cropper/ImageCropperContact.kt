package com.w36495.senty.view.screen.imageeditor.cropper

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize

sealed interface ImageCropperContact {
    data class State(
        val editedBitmap: Bitmap? = null,
        val topLeft: Offset = Offset.Zero,
        val topRight: Offset = Offset.Zero,
        val bottomLeft: Offset = Offset.Zero,
        val bottomRight: Offset = Offset.Zero,
        val draggingCorner: Corner? = null,
        val draggingCenter: Boolean = false,
    )

    sealed interface Event {
        data class Drag(val corner: Corner, val dragAmount: Offset, val imageRect: Rect) : Event
        data class Move(val dragAmount: Offset, val imageRect: Rect) : Event
    }

    sealed interface Effect {
        data object Complete : Effect
    }
}