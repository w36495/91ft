package com.w36495.senty.data.mapper

import com.w36495.senty.data.domain.FriendEntity
import com.w36495.senty.domain.entity.Friend
import com.w36495.senty.view.screen.friend.model.FriendUiModel

fun FriendEntity.toDomain(id: String) = Friend(
    id = id,
    name = this.name,
    birthday = this.birthday,
    groupId = this.groupId,
    groupName = this.groupName,
    groupColor = this.groupColor,
    memo = this.memo,
    received = this.received,
    sent = this.sent,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun Friend.toUiModel() = FriendUiModel(
    id = this.id,
    name = this.name,
    birthday = this.birthday,
    groupId = this.groupId,
    groupName = this.groupName,
    groupColor = this.groupColor.toComposeColor(),
    memo = this.memo,
    received = this.received,
    sent = this.sent,
)

fun Friend.toEntity() = FriendEntity(
    name = this.name,
    birthday = this.birthday,
    groupId = this.groupId,
    groupName = this.groupName,
    groupColor = this.groupColor,
    memo = this.memo,
    received = this.received,
    sent = this.sent,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun FriendUiModel.toDomain() = Friend(
    id = this.id,
    name = this.name,
    birthday = this.birthday,
    groupId = this.groupId,
    groupName = this.groupName,
    groupColor = this.groupColor.toHexColor(),
    memo = this.memo,
    received = this.received,
    sent = this.sent,
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis(),
)