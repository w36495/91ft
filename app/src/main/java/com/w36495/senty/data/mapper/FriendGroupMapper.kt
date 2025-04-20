package com.w36495.senty.data.mapper

import androidx.compose.ui.graphics.Color
import com.w36495.senty.data.domain.FriendGroupEntity
import com.w36495.senty.domain.entity.FriendGroup
import com.w36495.senty.view.screen.friendgroup.model.FriendGroupUiModel

fun FriendGroupEntity.toDomain(id: String) = FriendGroup(
    id = id,
    name = this.name,
    color = this.color,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun FriendGroup.toUiModel() = FriendGroupUiModel(
    id = this.id,
    name = this.name,
    color = this.color.toComposeColor(),
)

fun FriendGroup.toEntity() = FriendGroupEntity(
    name = this.name,
    color = this.color,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun FriendGroupUiModel.toDomain() = FriendGroup(
    id = this.id,
    name = this.name,
    color = this.color.toHexColor(),
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis(),
)

fun String.toComposeColor(): Color =
    Color(android.graphics.Color.parseColor(this))

fun Color.toHexColor(): String {
    val a = (alpha * 255).toInt()
    val r = (red * 255).toInt()
    val g = (green * 255).toInt()
    val b = (blue * 255).toInt()

    return String.format("#%02X%02X%02X%02X", a, r, g, b)
}
