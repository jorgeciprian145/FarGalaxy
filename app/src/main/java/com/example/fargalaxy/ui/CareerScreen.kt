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
import androidx.compose.ui.graphics.graphicsLayer
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
import com.example.fargalaxy.data.LocationRepository
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.alpha
import kotlinx.coroutines.delay
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.fargalaxy.R
import com.example.fargalaxy.data.ShipRepository
import com.example.fargalaxy.model.Ship

/**
 * CareerScreen composable - displays the career/progress screen content.
 * Note: Background, noise, and indicator are handled by MainScreen (static layers).
 * Only the content moves when swiping.
 * 
 * @param currentShip The currently selected ship
 * @param onViewShipClick Callback when the "view" button next to ship name is clicked
 * @param onShipSelectionClick Callback when the starships item in ProgressSection is clicked
 * @param onLocationsClick Callback when the locations item in ProgressSection is clicked
 * @param totalTravelMinutes The total number of minutes the user has been in travel
 * @param isPageActive Boolean flag indicating if this page is currently active and visible (used to reset scroll when returning)
 * @param scrollToTopTrigger State key that when changed triggers scrolling to top (used for back button handling)
 * @param modifier Modifier for the screen
 */
@Composable
fun CareerScreen(
    currentShip: Ship,
    onViewShipClick: () -> Unit = {},
    onShipSelectionClick: () -> Unit = {},
    onLocationsClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    totalTravelMinutes: Int = 45, // TODO: Connect to actual data source
    isPageActive: Boolean = true,
    scrollToTopTrigger: Int = 0,
    modifier: Modifier = Modifier
) {
    // State to trigger animation playback when screen becomes visible
    var animationKey by remember { mutableStateOf(0) }
    
    // Reset animation trigger when screen becomes visible (when composable is created)
    LaunchedEffect(Unit) {
        animationKey++
    }
    
    // Reset animation when composable is disposed (user leaves screen)
    DisposableEffect(Unit) {
        onDispose {
            // Animation will replay when composable is recreated
        }
    }
    
    // Scroll state to track when content is being clipped
    val scrollState = rememberScrollState()
    
    // Handle scroll to top trigger (used for back button)
    LaunchedEffect(scrollToTopTrigger) {
        if (scrollToTopTrigger > 0) {
            scrollState.animateScrollTo(0)
        }
    }
    
    // Track previous active state to detect when we return to this page
    var wasActive by remember { mutableStateOf(isPageActive) }
    
    // Reset scroll position when CareerScreen becomes visible again
    // This triggers when:
    // 1. Navigating back to this page from another page (GalaxyScreen, VaultScreen)
    // 2. Returning from overlays (ShipSelectionScreen, ShipDetailsScreen)
    LaunchedEffect(isPageActive) {
        if (isPageActive && !wasActive) {
            // Page just became active (returned from overlay or another page)
            scrollState.animateScrollTo(0)
        }
        wasActive = isPageActive
    }
    
    // Also reset on initial composition
    LaunchedEffect(Unit) {
        scrollState.animateScrollTo(0)
        wasActive = isPageActive
    }
    
    // Get density to convert dp to pixels
    val density = LocalDensity.current
    
    // Get ship counts for starships progress display
    val allShips = ShipRepository.getAllShips()
    val totalShipsCount = allShips.size
    // Unlocked ships: Currently all ships are shown in ShipSelectionScreen, so all ships are unlocked
    // TODO: In the future, filter to only unlocked ships when unlock system is implemented
    val unlockedShipsCount = allShips.size
    val starshipsCountText = "$unlockedShipsCount/$totalShipsCount"
    
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
        // Back button positioned on the right side, 16dp from edge, vertically aligned with indicator (same height container)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 48.dp)
                .fillMaxWidth()
                .height(51.dp)
        ) {
            // Title: Centered horizontally, positioned at top of container (matching original 32dp position relative to indicator)
            Text(
                text = "Your career",
                fontFamily = Exo2,
                fontSize = 18.sp,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-16).dp) // Offset to maintain original 32dp position (48dp - 16dp)
            )
            
            // Back button: Right side, 16dp from edge, 4dp upward offset, rotated 180 degrees
            Image(
                painter = painterResource(id = R.drawable.backdefault),
                contentDescription = "Back",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .offset(y = (-20).dp) // 20dp upward offset (4dp + 8dp + 8dp)
                    .height(51.dp)
                    .graphicsLayer { rotationZ = 180f } // Rotate 180 degrees to the right
                    .clickable(onClick = onBackClick),
                contentScale = ContentScale.Fit
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
                
                // Total Focus Time Counter - NEW COMPONENT
                TotalTimeTravelingCounter(
                    totalMinutes = totalTravelMinutes,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Spacing between Total Focus Time counter and stats section
                // TODO: Adjust height value as needed for desired spacing
                Spacer(modifier = Modifier.height(24.dp))
                
                // Stats section: 3 columns with current streak, sessions this month, and total sessions
                // TODO: Replace placeholder values with dynamic data
                // Placeholder values: currentStreak = "3 d", sessionsThisMonth = "2", totalSessions = "3"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp) // 0dp spacing between columns
                ) {
                    // Column 1: Current streak
                    // TODO: Replace placeholder with dynamic currentStreak value
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(0.dp) // 0dp spacing between value and label
                    ) {
                        // Value: Bold, 32sp
                        // TODO: This will be a dynamic value - current streak of days
                        // TODO: Adjust spacing between number and "d" by changing spacedBy value
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp), // Adjust spacing between "3" and "d"
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "3",
                                fontFamily = Exo2,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp,
                                color = Color(0xFFFFFFFF)
                            )
                            Text(
                                text = "d",
                                fontFamily = Exo2,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp,
                                color = Color(0xFFFFFFFF)
                            )
                        }
                        // Label: Regular, 14sp
                        // TODO: Adjust lineHeight value as needed for desired line spacing
                        Text(
                            text = "Current\nstreak",
                            fontFamily = Exo2,
                            fontWeight = FontWeight.W400,
                            fontSize = 14.sp,
                            lineHeight = 18.sp, // Adjust line height for multi-line label
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    // Column 2: Sessions this month
                    // TODO: Replace placeholder with dynamic sessionsThisMonth value
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(0.dp) // 0dp spacing between value and label
                    ) {
                        // Value: Bold, 32sp
                        // TODO: This will be a dynamic value - number of completed travels this month
                        Text(
                            text = "2",
                            fontFamily = Exo2,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center
                        )
                        // Label: Regular, 14sp
                        // TODO: Adjust lineHeight value as needed for desired line spacing
                        Text(
                            text = "Sessions\nthis month",
                            fontFamily = Exo2,
                            fontWeight = FontWeight.W400,
                            fontSize = 14.sp,
                            lineHeight = 18.sp, // Adjust line height for multi-line label
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    // Column 3: Total sessions
                    // TODO: Replace placeholder with dynamic totalSessions value
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(0.dp) // 0dp spacing between value and label
                    ) {
                        // Value: Bold, 32sp
                        // TODO: This will be a dynamic value - total number of sessions since app download
                        Text(
                            text = "3",
                            fontFamily = Exo2,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center
                        )
                        // Label: Regular, 14sp
                        // TODO: Adjust lineHeight value as needed for desired line spacing
                        Text(
                            text = "Total\nsessions",
                            fontFamily = Exo2,
                            fontWeight = FontWeight.W400,
                            fontSize = 14.sp,
                            lineHeight = 18.sp, // Adjust line height for multi-line label
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                // 32.dp spacing between stats section and level card (24dp + 8dp additional)
                Spacer(modifier = Modifier.height(32.dp))
                
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
                
                // 24.dp spacing between level card and horizontal divider
                Spacer(modifier = Modifier.height(24.dp))
                
                // Horizontal divider: Separates stats section from current ship section
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                
                // Current ship row: Contains ship name labels on left and "VIEW" button on right
                // Row has 20dp vertical padding, so no additional spacer needed
                // Use key() to force recomposition when ship changes
                // Display shortened name for Tortoise ship on CareerScreen
                androidx.compose.runtime.key(currentShip.id) {
                    val displayName = if (currentShip.id == "model3_tortoise_ccp") {
                        "Model 3 \"Tortoise\" CCP"
                    } else {
                        currentShip.name
                    }
                    CurrentShipRow(
                        shipName = displayName,
                        shipId = currentShip.id,
                        onViewClick = onViewShipClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
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
                // Calculate location counts from repository
                val discoveredLocations = LocationRepository.getDiscoveredLocations()
                val discoveredLocationsCount = discoveredLocations.size
                val totalLocationsCount = LocationRepository.getTotalLocationsCount()
                val locationsCountText = "$discoveredLocationsCount/$totalLocationsCount"
                
                ProgressSection(
                    starshipsCount = starshipsCountText,
                    locationsCount = locationsCountText,
                    collectiblesCount = "1/30",
                    showTitle = false, // Don't show title, it's handled separately above
                    onStarshipsClick = onShipSelectionClick,
                    onLocationsClick = onLocationsClick,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // 20.dp spacing between progress items row and second horizontal line
                Spacer(modifier = Modifier.height(28.dp))
                
                // Horizontal line 2: 1dp height white line with 16dp side padding
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                
                // 20.dp spacing between horizontal divider and "Travel log" label
                Spacer(modifier = Modifier.height(20.dp))
                
                // "Session log" label: 18sp, bold, Exo2 font with 16dp horizontal padding
                Text(
                    text = "Session log",
                    fontFamily = Exo2,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFFFFFFFF),
                    modifier = Modifier.padding(horizontal = 16.dp) // 16dp padding to match other sections
                )
                
                // 16.dp spacing between "Session log" label and session log rows
                Spacer(modifier = Modifier.height(20.dp))
                
                // Session log rows container
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp) // 8dp spacing between rows
                ) {
                    TravelLogRow(
                        label = "Last session",
                        value = "Today",
                        isValueBold = true
                    )
                    TravelLogRow(
                        label = "Sessions this week",
                        value = "2"
                    )
                    TravelLogRow(
                        label = "Sessions this month",
                        value = "2"
                    )
                    TravelLogRow(
                        label = "Average session time",
                        value = "15 m"
                    )
                    TravelLogRow(
                        label = "Longest session",
                        value = "20 mins",
                        isValueBold = true
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
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.dp), // No spacing, we'll use Spacers
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Outer left decorative element: sidedecoration2 - positioned at left edge with 16dp padding
            Box(
                modifier = Modifier
                    .padding(start = sidePadding) // 16dp padding from left edge
            ) {
                val outerLeftComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.sidedecoration2))
                LottieAnimation(
                    composition = outerLeftComposition,
                    iterations = 1, // Play once
                    modifier = Modifier.width(24.dp), // Fixed width of 24dp
                    contentScale = ContentScale.Fit // Maintain original aspect ratio
                )
            }
            
            // 8dp spacing between outer left sidedecoration2 and inner left sidedecoration
            Spacer(modifier = Modifier.width(8.dp))
            
            // Left decorative element: Lottie animation
            Box(
                modifier = Modifier
                    .onSizeChanged { size ->
                        with(density) {
                            leftSvgWidth = size.width.toDp()
                        }
                    }
            ) {
                val leftComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.sidedecoration))
                LottieAnimation(
                    composition = leftComposition,
                    iterations = 1, // Play once
                    modifier = Modifier.height(88.dp), // Fixed height of 88px
                    contentScale = ContentScale.Fit // Maintain original aspect ratio
                )
            }
            
            // Spacer: Stretches to fill available space between inner left decoration and center content
            Spacer(modifier = Modifier.weight(1f))
            
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
            
            // Spacer: Stretches to fill available space between center content and inner right decoration
            Spacer(modifier = Modifier.weight(1f))
            
            // Right decorative element: Lottie animation (mirrored)
            Box(
                modifier = Modifier
                    .onSizeChanged { size ->
                        with(density) {
                            rightSvgWidth = size.width.toDp()
                        }
                    }
            ) {
                val rightComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.sidedecoration))
                LottieAnimation(
                    composition = rightComposition,
                    iterations = 1, // Play once
                    modifier = Modifier
                        .height(88.dp) // Fixed height of 88px
                        .scale(scaleX = -1f, scaleY = 1f), // Mirror horizontally
                    contentScale = ContentScale.Fit // Maintain original aspect ratio
                )
            }
            
            // 8dp spacing between inner right sidedecoration and outer right sidedecoration2
            Spacer(modifier = Modifier.width(8.dp))
            
            // Outer right decorative element: sidedecoration2 (rotated 180 degrees) - positioned at right edge with 16dp padding
            Box(
                modifier = Modifier
                    .padding(end = sidePadding) // 16dp padding from right edge
            ) {
                val outerRightComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.sidedecoration2))
                LottieAnimation(
                    composition = outerRightComposition,
                    iterations = 1, // Play once
                    modifier = Modifier
                        .width(24.dp) // Fixed width of 24dp
                        .graphicsLayer { rotationZ = 180f }, // Rotate 180 degrees
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
        
        // "Total focus time" label: 14sp, regular (doesn't scale)
        Text(
            text = "Total focus time",
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
 *   - Top: Dynamic ship name (16sp, bold) - animates when ship changes
 *   - Bottom: Static "Current ship" label (14sp, regular)
 *   - 4dp vertical spacing between labels
 * - Right side: "view" button (88dp width, same style as ViewAllButton)
 * 
 * The row has 16dp horizontal padding and 20dp vertical padding.
 * 
 * When the ship changes, the ship name animates sliding in from the left while fading in.
 * Animation duration: 1 second with smooth easing.
 * 
 * @param shipName The name of the currently selected ship
 * @param shipId The ID of the currently selected ship (used to detect changes)
 * @param onViewClick Callback when the view button is clicked
 * @param modifier Modifier for the row
 */
@Composable
private fun CurrentShipRow(
    shipName: String,
    shipId: String,
    onViewClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    // When using key(), the composable is recreated each time ship changes
    // So we can just animate from 0 to 1 on first composition
    var animationTarget by remember { mutableStateOf(0f) }
    
    // Start animation on first composition (when key changes, composable is recreated)
    LaunchedEffect(Unit) {
        animationTarget = 0f // Start from left, invisible
        delay(50) // Small delay to ensure state is set
        animationTarget = 1f // Animate to center, visible
    }
    
    // Animate progress from 0 to 1
    val animationProgress by animateFloatAsState(
        targetValue = animationTarget,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "ship_name_animation"
    )
    
    // Calculate final values: map progress (0-1) to actual offset and alpha ranges
    // Offset: 0 = -50dp (off-screen left), 1 = 0dp (normal position)
    // Alpha: 0 = 0 (invisible), 1 = 1 (fully visible)
    val finalOffsetX = (-50f + (animationProgress * 50f))
    val finalAlpha = animationProgress
    
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
            // Dynamic ship name: 16sp, bold - with animation
            Text(
                text = shipName,
                fontFamily = Exo2,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFFFFFFFF),
                modifier = Modifier
                    .offset(x = with(density) { finalOffsetX.dp })
                    .alpha(finalAlpha)
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
 * - Fixed height of 56dp
 * - Full width with 16dp side padding from screen edges
 * - Internal padding of 16dp on sides (total 32dp from screen edge to content)
 * - Two labels: left (14sp regular) and right (20sp, can be bold or regular)
 * - Labels are bottom-aligned with each other
 * - Row is vertically centered (labels are centered in the 56dp container)
 * 
 * @param label The left label text (e.g., "Last session")
 * @param value The right value text (e.g., "Today")
 * @param isValueBold Whether the value should be displayed in bold (default false)
 * @param modifier Modifier for the row
 */
@Composable
private fun TravelLogRow(
    label: String,
    value: String,
    isValueBold: Boolean = false,
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
            
            // Right value: 20sp, bold or regular based on isValueBold
            Text(
                text = value,
                fontFamily = Exo2,
                fontWeight = if (isValueBold) FontWeight.Bold else FontWeight.W400,
                fontSize = 20.sp,
                color = Color(0xFFFFFFFF)
            )
        }
    }
}

