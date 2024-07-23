package com.w36495.senty.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDTO(
    val isInitialized: Boolean,
)