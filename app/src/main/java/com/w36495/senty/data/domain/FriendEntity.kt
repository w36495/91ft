package com.w36495.senty.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class FriendEntity(
    val name: String = "",
    val birthday: String? = null,
    val groupId: String = "",
    val groupName: String = "",
    val groupColor: String = "",
    val memo: String = "",
    val received: Int = 0,
    val sent: Int = 0,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
)