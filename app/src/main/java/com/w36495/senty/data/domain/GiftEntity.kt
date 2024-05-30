package com.w36495.senty.data.domain

import com.w36495.senty.util.DateUtil
import com.w36495.senty.view.entity.gift.GiftType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class GiftEntity(
    val id: String = "",
    val categoryId: String,
    val friendId: String,
    val date: String,
    val mood: String,
    val memo: String,
    val imgUri: String = "",
    val shareFlag: Boolean = false,
    val giftType: GiftType = GiftType.RECEIVED,
    @JsonNames("create_at")
    val createAt: String = DateUtil.toTimeStamp(System.currentTimeMillis()),
    @JsonNames("update_at")
    val updateAt: String = DateUtil.toTimeStamp(System.currentTimeMillis()),
) {
    fun toDomainEntity() = com.w36495.senty.view.entity.gift.GiftDetailEntity(
        categoryId = categoryId,
        friendId = friendId,
        date = date,
        mood = mood,
        memo = memo,
        imgUri = imgUri,
        shareFlag = shareFlag,
        giftType = giftType
    ).apply {
        setId(this@GiftEntity.id)
    }
}