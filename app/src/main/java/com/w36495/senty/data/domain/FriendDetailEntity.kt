package com.w36495.senty.data.domain

import com.w36495.senty.util.DateUtil
import com.w36495.senty.view.entity.FriendDetail
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class FriendDetailEntity(
    val id: String = "",
    val name: String,
    val groupId: String,
    val birthday: String,
    val memo: String,
    @JsonNames("create_at")
    val createAt: String = DateUtil.toTimeStamp(System.currentTimeMillis()),
    @JsonNames("update_at")
    val updateAt: String = DateUtil.toTimeStamp(System.currentTimeMillis())
) {
    fun toDomainEntity(): FriendDetail {
        val friendEntity = FriendDetail(
            name = name,
            birthday = birthday,
            memo = memo
        ).apply {
            setId(this@FriendDetailEntity.id)
        }

        return friendEntity
    }
}