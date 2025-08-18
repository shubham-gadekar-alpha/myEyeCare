package com.alpha.myeyecare.repository

import android.content.Context
import com.alpha.myeyecare.localDb.ReminderPreferences
import com.alpha.myeyecare.ui.screens.ReminderDetails
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDbRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocalDbRepository {

    private val localDbInstance = ReminderPreferences.getInstance(context)

    override fun updateReminderEnabledStatus(
        reminderId: String,
        isEnabled: Boolean
    ): Boolean {
        return localDbInstance.updateReminderEnabledStatus(reminderId, isEnabled)
    }

    override fun getReminder(reminderId: String): ReminderDetails? {
        return localDbInstance.getReminder(reminderId)
    }

    override fun saveReminder(reminderId: String, details: ReminderDetails) {
        localDbInstance.saveReminder(reminderId, details)
    }
}
