package com.w36495.senty.data.domain

import com.google.firebase.database.Exclude
import java.io.Serializable

data class Friend constructor(
    var key: String = "",
    val name: String = "",
    val phone: String = ""
) : Serializable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "key" to key,
            "name" to name,
            "phone" to phone
        )

    }
}