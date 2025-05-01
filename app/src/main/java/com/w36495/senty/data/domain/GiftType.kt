package com.w36495.senty.data.domain

import kotlinx.serialization.Serializable

@Serializable
enum class GiftType(val num: Int) {
    RECEIVED(
        num = 0,
    ),
    SENT(
        num = 1,
    )
}