package com.w36495.senty.view.screen.imageeditor.cropper

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.unit.toIntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min
import kotlin.math.roundToInt

@HiltViewModel
class ImageCropperViewModel @Inject constructor(

) : ViewModel() {
    private val _effect = Channel<ImageCropperContact.Effect>()
    val effect = _effect.receiveAsFlow()

    private val _state = MutableStateFlow(ImageCropperContact.State())
    val state get() = _state.asStateFlow()

    fun handleEvent(event: ImageCropperContact.Event) {
        when (event) {
            is ImageCropperContact.Event.Drag -> {
                handleDrag(
                    corner = event.corner,
                    dragAmount = event.dragAmount,
                    imageRect = event.imageRect,
                )
            }
            is ImageCropperContact.Event.Move -> {
                handleMove(
                    dragAmount = event.dragAmount,
                    imageRect = event.imageRect,
                )
            }
        }
    }

    fun setOffset(imageRect: Rect) {
        val minSize = min(imageRect.width, imageRect.height)
        val (imageX, imageY) = imageRect.topLeft.x to imageRect.topLeft.y
        Log.d("ImageCropperVM", "한 변의 길이 : $minSize")
        Log.d("ImageCropperVM", "초기 좌표 : $imageX / $imageY")



        _state.update { state ->
            val newState = state.copy(
                topLeft = Offset(imageX, imageY),
                topRight = Offset(imageX + minSize, imageY),
                bottomLeft = Offset(imageX, imageY + minSize),
                bottomRight = Offset(imageX + minSize, imageY + minSize),
            )

            Log.d("ImageCropperVM", "설정된 좌표 : ${newState.topLeft} / ${newState.topRight} / ${newState.bottomLeft} / ${newState.bottomRight}")
            newState
        }
    }

    fun dragCropBoxStarted(offset: Offset) {
        val draggingCorner = when {
            offset.isNear(state.value.topLeft) -> Corner.TopLeft
            offset.isNear(state.value.topRight) -> Corner.TopRight
            offset.isNear(state.value.bottomLeft) -> Corner.BottomLeft
            offset.isNear(state.value.bottomRight) -> Corner.BottomRight
            else -> null
        }

        _state.update {
            it.copy(
                draggingCorner = draggingCorner,
                draggingCenter = draggingCorner == null
                        && Rect(state.value.topLeft, state.value.bottomRight).contains(offset)
            )
        }
    }

    fun dragCropBoxEnded() {
        _state.update { it.copy(draggingCorner = null, draggingCenter = false) }
    }

    private fun handleDrag(
        corner: Corner,
        dragAmount: Offset,
        imageRect: Rect,
    ) {
        val minSize = calculateSquareSizeFromDrag(
            corner = corner,
            dragAmount = dragAmount
        )

        val newOffset = when (corner) {
            Corner.TopLeft -> {
                Offset(
                    x = state.value.bottomRight.x - minSize.getNewSize(imageRect.size.toIntSize()),
                    y = state.value.bottomRight.y - minSize.getNewSize(imageRect.size.toIntSize())
                )
            }
            Corner.TopRight -> {
                Offset(
                    x = state.value.bottomLeft.x + minSize.getNewSize(imageRect.size.toIntSize()),
                    y = state.value.bottomLeft.y - minSize.getNewSize(imageRect.size.toIntSize())
                )
            }
            Corner.BottomLeft -> {
                Offset(
                    x = state.value.topRight.x - minSize.getNewSize(imageRect.size.toIntSize()),
                    y = state.value.topRight.y + minSize.getNewSize(imageRect.size.toIntSize())
                )
            }
            Corner.BottomRight -> {
                Offset(
                    x = state.value.topLeft.x + minSize.getNewSize(imageRect.size.toIntSize()),
                    y = state.value.topLeft.y + minSize.getNewSize(imageRect.size.toIntSize())
                )
            }
        }.coerceIn(imageRect)

        updateCorners(corner, newOffset)
    }

    private fun handleMove(
        dragAmount: Offset,
        imageRect: Rect,
    ) {
        val dx = dragAmount.x
        val dy = dragAmount.y

        val maxLeft = imageRect.left - state.value.topLeft.x
        val maxTop = imageRect.top - state.value.topLeft.y
        val maxRight = imageRect.right - state.value.bottomRight.x
        val maxBottom = imageRect.bottom - state.value.bottomRight.y

        val safeDx = dx.coerceIn(maxLeft, maxRight)
        val safeDy = dy.coerceIn(maxTop, maxBottom)

        val safeOffset = Offset(safeDx, safeDy)

        _state.update { state ->
            state.copy(
                topLeft = state.topLeft + safeOffset,
                topRight = state.topRight + safeOffset,
                bottomLeft = state.bottomLeft + safeOffset,
                bottomRight = state.bottomRight + safeOffset
            )
        }
    }

    private fun updateCorners(corner: Corner, newOffset: Offset) {
        _state.update { state ->
            val newState = when (corner) {
                Corner.TopLeft -> state.copy(
                    topLeft = newOffset,
                    topRight = Offset(state.bottomRight.x, newOffset.y),
                    bottomLeft = Offset(newOffset.x, state.bottomRight.y)
                )
                Corner.TopRight -> state.copy(
                    topLeft = Offset(state.bottomLeft.x, newOffset.y),
                    topRight = newOffset,
                    bottomRight = Offset(newOffset.x, state.bottomLeft.y)
                )
                Corner.BottomLeft -> state.copy(
                    topLeft = Offset(newOffset.x, state.topRight.y),
                    bottomLeft = newOffset,
                    bottomRight = Offset(state.topRight.x, newOffset.y)
                )
                Corner.BottomRight -> state.copy(
                    topRight = Offset(newOffset.x, state.topLeft.y),
                    bottomLeft = Offset(state.topLeft.x, newOffset.y),
                    bottomRight = newOffset,
                )
            }

            Log.d("ImageCropperVM", "좌표 위치 : ${state.topLeft} / ${state.topRight} / ${state.bottomLeft} / ${state.bottomRight}")
            newState

        }

    }

    /**
     * 현재 상태를 기준으로, 정사각형의 한 변 길이를 계산합니다.
     */
    private fun calculateSquareSizeFromDrag(
        corner: Corner,
        dragAmount: Offset
    ): Float {
        val fixedCorner = when (corner) {
            Corner.TopLeft -> state.value.bottomRight
            Corner.TopRight -> state.value.bottomLeft
            Corner.BottomLeft -> state.value.topRight
            Corner.BottomRight -> state.value.topLeft
        }
        val draggedCorner = when (corner) {
            Corner.TopLeft -> state.value.topLeft
            Corner.TopRight -> state.value.topRight
            Corner.BottomLeft -> state.value.bottomLeft
            Corner.BottomRight -> state.value.bottomRight
        }

        val (width, height) = when (corner) {
            Corner.TopLeft -> {
                val w = fixedCorner.x - (draggedCorner.x + dragAmount.x)
                val h = fixedCorner.y - (draggedCorner.y + dragAmount.y)

                w to h
            }
            Corner.TopRight -> {
                val w = (draggedCorner.x + dragAmount.x) - fixedCorner.x
                val h = fixedCorner.y - (draggedCorner.y + dragAmount.y)

                w to h
            }
            Corner.BottomLeft -> {
                val w = fixedCorner.x - (draggedCorner.x + dragAmount.x)
                val h = (draggedCorner.y + dragAmount.y) - fixedCorner.y

                w to h
            }
            Corner.BottomRight -> {
                val w = (draggedCorner.x + dragAmount.x) - fixedCorner.x
                val h = (draggedCorner.y + dragAmount.y) - fixedCorner.y

                w to h
            }
        }

        return min(width, height)
    }

    fun editBitmap(
        image: ImageBitmap,
        canvasWidth: Float,
        canvasHeight: Float,
        isFlippedVertical: Boolean,
        isFlippedHorizontal: Boolean,
    ) {
        val cropRect = Rect(state.value.topLeft, state.value.bottomRight)
        Log.d("ImageCropperVM", "편집 진행중 .. ${cropRect.topLeft} / ${cropRect.topRight} / ${cropRect.bottomLeft} / ${cropRect.bottomRight}")

        val cropped = getCroppedBitmap(
            image = image,
            cropRect = cropRect,
            canvasWidth = canvasWidth,
            canvasHeight = canvasHeight
        )

        if (isFlippedHorizontal || isFlippedVertical) {
            val flipped = getFlippedBitmap(
                croppedBitmap = cropped,
                isFlippedVertical = isFlippedVertical,
                isFlippedHorizontal = isFlippedHorizontal)

            _state.update { it.copy(editedBitmap = flipped) }
        } else {
            _state.update { it.copy(editedBitmap = cropped) }
        }

        sendEffect(ImageCropperContact.Effect.Complete)
    }

    fun clearState() {
        _state.update {
            it.copy(
                editedBitmap = null,
                topLeft = Offset.Zero,
                topRight = Offset.Zero,
                bottomLeft = Offset.Zero,
                bottomRight = Offset.Zero,
                draggingCenter = false,
                draggingCorner = null,
            )
        }
    }
    private fun getFlippedBitmap(
        croppedBitmap: Bitmap,
        isFlippedVertical: Boolean,
        isFlippedHorizontal: Boolean,
    ): Bitmap {
        val matrix = Matrix().apply {
            postScale(
                if (isFlippedHorizontal) -1f else 1f,
                if (isFlippedVertical) -1f else 1f,
                croppedBitmap.width / 2f,
                croppedBitmap.height / 2f
            )
        }

        return Bitmap.createBitmap(
            croppedBitmap,
            0,
            0,
            croppedBitmap.width,
            croppedBitmap.height,
            matrix,
            false
        )
    }
    private fun getCroppedBitmap(
        image: ImageBitmap,
        cropRect: Rect,
        canvasWidth: Float,
        canvasHeight: Float
    ): Bitmap {
        val bitmapWidth = image.width.toFloat()
        val bitmapHeight = image.height.toFloat()

        Log.d("ImageCropperVM", "크롭 진행중 ..")
        Log.d("ImageCropperVM", "크롭할 Rect : ${cropRect.toString()}")

        val widthRatio = canvasWidth / bitmapWidth
        val heightRatio = canvasHeight / bitmapHeight

        val scaleFactor = min(widthRatio, heightRatio)

        val displayImageWidth = bitmapWidth * scaleFactor
        val displayImageHeight = bitmapHeight * scaleFactor

        val offsetX = (canvasWidth - displayImageWidth) / 2
        val offsetY = (canvasHeight - displayImageHeight) / 2

        val cropLeft =
            ((cropRect.left - offsetX) / scaleFactor).roundToInt().coerceIn(0, bitmapWidth.toInt())
        val cropTop =
            ((cropRect.top - offsetY) / scaleFactor).roundToInt().coerceIn(0, bitmapWidth.toInt())
        val cropRight =
            ((cropRect.right - offsetX) / scaleFactor).roundToInt().coerceIn(0, bitmapWidth.toInt())
        val cropBottom =
            ((cropRect.bottom - offsetY) / scaleFactor).roundToInt().coerceIn(0, bitmapWidth.toInt())

        val cropWidth = (cropRight - cropLeft).coerceAtLeast(1)
        val cropHeight = (cropBottom - cropTop).coerceAtLeast(1)

        Log.d("ImageCropperVM", "크롭된 이미지의 좌표 : $cropLeft / $cropTop / $cropRight / $cropBottom")
        Log.d("ImageCropperVM", "크롭된 이미지의 크기 : $cropWidth / $cropHeight")

        return Bitmap.createBitmap(
            image.asAndroidBitmap(),
            cropLeft,
            cropTop,
            cropWidth,
            cropHeight
        )
    }

    private fun sendEffect(effect: ImageCropperContact.Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}