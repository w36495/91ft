package com.w36495.senty.view.screen.gift.edit.model

import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.view.screen.gift.model.SimpleFriendUiModel

data class EditGiftUiModel(
    val id: String = "",
    val type: GiftType = GiftType.RECEIVED,
    val categoryId: String = "",
    val categoryName: String = "",
    val friendId: String = "",
    val friendName: String = "",
    val friends: List<SimpleFriendUiModel> = emptyList(),
    val date: String = "",
    val mood: String = "",
    val memo: String = "",
    val images: LinkedHashMap<String, EditImageUiModel> = linkedMapOf(),
    val originalImages: List<String> = emptyList(),
    val thumbnail: String? = null,
)