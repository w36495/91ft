package com.w36495.senty.view.entity.gift

data class GiftDetailEntity(
    val friendId: String,
    val categoryId: String,
    val date: String,
    val mood: String,
    val memo: String,
    val imgUri: String = "",
    val shareFlag: Boolean = false,
    val giftType: GiftType = GiftType.RECEIVED,
) {
    var id: String = ""
        private set

    fun setId(id: String) {
        this.id = id
    }

    fun toDataEntity() = com.w36495.senty.data.domain.GiftEntity(
        friendId = friendId,
        categoryId = categoryId,
        date = date,
        mood = mood,
        memo = memo,
        imgUri = imgUri,
        giftType = giftType
    )

    companion object {
        val emptyGiftDetail = GiftDetailEntity(
            friendId = "",
            categoryId = "",
            date = "",
            mood = "",
            memo = "",
            imgUri = "",
        )
    }
}