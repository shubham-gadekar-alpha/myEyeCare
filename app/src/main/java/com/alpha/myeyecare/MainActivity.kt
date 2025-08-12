package com.alpha.myeyecare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alpha.myeyecare.model.AppDestinations
import com.alpha.myeyecare.model.ReminderTypes.DRINKING_REMINDER
import com.alpha.myeyecare.model.ReminderTypes.EYE_REMINDER
import com.alpha.myeyecare.ui.screens.HomeScreen
import com.alpha.myeyecare.ui.screens.ReminderDetails
import com.alpha.myeyecare.ui.screens.SetupReminderScreen

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
            AppDestinations.EYE_CARE_REMINDER_SCREEN
        ) {
            SetupReminderScreen(
                reminderType = EYE_REMINDER,
                initialDetails = ReminderDetails(title = "My Eye Care Break"),
                onSaveReminder = {
                    navController.popBackStack()
                },
                onBackIconPressed = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppDestinations.WATER_REMINDER_SCREEN) {
            SetupReminderScreen(
                reminderType = DRINKING_REMINDER,
                initialDetails = ReminderDetails(title = "Drink Water Break"),
                onSaveReminder = {
                    navController.popBackStack()
                },
                onBackIconPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}
