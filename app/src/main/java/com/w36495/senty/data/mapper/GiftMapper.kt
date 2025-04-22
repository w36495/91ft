package com.w36495.senty.data.mapper

import com.w36495.senty.data.domain.GiftEntity
import com.w36495.senty.domain.entity.Gift
import com.w36495.senty.view.screen.gift.model.GiftUiModel

fun GiftEntity.toDomain(id: String) = Gift(
    id = id,
    type = this.type,
    categoryId = this.categoryId,
    categoryName = this.categoryName,
    friendId = this.friendId,
    friendName = this.friendName,
    date = this.date,
    mood = this.mood,
    memo = this.memo,
    hasImages = this.hasImages,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun Gift.toEntity() = GiftEntity(
    type = this.type,
    categoryId = this.categoryId,
    categoryName = this.categoryName,
    friendId = this.friendId,
    friendName = this.friendName,
    date = this.date,
    mood = this.mood,
    memo = this.memo,
    hasImages = this.hasImages,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun Gift.toUiModel() = GiftUiModel(
    id = id,
    type = this.type,
    categoryId = this.categoryId,
    categoryName = this.categoryName,
    friendId = this.friendId,
    friendName = this.friendName,
    date = this.date,
    mood = this.mood,
    memo = this.memo,
    hasImages = this.hasImages,
)

fun GiftUiModel.toDomain() = Gift(
    id = id,
    type = this.type,
    categoryId = this.categoryId,
    categoryName = this.categoryName,
    friendId = this.friendId,
    friendName = this.friendName,
    date = this.date,
    mood = this.mood,
    memo = this.memo,
    hasImages = this.hasImages,
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis(),
)