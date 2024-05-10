package com.w36495.senty.data.domain

import com.w36495.senty.view.entity.FriendGroup
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class FriendGroupEntity(
    val name: String,
    val color: String,
    @JsonNames("create_at")
    val createAt: String,
    @JsonNames("update_at")
    val updateAt: String
) {
    var id: String = ""
        private set

    fun setId(id: String) {
        this.id = id
    }

    fun toDomainModel(): FriendGroup = FriendGroup(
        id = this.id,
        name = this.name,
        color = this.color
    )
}