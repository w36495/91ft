package com.w36495.senty.view.entity.gift

import kotlinx.serialization.Serializable

@Serializable
data class Gift(
    val giftDetail: GiftDetail,
    val giftImages: List<String> = emptyList()
) {
    companion object {
        val emptyGift = Gift(giftDetail = GiftDetail.emptyGiftDetail)
    }
}