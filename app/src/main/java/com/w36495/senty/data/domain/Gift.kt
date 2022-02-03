package com.w36495.senty.data.domain

import com.google.firebase.database.Exclude
import java.io.Serializable

data class Gift(
    var key: String = "",
    val received: Boolean = false,
    val date: String = "",
    val title: String = "",
    val memo: String = "",
    var imagePath: String? = null
): Serializable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "key" to key,
            "received" to received,
            "date" to date,
            "title" to title,
            "memo" to memo,
            "imagePath" to imagePath
        )

    }
}
