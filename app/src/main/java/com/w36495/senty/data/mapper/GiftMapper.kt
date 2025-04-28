package com.w36495.senty.data.mapper

import com.w36495.senty.data.domain.GiftEntity
import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.domain.entity.Gift
import com.w36495.senty.view.screen.friend.detail.model.FriendDetailGiftUiModel
import com.w36495.senty.view.screen.gift.edit.model.EditGiftUiModel
import com.w36495.senty.view.screen.gift.list.model.GiftListUiModel
import com.w36495.senty.view.screen.gift.model.GiftUiModel
import com.w36495.senty.view.screen.home.model.HomeGiftUiModel

fun GiftEntity.toDomain(id: String) = Gift(
    id = id,
    type = if (this.type == 0) GiftType.RECEIVED else GiftType.SENT,
    categoryId = this.categoryId,
    categoryName = this.categoryName,
    friendId = this.friendId,
    friendName = this.friendName,
    date = this.date,
    mood = this.mood,
    memo = this.memo,
    hasImages = this.hasImages,
    thumbnailName = this.thumbnail,
    images = this.images,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun Gift.toEntity() = GiftEntity(
    type = this.type.num,
    categoryId = this.categoryId,
    categoryName = this.categoryName,
    friendId = this.friendId,
    friendName = this.friendName,
    date = this.date,
    mood = this.mood,
    memo = this.memo,
    hasImages = this.hasImages,
    thumbnail = this.thumbnailName,
    images = this.images,
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
    thumbnail = this.thumbnailName,
    images = this.images,
)

fun Gift.toHomeUiModel() = HomeGiftUiModel(
    id = this.id,
    type = this.type,
    friendName = this.friendName,
    thumbnailName = this.thumbnailName,
    date = this.date,
    hasImageCount = this.images.size,
)

fun Gift.toFriendDetailUiModel() = FriendDetailGiftUiModel(
    id = this.id,
    thumbnail = this.thumbnailName,
    hasImageCount = this.images.size,
)

fun Gift.toEditUiModel() = EditGiftUiModel(
    id = this.id,
    type = this.type,
    categoryId = this.categoryId,
    categoryName = this.categoryName,
    friendId = this.friendId,
    friendName = this.friendName,
    date = this.date,
    mood = this.mood,
    memo = this.memo,
    originalImages = this.images,
    thumbnail = this.thumbnailName,
)

fun Gift.toGiftListUiModel() = GiftListUiModel(
    id = this.id,
    type = this.type,
    thumbnailName = this.thumbnailName,
    hasImageCount = this.images.size,
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
    thumbnailName = this.thumbnail,
    images = this.images,
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis(),
)

fun EditGiftUiModel.toDomain() = Gift(
    id = id,
    type = this.type,
    categoryId = this.categoryId,
    categoryName = this.categoryName,
    friendId = this.friendId,
    friendName = this.friendName,
    date = this.date,
    mood = this.mood,
    memo = this.memo,
    hasImages = this.images.isNotEmpty(),
    thumbnailName = this.thumbnail,
    images = this.images.keys.toList(),
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis(),
)