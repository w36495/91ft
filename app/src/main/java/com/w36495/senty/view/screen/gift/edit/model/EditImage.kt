package com.w36495.senty.view.screen.gift.edit.model

import android.graphics.Bitmap

sealed interface EditImage {
    data class Original(val path: String) : EditImage
    data class New(val bitmap: Bitmap) : EditImage
}

fun EditImage.getImageData(): Any {
    return when(this) {
        is EditImage.Original -> path
        is EditImage.New -> bitmap
    }
}