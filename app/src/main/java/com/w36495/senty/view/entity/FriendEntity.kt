package com.w36495.senty.view.entity

import com.w36495.senty.data.domain.FriendEntity
import kotlinx.serialization.Serializable

@Serializable
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
        val month = birthday.substring(0, 2)
        val day = birthday.substring(2, 4)

        return StringBuilder().append(month).append("월 ").append(day).append("일").toString()
    }

    fun toDataEntity(): FriendEntity = FriendEntity(
        name = name,
        birthday = birthday,
        memo = memo,
        groupId = group!!.id,
    )

    companion object {
        val emptyFriendEntity = FriendEntity(name = "", birthday = "", memo = "")
    }
}