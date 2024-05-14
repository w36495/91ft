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