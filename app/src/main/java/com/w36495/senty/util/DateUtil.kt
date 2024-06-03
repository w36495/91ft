package com.w36495.senty.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateUtil {
    companion object {
        fun getDateTime(): String {
            val currentTime = System.currentTimeMillis()
            val currentDate = Date(currentTime)
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))

            return dateFormatter.format(currentDate)
        }

        fun toTimeStamp(dateTime: Long): String {
            val dateFormat = "yyyy-MM-dd HH:mm:ss"
            return SimpleDateFormat(dateFormat).format(dateTime)
        }

        fun getCurrentDate(): List<String> = getDateTime().split("-")

        fun changeDateFormatToDash(dateTime: String): String {
            val oldDateFormat = SimpleDateFormat("yyyy년 mm월 dd일", Locale.KOREA).parse(dateTime)
            val newDateFormat = SimpleDateFormat("yyyy-mm-dd", Locale.KOREA)

            return newDateFormat.format(oldDateFormat!!)
        }

        fun calRemainDate(date: String): Int {
            val (year, month, day) = getCurrentDate().map { it.toInt() }
            val currentDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
            }.timeInMillis

            val (sYear, sMonth, sDay) = date.split("-").map { it.toInt()}
            val savedScheduleDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, sYear)
                set(Calendar.MONTH, sMonth)
                set(Calendar.DAY_OF_MONTH, sDay)
            }.timeInMillis

            val calDay = getIgnoredTimeDays(savedScheduleDate) - getIgnoredTimeDays(currentDate)

            return (calDay / (24 * 60 * 60 * 1000)).toInt()
        }

        private fun getIgnoredTimeDays(time: Long): Long {
            return Calendar.getInstance().apply {
                timeInMillis = time

                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
    }
}