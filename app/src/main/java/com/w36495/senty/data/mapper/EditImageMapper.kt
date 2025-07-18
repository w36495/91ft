package com.w36495.senty.data.mapper

import android.content.Context
import com.w36495.senty.domain.entity.EditImage
import com.w36495.senty.util.ImageConverter
import com.w36495.senty.view.screen.gift.edit.model.EditImageUiModel

fun EditImageUiModel.New.toDomain(context: Context): EditImage.New {
    val resizedThumbnail = ImageConverter.resizeToWidth(context, this.bitmap, 600)
    val byteArray = ImageConverter.compressToWebP(resizedThumbnail)

    return EditImage.New(byteArray)
}

fun EditImageUiModel.Original.toDomain(): EditImage.Original {
    return EditImage.Original(this.path)
}