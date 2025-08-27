package com.alpha.myeyecare.common.utils

import com.alpha.myeyecare.common.constants.ReminderTypes
import com.alpha.myeyecare.data.local.entities.Reminder
import com.alpha.myeyecare.domain.model.DayOfWeek
import com.alpha.myeyecare.domain.model.ReminderDetails
import com.alpha.myeyecare.domain.model.ReminderFrequency

fun Reminder.toReminderDetails(): ReminderDetails {
    return ReminderDetails(
        title = title,
        hour = hour,
        minute = minute,
        frequency = ReminderFrequency.entries.first { it.displayName == frequency },
        selectedDays = selectedDays.map { DayOfWeek.valueOf(it) }.toSet(),
        customIntervalMinutes = customIntervalMinutes,
        startDateMillis = startDateMillis,
        isEnabled = isEnabled
    )
}

fun ReminderDetails.toReminder(): Reminder {
    return Reminder(
        id = type,
        title = title,
        hour = hour,
        minute = minute,
        frequency = frequency.displayName,
        selectedDays = selectedDays.map { it.fullName }.toSet(),
        customIntervalMinutes = customIntervalMinutes,
        startDateMillis = startDateMillis,
        isEnabled = isEnabled
    )
}

fun String.getTitleForReminder(): String {
    return if (this == ReminderTypes.EYE_REMINDER) return "My Eye Care Break" else "Drink Water Break"
}
