package com.w36495.senty.view.entity

import androidx.compose.ui.graphics.Color
import com.w36495.senty.data.domain.FriendGroupEntity
import kotlinx.serialization.Serializable

@Serializable
data class FriendGroup(
    val name: String,
    val color: String = DEFAULT_COLOR.toString()
) {
    var id = ""
        private set

    fun setId(id: String) {
        this.id = id
    }

    fun toDataEntity() = FriendGroupEntity(
        name = name,
        color = color,
    )

    fun getIntTypeColor(): Color {
        return Color(color.toULong())
    }

    fun copy() = FriendGroup(
        name = name,
        color = color
    ).apply { setId(this@FriendGroup.id) }

    override fun toString(): String {
        return "FriendGroup(id=$id, name=$name, color=$color)"
    }

    companion object {
        private val DEFAULT_COLOR = Color(0xFFCED4DA).value
        val emptyFriendGroup = FriendGroup(name = "")
    }
}