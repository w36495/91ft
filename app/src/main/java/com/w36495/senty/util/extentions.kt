package com.w36495.senty.util

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import kotlin.math.min


fun String.getTextColorByBackgroundColor(): Int {
    val rgb = androidx.compose.ui.graphics.Color(this.toULong())
    val r = rgb.red
    val g = rgb.green
    val b = rgb.blue

    val y = 0.2126 * r + 0.7152 * g + 0.0722 * b

    return if (y > 128) Color.White.toArgb() else Color.Black.toArgb()
}

enum class ImageSize(val size: Int) {
    THUMBNAIL(200),
    NORMAL(1480),
}
object ImageConverter {
    /**
     * 카메라/갤러리로부터 받은 Uri를 Bitmap으로 변환하는 함수
     */
    fun uriToBitmap(context: Context, uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

    /**
     * URL을 Bitmap으로 변환하는 함수
     */
    suspend fun urlToBitmap(url: String): Bitmap? {
        return try {
            val url = URL(url)
            val stream = url.openStream()

            BitmapFactory.decodeStream(stream)
        } catch (e: MalformedURLException) {
            Log.d("ImageConverter", "MalformedURLException: ${e.stackTraceToString()}")
            null
        } catch (e: Exception) {
            Log.d("ImageConverter", "Exception: ${e.stackTraceToString()}")
            null
        }
    }

    /**
     * 화면 해상도에 맞추어 Bitmap을 리사이징 합니다.
     */
    fun resizeToWidth(
        context: Context,
        original: Bitmap,
        maxWidth: Int = 1080,
        cropToSquare: Boolean = true,
    ): Bitmap {
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels.coerceAtMost(maxWidth)

        val resizedBitmap = if (original.width <= screenWidth) {
            original
        } else {
            val aspectRatio = original.width.toFloat() / original.height
            val resizedHeight = (screenWidth / aspectRatio).toInt()
            Bitmap.createScaledBitmap(original, screenWidth, resizedHeight, true)
        }

        return if (cropToSquare) {
            cropCenterSquare(resizedBitmap)
        } else {
            resizedBitmap
        }
    }

    private fun cropCenterSquare(bitmap: Bitmap): Bitmap {
        val size = min(bitmap.width, bitmap.height)
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2
        return Bitmap.createBitmap(bitmap, x, y, size, size)
    }

    /**
     * Bitmap을 WebP로 압축하여 ByteArray로 변환하는 함수입니다.
     */
    fun compressToWebP(bitmap: Bitmap, quality: Int = 90): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSLESS
        } else Bitmap.CompressFormat.WEBP

        bitmap.compress(format, quality, outputStream)

        return outputStream.toByteArray()
    }
    fun bitmapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)

        return byteArrayToString(baos.toByteArray())
    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()

        val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSLESS
        } else Bitmap.CompressFormat.WEBP

        bitmap.compress(format, 100, baos)

        return baos.toByteArray()
    }

    fun createThumbnailImage(originalBitmap: Bitmap): ByteArray {
        val bitmap = resizeBitmapToSquare(originalBitmap, ImageSize.THUMBNAIL.size)
        return bitmapToByteArray(bitmap)
    }

    fun createNormalImage(originalBitmap: Bitmap): ByteArray {
        val bitmap = if (originalBitmap.width < ImageSize.NORMAL.size && originalBitmap.height < ImageSize.NORMAL.size) {
            resizeBitmapToSquare(originalBitmap, originalBitmap.width.coerceAtMost(originalBitmap.height))
        } else resizeBitmapToSquare(originalBitmap, ImageSize.NORMAL.size)

        return bitmapToByteArray(bitmap)
    }

    fun resizeBitmapToSquare(bitmap: Bitmap, size: Int): Bitmap {
        val minSide = bitmap.width.coerceAtMost(bitmap.height)

        // 정사각형으로 자르기 (중앙 기준)
        val xOffset = (bitmap.width - minSide) / 2
        val yOffset = (bitmap.height - minSide) / 2
        val cropped = Bitmap.createBitmap(bitmap, xOffset, yOffset, minSide, minSide)

        // 리사이즈
        return Bitmap.createScaledBitmap(cropped, size, size, true)
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

@Composable
fun getScreenWidthPx(): Int {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    return with(density) {
        configuration.screenWidthDp.dp.roundToPx()
    }
}

fun <K, V> Map<K, V>.toLinkedMap(): LinkedHashMap<K, V> = LinkedHashMap(this)
