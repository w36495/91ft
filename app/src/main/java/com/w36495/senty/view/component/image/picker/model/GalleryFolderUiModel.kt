package com.w36495.senty.view.component.image.picker.model

import android.net.Uri

data class GalleryFolderUiModel(
    val name: String,
    val thumbnailUri: Uri,
    val count: Int,
)