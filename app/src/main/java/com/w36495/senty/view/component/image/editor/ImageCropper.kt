package com.w36495.senty.view.component.image.editor

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import com.w36495.senty.util.ImageConverter
import com.w36495.senty.view.ui.theme.SentyBlack
import com.w36495.senty.view.ui.theme.SentyWhite
import kotlin.math.min
import kotlin.math.roundToInt

enum class Corner {
    TopLeft,
    TopRight,
    BottomLeft,
    BottomRight,
}

private const val CROP_MIN_WIDTH = 200f

@Composable
fun ImageCropper(
    imageUri: Uri,
    imageOffset: Offset,
    imageSize: IntSize,
    imageRect: Rect,
    isEditing: Boolean,
    isFlippedVertical: Boolean,
    isFlippedHorizontal: Boolean,
    onCompleteCrop: (Bitmap) -> Unit,
) {
    val context = LocalContext.current

    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var cropBoxConstraints by remember { mutableStateOf<Constraints?>(null) }

    LaunchedEffect(imageUri) {
        imageBitmap = ImageConverter.uriToBitmap(context, imageUri)?.let {
            ImageConverter.resizeToWidth(context, it)
        }?.asImageBitmap()
    }

    var image by remember { mutableStateOf(imageBitmap) }
    var topLeft by remember { mutableStateOf(Offset(imageOffset.x, imageOffset.y)) }
    var topRight by remember { mutableStateOf(Offset(imageOffset.x + imageSize.width, imageOffset.y)) }
    var bottomLeft by remember { mutableStateOf(Offset(imageOffset.x, imageOffset.y + imageSize.width)) }
    var bottomRight by remember { mutableStateOf(Offset(imageOffset.x + imageSize.width, imageOffset.y + imageSize.width)) }

    var draggingCorner by remember { mutableStateOf<Corner?>(null) }
    var draggingCenter by remember { mutableStateOf(false) }

    LaunchedEffect(isEditing) {
        if (!isEditing) {
            cropBoxConstraints?.let { constraints ->
                imageBitmap?.let {
                    val croppedBitmap = getCroppedBitmap(
                        image = it,
                        cropRect = Rect(topLeft, bottomRight),
                        canvasWidth = constraints.maxWidth.toFloat(),
                        canvasHeight = constraints.maxHeight.toFloat(),
                        isFlippedVertical = isFlippedVertical,
                        isFlippedHorizontal = isFlippedHorizontal,
                    )

                    image = croppedBitmap.asImageBitmap()
                    onCompleteCrop(it.asAndroidBitmap())
                }
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        cropBoxConstraints = this.constraints

        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        draggingCorner = when {
                            offset.isNear(topLeft) -> Corner.TopLeft
                            offset.isNear(topRight) -> Corner.TopRight
                            offset.isNear(bottomLeft) -> Corner.BottomLeft
                            offset.isNear(bottomRight) -> Corner.BottomRight
                            else -> null
                        }

                        draggingCenter = draggingCorner == null &&
                                Rect(topLeft, bottomRight).contains(offset)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()

                        when (draggingCorner) {
                            Corner.TopLeft -> {
                                val width = bottomRight.x - (topLeft.x + dragAmount.x)
                                val height = bottomRight.y - (topLeft.y + dragAmount.y)
                                val minSize = min(width, height)

                                val newTopLeft = Offset(
                                    bottomRight.x - minSize.getNewSize(imageSize),
                                    bottomRight.y - minSize.getNewSize(imageSize)
                                )

                                // 드래그 시, 이미지 영역 안에서만 드래그 되도록 설정
                                topLeft = newTopLeft.coerceIn(imageRect)
                                topRight = Offset(bottomRight.x, newTopLeft.y)
                                bottomLeft = Offset(newTopLeft.x, bottomRight.y)
                            }

                            Corner.TopRight -> {
                                val width = (topRight.x + dragAmount.x) - bottomLeft.x
                                val height = bottomLeft.y - (topRight.y + dragAmount.y)
                                val minSize = min(width, height)

                                val newTopRight = Offset(
                                    bottomLeft.x + minSize.getNewSize(imageSize),
                                    bottomLeft.y - minSize.getNewSize(imageSize)
                                )

                                // 드래그 시, 이미지 영역 안에서만 드래그 되도록 설정
                                topRight = newTopRight.coerceIn(imageRect)
                                topLeft = Offset(bottomLeft.x, newTopRight.y)
                                bottomRight = Offset(newTopRight.x, bottomLeft.y)
                            }

                            Corner.BottomLeft -> {
                                val width = topRight.x - (bottomLeft.x + dragAmount.x)
                                val height = (bottomLeft.y + dragAmount.y) - topRight.y
                                val minSize = min(width, height)

                                val newBottomLeft = Offset(
                                    topRight.x - minSize.getNewSize(imageSize),
                                    topRight.y + minSize.getNewSize(imageSize)
                                )

                                // 드래그 시, 이미지 영역 안에서만 드래그 되도록 설정
                                bottomLeft = newBottomLeft.coerceIn(imageRect)
                                bottomRight = Offset(topRight.x, newBottomLeft.y)
                                topLeft = Offset(newBottomLeft.x, topRight.y)
                            }

                            Corner.BottomRight -> {
                                val width = (bottomRight.x + dragAmount.x) - topLeft.x
                                val height = (bottomRight.y + dragAmount.y) - topLeft.y
                                val minSize = min(width, height)

                                val newBottomRight = Offset(
                                    topLeft.x + minSize.getNewSize(imageSize),
                                    topLeft.y + minSize.getNewSize(imageSize)
                                )

                                // 드래그 시, 이미지 영역 안에서만 드래그 되도록 설정
                                bottomRight = newBottomRight.coerceIn(imageRect)
                                bottomLeft = Offset(topLeft.x, newBottomRight.y)
                                topRight = Offset(newBottomRight.x, topLeft.y)
                            }

                            // 코너를 잡고 이동시킬때가 아닌 그냥 이동시킬때는 여기서 !
                            null -> {
                                val dx = dragAmount.x
                                val dy = dragAmount.y

                                val maxLeft = imageRect.left - topLeft.x
                                val maxTop = imageRect.top - topLeft.y
                                val maxRight = imageRect.right - bottomRight.x
                                val maxBottom = imageRect.bottom - bottomRight.y

                                val safeDx = dx.coerceIn(maxLeft, maxRight)
                                val safeDy = dy.coerceIn(maxTop, maxBottom)

                                val safeOffset = Offset(safeDx, safeDy)

                                topLeft += safeOffset
                                topRight += safeOffset
                                bottomLeft += safeOffset
                                bottomRight += safeOffset
                            }
                        }
                    },
                    onDragEnd = {
                        draggingCenter = false
                        draggingCorner = null
                    }
                )
            }
        ) {
            val cropWidth = topRight.x - topLeft.x
            val cropHeight = bottomLeft.y - topLeft.y

            val cropRect = Rect(topLeft, Size(cropWidth, cropHeight))

            val rectSize = Size(
                width = topRight.x - topLeft.x,
                height = bottomLeft.y - topLeft.y
            )

            val maskColor = SentyBlack.copy(alpha = 0.5f)

            // 1. 상단 영역
            drawRect(
                color = maskColor,
                topLeft = Offset.Zero,
                size = Size(size.width, cropRect.top)
            )

            // 2. 하단 영역
            drawRect(
                color = maskColor,
                topLeft = Offset(0f, cropRect.bottom),
                size = Size(size.width, size.height - cropRect.bottom)
            )

            // 3. 좌측 영역
            drawRect(
                color = maskColor,
                topLeft = Offset(0f, cropRect.top),
                size = Size(cropRect.left, cropHeight)
            )

            // 4. 우측 영역
            drawRect(
                color = maskColor,
                topLeft = Offset(cropRect.right, cropRect.top),
                size = Size(size.width - cropRect.right, cropHeight)
            )

            // 크롭 박스
            drawRect(
                color = SentyWhite,
                topLeft = topLeft,
                size = cropRect.size,
                style = Stroke(4f)
            )

            drawHandle(topLeft)
            drawHandle(topRight)
            drawHandle(bottomLeft)
            drawHandle(bottomRight)
        }
    }
}

fun Offset.isNear(point: Offset, threshold: Float = 50f): Boolean {
    return (this - point).getDistance() <= threshold
}

fun DrawScope.drawHandle(center: Offset) {
    drawCircle(color = SentyWhite, radius = 25f, center = center)
}

fun Offset.coerceIn(rect: Rect): Offset {
    return Offset(
        x.coerceIn(rect.left, rect.right),
        y.coerceIn(rect.top, rect.bottom)
    )
}

// 드래그 시, 크롭 박스가 이미지 영역보다 넘어가지 않도록 설정 및 최소 크기 설정
fun Float.getNewSize(imageSize: IntSize): Float {
    return when {
        this >= imageSize.width -> imageSize.width.toFloat()
        this <= CROP_MIN_WIDTH -> CROP_MIN_WIDTH
        else -> this
    }
}

fun getCroppedBitmap(
    image: ImageBitmap,
    isFlippedVertical: Boolean,
    isFlippedHorizontal: Boolean,
    cropRect: Rect,
    canvasWidth: Float,
    canvasHeight: Float
): Bitmap {
    val bitmapWidth = image.width.toFloat()
    val bitmapHeight = image.height.toFloat()

    val matrix = android.graphics.Matrix().apply {
        preScale(
            if (isFlippedHorizontal) -1f else 1f,
            if (isFlippedVertical) -1f else 1f
        )
    }

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
        ((cropRect.top - offsetY) / scaleFactor).roundToInt().coerceIn(0, bitmapHeight.toInt())
    val cropRight =
        ((cropRect.right - offsetX) / scaleFactor).roundToInt().coerceIn(0, bitmapWidth.toInt())
    val cropBottom =
        ((cropRect.bottom - offsetY) / scaleFactor).roundToInt().coerceIn(0, bitmapHeight.toInt())

    val cropWidth = (cropRight - cropLeft).coerceAtLeast(1)
    val cropHeight = (cropBottom - cropTop).coerceAtLeast(1)

    return Bitmap.createBitmap(
        image.asAndroidBitmap(),
        cropLeft,
        cropTop,
        cropWidth,
        cropHeight
    )
}