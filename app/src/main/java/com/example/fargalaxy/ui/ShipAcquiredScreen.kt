package com.example.fargalaxy.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fargalaxy.R
import android.graphics.RenderEffect as AndroidRenderEffect
import android.graphics.Shader
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.fargalaxy.data.ShipRepository
import com.example.fargalaxy.model.Ship

/**
 * ShipAcquiredScreen composable - displays when a ship is purchased in the Staryard.
 * Shows the ship render image on top of an award background JSON animation.
 */
@Composable
fun ShipAcquiredScreen(
    shipId: String,
    onContinueClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val density = LocalDensity.current
    
    // Get ship data
    val ship = ShipRepository.getAllShips().find { it.id == shipId }
    
    // Track JSON size for image sizing
    var jsonWidth by remember { mutableStateOf(0.dp) }
    var jsonHeight by remember { mutableStateOf(0.dp) }
    
    // Load JSON composition
    val jsonComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.awardbackground))
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // Block all pointer events to prevent pager scrolling
                detectTapGestures { }
            },
        contentAlignment = Alignment.Center
    ) {
        // Blur and overlay: Same as TravelSuccessModal (96% opacity black overlay)
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
        
        // Main content container: JSON+image and title+label, centered on screen
        // Container stretches full width, elements inside follow original constraints
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // JSON background: 85% of screen width
            Box(
                modifier = Modifier
                    .width(screenWidth * 0.85f)
                    .onSizeChanged { size ->
                        with(density) {
                            jsonWidth = size.width.toDp()
                            jsonHeight = size.height.toDp()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // JSON animation (plays once)
                if (jsonComposition != null) {
                    LottieAnimation(
                        composition = jsonComposition,
                        iterations = 1, // Play only once
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
                
                // Ship render image: 80% of JSON width, maintaining aspect ratio
                // Vertically and horizontally centered on JSON
                if (ship != null) {
                    Image(
                        painter = painterResource(id = ship.renderImageResId),
                        contentDescription = ship.name,
                        modifier = Modifier
                            .width(jsonWidth * 0.8f)
                            .wrapContentHeight(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            
            // Title and label: Same format as TravelSuccessModal
            // Labels respect 16dp side padding
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Title: "(ship name) acquired" - bold, 28sp
                Text(
                    text = "${ship?.name ?: "Ship"} acquired",
                    fontFamily = Exo2,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center
                )
                
                // Label: "You can now use it in your journeys" - regular, 16sp
                Text(
                    text = "You can now use it in your journeys",
                    fontFamily = Exo2,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400, // Regular
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Continue button at bottom: Same format as TravelSuccessModal button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
        ) {
            // Button: "CONTINUE" - same format as LAUNCH button (primary style)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(80.dp))
                    .background(Color(0xFFFFFFFF)) // White fill
                    .clickable(onClick = onContinueClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CONTINUE",
                    fontFamily = Exo2,
                    fontSize = 24.sp,
                    lineHeight = 24.sp,
                    color = Color(0xFF010102), // Dark text
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-2).dp)
                )
            }
        }
    }
}
