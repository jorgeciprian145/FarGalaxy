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
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.radar))

    // reproducir mucho más lento (por ejemplo 0.0833f ≈ 1/12)
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 0.8f, // ajustá este valor hasta que el giro completo dure 60s reales
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}

