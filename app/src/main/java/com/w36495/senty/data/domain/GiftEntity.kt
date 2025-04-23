package com.w36495.senty.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class GiftEntity(
    val type: Int = 0,
    val categoryId: String = "",
    val categoryName: String = "",
    val friendId: String = "",
    val friendName: String = "",
    val date: String = "",
    val mood: String = "",
    val memo: String = "",
    val hasImages: Boolean = false,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
)