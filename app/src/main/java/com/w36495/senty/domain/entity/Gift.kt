package com.w36495.senty.domain.entity

import com.w36495.senty.data.domain.GiftType

data class Gift(
    val id: String,
    val type: GiftType,
    val categoryId: String,
    val categoryName: String,
    val friendId: String,
    val friendName: String,
    val date: String,
    val mood: String,
    val memo: String,
    val hasImages: Boolean,
    val thumbnailName: String?,
    val images: List<String>,
    val createdAt: Long,
    val updatedAt: Long,
)