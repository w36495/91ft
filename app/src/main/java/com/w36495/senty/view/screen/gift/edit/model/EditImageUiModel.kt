package com.w36495.senty.view.screen.gift.edit.model

import android.graphics.Bitmap

sealed interface EditImageUiModel {
    data class Original(val path: String) : EditImageUiModel
    data class New(val bitmap: Bitmap) : EditImageUiModel
}

fun EditImageUiModel.getImageData(): Any {
    return when(this) {
        is EditImageUiModel.Original -> path
        is EditImageUiModel.New -> bitmap
    }
}