package com.alpha.myeyecare

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alpha.myeyecare.localDb.ReminderPreferences
import com.alpha.myeyecare.model.AppDestinations
import com.alpha.myeyecare.model.NavigationEvent
import com.alpha.myeyecare.model.ReminderTypes.DRINKING_REMINDER
import com.alpha.myeyecare.model.ReminderTypes.EYE_REMINDER
import com.alpha.myeyecare.model.Suggestion
import com.alpha.myeyecare.ui.screens.HomeScreen
import com.alpha.myeyecare.ui.screens.ReminderDetails
import com.alpha.myeyecare.ui.screens.SetupReminderScreen
import com.alpha.myeyecare.ui.screens.SplashScreen
import com.alpha.myeyecare.ui.screens.UserSuggestionScreen
import com.alpha.myeyecare.viewModel.MainViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationUtils.createNotificationChannel(this)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainAppScreen()
                }
            }
        }
    }
}

@Composable
fun MainAppScreen(
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel = hiltViewModel()
) {

    // Observe navigation events from ViewModel
    LaunchedEffect(key1 = mainViewModel.navigationEvent) {
        mainViewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateToHome -> {
                    navController.navigate(AppDestinations.HOME_SCREEN) {
                        popUpTo(AppDestinations.SPLASH_SCREEN) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                is NavigationEvent.NavigateToSuggestions -> {
                    navController.navigate(AppDestinations.USER_SUGGESTION_SCREEN)
                }

                is NavigationEvent.NavigateBack -> {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    } else {
                        // Handle case where there's no screen to pop back to (e.g., from home)
                        // Maybe exit app or show a Toast. For now, let it be.
                    }
                }

                NavigationEvent.NavigateToDrinkWaterReminder -> {
                    navController.navigate(AppDestinations.WATER_REMINDER_SCREEN)
                }

                NavigationEvent.NavigateToEyeCareReminder -> {
                    navController.navigate(AppDestinations.EYE_CARE_REMINDER_SCREEN)
                }

                NavigationEvent.NavigateToSplash -> {
                    navController.navigate(AppDestinations.SPLASH_SCREEN)
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppDestinations.SPLASH_SCREEN // Set the starting screen
    ) {

        composable(AppDestinations.HOME_SCREEN) {
            HomeScreen(navController = navController) {
                mainViewModel.onGoToSuggestionsClicked()
            }
        }

        composable(AppDestinations.USER_SUGGESTION_SCREEN) {
            UserSuggestionScreen(
                viewModel = mainViewModel,
                onNavigateBack = {
                    mainViewModel.onNavigateBack()
                }
            )
        }

        composable(AppDestinations.SPLASH_SCREEN) {
            SplashScreen(navController = navController)
        }

        composable(
            AppDestinations.EYE_CARE_REMINDER_SCREEN
        ) {
            SetupReminderScreen(
                viewModel = mainViewModel,
                reminderType = EYE_REMINDER,
                initialDetails = mainViewModel.getReminder(EYE_REMINDER)
                    ?: ReminderDetails(title = "My Eye Care Break"),
                onSaveReminder = {
                    mainViewModel.onNavigateBack()
                },
                onBackIconPressed = {
                    mainViewModel.onNavigateBack()
                }
            )
        }

        composable(AppDestinations.WATER_REMINDER_SCREEN) {
            SetupReminderScreen(
                viewModel = mainViewModel,
                reminderType = DRINKING_REMINDER,
                initialDetails = mainViewModel.getReminder(DRINKING_REMINDER)
                    ?: ReminderDetails(title = "Drink Water Break"),
                onSaveReminder = {
                    mainViewModel.onNavigateBack()
                },
                onBackIconPressed = {
                    mainViewModel.onNavigateBack()
                }
            )
        }
    }
}
