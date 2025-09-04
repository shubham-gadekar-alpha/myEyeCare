package com.alpha.myeyecare.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alpha.myeyecare.common.constants.AppDestinations
import com.alpha.myeyecare.common.constants.ReminderTypes.DRINKING_REMINDER
import com.alpha.myeyecare.common.constants.ReminderTypes.EYE_REMINDER
import com.alpha.myeyecare.presentation.ui.detailScreen.SetupReminderScreen
import com.alpha.myeyecare.presentation.ui.home.HomeScreen
import com.alpha.myeyecare.presentation.ui.splash.SplashScreen
import com.alpha.myeyecare.presentation.ui.suggestion.UserSuggestionScreen

@Composable
fun NavGraph() {
    val navController: NavHostController = rememberNavController()

    var shouldShowPermissionDialog by remember { mutableStateOf(true) }

    NavHost(
        navController = navController, startDestination = AppDestinations.SPLASH_SCREEN
    ) {
        composable(AppDestinations.HOME_SCREEN) {
            HomeScreen(
                navController = navController,
                shouldCheckPermission = shouldShowPermissionDialog == true,
                onGoToSuggestionsClicked = {
                    navController.navigate(AppDestinations.USER_SUGGESTION_SCREEN)
                },
                updatePermissionStatus = {
                    shouldShowPermissionDialog = false
                }
            )
        }

        composable(AppDestinations.USER_SUGGESTION_SCREEN) {
            UserSuggestionScreen(
                onNavigateBack = {
                    navController.popBackStack()
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
                reminderType = EYE_REMINDER, onSaveReminder = {
                    navController.popBackStack()
                }, onBackIconPressed = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppDestinations.WATER_REMINDER_SCREEN) {
            SetupReminderScreen(
                reminderType = DRINKING_REMINDER, onSaveReminder = {
                    navController.popBackStack()
                }, onBackIconPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}
