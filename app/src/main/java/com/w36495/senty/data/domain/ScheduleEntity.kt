package com.w36495.senty.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleEntity(
    val title: String = "",
    val date: String = "",
    val location: String = "",
    val time: String = "",
    val memo: String = "",
    val friends: List<ScheduleFriendEntity> = emptyList(),
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
)

@Serializable
data class ScheduleFriendEntity(
    val friendId: String = "",
    val friendName: String = "",
)