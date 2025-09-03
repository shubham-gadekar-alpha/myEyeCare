package com.alpha.myeyecare.presentation.ui.splash

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.alpha.myeyecare.R
import com.alpha.myeyecare.common.constants.AppDestinations

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(viewModel.navigationToHome) {
        viewModel.navigationToHome.collect {
            if (it) {
                navController.navigate(AppDestinations.HOME_SCREEN)
            }
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 2000, easing = LinearEasing),
        label = "SplashScreenProgress"
    )

    LaunchedEffect(key1 = true) {
        progress = 1f
    }

    Box(
        modifier = Modifier.Companion
            .fillMaxSize()
            .background(Color(0xFF223F2E)),
        contentAlignment = Alignment.Companion.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_app_launcher_foreground),
            contentDescription = "App Logo",
            modifier = Modifier.Companion.size(300.dp)
        )

        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(bottom = 150.dp, start = 180.dp, end = 180.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(MaterialTheme.shapes.small),
                color = Color(0xFF59E10B),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Companion.Round
            )
            Spacer(modifier = Modifier.Companion.height(8.dp))
            Text(
                text = "Loading...",
                color = Color.Companion.White,
                fontSize = 12.sp
            )
        }
    }
}
