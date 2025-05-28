package com.w36495.senty.view.component.image.picker.model

import android.net.Uri

data class GalleryImageUiModel(
    val id: Long,
    val folderName: String,
    val uri: Uri,
    val width: Int,
    val height: Int,
    val orientation: Int,
)