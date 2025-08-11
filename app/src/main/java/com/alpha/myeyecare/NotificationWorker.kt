package com.alpha.myeyecare

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val startHour = inputData.getInt("startHour", 9)
        val startMinute = inputData.getInt("startMinute", 0)
        val endHour = inputData.getInt("endHour", 17)
        val endMinute = inputData.getInt("endMinute", 0)

        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis

        val startCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, startHour)
            set(Calendar.MINUTE, startMinute)
        }

        val endCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, endHour)
            set(Calendar.MINUTE, endMinute)
        }

        if (currentTime in startCal.timeInMillis..endCal.timeInMillis) {
            NotificationUtils.showNotification(applicationContext, "Eye Reminder", "Take a break and look away from the screen!")
        }

        return Result.success()
    }
}
