package com.w36495.senty.data.domain

import com.w36495.senty.util.DateUtil
import com.w36495.senty.view.entity.FriendEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class FriendEntity(
    val name: String,
    val groupId: String,
    val birthday: String,
    val memo: String,
    @JsonNames("create_at")
    val createAt: String = DateUtil.toTimeStamp(System.currentTimeMillis()),
    @JsonNames("update_at")
    val updateAt: String = DateUtil.toTimeStamp(System.currentTimeMillis())
) {
    var id: String = ""
        private set

    fun setId(id: String) {
        this.id = id
    }

    fun toDomainEntity(): FriendEntity {
        val friendEntity = FriendEntity(
            name = name,
            groupId = groupId,
            birthday = birthday,
            memo = memo
        )

        friendEntity.setId(this.id)

        return friendEntity
    }
}