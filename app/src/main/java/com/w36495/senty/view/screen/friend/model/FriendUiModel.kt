package com.w36495.senty.view.screen.friend.model

import androidx.compose.ui.graphics.Color
import com.w36495.senty.util.StringUtils
import com.w36495.senty.view.screen.friendgroup.model.FriendGroupUiModel

data class FriendUiModel(
    val id: String = "",
    val name: String = "",
    val birthday: String = "",
    val groupId: String = "",
    val groupName: String = "",
    val groupColor: Color = FriendGroupUiModel.DEFAULT_COLOR,
    val memo: String = "",
    val received: Int = 0,
    val sent: Int = 0,
) {
    fun displayBirthdayOnlyDate(): String {
        val date = birthday.split("-").map { it.toInt() }
        val month = StringUtils.format2Digits(date[1])
        val day = StringUtils.format2Digits(date[2])

        return StringBuilder().append(month).append("월 ").append(day).append("일").toString()
    }

    fun displayBirthdayWithYear(): String {
        val date = birthday.split("-").map { it.toInt() }
        val month = StringUtils.format2Digits(date[1])
        val day = StringUtils.format2Digits(date[2])

        return StringBuilder().append(date[0]).append("년 ").append(month).append("월 ").append(day).append("일").toString()
    }
}
