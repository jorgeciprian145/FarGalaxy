package com.example.fargalaxy.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.fargalaxy.R
import kotlinx.coroutines.delay

@Composable
fun RadarLayer(
    isTraveling: Boolean = false,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.radar))

    // Animation speed changes based on travel state
    // When traveling: 6f (faster animation)
    // When idle: 0.8f (normal speed)
    val animationSpeed = if (isTraveling) 6f else 0.8f

    // State to control opacity target - starts at 1f (100%), fades to 0f (0%) after 5 seconds when traveling
    var targetOpacity by remember(isTraveling) { mutableStateOf(1f) }
    
    // Trigger fade-out sequence when isTraveling becomes true
    // Wait 5 seconds, then start fading to 0% opacity over 4 seconds
    LaunchedEffect(isTraveling) {
        if (isTraveling) {
            targetOpacity = 1f // Start at 100% opacity
            delay(5000) // Wait 5 seconds
            targetOpacity = 0f // Then fade to 0% opacity
        } else {
            targetOpacity = 1f // Reset to 100% immediately when travel stops
        }
    }

    // Animate opacity from current value to target over 4 seconds (smooth and subtle transition)
    val opacity by animateFloatAsState(
        targetValue = targetOpacity,
        animationSpec = tween(durationMillis = 4000),
        label = "radar_fade_out"
    )

    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = animationSpeed,
        modifier = modifier
            .alpha(opacity), // Apply fade-out animation
        contentScale = ContentScale.Crop
    )
}

