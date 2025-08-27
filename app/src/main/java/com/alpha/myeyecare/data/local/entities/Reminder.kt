package com.alpha.myeyecare.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder")
data class Reminder(
    @PrimaryKey
    val id: String,
    val title: String,
    val hour: Int,
    val minute: Int,
    val frequency: String,
    val selectedDays: Set<String>,
    val customIntervalMinutes: Int,
    val startDateMillis: Long,
    val isEnabled: Boolean
)
