package com.w36495.senty.domain.entity

data class Schedule(
    val id: String,
    val title: String,
    val date: String,
    val location: String,
    val time: String,
    val memo: String,
    val friends: List<ScheduleFriend>,
    val createdAt: Long,
    val updatedAt: Long,
)

data class ScheduleFriend(
    val id: String,
    val name: String,
)