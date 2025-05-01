package com.w36495.senty.view.screen.anniversary.model

data class ScheduleUiModel(
    val id: String = "",
    val title: String = "",
    val date: String = "",
    val location: String = "",
    val time: String = "",
    val memo: String = "",
    val friends: List<ScheduleFriendUiModel> = emptyList(),
)

data class ScheduleFriendUiModel(
    val id: String,
    val name: String
)