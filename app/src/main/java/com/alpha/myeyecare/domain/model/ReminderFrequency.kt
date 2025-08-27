package com.alpha.myeyecare.domain.model

enum class ReminderFrequency(val displayName: String) {
    ONCE("Once"),
    DAILY("Daily"),
    SPECIFIC_DAYS("Specific Days"),
    HOURLY("Hourly (on the hour)"),
    EVERY_X_MINUTES("Every X minutes")
}
