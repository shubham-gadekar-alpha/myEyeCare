package com.alpha.myeyecare

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.*
import com.alpha.myeyecare.ReminderTypes.DRINKING_REMINDER
import com.alpha.myeyecare.ReminderTypes.EYE_REMINDER
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationUtils.createNotificationChannel(this)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}

object AppDestinations {
    const val HOME_SCREEN = "home"
    const val EYE_CARE_SCREEN = "eye_care"
    const val WATER_REMINDER_SCREEN = "water_reminder"
    const val SET_REMINDER_SCREEN = "set_reminder_screen"
}

object ReminderTypes {
    const val EYE_REMINDER = "eye_reminder"
    const val DRINKING_REMINDER = "drinking_reminder"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController() // Create a NavController

    NavHost(
        navController = navController,
        startDestination = AppDestinations.HOME_SCREEN // Set the starting screen
    ) {
        composable(AppDestinations.HOME_SCREEN) {
            HomeScreen(navController = navController)
        }
        composable(
            AppDestinations.EYE_CARE_SCREEN
        ) {
            EyeCareScreen(navController = navController)
        }
        composable(AppDestinations.WATER_REMINDER_SCREEN) {
            WaterReminderScreen(navController = navController)
        }

        composable(
            AppDestinations.SET_REMINDER_SCREEN,
            arguments = listOf(
                navArgument("reminderType") {
                    type = NavType.StringType
                    defaultValue = EYE_REMINDER
                }
            )) {
            val reminderType = it.arguments?.getString("reminderType").toString()
            ReminderUI(navController = navController, reminderType = reminderType)
        }
    }
}

// --- Reusable Button Style (Optional) ---
@Composable
fun FeatureButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 8.dp)
            .height(50.dp)
    ) {
        Text(text)
    }
}

// --- Screen Composable Functions ---

@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choose Your Reminder",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        FeatureButton(
            text = "Eye Care Reminder",
            onClick = {
                navController.navigate(AppDestinations.EYE_CARE_SCREEN)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        FeatureButton(
            text = "Water Drink Reminder",
            onClick = { navController.navigate(AppDestinations.WATER_REMINDER_SCREEN) }
        )
    }
}

@Composable
fun EyeCareScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Replace with your actual Eye Care UI and reminder setup logic
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Eye Care Reminder Setup", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        // Add your UI elements for setting eye care reminders here
        // e.g., Time pickers, frequency selectors, save button

        Button(onClick = { navController.navigate("${AppDestinations.SET_REMINDER_SCREEN}?reminderType=$EYE_REMINDER") }) {
            Text("Set Eye Care Reminder")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) { // Go back to Home
            Text("Back to Home")
        }
    }
}

@Composable
fun WaterReminderScreen(navController: NavController, modifier: Modifier = Modifier) {
    // Replace with your actual Water Reminder UI and reminder setup logic
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Water Drink Reminder Setup", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        // Add your UI elements for setting water reminders here
        // e.g., Target amount, interval pickers, save button

        Button(onClick = { navController.navigate("${AppDestinations.SET_REMINDER_SCREEN}?reminderType=$DRINKING_REMINDER") }) {
            Text("Set Water Reminder")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) { // Go back to Home
            Text("Back to Home")
        }
    }
}

@Composable
fun ReminderUI(
    navController: NavController,
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
