package com.example.fargalaxy.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.fargalaxy.data.ShipRepository

/**
 * ShipAcquiredScreen composable - displays when a ship is purchased in the Staryard.
 * Uses the same structure as RewardsScreen top portion, but with awardbackground JSON + ship image.
 */
@Composable
fun ShipAcquiredScreen(
    shipId: String,
    onContinueClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp }
    
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
                // Block all pointer events to prevent pager scrolling and interaction
                detectTapGestures { }
            },
        contentAlignment = Alignment.Center
    ) {
        // Blur and overlay: Same as RewardsScreen (96% opacity black overlay)
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
        
        // Main layout: Button at bottom, content container centered vertically
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp) // 16dp side padding
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Spacer to push content container to center
            Spacer(modifier = Modifier.weight(1f))
            
            // Content container: JSON + title + label, vertically centered
            // Maintains 24dp spacing between JSON and title
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // JSON Animation with ship image: awardbackground + ship render
                Box(
                    modifier = Modifier
                        .width(screenWidth * 0.85f) // 85% of screen width (awardbackground size)
                        .onSizeChanged { size ->
                            with(density) {
                                jsonWidth = size.width.toDp()
                                jsonHeight = size.height.toDp()
                            }
                        }
                ) {
                    // JSON animation (plays once)
                    if (jsonComposition != null) {
                        LottieAnimation(
                            composition = jsonComposition,
                            iterations = 1, // Play only once
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
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
                                .align(Alignment.Center)
                                .width(jsonWidth * 0.8f)
                                .wrapContentHeight(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                
                // 24dp spacing between JSON and title (maintains current spacing)
                Spacer(modifier = Modifier.height(24.dp))
                
                // Title: "(ship name) acquired" - bold, 28sp (same format as RewardsScreen)
                Text(
                    text = "${ship?.name ?: "Ship"} acquired",
                    fontFamily = Exo2,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                // 4dp spacing between title and label (same as RewardsScreen)
                Spacer(modifier = Modifier.height(4.dp))
                
                // Label: "You can now use it in your journeys" - regular, 16sp
                Text(
                    text = "You can now use it in your journeys",
                    fontFamily = Exo2,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
            
            // Spacer to push button to bottom
            Spacer(modifier = Modifier.weight(1f))
            
            // Continue button at bottom: Same format as RewardsScreen button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(80.dp))
                    .background(Color(0xFFFFFFFF))
                    .clickable(onClick = onContinueClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CONTINUE",
                    fontFamily = Exo2,
                    fontSize = 24.sp,
                    lineHeight = 24.sp,
                    color = Color(0xFF010102),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-2).dp)
                )
            }
        }
    }
}
