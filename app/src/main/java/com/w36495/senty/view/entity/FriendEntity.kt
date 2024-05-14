package com.w36495.senty.view.entity

import com.w36495.senty.data.domain.FriendEntity

data class FriendEntity(
    val name: String,
    val birthday: String,
    val memo: String,
    val sentGiftCount: Int = 0,
    val receivedGiftCount: Int = 0,
) {
    var id: String = ""
        private set

    var group: FriendGroup? = null
        private set
    fun setId(id: String) {
        this.id = id
    }
    fun setFriendGroup(group: FriendGroup) {
        this.group = group
    }

    fun displayBirthday(): String {
        return StringBuilder().append(birthday.substring(0, 2)).append("월 ").append(birthday.substring(2, 4)).append("일").toString()
    }

    fun toDataEntity(): FriendEntity = FriendEntity(
        name = name,
        birthday = birthday,
        memo = memo,
        groupId = group!!.id,
    )
}