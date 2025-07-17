package com.w36495.senty.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class SimpleFriend(
    val id: String,
    val name: String,
)