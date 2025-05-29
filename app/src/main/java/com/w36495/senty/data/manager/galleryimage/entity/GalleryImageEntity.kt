package com.w36495.senty.data.manager.galleryimage.entity

import android.net.Uri

data class GalleryImageEntity(
    val id: Long,
    val folderName: String,
    val uri: Uri,
    val width: Int,
    val height: Int,
    val orientation: Int,
)