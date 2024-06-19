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