package com.w36495.senty.view.screen.gift.list.model

import com.w36495.senty.data.domain.GiftType

data class GiftListUiModel(
    val id: String,
    val type: GiftType = GiftType.RECEIVED,
    val thumbnailName: String? = null,
    val thumbnailPath: String? = null,
    val hasImageCount: Int = 0,
)