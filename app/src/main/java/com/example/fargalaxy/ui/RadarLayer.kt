package com.example.fargalaxy.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.fargalaxy.R

@Composable
fun RadarLayer(
    isTraveling: Boolean = false,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.radar))

    // Animation speed changes based on travel state
    // When traveling: 1.75f (faster animation)
    // When idle: 0.8f (normal speed)
    val animationSpeed = if (isTraveling) 6f else 0.8f

    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = animationSpeed,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}

