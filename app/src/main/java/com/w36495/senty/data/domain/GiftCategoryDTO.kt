package com.w36495.senty.data.domain

import com.w36495.senty.util.DateUtil
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class GiftCategoryPatchDTO(
    val name: String,
    @JsonNames("update_at")
    val updateAt: String = DateUtil.toTimeStamp(System.currentTimeMillis())
)