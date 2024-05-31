package com.w36495.senty.view.entity

import com.w36495.senty.data.domain.FriendDetailEntity
import kotlinx.serialization.Serializable

@Serializable
data class FriendDetail(
    val name: String,
    val birthday: String,
    val memo: String,
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

    fun toDataEntity(): FriendDetailEntity = FriendDetailEntity(
        name = name,
        birthday = birthday,
        memo = memo,
        groupId = group!!.id,
    )

    fun copy() = FriendDetail(name = this@FriendDetail.name, birthday = this@FriendDetail.birthday, memo = this@FriendDetail.memo).apply {
        setId(this@FriendDetail.id)
    }

    companion object {
        val emptyFriendEntity = FriendDetail(name = "", birthday = "", memo = "")
    }
}