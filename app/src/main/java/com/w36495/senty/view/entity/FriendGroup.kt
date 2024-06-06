package com.w36495.senty.view.entity

import android.graphics.Color
import com.w36495.senty.data.domain.FriendGroupEntity
import kotlinx.serialization.Serializable

@Serializable
data class FriendGroup(
    val name: String,
    val color: String = DEFAULT_COLOR
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

    fun getIntTypeColor(): Int {
        val formatColor = StringBuilder().append("#").append(this.color).toString()
        return Color.parseColor(formatColor)
    }

    override fun toString(): String {
        return "FriendGroup(id=$id name=$name, color=$color)"
    }

    companion object {
        private const val DEFAULT_COLOR = "D9D9D9"
        val emptyFriendGroup = FriendGroup(name = "")
    }
}