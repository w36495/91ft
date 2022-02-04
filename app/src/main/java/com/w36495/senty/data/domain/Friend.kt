package com.w36495.senty.data.domain

import com.google.firebase.database.Exclude
import java.io.Serializable

data class Friend constructor(
    var key: String = "",
    val name: String = "",
    var imagePath: String? = null
) : Serializable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "key" to key,
            "name" to name,
            "imagePath" to imagePath
        )
    }
}