package com.alpha.myeyecare.common.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun formatTime(hour: Int, minute: Int): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
    }
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
}

fun formatDate(millis: Long): String {
    val today = Calendar.getInstance()
    val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
    val dateToFormat = Calendar.getInstance().apply { timeInMillis = millis }

    return when {
        isSameDay(dateToFormat, today) -> "Today"
        isSameDay(dateToFormat, tomorrow) -> "Tomorrow"
        else -> SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault()).format(Date(millis))
    }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(
        Calendar.DAY_OF_YEAR
    )
}
