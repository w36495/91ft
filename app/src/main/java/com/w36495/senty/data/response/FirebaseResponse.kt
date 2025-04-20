package com.w36495.senty.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FirebasePostResponse(
    @SerialName("name")
    val key: String,
)