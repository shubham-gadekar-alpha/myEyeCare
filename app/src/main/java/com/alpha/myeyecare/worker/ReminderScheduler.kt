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
import com.alpha.myeyecare.domain.model.DayOfWeek
import com.alpha.myeyecare.domain.model.ReminderDetails
import com.alpha.myeyecare.domain.model.ReminderFrequency
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun scheduleReminder(
        context: Context,
        reminderDetails: ReminderDetails,
        reminderType: String,
        onSuccess: () -> Unit
    ) {
        val workManager = WorkManager.getInstance(context)
        val workTag = reminderType

        if (!reminderDetails.isEnabled) {
            workManager.cancelAllWorkByTag(workTag)
            return
        }

        val inputData = workDataOf(
            ReminderWorker.NOTIFICATION_ID_KEY to workTag.hashCode(), // Use a derivative of tag for notification ID
            ReminderWorker.NOTIFICATION_TITLE_KEY to reminderDetails.title,
            ReminderWorker.NOTIFICATION_MESSAGE_KEY to "It's time for your ${reminderDetails.title} reminder!"
        )

        val now = Calendar.getInstance()
        val scheduledTime = Calendar.getInstance().apply {
            timeInMillis = reminderDetails.startDateMillis
            set(Calendar.HOUR_OF_DAY, reminderDetails.hour)
            set(Calendar.MINUTE, reminderDetails.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (before(now)) {
                when (reminderDetails.frequency) {
                    ReminderFrequency.DAILY, ReminderFrequency.SPECIFIC_DAYS -> {
                        add(
                            Calendar.DAY_OF_MONTH, 1
                        )
                    }

                    ReminderFrequency.HOURLY -> {
                        if (get(Calendar.MINUTE) < now.get(Calendar.MINUTE) || (get(Calendar.MINUTE) == now.get(
                                Calendar.MINUTE
                            ) && get(Calendar.SECOND) < now.get(
                                Calendar.SECOND
                            ))
                        ) {
                            add(
                                Calendar.HOUR_OF_DAY, 1
                            )
                            set(
                                Calendar.MINUTE, 0
                            )
                        }
                    }

                    ReminderFrequency.EVERY_X_MINUTES -> {
                        while (before(now)) {
                            add(Calendar.MINUTE, reminderDetails.customIntervalMinutes)
                        }
                    }

                    ReminderFrequency.ONCE -> {
                        return
                    }
                }
            }
        }

        if (reminderDetails.frequency == ReminderFrequency.SPECIFIC_DAYS && reminderDetails.selectedDays.isNotEmpty()) {
            while (!isDaySelected(scheduledTime, reminderDetails.selectedDays)) {
                scheduledTime.add(
                    Calendar.DAY_OF_MONTH, 1
                )
            }
        }

        val initialDelay = scheduledTime.timeInMillis - now.timeInMillis
        if (initialDelay < 0 && reminderDetails.frequency == ReminderFrequency.ONCE) {
            return
        }

        var workRequest: WorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>().setInitialDelay(
            initialDelay, TimeUnit.MILLISECONDS
        ).setInputData(inputData).addTag(workTag).build()

        when (reminderDetails.frequency) {
            ReminderFrequency.ONCE -> {
                if (initialDelay <= 0) {
                    return
                }
                workRequest = OneTimeWorkRequestBuilder<ReminderWorker>().setInitialDelay(
                    initialDelay, TimeUnit.MILLISECONDS
                ).setInputData(inputData).addTag(workTag).build()
            }

            ReminderFrequency.DAILY -> {
                workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                    1, TimeUnit.DAYS
                ).setInitialDelay(
                    initialDelay, TimeUnit.MILLISECONDS
                ).setInputData(inputData).addTag(workTag).build()
            }

            ReminderFrequency.HOURLY -> {
                workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                    1, TimeUnit.HOURS
                ).setInitialDelay(initialDelay, TimeUnit.MILLISECONDS).setInputData(inputData)
                    .addTag(workTag).build()
            }

            ReminderFrequency.EVERY_X_MINUTES -> {
                val interval = reminderDetails.customIntervalMinutes.toLong()
                workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                    interval, TimeUnit.MINUTES
                ).setInitialDelay(initialDelay, TimeUnit.MILLISECONDS).setInputData(inputData)
                    .addTag(workTag).build()
            }

            ReminderFrequency.SPECIFIC_DAYS -> {
                workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                    1, TimeUnit.DAYS
                ).setInitialDelay(initialDelay, TimeUnit.MILLISECONDS).setInputData(
                    Data.Builder().putAll(inputData.keyValueMap).putString(
                        "selected_days", reminderDetails.selectedDays.joinToString(",") { it.name })
                        .build()
                ).addTag(workTag).build()
            }
        }

        if (workRequest is PeriodicWorkRequest) {
            workManager.enqueueUniquePeriodicWork(
                workTag, ExistingPeriodicWorkPolicy.REPLACE, workRequest
            )
            onSuccess.invoke()
        } else if (workRequest is OneTimeWorkRequest) {
            workManager.enqueueUniqueWork(
                workTag, ExistingWorkPolicy.REPLACE, workRequest
            )
            onSuccess.invoke()
        }
    }

    private fun isDaySelected(calendar: Calendar, selectedDays: Set<DayOfWeek>): Boolean {
        if (selectedDays.isEmpty()) return true
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

    fun cancelReminderById(context: Context, id: String) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag(id)
    }
}
