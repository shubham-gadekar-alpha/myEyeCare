package com.alpha.myeyecare

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.*
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationUtils.createNotificationChannel(this)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ReminderUI()
                }
            }
        }
    }
}

@Composable
fun ReminderUI() {
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
                    intervalMinutes.toLongOrNull() ?: 30
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
    interval: Long
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
        "eye_reminder",
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest
    )
}
