package com.w36495.senty.data.domain

import com.w36495.senty.util.DateUtil
import com.w36495.senty.view.entity.FriendGroup
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class FriendGroupEntity(
    val id: String = "",
    val name: String,
    val color: String,
    @JsonNames("create_at")
    val createAt: String = DateUtil.toTimeStamp(System.currentTimeMillis()),
    @JsonNames("update_at")
    val updateAt: String = DateUtil.toTimeStamp(System.currentTimeMillis()),
) {
    fun toDomainModel(): FriendGroup = FriendGroup(
        name = this.name,
        color = this.color
    ).apply { setId(this@FriendGroupEntity.id) }
}