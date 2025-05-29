package com.w36495.senty.data.mapper

import com.w36495.senty.data.manager.galleryimage.entity.GalleryFolderEntity
import com.w36495.senty.view.component.image.picker.model.GalleryFolderUiModel

fun GalleryFolderEntity.toUiModel() = GalleryFolderUiModel(
    name = this.name,
    thumbnailUri = this.thumbnailUri,
    count = this.count,
)