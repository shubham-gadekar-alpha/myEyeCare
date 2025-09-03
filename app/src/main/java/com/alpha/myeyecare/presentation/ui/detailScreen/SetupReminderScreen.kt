package com.alpha.myeyecare.presentation.ui.detailScreen

import android.Manifest
import android.os.Build
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alpha.myeyecare.common.utils.formatDate
import com.alpha.myeyecare.common.utils.formatTime
import com.alpha.myeyecare.domain.model.DayOfWeek
import com.alpha.myeyecare.domain.model.ReminderDetails
import com.alpha.myeyecare.domain.model.ReminderFrequency
import com.alpha.myeyecare.presentation.ui.CheckUserNotificationPermission
import com.alpha.myeyecare.worker.ReminderScheduler
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SetupReminderScreen(
    viewModel: SetupReminderViewModel = hiltViewModel(),
    reminderType: String,
    onSaveReminder: (ReminderDetails) -> Unit,
    onBackIconPressed: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.fetchData(reminderType)
    }

    var shouldCheckPermission by remember { mutableStateOf(false) }

    val initialDetails = viewModel.reminderDetails.collectAsState().value

    var reminderTitle by remember { mutableStateOf(initialDetails.title) }

    var selectedHour by remember { mutableIntStateOf(initialDetails.hour) }
    var selectedMinute by remember { mutableIntStateOf(initialDetails.minute) }
    var startDateMillis by remember { mutableLongStateOf(initialDetails.startDateMillis) }

    var selectedFrequency by remember { mutableStateOf(initialDetails.frequency) }
    var selectedDays by remember { mutableStateOf<Set<DayOfWeek>>(LinkedHashSet(initialDetails.selectedDays)) }
    var customIntervalMinutes by remember { mutableIntStateOf(initialDetails.customIntervalMinutes) }
    var isEnabled by remember { mutableStateOf(initialDetails.isEnabled) }

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    var showTurnOffReminderDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = startDateMillis,
    )
    val timePickerState = rememberTimePickerState(
        initialHour = selectedHour, initialMinute = selectedMinute, is24Hour = false
    )

    LaunchedEffect(viewModel.reminderDetails) {
        viewModel.reminderDetails.collect { details ->
            reminderTitle = details.title
            selectedHour = details.hour
            selectedMinute = details.minute
            startDateMillis = details.startDateMillis
            selectedFrequency = details.frequency
            selectedDays = LinkedHashSet(details.selectedDays)
            customIntervalMinutes = details.customIntervalMinutes
            isEnabled = details.isEnabled
        }
    }

    if (shouldCheckPermission) {
        CheckUserNotificationPermission(
            fromReminderDetailsScreen = true,
            shouldCheckPermission = true,
            permissionGranted = {
                val detailsToSave = ReminderDetails(
                    title = reminderTitle.ifBlank { "Untitled Reminder" },
                    hour = selectedHour,
                    minute = selectedMinute,
                    frequency = selectedFrequency,
                    selectedDays = if (selectedFrequency == ReminderFrequency.SPECIFIC_DAYS) {
                        selectedDays
                    } else {
                        emptySet()
                    },
                    customIntervalMinutes = customIntervalMinutes,
                    startDateMillis = startDateMillis,
                    isEnabled = isEnabled
                )

                ReminderScheduler.scheduleReminder(context, detailsToSave, reminderType) {
                    viewModel.saveReminder(reminderType, detailsToSave)
                }

                onSaveReminder(detailsToSave)
                shouldCheckPermission = false
            },
            onActionClick = {
                shouldCheckPermission = false
            }
        )
    }

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
                    }) { Text("OK") }
            }, dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePickerDialog) {
        AlertDialog(
            onDismissRequest = { showTimePickerDialog = false },
            modifier = Modifier.Companion.fillMaxWidth(),
            title = {
                Text(
                    "Select Time",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.Companion.padding(start = 16.dp, top = 16.dp)
                )
            },
            text = {
                Column(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.Companion.CenterHorizontally
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
            modifier = Modifier.Companion.fillMaxWidth(),
            text = {
                Text(
                    "Are you sure want to Turn OFF Reminder?",
                    modifier = Modifier.Companion.padding(start = 16.dp, top = 16.dp)
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
                title = { Text("Set Reminder", fontWeight = FontWeight.Companion.Bold) },
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
        }, floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    shouldCheckPermission = true
                },
                icon = { Icon(Icons.Filled.Check, "Save Reminder") },
                text = { Text("Save Reminder") },
            )
        }, floatingActionButtonPosition = FabPosition.Companion.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val notificationPermissionState = rememberPermissionState(
                    permission = Manifest.permission.POST_NOTIFICATIONS
                )

                if (!notificationPermissionState.status.isGranted) {
                    Text(
                        text = "You Might not receive the Notifications due to not granting the Notification permission",
                        color = Color.Red
                    )
                }
            }
            Spacer(modifier = Modifier.Companion.height(16.dp))

            OutlinedTextField(
                value = reminderTitle,
                onValueChange = { reminderTitle = it },
                label = { Text("Reminder Name") },
                modifier = Modifier.Companion.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        Icons.Filled.DriveFileRenameOutline, contentDescription = "Reminder Name"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.Companion.height(24.dp))

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

            Spacer(modifier = Modifier.Companion.height(24.dp))
            SectionTitle(text = "Frequency")
            FrequencySelector(
                selectedFrequency = selectedFrequency, onFrequencySelected = { newFrequency ->
                    selectedFrequency = newFrequency
                    if (newFrequency != ReminderFrequency.SPECIFIC_DAYS) {
                        selectedDays = emptySet()
                    }
                }
            )

            if (selectedFrequency == ReminderFrequency.SPECIFIC_DAYS) {
                Spacer(modifier = Modifier.Companion.height(12.dp))
                DayOfWeekSelector(
                    selectedDays = selectedDays, onDaySelected = { day ->
                        selectedDays = if (selectedDays.contains(day)) {
                            LinkedHashSet(selectedDays - day)
                        } else {
                            LinkedHashSet(selectedDays + day)
                        }
                    }
                )
            }

            if (selectedFrequency == ReminderFrequency.EVERY_X_MINUTES) {
                Spacer(modifier = Modifier.Companion.height(12.dp))
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
                    modifier = Modifier.Companion.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.AvTimer, "Interval") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.Companion.height(24.dp))
            SectionTitle(text = "Status")
            Row(
                modifier = Modifier.Companion
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
                verticalAlignment = Alignment.Companion.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                    Icon(
                        imageVector = if (isEnabled) Icons.Filled.NotificationsActive else Icons.Filled.NotificationsOff,
                        contentDescription = "Reminder Status",
                        tint = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.Companion.size(24.dp)
                    )
                    Spacer(modifier = Modifier.Companion.width(16.dp))
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
                    }, colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
            Spacer(modifier = Modifier.Companion.height(80.dp))
        }
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Companion.Bold),
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
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Flat design
    ) {
        Row(
            modifier = Modifier.Companion.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.Companion.size(24.dp)
            )
            Spacer(modifier = Modifier.Companion.width(16.dp))
            Column(modifier = Modifier.Companion.weight(1f)) {
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
    Spacer(modifier = Modifier.Companion.height(8.dp))
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
        FlowRow(
            modifier = Modifier.Companion.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                modifier = Modifier.Companion.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    },
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
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
        horizontalArrangement = Arrangement.SpaceAround,
        verticalArrangement = Arrangement.Center
    ) {
        DayOfWeek.entries.forEach { day ->
            val isSelected = selectedDays.contains(day)
            Box(
                modifier = Modifier.Companion
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        CircleShape
                    )
                    .clickable { onDaySelected(day) },
                contentAlignment = Alignment.Companion.Center
            ) {
                Text(
                    text = day.shortName,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.Companion.Bold else FontWeight.Companion.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }
}
