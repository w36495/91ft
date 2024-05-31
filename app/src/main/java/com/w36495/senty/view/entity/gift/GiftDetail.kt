package com.w36495.senty.view.entity.gift

import com.w36495.senty.view.entity.FriendDetail
import kotlinx.serialization.Serializable

@Serializable
data class GiftDetail(
    val category: GiftCategory,
    val friend: FriendDetail,
    val gift: GiftDetailEntity,
    val imgPath: String = "",
) {
    companion object {
        val emptyGiftDetail = GiftDetail(
            category = GiftCategory.emptyCategory,
            friend = FriendDetail.emptyFriendEntity,
            gift = GiftDetailEntity.emptyGiftDetail
        )
    }
}