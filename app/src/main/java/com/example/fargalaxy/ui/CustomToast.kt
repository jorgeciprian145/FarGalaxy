package com.example.fargalaxy.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * CustomToast composable - displays a custom toast message with modal styling.
 * 
 * Styling matches modals:
 * - Background: Vertical gradient from #373A3E to #2B2E32
 * - Border: 1dp, color #6B6C6F
 * - Corner radius: 32dp
 * - Font: Exo2, regular weight
 * - Text only, no icons
 * 
 * @param message The toast message to display
 * @param durationMillis How long to show the toast (default: 2000ms)
 * @param onDismiss Callback when toast is dismissed
 * @param modifier Modifier for the toast container
 */
@Composable
fun CustomToast(
    message: String,
    durationMillis: Long = 2000,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    // Animate slide-in from bottom
    val offsetY by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 100.dp, // Slide up from 100dp below
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "toast_slide"
    )
    
    // Show toast and auto-dismiss
    LaunchedEffect(Unit) {
        isVisible = true
        delay(durationMillis)
        isVisible = false
        delay(300) // Wait for slide-out animation
        onDismiss()
    }
    
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(bottom = 100.dp) // Position from bottom
                .offset(y = offsetY) // Animate slide-in/out
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF373A3E), // Top color (same as modal)
                            Color(0xFF2B2E32)  // Bottom color (same as modal)
                        )
                    ),
                    shape = RoundedCornerShape(32.dp) // 32dp corner radius (same as modal)
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFF6B6C6F), // Stroke color (same as modal)
                    shape = RoundedCornerShape(32.dp) // 32dp corner radius
                )
                .padding(horizontal = 24.dp, vertical = 16.dp), // Padding for text
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                fontFamily = Exo2,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal, // Regular weight
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}
