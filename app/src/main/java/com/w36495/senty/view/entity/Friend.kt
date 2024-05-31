package com.w36495.senty.view.entity

data class Friend(
    val id: String,
    val name: String,
    val birthday: String,
    val memo: String,
    val group: FriendGroup,
    val sentGiftCount: Int = 0,
    val receivedGiftCount: Int = 0
) {
    fun displayBirthday(): String {
        val month = birthday.substring(0, 2)
        val day = birthday.substring(2, 4)

        return StringBuilder().append(month).append("월 ").append(day).append("일").toString()
    }

    fun toFriendDetail() = FriendDetail(name = this.name, birthday = this.birthday, memo = this.memo).apply {
        setId(this@Friend.id)
    }

    companion object {
        val emptyFriend = Friend(id = "", name = "", birthday = "", memo = "", group = FriendGroup.emptyFriendGroup)
    }
}