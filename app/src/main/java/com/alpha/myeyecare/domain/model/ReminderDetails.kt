package com.alpha.myeyecare.domain.model

import com.alpha.myeyecare.common.constants.ReminderTypes

data class ReminderDetails(
    var type: String = ReminderTypes.UNDEFINED,
    var title: String = "Undefined",
    var hour: Int = 10,
    var minute: Int = 30,
    var frequency: ReminderFrequency = ReminderFrequency.DAILY,
    var selectedDays: Set<DayOfWeek> = emptySet(),
    var customIntervalMinutes: Int = 60,
    var startDateMillis: Long = System.currentTimeMillis(),
    var isEnabled: Boolean = false
)
