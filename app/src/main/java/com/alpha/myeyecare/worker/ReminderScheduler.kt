package com.alpha.myeyecare.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.alpha.myeyecare.ui.screens.DayOfWeek
import com.alpha.myeyecare.ui.screens.ReminderDetails
import com.alpha.myeyecare.ui.screens.ReminderFrequency
import java.util.Calendar
import java.util.concurrent.TimeUnit


object ReminderScheduler {


    fun scheduleReminder(context: Context, reminderDetails: ReminderDetails, reminderType: String) {
        val workManager = WorkManager.getInstance(context)
        // Use a more robust unique ID if titles can be duplicated.
        // For simplicity, using title.hashCode() as part of the tag.
        // Consider generating a UUID for each reminder and storing it with ReminderDetails.
//        val workTag = ReminderWorker.WORKER_TAG_PREFIX + reminderDetails.title.hashCode()
        val workTag = reminderType

        if (!reminderDetails.isEnabled) {
            // If reminder is disabled, cancel any existing work for it
            workManager.cancelAllWorkByTag(workTag)
            // Log.d("ReminderScheduler", "Cancelled reminder: ${reminderDetails.title}")
            return
        }

        // --- Prepare Input Data for the Worker ---
        val inputData = workDataOf(
            ReminderWorker.NOTIFICATION_ID_KEY to workTag.hashCode(), // Use a derivative of tag for notification ID
            ReminderWorker.NOTIFICATION_TITLE_KEY to reminderDetails.title,
            ReminderWorker.NOTIFICATION_MESSAGE_KEY to "It's time for your ${reminderDetails.title} reminder!"
            // Add more data if your worker needs it (e.g., reminder type for custom logic)
        )

        // --- Calculate Initial Delay ---
        val now = Calendar.getInstance()
        val scheduledTime = Calendar.getInstance().apply {
            timeInMillis = reminderDetails.startDateMillis // Start with the selected date
            set(Calendar.HOUR_OF_DAY, reminderDetails.hour)
            set(Calendar.MINUTE, reminderDetails.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If the calculated time is in the past based on startDate and time,
            // and it's a repeating reminder, adjust to the next valid occurrence.
            if (before(now)) {
                when (reminderDetails.frequency) {
                    ReminderFrequency.DAILY, ReminderFrequency.SPECIFIC_DAYS -> {
                        // If it's daily or specific days and already past for today, move to tomorrow
                        // More complex logic might be needed for SPECIFIC_DAYS to find the *next* selected day.
                        add(Calendar.DAY_OF_MONTH, 1)
                    }

                    ReminderFrequency.HOURLY -> {
                        // If past the minute for the current hour, move to the next hour
                        if (get(Calendar.MINUTE) < now.get(Calendar.MINUTE) ||
                            (get(Calendar.MINUTE) == now.get(Calendar.MINUTE) && get(Calendar.SECOND) < now.get(
                                Calendar.SECOND
                            ))
                        ) {
                            add(Calendar.HOUR_OF_DAY, 1)
                            set(Calendar.MINUTE, 0) // Assuming hourly means on the hour
                        }
                    }

                    ReminderFrequency.EVERY_X_MINUTES -> {
                        // If past, find the next interval from now
                        while (before(now)) {
                            add(Calendar.MINUTE, reminderDetails.customIntervalMinutes)
                        }
                    }

                    ReminderFrequency.ONCE -> {
                        // If a one-time reminder is in the past, don't schedule or notify user.
                        // Log.d("ReminderScheduler", "One-time reminder for '${reminderDetails.title}' is in the past. Not scheduling.")
                        return
                    }
                }
            }
        }
        // Additional adjustment for SPECIFIC_DAYS if the initially advanced day is not a selected day
        if (reminderDetails.frequency == ReminderFrequency.SPECIFIC_DAYS && reminderDetails.selectedDays.isNotEmpty()) {
            while (!isDaySelected(scheduledTime, reminderDetails.selectedDays)) {
                scheduledTime.add(
                    Calendar.DAY_OF_MONTH,
                    1
                ) // Keep moving to the next day until a selected day is found
            }
        }


        val initialDelay = scheduledTime.timeInMillis - now.timeInMillis
        if (initialDelay < 0 && reminderDetails.frequency == ReminderFrequency.ONCE) {
            // Log.d("ReminderScheduler", "One-time reminder '${reminderDetails.title}' initial delay is negative. Not scheduling.")
            return // Don't schedule one-time past events after adjustments
        }


        // --- Build Work Request ---
        var workRequest: WorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag(workTag) // Tag for cancellation
            .build()

        when (reminderDetails.frequency) {
            ReminderFrequency.ONCE -> {
                if (initialDelay <= 0) { // Check again after all adjustments
                    // Log.d("ReminderScheduler", "One-time reminder '${reminderDetails.title}' is in the past. Not scheduling.")
                    return
                }
                workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag(workTag) // Tag for cancellation
                    .build()
                // Log.d("ReminderScheduler", "Scheduling ONCE for '${reminderDetails.title}' in $initialDelay ms")
            }

            ReminderFrequency.DAILY -> {
                workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                    1, TimeUnit.DAYS // repeatInterval
                )
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag(workTag)
                    .build()
                // Log.d("ReminderScheduler", "Scheduling DAILY for '${reminderDetails.title}' initial delay $initialDelay ms")
            }

            ReminderFrequency.HOURLY -> {
                workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                    1, TimeUnit.HOURS
                )
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag(workTag)
                    .build()
            }

            ReminderFrequency.EVERY_X_MINUTES -> {
                val interval = reminderDetails.customIntervalMinutes.toLong()
//                if (interval < PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS / (60 * 1000)) {
//                    // Log.w("ReminderScheduler", "Interval for ${reminderDetails.title} is too short. Setting to minimum 15 minutes.")
//                    // Minimum interval for periodic work is 15 minutes.
//                    // Consider falling back to OneTimeWorkRequest and re-scheduling from the worker if shorter intervals are absolutely needed (more complex).
//                    // For now, let's just log or adjust.
//                    // For simplicity, we'll proceed, but WorkManager will adjust it.
//                }
                workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                    interval, TimeUnit.MINUTES
                )
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag(workTag)
                    .build()
            }

            ReminderFrequency.SPECIFIC_DAYS -> {
                // For SPECIFIC_DAYS, WorkManager's PeriodicWorkRequest doesn't directly support "only on these days".
                // The common approach is to schedule a daily periodic worker and then,
                // inside the worker, check if the current day is one of the selected days.
                // The initialDelay calculation above already tries to set it to the *next* valid selected day.
                workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                    1, TimeUnit.DAYS // It will run daily
                )
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .setInputData(
                        Data.Builder().putAll(inputData.keyValueMap).putString(
                            "selected_days",
                            reminderDetails.selectedDays.joinToString(",") { it.name }
                        ).build()
//                        inputData.toBuilder() // Add selected days to input data for worker to check
//                            .putString(
//                                "selected_days",
//                                reminderDetails.selectedDays.joinToString(",") { it.name })
//                            .build()
                    )
                    .addTag(workTag)
                    .build()
                // Log.d("ReminderScheduler", "Scheduling SPECIFIC_DAYS (as daily with check) for '${reminderDetails.title}' initial delay $initialDelay ms")
            }
        }

        if (workRequest is PeriodicWorkRequest) {
            workManager.enqueueUniquePeriodicWork(
                workTag, // Use the tag as the unique work name
                ExistingPeriodicWorkPolicy.REPLACE, // Or .KEEP if you don't want to replace if one exists
                workRequest
            )
            // Log.d("ReminderScheduler", "Enqueued unique periodic work: $workTag for ${reminderDetails.title}")
        } else if (workRequest is OneTimeWorkRequest) {
            workManager.enqueueUniqueWork(
                workTag,
                ExistingWorkPolicy.REPLACE, // Or .KEEP
                workRequest
            )
            // Log.d("ReminderScheduler", "Enqueued unique one-time work: $workTag for ${reminderDetails.title}")
        }
    }

    // Helper function to check if the Calendar day is one of the selected DayOfWeek
    private fun isDaySelected(calendar: Calendar, selectedDays: Set<DayOfWeek>): Boolean {
        if (selectedDays.isEmpty()) return true // If no days are selected, effectively it's like daily for this check logic
        val dayOfWeekToday = calendar.get(Calendar.DAY_OF_WEEK) // Sunday = 1, Saturday = 7
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

    fun cancelReminderById(context: Context, id: String) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag(id)
    }
}
