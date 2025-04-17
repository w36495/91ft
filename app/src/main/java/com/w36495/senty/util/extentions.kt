package com.w36495.senty.util

import android.graphics.Color


fun String.getTextColorByBackgroundColor(): Int {
    val rgb = this.toInt(16)
    val r = Color.red(rgb)
    val g = Color.green(rgb)
    val b = Color.blue(rgb)

    val y = 0.2126 * r + 0.7152 * g + 0.0722 * b

    return if (y > 128) Color.WHITE else Color.BLACK
}

object ImgConverter {
    fun bitmapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)

        return byteArrayToString(baos.toByteArray())
    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)

        return baos.toByteArray()
    }

    fun byteArrayToString(byteArray: ByteArray): String {
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun stringToByteArray(encodedString: String): ByteArray {
        return Base64.decode(encodedString, Base64.DEFAULT)
    }

    fun stringToBitmap(encodedString: String): Bitmap {
        val encodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(encodedBytes, 0, encodedBytes.size)
    }
}

fun Context.getUriFile(): Uri {
    val imagePath = File(cacheDir, "gift_image")
    if (!imagePath.exists()) imagePath.mkdirs()
    val newFile = File(imagePath, "${System.currentTimeMillis()}.jpg")

    return FileProvider.getUriForFile(this, "com.w36495.senty.fileprovider", newFile)
}

fun Context.checkCameraPermission(
    permission: Array<String>,
): Boolean {
    return permission.all {
        ContextCompat.checkSelfPermission(
            this,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }
}

/**
 * 색상의 밝기를 줄여서 더 어두운 색상을 만듭니다.
 */
fun androidx.compose.ui.graphics.Color.darken(factor: Float = 0.2f): androidx.compose.ui.graphics.Color {
    return Color(
        red = (red * (1 - factor)).coerceIn(0f, 1f),
        green = (green * (1 - factor)).coerceIn(0f, 1f),
        blue = (blue * (1 - factor)).coerceIn(0f, 1f),
        alpha = alpha
    )
}

fun Modifier.dropShadow(
    shape: Shape,
    color: Color = Color(0x29000000),
    blur: Dp = 4.dp,
    offsetY: Dp = 4.dp,
    offsetX: Dp = 0.dp,
    spread: Dp = 0.dp
) = this.drawBehind {
    val shadowSize = Size(size.width + spread.toPx(), size.height + spread.toPx())
    val shadowOutline = shape.createOutline(shadowSize, layoutDirection, this)

    val paint = Paint().apply {
        this.color = color
    }

    if (blur.toPx() > 0) {
        paint.asFrameworkPaint().apply {
            maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
        }
    }

    drawIntoCanvas { canvas ->
        canvas.save()
        canvas.translate(offsetX.toPx(), offsetY.toPx())
        canvas.drawOutline(shadowOutline, paint)
        canvas.restore()
    }
}