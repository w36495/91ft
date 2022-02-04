package com.w36495.senty.util

import java.text.SimpleDateFormat
import java.util.*

class DateUtil {
    companion object {
        fun getDateTime(): String {
            val currentTime = System.currentTimeMillis()
            val currentDate = Date(currentTime)
            val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale("ko", "KR"))

            return dateFormatter.format(currentDate)
        }
    }
}