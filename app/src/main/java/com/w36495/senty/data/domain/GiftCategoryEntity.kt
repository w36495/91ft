package com.w36495.senty.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class GiftCategoryEntity(
    val name: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
)