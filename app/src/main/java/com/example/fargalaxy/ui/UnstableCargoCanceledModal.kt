package com.example.fargalaxy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.RenderEffect as AndroidRenderEffect
import android.graphics.Shader
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.fargalaxy.R

/**
 * UnstableCargoCanceledModal composable - displays a cancellation modal when unstable cargo penalty occurs.
 * Similar to TravelCanceledModal but with specific title and message for unstable cargo.
 * 
 * @param onContinueClick Callback when continue button is clicked
 * @param modifier Modifier for the modal
 */
@Composable
fun UnstableCargoCanceledModal(
    onContinueClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Track JSON height for offset calculation
    var jsonHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    
    // Load JSON composition
    val jsonComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.modalcheck))
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Blur and overlay: Blurs the content behind and applies dark overlay with 96% opacity
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    renderEffect = AndroidRenderEffect.createBlurEffect(
                        16f,
                        16f,
                        Shader.TileMode.CLAMP
                    ).asComposeRenderEffect()
                }
                .background(Color.Black.copy(alpha = 0.96f))
        )
        
        // Modal Container with JSON on top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Modal Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF373A3E), // Top color
                                Color(0xFF2B2E32)  // Bottom color
                            )
                        ),
                        shape = RoundedCornerShape(32.dp) // 32dp corner radius
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFF6B6C6F),
                        shape = RoundedCornerShape(32.dp) // 32dp corner radius
                    )
                    .padding(
                        top = 72.dp,
                        bottom = 24.dp,
                        start = 24.dp,
                        end = 24.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title: "Unstable cargo" - bold, 28sp
                Text(
                    text = "Unstable cargo",
                    fontFamily = Exo2,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                // Spacing: 4dp between title and label
                Spacer(modifier = Modifier.height(4.dp))
                
                // Label: "You quit the app with the unstable cargo, the trip got canceled" - regular, 16sp
                Text(
                    text = "You quit the app with the unstable cargo, the trip got canceled",
                    fontFamily = Exo2,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                // Spacing: 16dp between label and button
                Spacer(modifier = Modifier.height(16.dp))
                
                // Button: "CONTINUE" - same format as LAUNCH button (primary style)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(80.dp))
                        .background(Color(0xFFFFFFFF)) // White background (primary style)
                        .clickable(onClick = onContinueClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CONTINUE",
                        fontFamily = Exo2,
                        fontSize = 24.sp,
                        lineHeight = 24.sp,
                        color = Color(0xFF010102), // Dark text (primary style)
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = (-2).dp)
                    )
                }
            }
            
            // JSON Animation: Positioned right above the container (bottom edge at container top), then offset down by 50% of its height
            // Width is 70% of screen width
            val configuration = LocalConfiguration.current
            val screenWidth = with(density) { configuration.screenWidthDp.dp }
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .width(screenWidth * 0.7f) // 70% of screen width
                    .onSizeChanged { size ->
                        with(density) {
                            jsonHeight = size.height.toDp()
                        }
                    }
                    .offset(y = -jsonHeight + jsonHeight / 2) // Move up by full height (so bottom aligns with container top), then offset down by 50%
            ) {
                LottieAnimation(
                    composition = jsonComposition,
                    iterations = 1, // Play only once
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
