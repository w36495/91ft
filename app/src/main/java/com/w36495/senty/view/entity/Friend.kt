package com.w36495.senty.view.entity

data class Friend(
    val friendDetail: FriendDetail,
    val sentGiftCount: Int = 0,
    val receivedGiftCount: Int = 0
) {
    override fun toString(): String {
        return "Friend(friendDetail=$friendDetail, sentGiftCount=$sentGiftCount, receivedGiftCount=$receivedGiftCount)"
    }
}