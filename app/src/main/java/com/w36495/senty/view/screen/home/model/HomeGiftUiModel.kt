package com.w36495.senty.view.screen.home.model

import com.w36495.senty.data.domain.GiftType

data class HomeGiftUiModel(
    val id: String = "",
    val type: GiftType = GiftType.RECEIVED,
    val friendName: String = "",
    val date: String = "",
    val thumbnailName: String? = null,
    val thumbnailPath: String? = null,
    val hasImageCount: Int = 0,
)