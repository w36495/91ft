package com.w36495.senty.view.screen.gift.edit.model

import androidx.annotation.StringRes
import com.w36495.senty.R

enum class ImageSelectionType(@StringRes val title: Int, val num: Int) {
    CAMERA(
        title = R.string.gift_edit_image_selection_type_camera,
        num = 0,
    ),
    GALLERY(
        title = R.string.gift_edit_image_selection_type_gallery,
        num = 1,
    ),
}