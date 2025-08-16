package com.alpha.myeyecare.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.alpha.myeyecare.R
import com.alpha.myeyecare.model.AppDestinations
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navController: NavController) {
    var progress by remember { mutableFloatStateOf(0f) } // For the horizontal loader

    // Animate the progress from 0f to 1f over 1000ms (1 second)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 2000, easing = LinearEasing),
        label = "SplashScreenProgress"
    )

    LaunchedEffect(key1 = true) {
        progress = 1f // Trigger the animation to start towards 1f
        delay(2000L) // Total duration of the splash screen
        navController.navigate(AppDestinations.HOME_SCREEN) {
            // Pop Splash Sreen from backstack so user can't go back to it
            popUpTo(AppDestinations.SPLASH_SCREEN) {
                inclusive = true
            }
            // Avoid multiple copies of HomeScreen when re-navigating
            launchSingleTop = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF223F2E)), // Or your desired splash background
        contentAlignment = Alignment.Center
    ) {
        // Icon in the Middle
        Image(
            painter = painterResource(id = R.drawable.ic_app_launcher_foreground), // Replace with your logo
            contentDescription = "App Logo",
            modifier = Modifier.size(300.dp) // Adjust size as needed
        )

        // Horizontal Loader at the Bottom
        Column(
            modifier = Modifier
                .fillMaxSize() // Fill the screen to align loader at bottom
                .padding(bottom = 150.dp, start = 180.dp, end = 180.dp), // Padding for the loader
            verticalArrangement = Arrangement.Bottom, // Align loader to the bottom
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                progress = { animatedProgress }, // Use the animated progress
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp) // Adjust height of the loader
                    .clip(MaterialTheme.shapes.small), // Optional: rounded corners
                color = Color(0xFF59E10B), // Loader filled color
                trackColor = MaterialTheme.colorScheme.surfaceVariant, // Loader track color
                strokeCap = StrokeCap.Round // Optional: rounded ends for the progress
            )
            Spacer(modifier = Modifier.height(8.dp)) // Optional: space below loader
            Text(
                text = "Loading...",
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}