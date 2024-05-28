package com.w36495.senty.view.entity

import com.w36495.senty.data.domain.ScheduleEntity
import com.w36495.senty.util.DateUtil
import java.util.Calendar

data class Schedule(
    val title: String,
    val date: String,
    val location: String = "",
    val time: String = "",
    val memo: String = "",
    val isPast: Boolean = false,
) {
    var id: String = ""
        private set

    fun setId(id: String) {
        this.id = id
    }

    fun getYear(): Int = date.split("-")[0].toInt()
    fun getMonth(): Int = date.split("-")[1].toInt()
    fun getDay(): Int = date.split("-")[2].toInt()

    fun calculateDays(): Int {
        val (year, month, day) = DateUtil.getCurrentDate().map { it.toInt() }
        val (tYear, tMonth, tDay) = date.split("-").map { it.toInt() }
        val currentCalendar = Calendar.getInstance().apply {
            set(year, month, day)
        }.time.time

        val targetCalendar = Calendar.getInstance().apply {
            set(tYear, tMonth, tDay)
        }.time.time

        return ((targetCalendar - currentCalendar) / (1000 * 60 * 60 * 24)).toInt()
    }

    fun toDataEntity() = ScheduleEntity(
        id = this@Schedule.id,
        title = title,
        date = date,
        location = location,
        time = time,
        memo = memo,
        isPast = isPast,
    )

    companion object {
        val emptySchedule = Schedule(title = "", date = "")
    }
}