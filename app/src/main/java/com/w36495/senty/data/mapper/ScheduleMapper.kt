package com.w36495.senty.data.mapper

import com.w36495.senty.data.domain.ScheduleEntity
import com.w36495.senty.data.domain.ScheduleFriendEntity
import com.w36495.senty.domain.entity.Schedule
import com.w36495.senty.domain.entity.ScheduleFriend
import com.w36495.senty.view.screen.anniversary.model.ScheduleFriendUiModel
import com.w36495.senty.view.screen.anniversary.model.ScheduleUiModel

fun ScheduleEntity.toDomain(id: String) = Schedule(
    id = id,
    title = this.title,
    date = this.date,
    location = this.location,
    time = this.time,
    memo = this.memo,
    friends = this.friends.map { it.toDomain() },
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun Schedule.toEntity() = ScheduleEntity(
    title = this.title,
    location = this.location,
    date = this.date,
    time = this.time,
    memo = this.memo,
    friends = this.friends.map { it.toEntity() },
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)
fun Schedule.toUiModel() = ScheduleUiModel(
    id = this.id,
    title = this.title,
    date = this.date,
    location = this.location,
    time = this.time,
    memo = this.memo,
    friends = this.friends.map { it.toUiModel() },
)

fun ScheduleUiModel.toDomain() = Schedule(
    id = this.id,
    title = this.title,
    date = this.date,
    location = this.location,
    time = this.time,
    memo = this.memo,
    friends = this.friends.map { it.toDomain() },
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis(),
)

fun ScheduleFriendEntity.toDomain() = ScheduleFriend(
    id = this.friendId,
    name = this.friendName,
)

fun ScheduleFriend.toEntity() = ScheduleFriendEntity(
    friendId = this.id,
    friendName = this.name,
)

fun ScheduleFriend.toUiModel() = ScheduleFriendUiModel(
    id = this.id,
    name = this.name,
)

fun ScheduleFriendUiModel.toDomain() = ScheduleFriend(
    id = this.id,
    name = this.name,
)