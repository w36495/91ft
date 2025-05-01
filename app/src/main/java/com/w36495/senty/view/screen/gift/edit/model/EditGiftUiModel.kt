package com.w36495.senty.view.screen.gift.edit.model

import com.w36495.senty.data.domain.GiftType

data class EditGiftUiModel(
    val id: String = "",
    val type: GiftType = GiftType.RECEIVED,
    val categoryId: String = "",
    val categoryName: String = "",
    val friendId: String = "",
    val friendName: String = "",
    val date: String = "",
    val mood: String = "",
    val memo: String = "",
    val images: LinkedHashMap<String, EditImage> = linkedMapOf(),
    val originalImages: List<String> = emptyList(),
    val thumbnail: String? = null,
)