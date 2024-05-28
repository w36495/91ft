package com.w36495.senty.data.domain

import com.w36495.senty.util.DateUtil
import com.w36495.senty.view.entity.Schedule
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class ScheduleEntity(
    val id: String = "",
    val title: String,
    val date: String,
    val location: String,
    val time: String,
    val memo: String,
    val isPast: Boolean = false,
    @JsonNames("create_at")
    val createAt: String = DateUtil.toTimeStamp(System.currentTimeMillis()),
    @JsonNames("update_at")
    val updateAt: String = DateUtil.toTimeStamp(System.currentTimeMillis()),
) {
    fun toDomainEntity() = Schedule(
        title = title,
        date = date,
        location = location,
        time = time,
        memo = memo,
        isPast = isPast
    ).apply {
        setId(this@ScheduleEntity.id)
    }
}