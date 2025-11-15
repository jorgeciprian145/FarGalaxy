package com.example.fargalaxy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * CareerScreen composable - displays the career/progress screen content.
 * Note: Background, noise, and indicator are handled by MainScreen (static layers).
 * Only the content moves when swiping.
 * 
 * @param modifier Modifier for the screen
 */
@Composable
fun CareerScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        // Top gradient overlay: Covers 20% of screen height, creating a fade effect at the top.
        // Gradient transitions from solid black at the top to transparent at the bottom.
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.20f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF000000),
                            Color(0x00000000)
                        )
                    )
                )
        )

        // Bottom gradient overlay: Covers 25% of screen height, creating a fade effect at the bottom.
        // Gradient transitions from transparent at the top to solid black at the bottom.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.25f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x00000000),
                            Color(0xFF000000)
                        )
                    )
                )
        )

        // Title area: "Your career" text positioned 16dp above the indicator
        // Indicator is at 48.dp from top (with status bar padding), so title should be at 32.dp (48.dp - 16.dp)
        // Rendered after gradients so it appears on top
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your career",
                fontFamily = Exo2,
                fontSize = 18.sp,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center
            )
        }
        
        // Main content column: Contains all elements with consistent 20.dp spacing
        // Uses Column layout for automatic spacing instead of fixed positions
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 123.dp) // Position 24.dp below indicator (48.dp + 51.dp + 24.dp = 123.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp) // Manual spacing control for 20.dp between elements
        ) {
            // LevelStatusCard: combines the badge and SpaceLicenseCard into a single component
            // Card has 8px left margin and 16px right margin, full width otherwise
            LevelStatusCard(
                title = "Space license",
                xpCurrent = 320,
                xpToNext = 680,
                level = 1,
                progress = 320f / (320f + 680f), // 320 / 1000 = 0.32
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 16.dp)
            )
            
            // 20.dp spacing between level card and first horizontal line
            Spacer(modifier = Modifier.height(20.dp))
            
            // Horizontal line 1: 1dp height white line with 16dp side padding
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            
            // 20.dp spacing between first horizontal line and "Discoveries" label
            Spacer(modifier = Modifier.height(20.dp))
            
            // "Discoveries" label: 18sp, bold, Exo2 font with 24dp horizontal padding
            Text(
                text = "Discoveries",
                fontFamily = Exo2,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFFFFFFFF),
                modifier = Modifier.padding(horizontal = 24.dp) // 24dp padding to match ProgressSection Row padding
            )
            
            // 20.dp spacing between "Discoveries" label and progress items row
            Spacer(modifier = Modifier.height(20.dp))
            
            // ProgressSection: Contains the progress items row (no title shown, handled separately above)
            ProgressSection(
                starshipsCount = "1/10",
                locationsCount = "1/30",
                collectiblesCount = "1/30",
                showTitle = false, // Don't show title, it's handled separately above
                modifier = Modifier.fillMaxWidth()
            )
            
            // 20.dp spacing between progress items row and second horizontal line
            Spacer(modifier = Modifier.height(20.dp))
            
            // Horizontal line 2: 1dp height white line with 16dp side padding
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            
            // 20.dp spacing between second horizontal line and "Achievements" label
            Spacer(modifier = Modifier.height(20.dp))
            
            // "Achievements" label: 18sp, bold, Exo2 font with 24dp horizontal padding
            Text(
                text = "Achievements",
                fontFamily = Exo2,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFFFFFFFF),
                modifier = Modifier.padding(horizontal = 24.dp) // 24dp padding to match other labels
            )
        }
    }
}

/**
 * HorizontalDivider composable - displays a horizontal white line with side padding.
 * 
 * Creates a 1dp height white line with 16dp horizontal padding on both sides.
 * Used to separate sections in the CareerScreen.
 * 
 * @param modifier Modifier for the divider
 */
@Composable
private fun HorizontalDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(1.dp)
            .padding(horizontal = 16.dp) // 16dp side padding
            .fillMaxWidth()
            .background(Color(0xFFFFFFFF)) // White color (#FFFFFF)
    )
}

