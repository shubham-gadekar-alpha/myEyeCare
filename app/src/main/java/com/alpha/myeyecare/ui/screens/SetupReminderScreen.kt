package com.alpha.myeyecare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alpha.myeyecare.localDb.ReminderPreferences
import com.alpha.myeyecare.viewModel.MainViewModel
import com.alpha.myeyecare.worker.ReminderScheduler
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// --- Data Classes and Enums ---
data class ReminderDetails(
    var title: String = "My Eye Care Break",
    var hour: Int = 10,
    var minute: Int = 30,
    var frequency: ReminderFrequency = ReminderFrequency.DAILY,
    var selectedDays: Set<DayOfWeek> = emptySet(),
    var customIntervalMinutes: Int = 60,
    var startDateMillis: Long = System.currentTimeMillis(),
    var isEnabled: Boolean = true
)

enum class ReminderFrequency(val displayName: String) {
    ONCE("Once"),
    DAILY("Daily"),
    SPECIFIC_DAYS("Specific Days"),
    HOURLY("Hourly (on the hour)"), // Consider if this needs more specific time or just the hour
    EVERY_X_MINUTES("Every X minutes")
}

enum class DayOfWeek(val shortName: String, val fullName: String) {
    MON("M", "Monday"), TUE("T", "Tuesday"), WED("W", "Wednesday"),
    THU("T", "Thursday"), FRI("F", "Friday"), SAT("S", "Saturday"), SUN("S", "Sunday")
}

// --- Helper Functions for Formatting ---
fun formatTime(hour: Int, minute: Int): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
    }
    // Consider AM/PM based on locale or a 12/24 hour setting in your app
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
}

fun formatDate(millis: Long): String {
    // Check if the date is today or tomorrow for more friendly display
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
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupReminderScreen(
    viewModel: MainViewModel,
    reminderType: String,
    initialDetails: ReminderDetails,
    onSaveReminder: (ReminderDetails) -> Unit,
    onBackIconPressed: () -> Unit
) {
    var reminderTitle by remember { mutableStateOf(initialDetails.title) }

    var selectedHour by remember { mutableStateOf(initialDetails.hour) }
    var selectedMinute by remember { mutableStateOf(initialDetails.minute) }
    var startDateMillis by remember { mutableStateOf(initialDetails.startDateMillis) }

    var selectedFrequency by remember { mutableStateOf(initialDetails.frequency) }
    var selectedDays by remember { mutableStateOf<Set<DayOfWeek>>(LinkedHashSet(initialDetails.selectedDays)) }
    var customIntervalMinutes by remember { mutableStateOf(initialDetails.customIntervalMinutes) }
    var isEnabled by remember { mutableStateOf(initialDetails.isEnabled) }

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    var showTurnOffReminderDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = startDateMillis,
    )
    val timePickerState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute,
        is24Hour = false
    )

    // --- Dialogs ---
    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialog = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            startDateMillis = millis // Update the screen's state
                        }
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePickerDialog) {
        // Basic TimePickerDialog structure (Material 3 doesn't have a one-liner TimePickerDialog yet like DatePickerDialog)
        AlertDialog(
            onDismissRequest = { showTimePickerDialog = false },
            modifier = Modifier.fillMaxWidth(), // Adjust width as needed
            title = {
                Text(
                    "Select Time",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                )
            },
            text = {
                // TimePicker is typically wide, so Column layout might be better than the default AlertDialog text area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp), // Add padding around the TimePicker
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showTimePickerDialog = false
                        selectedHour = timePickerState.hour
                        selectedMinute = timePickerState.minute
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showTurnOffReminderDialog) {
        AlertDialog(
            onDismissRequest = { showTurnOffReminderDialog = false },
            modifier = Modifier.fillMaxWidth(), // Adjust width as needed
            text = {
                Text(
                    "Are you sure want to Turn OFF Reminder?",
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isEnabled = !isEnabled
                        showTurnOffReminderDialog = false
                        ReminderScheduler.cancelReminderById(context, reminderType)
                        viewModel.updateReminderEnabledStatus(reminderType, false)
                    }
                ) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showTurnOffReminderDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Set Reminder", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackIconPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val detailsToSave = ReminderDetails(
                        title = reminderTitle.ifBlank { "Untitled Reminder" },
                        hour = selectedHour,
                        minute = selectedMinute,
                        frequency = selectedFrequency,
                        selectedDays = if (selectedFrequency == ReminderFrequency.SPECIFIC_DAYS) selectedDays else emptySet(),
                        customIntervalMinutes = customIntervalMinutes,
                        startDateMillis = startDateMillis,
                        isEnabled = isEnabled
                    )
                    // ----> Schedule the work <----
                    ReminderScheduler.scheduleReminder(context, detailsToSave, reminderType) {
                        viewModel.saveReminder(reminderType, detailsToSave)
                    }

                    onSaveReminder(detailsToSave) // Call original onSaveReminder (e.g., to navigate back or update UI)
                },
                icon = { Icon(Icons.Filled.Check, "Save Reminder") },
                text = { Text("Save Reminder") },
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- Reminder Title ---
            OutlinedTextField(
                value = reminderTitle,
                onValueChange = { reminderTitle = it },
                label = { Text("Reminder Name") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        Icons.Filled.DriveFileRenameOutline,
                        contentDescription = "Reminder Name"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Time & Date Section ---
            SectionTitle(text = "Time & Date")
            SettingItem(
                icon = Icons.Filled.Schedule,
                label = "Remind me at",
                value = formatTime(selectedHour, selectedMinute),
                onClick = {
                    showTimePickerDialog = true
                }
            )
            SettingItem(
                icon = Icons.Filled.CalendarToday,
                label = "Start on",
                value = formatDate(startDateMillis),
                onClick = {
                    showDatePickerDialog = true
                }
            )

            Spacer(modifier = Modifier.height(24.dp)) // Space before Frequency section
            SectionTitle(text = "Frequency")
            FrequencySelector( // This is the composable defined further down
                selectedFrequency = selectedFrequency,
                onFrequencySelected = { newFrequency ->
                    selectedFrequency = newFrequency
                    // Reset specific days if frequency changes from SPECIFIC_DAYS
                    if (newFrequency != ReminderFrequency.SPECIFIC_DAYS) {
                        selectedDays = emptySet()
                    }
                }
            )

            // Conditional UI based on selected frequency
            if (selectedFrequency == ReminderFrequency.SPECIFIC_DAYS) {
                Spacer(modifier = Modifier.height(12.dp))
                DayOfWeekSelector( // This is the composable defined further down
                    selectedDays = selectedDays,
                    onDaySelected = { day ->
                        selectedDays = if (selectedDays.contains(day)) {
                            LinkedHashSet(selectedDays - day) // Maintain order
                        } else {
                            LinkedHashSet(selectedDays + day) // Maintain order
                        }
                    }
                )
            }

            if (selectedFrequency == ReminderFrequency.EVERY_X_MINUTES) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = customIntervalMinutes.toString(),
                    onValueChange = { value ->
                        val filteredValue = value.filter { it.isDigit() }
                        val enteredMinutes =
                            filteredValue.toIntOrNull() ?: initialDetails.customIntervalMinutes

                        customIntervalMinutes = if (enteredMinutes >= 15) {
                            enteredMinutes
                        } else {
                            15
                        }

                    },
                    label = { Text("Interval (min 15 mins)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.AvTimer, "Interval") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle(text = "Status")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isEnabled) {
                            showTurnOffReminderDialog = true
                            ReminderScheduler.cancelReminderById(context, reminderType)
                        } else {
                            isEnabled = !isEnabled
                        }
                    }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isEnabled) Icons.Filled.NotificationsActive else Icons.Filled.NotificationsOff,
                        contentDescription = "Reminder Status",
                        tint = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = if (isEnabled) "Reminder is ON" else "Reminder is OFF",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isEnabled, onCheckedChange = {
                        if (isEnabled) {
                            showTurnOffReminderDialog = true
                            ReminderScheduler.cancelReminderById(context, reminderType)
                        } else {
                            isEnabled = !isEnabled
                        }
//                        isEnabled = it
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(80.dp)) // Space for the FAB
        } // End of Column
    } // End of Scaffold
} // End of ModernReminderScreen

// --- Helper UI Components ---

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SettingItem(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Flat design
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "Select",
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp)) // Space between setting items
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FrequencySelector(
    selectedFrequency: ReminderFrequency,
    onFrequencySelected: (ReminderFrequency) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        val frequencies = ReminderFrequency.entries.toTypedArray()
        FlowRow( // Use FlowRow for responsiveness if items don't fit
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) // For multi-line
        ) {
            frequencies.forEach { frequency ->
                FilterChip(
                    selected = frequency == selectedFrequency,
                    onClick = { onFrequencySelected(frequency) },
                    label = { Text(frequency.displayName) },
                    leadingIcon = if (frequency == selectedFrequency) {
                        {
                            Icon(
                                Icons.Filled.Done,
                                "Selected",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null,
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DayOfWeekSelector(
    selectedDays: Set<DayOfWeek>,
    onDaySelected: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround, // Distribute space evenly
        verticalArrangement = Arrangement.Center
    ) {
        DayOfWeek.entries.forEach { day ->
            val isSelected = selectedDays.contains(day)
            Box(
                modifier = Modifier
                    .size(40.dp) // Consistent size for tappable area
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        CircleShape
                    )
                    .clickable { onDaySelected(day) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.shortName,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp
                )
            } // End of Box for day
        } // End of forEach DayOfWeek
    } // End of FlowRow for DayOfWeekSelector
} // End of DayOfWeekSelector
