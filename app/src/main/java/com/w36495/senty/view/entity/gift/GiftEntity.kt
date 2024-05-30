package com.w36495.senty.view.entity.gift

import com.w36495.senty.view.entity.FriendDetail

data class GiftEntity(
    val gift: GiftDetailEntity,
    val friend: FriendDetail,
    val giftImg: String = ""
)