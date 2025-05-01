package com.w36495.senty.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.w36495.senty.R

class NotificationMessageService(
    private val context: Context,
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun getSchedules() {
//        scheduleCoroutineScope.launch {
//            anniversaryRepository.getSchedules().map {schedules ->
//                //하루 전 날인 일정들
//                schedules.filter { schedule -> DateUtil.calRemainDate(schedule.date) == 1 }
//            }.collect {
//                Log.d("Notification(getSchedules)", it.toString())
//            }
//        }

    }

    fun buildNotification(message: String, pendingIntent: PendingIntent) {
        Log.d("NotificationService", "buildNotification()")
        val builder : NotificationCompat.Builder = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                NotificationCompat.Builder(
                    context,
                    SCHEDULE_NOTIFICATION_CHANNEL_ID
                )
            }

            else -> {
                NotificationCompat.Builder(context)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            }
        }.apply {
            setContentTitle("기념일 리마인더")
            setContentText("내일로 다가온 기념일, 행복한 하루 되세요!")
            setSmallIcon(R.mipmap.ic_launcher_round)
            setAutoCancel(true)
        }


        notificationManager.notify(100, builder.build())
    }

    private fun createNotificationChannel() {
        Log.d("NotificationService", "createNotificationChannel()")
        // API 26이상의 경우
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 사용자의 행동에 알림의 우선순위를 정하는것
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(SCHEDULE_NOTIFICATION_CHANNEL_ID, SCHEDULE_NOTIFICATION_CHANNEL_NAME, importance).apply {
                setShowBadge(true)
                enableLights(true)
            }

            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }

    companion object {
        private const val SCHEDULE_NOTIFICATION_CHANNEL_ID: String = "SCHEDULES_NOTIFICATION_ID"
        private const val SCHEDULE_NOTIFICATION_CHANNEL_NAME: String = "SCHEDULES_NOTIFICATION_NAME"
    }
}