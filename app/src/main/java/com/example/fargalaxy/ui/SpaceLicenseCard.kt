package com.example.fargalaxy.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * SpaceLicenseCard composable - displays the right section of the level card.
 * Shows title, current XP, progress bar, level, and XP needed for next level.
 * 
 * Design features:
 * - Semi-transparent dark background
 * - 1.dp border on top, right, and bottom edges only (no left border)
 * - Animated progress bar
 * - Responsive layout that expands horizontally
 * 
 * @param title The title text (e.g., "Space license")
 * @param xpCurrent Current experience points
 * @param xpToNext Experience points needed to reach next level
 * @param level Current level
 * @param progress Progress value between 0f and 1f (0 = 0%, 1 = 100%)
 * @param modifier Modifier for the card
 */
@Composable
fun SpaceLicenseCard(
    title: String,
    xpCurrent: Int,
    xpToNext: Int,
    level: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    // Animate progress value for smooth transitions
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500),
        label = "progress_animation"
    )
    
    // Gradient background: white with 16% overlay at top, fading to 0% at bottom
    // Top: FFFFFF at 16% opacity (0x29 = 41 ≈ 255 * 0.16)
    // Bottom: FFFFFF at 0% opacity
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0x29FFFFFF), // White at 16% opacity (top)
            Color(0x00FFFFFF)  // White at 0% opacity (bottom)
        )
    )
    
    // Border color - white
    val borderColor = Color(0xFFFFFFFF)
    
    // Progress bar background color - white with 20% opacity (#FFFFFF20)
    val progressBarBackground = Color(0x33FFFFFF) // #FFFFFF20 in hex
    // Progress bar foreground color - white (#FFFFFF)
    val progressBarForeground = Color(0xFFFFFFFF)
    
    // Text color - white
    val textColor = Color(0xFFFFFFFF)
    
    // Border width
    val borderWidth = 1.dp
    
    // Card with gradient background and custom border (top, right, bottom only)
    // Outer Box: draws background first, then border on top
    // Removed fillMaxHeight() - let the Box size to its content height
    Box(
        modifier = modifier
            .fillMaxWidth()
            // Removed .fillMaxHeight() - Box will size to content height
            .background(
                brush = gradientBrush,
                shape = RoundedCornerShape(0.dp) // No rounded corners for now
            )
            .drawWithContent {
                // Draw the content first (background + children)
                drawContent()
                
                // Then draw border on top, right, and bottom edges only (no left border)
                val strokeWidth = borderWidth.toPx()
                
                // Top border - drawn at the top edge
                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = strokeWidth
                )
                
                // Right border - drawn at the right edge
                drawLine(
                    color = borderColor,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )
                
                // Bottom border - drawn at the bottom edge
                drawLine(
                    color = borderColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )
            }
    ) {
        // Internal content column with exact 16.dp padding on all sides
        // Column is directly inside the Box with padding applied
        // Padding creates exactly 16.dp space from top, bottom, left, and right edges
        // Column sizes to its content, and padding ensures proper spacing from edges
        Column(
            modifier = Modifier
                .align(Alignment.TopStart) // Align to top-left of Box
                .fillMaxWidth()
                .padding(16.dp) // Exact 16.dp padding on all sides: top=16.dp, bottom=16.dp, left=16.dp, right=16.dp
            ,
            verticalArrangement = Arrangement.spacedBy(8.dp) // 8.dp spacing between items only (not extra space)
        ) {
            // Top title text - "Space license"
            Text(
                text = title,
                fontFamily = Exo2,
                fontWeight = FontWeight.W400, // Regular
                fontSize = 14.sp,
                lineHeight = 16.sp, // Explicit line height to control spacing
                color = textColor
            )
            
            // Main XP text - "320 XP" (using animated counter for smooth transitions)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AnimatedNumberCounter(
                    targetValue = xpCurrent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = "XP",
                    fontFamily = Exo2,
                    fontWeight = FontWeight.Bold, // Bold
                    fontSize = 20.sp,
                    lineHeight = 24.sp, // Explicit line height to control spacing
                    color = textColor
                )
            }
            
            // Progress bar
            ProgressBar(
                progress = animatedProgress,
                backgroundColor = progressBarBackground,
                foregroundColor = progressBarForeground,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            
            // Bottom row with level and XP to next level
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left label - "LEVEL 1"
                Text(
                    text = "LEVEL $level",
                    fontFamily = Exo2,
                    fontWeight = FontWeight.Bold, // Bold
                    fontSize = 16.sp,
                    lineHeight = 20.sp, // Explicit line height to control spacing
                    color = textColor
                )
                
                // Right label - "680 XP to LVL 2" (using animated counter for XP value)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AnimatedNumberCounter(
                        targetValue = xpToNext,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        color = textColor
                    )
                    Text(
                        text = "XP to LVL ${level + 1}",
                        fontFamily = Exo2,
                        fontWeight = FontWeight.W400, // Regular
                        fontSize = 14.sp,
                        lineHeight = 18.sp, // Explicit line height to control spacing
                        color = textColor
                    )
                }
            }
        }
    }
}

/**
 * ProgressBar composable - displays an animated progress bar with rounded corners.
 * 
 * @param progress Progress value between 0f and 1f
 * @param backgroundColor Background track color
 * @param foregroundColor Foreground fill color
 * @param modifier Modifier for the progress bar
 */
@Composable
fun ProgressBar(
    progress: Float,
    backgroundColor: Color,
    foregroundColor: Color,
    modifier: Modifier = Modifier
) {
    // Clamp progress between 0 and 1
    val clampedProgress = progress.coerceIn(0f, 1f)
    
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(4.dp) // Rounded corners
            )
            .border(
                width = 1.dp,
                color = Color.White, // White stroke for the empty portion
                shape = RoundedCornerShape(4.dp) // Match the background shape
            )
    ) {
        // Animated progress fill - uses Box with fraction of width
        Box(
            modifier = Modifier
                .fillMaxWidth(clampedProgress)
                .fillMaxHeight()
                .background(
                    color = foregroundColor,
                    shape = RoundedCornerShape(20.dp) // 20dp corner radius for the fill
                )
        )
    }
}

