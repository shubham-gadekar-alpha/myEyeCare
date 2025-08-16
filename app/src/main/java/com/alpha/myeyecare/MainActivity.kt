package com.alpha.myeyecare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alpha.myeyecare.localDb.ReminderPreferences
import com.alpha.myeyecare.model.AppDestinations
import com.alpha.myeyecare.model.ReminderTypes.DRINKING_REMINDER
import com.alpha.myeyecare.model.ReminderTypes.EYE_REMINDER
import com.alpha.myeyecare.ui.screens.HomeScreen
import com.alpha.myeyecare.ui.screens.ReminderDetails
import com.alpha.myeyecare.ui.screens.SetupReminderScreen
import com.alpha.myeyecare.ui.screens.SplashScreen
import com.alpha.myeyecare.ui.screens.UserSuggestionScreen

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
    val navController = rememberNavController()
    val localDb = ReminderPreferences(LocalContext.current)

    NavHost(
        navController = navController,
        startDestination = AppDestinations.SPLASH_SCREEN // Set the starting screen
    ) {

        composable(AppDestinations.HOME_SCREEN) {
            HomeScreen(navController = navController)
        }

        composable(AppDestinations.USER_SUGGESTION_SCREEN) {
            UserSuggestionScreen(
                navController = navController,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSubmitSuggestion = { name, email, text ->

                })
        }

        composable(AppDestinations.SPLASH_SCREEN) {
            SplashScreen(navController = navController)
        }

        composable(
            AppDestinations.EYE_CARE_REMINDER_SCREEN
        ) {
            SetupReminderScreen(
                reminderType = EYE_REMINDER,
                localDb = localDb,
                initialDetails = localDb.getReminder(EYE_REMINDER)
                    ?: ReminderDetails(title = "My Eye Care Break"),
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
                localDb = localDb,
                initialDetails = localDb.getReminder(DRINKING_REMINDER)
                    ?: ReminderDetails(title = "Drink Water Break"),
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
