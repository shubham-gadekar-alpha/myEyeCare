package com.alpha.myeyecare.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alpha.myeyecare.model.AppDestinations.EYE_CARE_REMINDER_SCREEN
import com.alpha.myeyecare.model.AppDestinations.WATER_REMINDER_SCREEN
import com.alpha.myeyecare.ui.FeatureCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier, onGoToSuggestionsClicked: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Health Reminders", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton( // Use ExtendedFloatingActionButton
                onClick = {
                    onGoToSuggestionsClicked.invoke()
                },
                icon = { // Icon slot
                    Icon(
                        Icons.Filled.RateReview, // Or Icons.Filled.AddComment, Icons.Filled.Feedback
                        contentDescription = "Suggestion Icon"
                    )
                },
                text = { // Text slot
                    Text(text = "Suggestion")
                },
                containerColor = MaterialTheme.colorScheme.primary, // Or secondaryContainer
                contentColor = MaterialTheme.colorScheme.onPrimary  // Or onSecondaryContainer
                // You can also control expansion/shrinking with the `expanded` parameter
                // expanded = true // Default is true, set to false to make it shrink to icon-only
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
                .padding(16.dp), // Additional padding for content
            verticalArrangement = Arrangement.Center, // Center content vertically
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "What would you like to set up?",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            FeatureCard(
                title = "Eye Care Reminder",
                description = "Protect your vision, take regular breaks.",
                icon = Icons.Rounded.Visibility,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                onClick = { navController.navigate(EYE_CARE_REMINDER_SCREEN) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            FeatureCard(
                title = "Water Drink Reminder",
                description = "Stay hydrated throughout the day.",
                icon = Icons.Filled.WaterDrop,
                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                onClick = { navController.navigate(WATER_REMINDER_SCREEN) }
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "\"Take care of your body. It's the only place you have to live.\"",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = 32.dp, bottom = 60.dp)
            )
        }
    }
}
