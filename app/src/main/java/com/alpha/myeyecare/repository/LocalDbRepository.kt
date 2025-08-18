package com.alpha.myeyecare.repository

import com.alpha.myeyecare.ui.screens.ReminderDetails

interface LocalDbRepository {
    fun updateReminderEnabledStatus(reminderId: String, isEnabled: Boolean): Boolean

    fun getReminder(reminderId: String): ReminderDetails?

    fun saveReminder(reminderId: String, details: ReminderDetails)
}
