package com.w36495.senty.data.domain

import com.w36495.senty.util.DateUtil
import com.w36495.senty.view.entity.gift.GiftCategory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class GiftCategoryEntity(
    val id: String = "",
    val name: String,
    @JsonNames("create_at")
    val createAt: String = DateUtil.toTimeStamp(System.currentTimeMillis()),
    @JsonNames("update_at")
    val updateAt: String = DateUtil.toTimeStamp(System.currentTimeMillis())
) {
    fun toDomainEntity() = GiftCategory(name = this.name).apply { setId(this.id) }
}