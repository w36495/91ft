package com.w36495.senty.view.entity

import com.w36495.senty.data.domain.FriendDetailEntity
import kotlinx.serialization.Serializable

@Serializable
data class FriendDetail(
    val name: String,
    val birthday: String,
    val memo: String,
    val friendGroup: FriendGroup = FriendGroup.emptyFriendGroup,
) {
    var id: String = ""
        private set

    fun setId(id: String) {
        this.id = id
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
        groupId = friendGroup.id,
    )

    fun copy() = FriendDetail(
        name = this.name,
        birthday = this.birthday,
        memo = this.memo,
        friendGroup = this.friendGroup
    ).apply { setId(this@FriendDetail.id) }

    fun copy(friendGroup: FriendGroup) = FriendDetail(
        name = this.name,
        birthday = this.birthday,
        memo = this.memo,
        friendGroup = friendGroup
    ).apply {
        setId(this@FriendDetail.id)
    }

    companion object {
        val emptyFriendEntity = FriendDetail(
            name = "",
            birthday = "",
            memo = "",
            friendGroup = FriendGroup.emptyFriendGroup
        )
    }

    override fun toString(): String {
        return "FriendDetail(id=$id name=$name, birthday=$birthday, memo=$memo, friendGroup=$friendGroup)"
    }
}