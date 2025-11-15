package com.example.fargalaxy.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fargalaxy.R

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
            
            // 20.dp spacing between level card and horizontal divider
            Spacer(modifier = Modifier.height(20.dp))
            
            // Horizontal divider: Separates level card from current ship section
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            
            // Current ship row: Contains ship name labels on left and "VIEW" button on right
            // Row has 20dp vertical padding, so no additional spacer needed
            CurrentShipRow(
                shipName = "B14 Phantom", // TODO: Connect to actual ship state
                onViewClick = { /* TODO: Navigate to ship details screen */ },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Horizontal line 1: 1dp height white line with 16dp side padding
            // Row has 20dp vertical padding, so no additional spacer needed
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
            
            // "Achievements" section header: Row with "Achievements" label on left and count on right
            // Both labels are bottom-aligned, with 16dp padding on respective sides
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // "Achievements" label: 18sp, bold, Exo2 font
                Text(
                    text = "Achievements",
                    fontFamily = Exo2,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFFFFFFFF)
                )
                
                // Dynamic count label: 20sp, bold, Exo2 font (e.g., "1/24")
                Text(
                    text = "1/24", // TODO: Make this dynamic based on unlocked achievements
                    fontFamily = Exo2,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFFFFFFFF)
                )
            }
            
            // 20.dp spacing between labels and achievements grid
            Spacer(modifier = Modifier.height(20.dp))
            
            // Achievements grid container: 4 columns with 16dp side padding
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Column 1
                AchievementColumn(
                    achievementName = "Locked",
                    isLocked = true,
                    modifier = Modifier.weight(1f)
                )
                
                // Column 2
                AchievementColumn(
                    achievementName = "Locked",
                    isLocked = true,
                    modifier = Modifier.weight(1f)
                )
                
                // Column 3
                AchievementColumn(
                    achievementName = "Locked",
                    isLocked = true,
                    modifier = Modifier.weight(1f)
                )
                
                // Column 4
                AchievementColumn(
                    achievementName = "Locked",
                    isLocked = true,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // 20.dp spacing between achievements grid and VIEW ALL button
            Spacer(modifier = Modifier.height(20.dp))
            
            // VIEW ALL button: Fixed size, same style as CANCEL/STOP TRAVEL buttons
            // Centered horizontally in the available width
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ViewAllButton(
                    onClick = { /* TODO: Navigate to achievements screen */ }
                )
            }
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
            .background(Color(0x66FFFFFF)) // White color with 40% opacity (0x66 = ~40% alpha)
    )
}

/**
 * AchievementColumn composable - displays a single achievement column with icon and label.
 * 
 * When locked: Shows achievementlocked SVG and "Locked" label
 * When unlocked: Shows achievement-specific SVG and achievement name
 * 
 * @param achievementName The name of the achievement (or "Locked" when not unlocked)
 * @param isLocked Whether the achievement is currently locked
 * @param modifier Modifier for the column
 */
@Composable
private fun AchievementColumn(
    achievementName: String = "Locked",
    isLocked: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .widthIn(max = 96.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Achievement icon: 48x48dp fixed size
        Image(
            painter = painterResource(
                id = if (isLocked) {
                    R.drawable.achievementlocked
                } else {
                    // TODO: Add achievement-specific drawables when implementing unlock logic
                    R.drawable.achievementlocked
                }
            ),
            contentDescription = achievementName,
            modifier = Modifier.size(48.dp),
            contentScale = ContentScale.Fit
        )
        
        // Achievement label: 4dp spacing below icon, 14sp font size, regular weight
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = achievementName,
            fontFamily = Exo2,
            fontSize = 14.sp,
            color = Color(0xFFFFFFFF),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * CurrentShipRow composable - displays the current ship information with a view button.
 * 
 * Contains:
 * - Left side: Two labels stacked vertically
 *   - Top: Dynamic ship name (16sp, bold)
 *   - Bottom: Static "Current ship" label (14sp, regular)
 *   - 4dp vertical spacing between labels
 * - Right side: "view" button (88dp width, same style as ViewAllButton)
 * 
 * The row has 16dp horizontal padding and 20dp vertical padding.
 * 
 * @param shipName The name of the currently selected ship
 * @param onViewClick Callback when the view button is clicked
 * @param modifier Modifier for the row
 */
@Composable
private fun CurrentShipRow(
    shipName: String,
    onViewClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Column with ship name and "Current ship" label
        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Dynamic ship name: 16sp, bold
            Text(
                text = shipName,
                fontFamily = Exo2,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFFFFFFFF)
            )
            
            // Static "Current ship" label: 14sp, regular
            Text(
                text = "Current ship",
                fontFamily = Exo2,
                fontSize = 14.sp,
                color = Color(0xFFFFFFFF)
            )
        }
        
        // Right side: "view" button
        ViewButton(
            onClick = onViewClick
        )
    }
}

/**
 * ViewButton composable - displays the "view" button with secondary style.
 * 
 * Uses the same visual format as ViewAllButton but with a fixed width of 88dp:
 * - Transparent background with white border
 * - White text
 * - Rounded corners (80dp radius)
 * - Fixed size: 88dp width, 32dp height
 * - 16sp font size, regular weight
 * 
 * @param onClick Callback when button is clicked
 * @param modifier Modifier for the button
 */
@Composable
private fun ViewButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(88.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(80.dp))
            .border(
                width = 1.dp,
                color = Color(0xFFFFFFFF), // White border
                shape = RoundedCornerShape(80.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "VIEW",
            fontFamily = Exo2,
            fontSize = 16.sp,
            color = Color(0xFFFFFFFF), // White text
            textAlign = TextAlign.Center
        )
    }
}

/**
 * ViewAllButton composable - displays the "VIEW ALL" button with secondary style.
 * 
 * Uses the same visual format as CANCEL/STOP TRAVEL buttons:
 * - Transparent background with white border
 * - White text
 * - Rounded corners (80dp radius)
 * - Fixed size: 144dp width, 32dp height
 * - 16sp font size, regular weight
 * 
 * @param onClick Callback when button is clicked
 * @param modifier Modifier for the button
 */
@Composable
private fun ViewAllButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(144.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(80.dp))
            .border(
                width = 1.dp,
                color = Color(0xFFFFFFFF), // White border
                shape = RoundedCornerShape(80.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "VIEW ALL",
            fontFamily = Exo2,
            fontSize = 16.sp,
            color = Color(0xFFFFFFFF), // White text
            textAlign = TextAlign.Center
        )
    }
}

