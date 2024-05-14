package com.w36495.senty.view.entity

import android.graphics.Color
import com.w36495.senty.data.domain.FriendGroupEntity
import com.w36495.senty.util.DateUtil
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class FriendGroup(
    val id: String = "",
    val name: String = "",
    val color: String = DEFAULT_COLOR
) {
    fun toDataEntity() = FriendGroupEntity(
        name = name,
        color = color,
        createAt = DateUtil.toTimeStamp(System.currentTimeMillis()),
        updateAt = DateUtil.toTimeStamp(System.currentTimeMillis())
    )

    fun getIntTypeColor(): Int {
        val formatColor = StringBuilder().append("#").append(this.color).toString()
        return Color.parseColor(formatColor)
    }

    companion object {
        private const val DEFAULT_COLOR = "D9D9D9"
        fun encodeToJson(group: FriendGroup): String = Json.encodeToString(group)
        fun decodeToObject(jsonGroup: String): FriendGroup = Json.decodeFromString(jsonGroup)
    }
}