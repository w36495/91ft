package com.w36495.senty.data.domain

import com.w36495.senty.util.DateUtil
import com.w36495.senty.view.entity.FriendDetail
import com.w36495.senty.view.entity.gift.GiftCategory
import com.w36495.senty.view.entity.gift.GiftDetail
import com.w36495.senty.view.entity.gift.GiftType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class GiftDetailEntity(
    val categoryId: String,
    val friendId: String,
    val date: String,
    val mood: String,
    val memo: String,
    val shareFlag: Boolean = false,
    val giftType: GiftType = GiftType.RECEIVED,
    @JsonNames("create_at")
    val createAt: String = DateUtil.toTimeStamp(System.currentTimeMillis()),
    @JsonNames("update_at")
    val updateAt: String = DateUtil.toTimeStamp(System.currentTimeMillis()),
) {
    fun toDomainEntity() = GiftDetail(
        category = GiftCategory.emptyCategory,
        friend = FriendDetail.emptyFriendEntity,
        date = date,
        mood = mood,
        memo = memo,
        shareFlag = shareFlag,
        giftType = giftType
    )
}