package com.alpha.myeyecare.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alpha.myeyecare.MainActivity
import com.alpha.myeyecare.domain.model.DayOfWeek

class ReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val NOTIFICATION_ID_KEY = "notification_id"
        const val NOTIFICATION_TITLE_KEY = "notification_title"
        const val NOTIFICATION_MESSAGE_KEY = "notification_message"
        const val CHANNEL_ID = "reminder_channel_id"
        const val CHANNEL_NAME = "Reminder Notifications"
    }

    override suspend fun doWork(): Result {
        val notificationId =
            inputData.getInt(NOTIFICATION_ID_KEY, System.currentTimeMillis().toInt())
        val title = inputData.getString(NOTIFICATION_TITLE_KEY) ?: "Reminder"
        val message = inputData.getString(NOTIFICATION_MESSAGE_KEY) ?: "It's time!"

        val selectedDaysString = inputData.getString("selected_days")
        if (selectedDaysString != null) {
            val selectedDays = selectedDaysString.split(",").map { DayOfWeek.valueOf(it) }.toSet()
            val todayCalendar = Calendar.getInstance()
            if (!isTodaySelected(todayCalendar, selectedDays)) {
                return Result.success()
            }
        }

        try {
            sendNotification(applicationContext, notificationId, title, message)
            return Result.success()
        } catch (e: Exception) {
            Log.e("ReminderWorker", "Error sending notification", e)
            return Result.failure()
        }
    }

    private fun sendNotification(context: Context, id: Int, title: String, message: String) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
            }
            notify(id, builder.build())
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val descriptionText = "Channel for reminder notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun isTodaySelected(calendar: Calendar, selectedDays: Set<DayOfWeek>): Boolean {
        if (selectedDays.isEmpty()) return false
        val dayOfWeekToday = calendar.get(Calendar.DAY_OF_WEEK)
        return selectedDays.any { selectedDay ->
            when (selectedDay) {
                DayOfWeek.SUN -> dayOfWeekToday == Calendar.SUNDAY
                DayOfWeek.MON -> dayOfWeekToday == Calendar.MONDAY
                DayOfWeek.TUE -> dayOfWeekToday == Calendar.TUESDAY
                DayOfWeek.WED -> dayOfWeekToday == Calendar.WEDNESDAY
                DayOfWeek.THU -> dayOfWeekToday == Calendar.THURSDAY
                DayOfWeek.FRI -> dayOfWeekToday == Calendar.FRIDAY
                DayOfWeek.SAT -> dayOfWeekToday == Calendar.SATURDAY
            }
        }
    }
}
