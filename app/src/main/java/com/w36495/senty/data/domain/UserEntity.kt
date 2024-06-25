package com.w36495.senty.data.domain

import com.w36495.senty.util.DateUtil
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    val uid: String?,
    val createAt: String = DateUtil.toTimeStamp(System.currentTimeMillis())
)