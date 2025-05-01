package com.w36495.senty.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import com.w36495.senty.service.NotificationMessageService
import okhttp3.internal.cookieToString
import java.util.Calendar
import java.util.TimeZone

//class AlarmScheduler (
//    private val context: Context
//) {
//    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//    fun setAlarm() {
//        Log.d("AlarmScheduler", "setAlarm()")
//
//        // 알람 발생시 실행될 Receiver Intent
//        val intent = Intent(context, AlarmReceiver::class.java).apply {
//            putExtra("alarmMessage", "Alarm Test")
//        }
//        val pendingIntent = PendingIntent.getBroadcast(context, 101, intent, PendingIntent.FLAG_IMMUTABLE)
//
//        // 오후 4시 00분에 알람 발생
//        val calendar = Calendar.getInstance().apply {
//            timeInMillis = System.currentTimeMillis()
//            timeZone = TimeZone.getTimeZone("Asia/Seoul")
//            set(Calendar.HOUR_OF_DAY, 18)
//            set(Calendar.MINUTE, 30)
//        }
//
//        alarmManager.setAndAllowWhileIdle(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            pendingIntent
//        )
//    }
//
//    fun cancelAlarm(code: Int) {
//        Log.d("AlarmScheduler", "cancelAlarm()")
//
//        val intent = Intent(context, AlarmReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(context, code, intent, PendingIntent.FLAG_IMMUTABLE)
//
//        alarmManager.cancel(pendingIntent)
//    }
//}
//
//class AlarmReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context?, intent: Intent?) {
//        Log.d("AlarmReceiver", "onReceive()")
//
//        val result = intent?.extras?.getString("alarmMessage")
//
//        Log.d("AlarmReceiver", "onReceive($result)")
//
//        context?.let {
//            deliverNotification(it)
//        }
//
////        context?.let {
////            AlarmScheduler(context).cancelAlarm(101)
////        }
//
////        if (intent?.extras?.getString("alarmMessage") == "Alarm Test") {
////            context?.let {
//////                deliverNotification(it)
////            }
////        }
//    }

//    private fun deliverNotification(context: Context) {
//        Log.d("AlarmReceiver", "deliverNotification()")
//
//        val intent = Intent(context, AppActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//
//        val pendingIntent = PendingIntent.getActivity(context, 101, intent, PendingIntent.FLAG_IMMUTABLE)
//
//        NotificationMessageService(context).buildNotification("Alarm + Notification Test", pendingIntent)
//    }
//}