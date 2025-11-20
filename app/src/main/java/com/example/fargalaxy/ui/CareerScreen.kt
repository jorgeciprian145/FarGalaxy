package com.example.fargalaxy.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
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
                Spacer(modifier = Modifier.height(0git .dp))
                
                // Total Time Traveling Counter - NEW COMPONENT
                TotalTimeTravelingCounter(
                    totalMinutes = totalTravelMinutes,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // 20.dp spacing between time counter and level card
                Spacer(modifier = Modifier.height(20.dp))
                
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
 * TotalTimeTravelingCounter composable - displays the total time traveling with decorative side elements.
 * 
 * Layout:
 * - Row with decorative elements on left, center content, and decorative elements on right
 * - Left decorative: 3 horizontal lines + vertical line on right (touching)
 * - Right decorative: vertical line on left + 3 horizontal lines (touching)
 * - Center: Large number (56sp bold) + "m" (40sp bold) with 0px spacing, and label below (14sp regular)
 * 
 * Spacing:
 * - 16dp padding from screen edges
 * - 16dp internal padding between decorative elements and center content
 * 
 * @param totalMinutes The total number of minutes traveled
 * @param modifier Modifier for the component
 */
@Composable
private fun TotalTimeTravelingCounter(
    totalMinutes: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // 16dp padding from screen edges
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left decorative element: 3 horizontal lines + vertical line on right
        DecorativeSideElement(
            isLeft = true,
            modifier = Modifier
                .weight(1f, fill = false)
                .padding(end = 16.dp) // 16dp internal padding from center content
        )
        
        // Center content: Number + "m" + label
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp) // 4px spacing between number/m and label
        ) {
            // Number and "m" in a Row with 0px spacing (marked for manual adjustment)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp), // TODO: Adjust spacing manually if needed
                verticalAlignment = Alignment.Bottom
            ) {
                // Number: 56sp, bold
                Text(
                    text = totalMinutes.toString(),
                    fontFamily = Exo2,
                    fontWeight = FontWeight.Bold,
                    fontSize = 56.sp,
                    color = Color(0xFFFFFFFF)
                )
                // "m": 40sp, bold
                Text(
                    text = "m",
                    fontFamily = Exo2,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    color = Color(0xFFFFFFFF)
                )
            }
            
            // "Total time traveling" label: 14sp, regular
            Text(
                text = "Total time traveling",
                fontFamily = Exo2,
                fontSize = 14.sp,
                color = Color(0xFFFFFFFF)
            )
        }
        
        // Right decorative element: vertical line on left + 3 horizontal lines
        DecorativeSideElement(
            isLeft = false,
            modifier = Modifier
                .weight(1f, fill = false)
                .padding(start = 16.dp) // 16dp internal padding from center content
        )
    }
}

/**
 * DecorativeSideElement composable - displays decorative lines on the sides of the time counter.
 * 
 * Structure:
 * - Left side: 3 horizontal lines (responsive width) + vertical line on right (48dp height, touching)
 * - Right side: vertical line on left (48dp height) + 3 horizontal lines (responsive width, touching)
 * 
 * Lines:
 * - Horizontal lines: 1px stroke width, 4dp vertical spacing between them
 * - Vertical line: 1px width, 48dp height
 * - Lines touch each other (0px distance)
 * 
 * @param isLeft Whether this is the left side element (true) or right side element (false)
 * @param modifier Modifier for the element
 */
@Composable
private fun DecorativeSideElement(
    isLeft: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp) // Height matches vertical line height
        ) {
            val strokeWidth = 1.dp.toPx()
            val lineSpacing = 8.dp.toPx()
            val verticalLineHeight = 64.dp.toPx()
            
            // Calculate positions for 3 horizontal lines
            // They should be centered vertically in the 48dp container
            val totalHorizontalLinesHeight = 2 * lineSpacing // Space between 3 lines
            val startY = (size.height - totalHorizontalLinesHeight) / 2
            
            val line1Y = startY
            val line2Y = startY + lineSpacing
            val line3Y = startY + 2 * lineSpacing
            
            if (isLeft) {
                // Left side: 3 horizontal lines + vertical line on right
                // Horizontal lines expand to fill available width (responsive)
                val horizontalLineEndX = size.width - strokeWidth // Leave space for vertical line
                
                // Draw 3 horizontal lines
                drawLine(
                    color = Color(0xFFFFFFFF),
                    start = Offset(0f, line1Y),
                    end = Offset(horizontalLineEndX, line1Y),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = Color(0xFFFFFFFF),
                    start = Offset(0f, line2Y),
                    end = Offset(horizontalLineEndX, line2Y),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = Color(0xFFFFFFFF),
                    start = Offset(0f, line3Y),
                    end = Offset(horizontalLineEndX, line3Y),
                    strokeWidth = strokeWidth
                )
                
                // Vertical line on right, touching the horizontal lines (0px distance)
                val verticalLineX = size.width - strokeWidth / 2
                val verticalLineTopY = (size.height - verticalLineHeight) / 2
                drawLine(
                    color = Color(0xFFFFFFFF),
                    start = Offset(verticalLineX, verticalLineTopY),
                    end = Offset(verticalLineX, verticalLineTopY + verticalLineHeight),
                    strokeWidth = strokeWidth
                )
            } else {
                // Right side: vertical line on left + 3 horizontal lines
                // Vertical line on left, touching the horizontal lines (0px distance)
                val verticalLineX = strokeWidth / 2
                val verticalLineTopY = (size.height - verticalLineHeight) / 2
                drawLine(
                    color = Color(0xFFFFFFFF),
                    start = Offset(verticalLineX, verticalLineTopY),
                    end = Offset(verticalLineX, verticalLineTopY + verticalLineHeight),
                    strokeWidth = strokeWidth
                )
                
                // Horizontal lines expand to fill available width (responsive)
                val horizontalLineStartX = strokeWidth // Start after vertical line
                
                // Draw 3 horizontal lines
                drawLine(
                    color = Color(0xFFFFFFFF),
                    start = Offset(horizontalLineStartX, line1Y),
                    end = Offset(size.width, line1Y),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = Color(0xFFFFFFFF),
                    start = Offset(horizontalLineStartX, line2Y),
                    end = Offset(size.width, line2Y),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = Color(0xFFFFFFFF),
                    start = Offset(horizontalLineStartX, line3Y),
                    end = Offset(size.width, line3Y),
                    strokeWidth = strokeWidth
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

