package com.w36495.senty.view.screen.friendgroup.model

import androidx.compose.ui.graphics.Color

data class FriendGroupUiModel(
    val id: String = "",
    val name: String = "",
    val color: Color = DEFAULT_COLOR,
) {
    companion object {
        val DEFAULT_COLOR = Color(0xFFCED4DA)
        val emptyFriendGroup = FriendGroupUiModel(id = "", name = "", DEFAULT_COLOR)
        val allFriendGroup = FriendGroupUiModel(id = "", name = "전체", DEFAULT_COLOR)
    }
}