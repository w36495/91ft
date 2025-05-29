package com.w36495.senty.data.manager.galleryimage.entity

import android.net.Uri

data class GalleryFolderEntity(
    val name: String,
    val thumbnailUri: Uri,
    val count: Int,
)