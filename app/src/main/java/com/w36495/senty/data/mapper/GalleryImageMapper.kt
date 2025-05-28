package com.w36495.senty.data.mapper

import com.w36495.senty.data.manager.galleryimage.entity.GalleryImageEntity
import com.w36495.senty.view.component.image.picker.model.GalleryImageUiModel

fun GalleryImageEntity.toUiModel() = GalleryImageUiModel(
    id = this.id,
    folderName = this.folderName,
    uri = this.uri,
    width = this.width,
    height = this.height,
    orientation = this.orientation,
)