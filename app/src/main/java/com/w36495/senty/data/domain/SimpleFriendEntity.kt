package com.w36495.senty.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class SimpleFriendEntity(
    val id: String = "",
    val name: String = "",
)