package com.example.fargalaxy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fargalaxy.data.GameStateRepository

/**
 * DeveloperMenu composable - a simple developer options menu.
 * 
 * Features:
 * - Toggle test mode (only available in debug builds)
 * - Reset progress button
 * 
 * This menu should be hidden from regular users and only accessible
 * through a developer gesture (e.g., tapping version number 7 times).
 * 
 * @param onClose Callback when the menu should be closed
 * @param modifier Modifier for the menu
 */
@Composable
fun DeveloperMenu(
    onClose: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isTestMode = GameStateRepository.isTestMode
    var showResetConfirm by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable(onClick = onClose), // Click outside to close
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .background(
                    color = Color(0xFF2B2E32),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp)
                .clickable(enabled = false) { }, // Prevent clicks from closing
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            Text(
                text = "Developer Options",
                fontFamily = Exo2,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            // Test Mode Toggle
            val isDebugBuild = try {
                com.example.fargalaxy.BuildConfig.DEBUG
            } catch (e: Exception) {
                false // Assume release if BuildConfig not available
            }
            
            if (isDebugBuild) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (isTestMode) Color(0xFF4CAF50) else Color(0xFF6B6C6F),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            GameStateRepository.isTestMode = !isTestMode
                        }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isTestMode) "Test Mode: ON" else "Test Mode: OFF",
                        fontFamily = Exo2,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Test mode not available in release builds
                Text(
                    text = "Test Mode: Not available in release builds",
                    fontFamily = Exo2,
                    fontSize = 14.sp,
                    color = Color(0xFF999999),
                    textAlign = TextAlign.Center
                )
            }
            
            // Reset Progress Button
            if (showResetConfirm) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Reset all progress?",
                        fontFamily = Exo2,
                        fontSize = 16.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFD32F2F),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                GameStateRepository.resetProgress()
                                showResetConfirm = false
                            }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Confirm Reset",
                            fontFamily = Exo2,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFF6B6C6F),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                showResetConfirm = false
                            }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel",
                            fontFamily = Exo2,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFFF9800),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            showResetConfirm = true
                        }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Reset Progress",
                        fontFamily = Exo2,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Close button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF6B6C6F),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable(onClick = onClose)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Close",
                    fontFamily = Exo2,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
