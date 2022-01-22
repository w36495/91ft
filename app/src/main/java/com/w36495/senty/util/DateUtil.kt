package com.w36495.senty.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.*

class DateUtil {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDateTime(): String {
        val currentTime = System.currentTimeMillis()
        val currentDate = Date(currentTime)
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

        return dateFormatter.format(currentDate)
    }

}