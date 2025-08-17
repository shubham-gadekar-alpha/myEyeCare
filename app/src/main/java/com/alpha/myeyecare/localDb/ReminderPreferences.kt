package com.alpha.myeyecare.localDb


import android.content.Context
import android.content.SharedPreferences
import com.alpha.myeyecare.ui.screens.ReminderDetails
import com.google.gson.Gson
import androidx.core.content.edit

class ReminderPreferences(context: Context) {

    private val gson = Gson()
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        fun getInstance(context: Context): ReminderPreferences {
            if (!::instance.isInitialized) {
                instance = ReminderPreferences(context)
            }
            return instance
        }

        private lateinit var instance: ReminderPreferences
        private const val PREFS_NAME = "reminder_prefs"
        private const val KEY_REMINDER_PREFIX = "reminder_" // Prefix for each reminder key
        // If you only plan to store ONE reminder, you can use a fixed key instead of a prefix.
        // private const val KEY_SINGLE_REMINDER = "single_reminder_details"
    }

    // --- Save ReminderDetails ---
    // This example assumes you identify reminders by a unique ID (e.g., title, or a generated UUID)
    // If you only ever have ONE reminder, you can simplify this to not require an ID.
    fun saveReminder(reminderId: String, details: ReminderDetails) {
        val reminderJson = gson.toJson(details)
        prefs.edit { putString(KEY_REMINDER_PREFIX + reminderId, reminderJson) }
    }

    // --- Fetch ReminderDetails ---
    fun getReminder(reminderId: String): ReminderDetails? {
        val reminderJson = prefs.getString(KEY_REMINDER_PREFIX + reminderId, null)
        return if (reminderJson != null) {
            try {
                gson.fromJson(reminderJson, ReminderDetails::class.java)
            } catch (e: Exception) {
                // Log.e("ReminderPreferences", "Error parsing reminder JSON for ID $reminderId", e)
                null // Error during deserialization
            }
        } else {
            null // Reminder not found
        }
    }

    // --- Function to update only the isEnabled field ---
    fun updateReminderEnabledStatus(reminderId: String, isEnabled: Boolean): Boolean {
        val existingReminder = getReminder(reminderId)
        return if (existingReminder != null) {
            val updatedReminder = existingReminder.copy(isEnabled = isEnabled)
            saveReminder(reminderId, updatedReminder)
            true // Indicates success
        } else {
            // Log.w("ReminderPreferences", "Reminder with ID $reminderId not found for updating status.")
            false // Reminder not found
        }
    }

    // --- Get all saved reminders (if you store multiple) ---
    // This is more complex as SharedPreferences doesn't have a direct "get all keys with prefix"
    fun getAllReminders(): List<ReminderDetails> {
        val allEntries = prefs.all
        val reminders = mutableListOf<ReminderDetails>()
        for ((key, value) in allEntries) {
            if (key.startsWith(KEY_REMINDER_PREFIX) && value is String) {
                try {
                    val reminder = gson.fromJson(value, ReminderDetails::class.java)
                    reminders.add(reminder)
                } catch (e: Exception) {
                    // Log.e("ReminderPreferences", "Error parsing reminder JSON for key $key", e)
                }
            }
        }
        return reminders
    }

    // --- Delete a reminder ---
    fun deleteReminder(reminderId: String) {
        prefs.edit { remove(KEY_REMINDER_PREFIX + reminderId) }
    }

    // --- Clear all reminders (use with caution) ---
    fun clearAllReminders() {
        prefs.edit {
            prefs.all.keys.forEach { key ->
                if (key.startsWith(KEY_REMINDER_PREFIX)) {
                    remove(key)
                }
            }
        }
    }
}
