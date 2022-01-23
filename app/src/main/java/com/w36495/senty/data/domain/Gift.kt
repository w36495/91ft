package com.w36495.senty.data.domain

import com.google.firebase.database.Exclude
import java.io.Serializable

data class Gift(
    var giftKey: String = "",
    val received: Boolean = false,
    val giftDate: String = "",
    val giftTitle: String = "",
    val giftMemo: String = ""
): Serializable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "giftKey" to giftKey,
            "received" to received,
            "giftDate" to giftDate,
            "giftTitle" to giftTitle,
            "giftMemo" to giftMemo
        )

    }
}
