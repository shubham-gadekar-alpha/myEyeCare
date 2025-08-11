package com.alpha.myeyecare.ui.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.alpha.myeyecare.NotificationWorker
import java.util.concurrent.TimeUnit

@Composable
fun SetupReminderScreen(
    reminderType: String
) {
    val context = LocalContext.current
    var startHour by remember { mutableStateOf(9) }
    var startMinute by remember { mutableStateOf(0) }
    var endHour by remember { mutableStateOf(17) }
    var endMinute by remember { mutableStateOf(0) }
    var intervalMinutes by remember { mutableStateOf("30") }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Set Reminder Time and Interval", style = MaterialTheme.typography.titleLarge)

        Button(onClick = {
            TimePickerDialog(context, { _, hour, minute ->
                startHour = hour
                startMinute = minute
            }, startHour, startMinute, true).show()
        }) {
            Text("Select Start Time: %02d:%02d".format(startHour, startMinute))
        }

        Button(onClick = {
            TimePickerDialog(context, { _, hour, minute ->
                endHour = hour
                endMinute = minute
            }, endHour, endMinute, true).show()
        }) {
            Text("Select End Time: %02d:%02d".format(endHour, endMinute))
        }

        OutlinedTextField(
            value = intervalMinutes,
            onValueChange = { intervalMinutes = it },
            label = { Text("Interval (minutes)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                scheduleRepeatingNotification(
                    context,
                    startHour, startMinute,
                    endHour, endMinute,
                    intervalMinutes.toLongOrNull() ?: 30,
                    reminderType
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Reminder")
        }
    }
}

fun scheduleRepeatingNotification(
    context: android.content.Context,
    startHour: Int, startMinute: Int,
    endHour: Int, endMinute: Int,
    interval: Long,
    reminderType: String
) {
    val data = Data.Builder()
        .putInt("startHour", startHour)
        .putInt("startMinute", startMinute)
        .putInt("endHour", endHour)
        .putInt("endMinute", endMinute)
        .build()

    val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(interval, TimeUnit.MINUTES)
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        reminderType,
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest
    )
}
