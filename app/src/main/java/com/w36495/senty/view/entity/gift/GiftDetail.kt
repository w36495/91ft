package com.w36495.senty.view.entity.gift

import com.w36495.senty.view.entity.FriendDetail
import kotlinx.serialization.Serializable

@Serializable
data class GiftDetail(
    val category: GiftCategory,
    val friend: FriendDetail,
    val date: String,
    val mood: String,
    val memo: String,
    val shareFlag: Boolean = false,
    val giftType: GiftType = GiftType.RECEIVED,
) {
    var id: String = ""
        private set

    fun setId(id: String) {
        this.id = id
    }

    fun toDataEntity() = com.w36495.senty.data.domain.GiftDetailEntity(
        friendId = friend.id,
        categoryId = category.id,
        date = date,
        mood = mood,
        memo = memo,
        giftType = giftType
    )

    fun copy(friend: FriendDetail) = GiftDetail(
        category = category,
        friend = friend,
        date = date,
        mood = mood,
        memo = memo,
        shareFlag = shareFlag,
        giftType = giftType
    ).apply { setId(this@GiftDetail.id) }

    fun copy(category: GiftCategory, friend: FriendDetail) = GiftDetail(
        category = category,
        friend = friend,
        date = date,
        mood = mood,
        memo = memo,
        shareFlag = shareFlag,
        giftType = giftType
    ).apply { setId(this@GiftDetail.id) }

    override fun toString(): String {
        return "GiftDetail(id=$id, category=$category, friend=$friend, date='$date', mood='$mood', memo='$memo', shareFlag=$shareFlag, giftType=$giftType)"
    }

    companion object {
        val emptyGiftDetail = GiftDetail(
            friend = FriendDetail.emptyFriendEntity,
            category = GiftCategory.emptyCategory,
            date = "",
            mood = "",
            memo = "",
        )
    }
}