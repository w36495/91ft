package com.w36495.senty.view.entity

data class Friend(
    val friendDetail: FriendDetail,
    val sentGiftCount: Int = 0,
    val receivedGiftCount: Int = 0
) {
    fun displayBirthday(): String {
        val month = friendDetail.birthday.substring(0, 2)
        val day = friendDetail.birthday.substring(2, 4)

        return StringBuilder().append(month).append("월 ").append(day).append("일").toString()
    }

    override fun toString(): String {
        return "Friend(friendDetail=$friendDetail, sentGiftCount=$sentGiftCount, receivedGiftCount=$receivedGiftCount)"
    }
}