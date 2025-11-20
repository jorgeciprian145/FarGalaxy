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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fargalaxy.R
import com.example.fargalaxy.model.Ship

/**
 * CareerScreen composable - displays the career/progress screen content.
 * Note: Background, noise, and indicator are handled by MainScreen (static layers).
 * Only the content moves when swiping.
 * 
 * @param currentShip The currently selected ship
 * @param onViewShipClick Callback when the "view" button next to ship name is clicked
 * @param totalTravelMinutes The total number of minutes the user has been in travel
 * @param modifier Modifier for the screen
 */
@Composable
fun CareerScreen(
    currentShip: Ship,
    onViewShipClick: () -> Unit = {},
    totalTravelMinutes: Int = 45, // TODO: Connect to actual data source
    modifier: Modifier = Modifier
) {
    // Scroll state to track when content is being clipped
    val scrollState = rememberScrollState()
    
    // Get density to convert dp to pixels
    val density = LocalDensity.current
    
    // Calculate if content is being clipped (scroll position >= 8dp means content moved up past initial spacer)
    // When scrolled 8dp or more, content reaches the clip boundary at 115dp
    val isContentClipped = derivedStateOf {
        with(density) {
            scrollState.value >= 8.dp.toPx().toInt()
        }
    }
    
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
        
        // Clip boundary container: Box positioned at 115dp to define clipping boundary
        // Indicator bottom is at: statusBarPadding + 48dp + 51dp = statusBarPadding + 99dp
        // Clip line is at: statusBarPadding + 99dp + 16dp = statusBarPadding + 115dp
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 115.dp) // Clip boundary position
                .fillMaxWidth()
                .fillMaxHeight()
                .clipToBounds() // Clip content that goes above this boundary
        ) {
            // White divider line: Only visible when content is being clipped
            // Positioned behind the Column so it doesn't block touch events
            if (isContentClipped.value) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFFFFFFF)) // White line, full width, 1px
                )
            }
            
            // Scrollable content column: Content can scroll up and get clipped at the boundary
            // Initially, content starts 8dp below clip line (via spacer)
            // Column fills available height to enable proper scrolling
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .verticalScroll(scrollState)
                    .navigationBarsPadding() // Account for navigation bar height
                    .padding(bottom = 32.dp), // Allow last row to be 32dp above bottom bar
                verticalArrangement = Arrangement.spacedBy(0.dp) // Manual spacing control for 20.dp between elements
            ) {
                // Initial spacer: Push content down 8dp from clip line (so it starts at 123dp visually)
                Spacer(modifier = Modifier.height(0.dp))
                
                // Total Time Traveling Counter - NEW COMPONENT
                TotalTimeTravelingCounter(
                    totalMinutes = totalTravelMinutes,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // 20.dp spacing between time counter and level card
                Spacer(modifier = Modifier.height(24.dp))
                
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
                Spacer(modifier = Modifier.height(28.dp))
                
                // Horizontal divider: Separates level card from current ship section
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                
                // Current ship row: Contains ship name labels on left and "VIEW" button on right
                // Row has 20dp vertical padding, so no additional spacer needed
                CurrentShipRow(
                    shipName = currentShip.name,
                    onViewClick = onViewShipClick,
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
                Spacer(modifier = Modifier.height(28.dp))
                
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
                Spacer(modifier = Modifier.height(28.dp))
                
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
                
                // 20.dp spacing between VIEW ALL button and horizontal divider
                Spacer(modifier = Modifier.height(28.dp))
                
                // Horizontal divider: Separates Achievements section from Travel log
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                
                // 20.dp spacing between horizontal divider and "Travel log" label
                Spacer(modifier = Modifier.height(20.dp))
                
                // "Travel log" label: 18sp, bold, Exo2 font with 24dp horizontal padding
                Text(
                    text = "Travel log",
                    fontFamily = Exo2,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFFFFFFFF),
                    modifier = Modifier.padding(horizontal = 16.dp) // 24dp padding to match Discoveries/Achievements
                )
                
                // 16.dp spacing between "Travel log" label and travel log rows
                Spacer(modifier = Modifier.height(20.dp))
                
                // Travel log rows container
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp) // 8dp spacing between rows
                ) {
                    TravelLogRow(
                        label = "Total time traveling",
                        value = "75 mins"
                    )
                    TravelLogRow(
                        label = "Total successful travels",
                        value = "4"
                    )
                    TravelLogRow(
                        label = "Total attempted travels",
                        value = "8"
                    )
                    TravelLogRow(
                        label = "Longest travel time",
                        value = "45 mins"
                    )
                    TravelLogRow(
                        label = "Top speed reached",
                        value = "48965523689"
                    )
                    TravelLogRow(
                        label = "Travel success ratio",
                        value = "50%"
                    )
                }
            }
        }
    }
}

/**
 * TotalTimeTravelingCounter composable - displays the total time traveling with decorative SVG side elements.
 * 
 * Layout:
 * - Row with decorative SVG elements on left, center content, and decorative SVG elements on right
 * - Left decorative: SVG image (sidedecoration)
 * - Right decorative: SVG image (sidedecoration, mirrored)
 * - Center: Large number (56sp bold) + "m" (40sp bold) with spacing, and label below (14sp regular)
 * - Center content auto-scales down if it exceeds available space
 * 
 * Spacing:
 * - 16dp padding from screen edges (for SVGs)
 * - 8dp internal padding between decorative elements and center content
 * 
 * @param totalMinutes The total number of minutes traveled
 * @param modifier Modifier for the component
 */
@Composable
private fun TotalTimeTravelingCounter(
    totalMinutes: Int,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    // Track SVG width to calculate available space for center content
    var leftSvgWidth by remember { mutableStateOf(0.dp) }
    var rightSvgWidth by remember { mutableStateOf(0.dp) }
    
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val totalWidth = maxWidth
        val sidePadding = 16.dp
        val internalPadding = 12.dp
        
        // Calculate available width based on SVG widths (recalculates when SVG widths change)
        val availableWidth = derivedStateOf {
            (totalWidth - sidePadding.times(2) - leftSvgWidth - rightSvgWidth - internalPadding.times(2))
                .coerceAtLeast(0.dp)
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = sidePadding), // 16dp padding from screen edges
            horizontalArrangement = Arrangement.Center, // Center the entire group
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left decorative element: SVG image
            Box(
                modifier = Modifier
                    .onSizeChanged { size ->
                        with(density) {
                            leftSvgWidth = size.width.toDp()
                        }
                    }
                    .padding(end = internalPadding) // 8dp internal padding to center content
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sidedecoration),
                    contentDescription = null,
                    modifier = Modifier.height(88.dp), // Fixed height of 88px
                    contentScale = ContentScale.Fit // Maintain original aspect ratio
                )
            }
            
            // Center content: Number + "m" + label (responsive, auto-scales)
            // This should be centered as a unit with the SVGs hugging it
            Box(
                modifier = Modifier
                    .widthIn(max = availableWidth.value)
            ) {
                AutoSizingTimeDisplay(
                    totalMinutes = totalMinutes,
                    maxWidth = availableWidth.value,
                    density = density
                )
            }
            
            // Right decorative element: SVG image (mirrored)
            Box(
                modifier = Modifier
                    .onSizeChanged { size ->
                        with(density) {
                            rightSvgWidth = size.width.toDp()
                        }
                    }
                    .padding(start = internalPadding) // 8dp internal padding to center content
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sidedecoration),
                    contentDescription = null,
                    modifier = Modifier
                        .height(88.dp) // Fixed height of 88px
                        .scale(scaleX = -1f, scaleY = 1f), // Mirror horizontally
                    contentScale = ContentScale.Fit // Maintain original aspect ratio
                )
            }
        }
    }
}

/**
 * AutoSizingTimeDisplay composable - displays the time counter with auto-scaling text.
 * 
 * The number and "m" will scale down if they exceed the available width, maintaining
 * the relative sizes (56sp for number, 40sp for "m").
 * 
 * @param totalMinutes The total number of minutes
 * @param maxWidth Maximum available width for the content
 * @param density Density for unit conversion
 */
@Composable
private fun AutoSizingTimeDisplay(
    totalMinutes: Int,
    maxWidth: androidx.compose.ui.unit.Dp,
    density: Density
) {
    val numberText = totalMinutes.toString()
    val mText = "m"
    val spacingDp = 6.dp
    
    // Base font sizes
    val baseNumberSize = 56.sp
    val baseMSize = 40.sp
    
    val textMeasurer = rememberTextMeasurer()
    
    // Calculate scale factor based on available width
    val scaleFactor = remember(maxWidth, numberText) {
        if (maxWidth <= 0.dp) {
            1f
        } else {
            with(density) {
                val maxWidthPx = maxWidth.toPx()
                val spacingPx = spacingDp.toPx()
                
                // Measure text at base size
                val numberWidth = textMeasurer.measure(
                    text = numberText,
                    style = TextStyle(
                        fontFamily = Exo2,
                        fontWeight = FontWeight.Bold,
                        fontSize = baseNumberSize
                    )
                ).size.width
                
                val mWidth = textMeasurer.measure(
                    text = mText,
                    style = TextStyle(
                        fontFamily = Exo2,
                        fontWeight = FontWeight.Bold,
                        fontSize = baseMSize
                    )
                ).size.width
                
                val totalWidth = numberWidth + mWidth + spacingPx
                
                if (totalWidth <= maxWidthPx) {
                    1f
                } else {
                    // Calculate scale to fit
                    val scale = (maxWidthPx / totalWidth).coerceAtLeast(0.3f) // Minimum 30% scale
                    scale
                }
            }
        }
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp) // 4px spacing between number/m and label
    ) {
        // Number and "m" in a Row with spacing
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacingDp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Number: scaled font size, bold
            Text(
                text = numberText,
                fontFamily = Exo2,
                fontWeight = FontWeight.Bold,
                fontSize = baseNumberSize * scaleFactor,
                color = Color(0xFFFFFFFF)
            )
            // "m": scaled font size, bold
            Text(
                text = mText,
                fontFamily = Exo2,
                fontWeight = FontWeight.Bold,
                fontSize = baseMSize * scaleFactor,
                color = Color(0xFFFFFFFF)
            )
        }
        
        // "Total time traveling" label: 14sp, regular (doesn't scale)
        Text(
            text = "Total time traveling",
            fontFamily = Exo2,
            fontSize = 14.sp,
            color = Color(0xFFFFFFFF)
        )
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

/**
 * TravelLogRow composable - displays a single row in the travel log section.
 * 
 * Each row has:
 * - Fixed height of 44dp
 * - Full width with 16dp side padding from screen edges
 * - Internal padding of 16dp on sides (total 32dp from screen edge to content)
 * - Two labels: left (14sp regular) and right (20sp bold)
 * - Labels are bottom-aligned with each other
 * - Row is vertically centered (labels are centered in the 44dp container)
 * 
 * @param label The left label text (e.g., "Total time traveling")
 * @param value The right value text (e.g., "75 mins")
 * @param modifier Modifier for the row
 */
@Composable
private fun TravelLogRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp) // 16dp side padding from screen edges
            .border(
                width = 1.dp,
                color = Color(0x52FFFFFF) // White border, 1px
            ),
        contentAlignment = Alignment.Center // Center the content vertically
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), // 16dp internal padding
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom // Bottom-align labels with each other
        ) {
            // Left label: 14sp, regular weight
            Text(
                text = label,
                fontFamily = Exo2,
                fontSize = 14.sp,
                color = Color(0xFFFFFFFF)
            )
            
            // Right value: 20sp, bold
            Text(
                text = value,
                fontFamily = Exo2,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFFFFFFFF)
            )
        }
    }
}

