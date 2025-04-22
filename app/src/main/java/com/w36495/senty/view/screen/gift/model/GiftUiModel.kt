package com.w36495.senty.view.screen.gift.model

import com.w36495.senty.data.domain.GiftType

data class GiftUiModel(
    val id: String = "",
    val type: GiftType = GiftType.RECEIVED,
    val categoryId: String = "",
    val categoryName: String = "",
    val friendId: String = "",
    val friendName: String = "",
    val date: String = "",
    val mood: String = "",
    val memo: String = "",
    val hasImages: Boolean = false,
)