package com.w36495.senty.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class FriendGroupEntity(
    val name: String = "",
    val color: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
)