package com.alpha.myeyecare.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alpha.myeyecare.model.AppDestinations.EYE_CARE_SCREEN
import com.alpha.myeyecare.model.AppDestinations.WATER_REMINDER_SCREEN
import com.alpha.myeyecare.ui.FeatureButton

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
                navController.navigate(EYE_CARE_SCREEN)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        FeatureButton(
            text = "Water Drink Reminder",
            onClick = { navController.navigate(WATER_REMINDER_SCREEN) }
        )
    }
}
