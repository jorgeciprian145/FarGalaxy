package com.example.fargalaxy.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.fargalaxy.R

/**
 * VaultScreen composable - displays the vault/collection screen content.
 * Note: Background, noise, and indicator are handled by MainScreen (static layers).
 * Only the content moves when swiping.
 * 
 * @param onBackClick Callback when the back button is clicked
 * @param modifier Modifier for the screen
 */
@Composable
fun VaultScreen(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
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

        // Title area: "Vault" text positioned 16dp above the indicator
        // Indicator is at 48.dp from top (with status bar padding), so title should be at 32.dp (48.dp - 16.dp)
        // Rendered after gradients so it appears on top
        // Back button positioned on the left side, 16dp from edge, vertically aligned with indicator (same height container)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 48.dp) // Container starts at 48dp
                .fillMaxWidth()
                .height(51.dp) // Height of the indicator
        ) {
            // Title: Centered horizontally, positioned at top of container (matching original 32dp position relative to indicator)
            Text(
                text = "Vault",
                fontFamily = Exo2,
                fontSize = 18.sp,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-16).dp) // Offset to maintain original 32dp position (48dp - 16dp)
            )
            
            // Back button: Left side, 16dp from edge, 20dp upward offset
            Image(
                painter = painterResource(id = R.drawable.backdefault),
                contentDescription = "Back",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .offset(y = (-20).dp) // 20dp upward offset (4dp + 8dp + 8dp)
                    .height(51.dp)
                    .clickable(onClick = onBackClick), // Clickable modifier applied last
                contentScale = ContentScale.Fit
            )
        }
        
        // Content area: Labels and sector exploration progress
        // Header trimming line is at 115dp (48dp + 51dp + 16dp), content starts at 115dp
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 115.dp) // Start at header trimming line (0dp from trimming line)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Box containing labels (centered) and info icon (positioned to the right)
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Labels group: "Astra Verge" and "Current galaxy sector" - centered horizontally
                // Measure labels width to position icon correctly
                var labelsWidth by remember { mutableStateOf(0.dp) }
                val density = LocalDensity.current
                
                Column(
                    modifier = Modifier
                        .align(Alignment.Center) // Center the labels group
                        .onSizeChanged { size ->
                            with(density) {
                                labelsWidth = size.width.toDp()
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(0.dp) // 0dp spacing between labels
                ) {
                    // Top label: "Astra Verge" - Bold, 18sp
                    Text(
                        text = "Astra Verge", // TODO: Replace with dynamic value
                        fontFamily = Exo2,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center
                    )
                    
                    // Bottom label: "Current galaxy sector" - Regular, 14sp
                    Text(
                        text = "Current galaxy sector",
                        fontFamily = Exo2,
                        fontWeight = FontWeight.W400, // Regular
                        fontSize = 14.sp,
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center
                    )
                }
                
                // Info icon: Positioned 16dp to the right of the labels group, vertically aligned
                Box(
                    modifier = Modifier
                        .align(Alignment.Center) // Start from center
                        .offset(x = labelsWidth / 2 + 16.dp), // Move to the right edge of labels + 16dp
                    contentAlignment = Alignment.Center // Center the icon vertically within the box
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.infoicon),
                        contentDescription = "Info",
                        modifier = Modifier.height(40.dp), // 40dp height, maintaining aspect ratio
                        contentScale = ContentScale.Fit
                    )
                }
            }
            
            // 12dp spacing between bottom label and counter composable
            Spacer(modifier = Modifier.height(12.dp))
            
            // Sector Exploration Progress Counter
            SectorExplorationProgress(
                progress = 0.25f, // TODO: Replace with dynamic value (placeholder: 25%)
                modifier = Modifier.fillMaxWidth()
            )
            
            // 24dp spacing between SectorExplorationProgress and stats section
            Spacer(modifier = Modifier.height(24.dp))
            
            // Stats section: 3 columns with side rectangles and center stats
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp) // 16dp side padding
            ) {
                val totalWidth = maxWidth
                val subColumnWidth = totalWidth * 0.32f // 32% of screen width for each sub-column
                val centerColumnWidth = totalWidth * 0.68f // 68% of screen width
                val subColumnSpacing = totalWidth * 0.04f // 4% of screen width spacing between sub-columns
                val columnSpacing = 16.dp
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(columnSpacing), // 16dp spacing between columns
                    verticalAlignment = Alignment.CenterVertically // Vertically align JSONs and labels
                ) {
                    // Left column: Rectangle JSON aligned towards the center component, stretches to fill remaining space
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterEnd // Align rectangle towards the center (right side of left column)
                    ) {
                        val leftRectangleComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.rectangle))
                        LottieAnimation(
                            composition = leftRectangleComposition,
                            iterations = 1,
                            modifier = Modifier.height(8.dp), // 8dp height, maintaining aspect ratio
                            contentScale = ContentScale.Fit
                        )
                    }
                    
                    // Center column: 68% of screen width, subdivided into 2 columns with 4% spacing
                    Box(
                        modifier = Modifier.width(centerColumnWidth)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(subColumnSpacing) // 4% of screen width spacing between sub-columns
                        ) {
                            // Left sub-column: Focus time stats - 32% of screen width
                            Column(
                                modifier = Modifier.width(subColumnWidth),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(0.dp) // 0dp spacing between labels
                            ) {
                                // Top label: Bold, 32sp
                                Text(
                                    text = "45 m", // TODO: Replace with dynamic value
                                    fontFamily = Exo2,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32.sp,
                                    color = Color(0xFFFFFFFF),
                                    textAlign = TextAlign.Center
                                )
                                
                                // Bottom label: Regular, 14sp
                                Text(
                                    text = "Focus time in this sector",
                                    fontFamily = Exo2,
                                    fontWeight = FontWeight.W400, // Regular
                                    fontSize = 14.sp,
                                    lineHeight = 18.sp, // Same line height as paragraphs in ShipDetailsScreen
                                    color = Color(0xFFFFFFFF),
                                    textAlign = TextAlign.Center
                                )
                            }
                            
                            // Right sub-column: Sector rewards stats - 32% of screen width
                            Column(
                                modifier = Modifier.width(subColumnWidth),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(0.dp) // 0dp spacing between labels
                            ) {
                                // Top label: Bold, 32sp
                                Text(
                                    text = "8", // TODO: Replace with dynamic value
                                    fontFamily = Exo2,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32.sp,
                                    color = Color(0xFFFFFFFF),
                                    textAlign = TextAlign.Center
                                )
                                
                                // Bottom label: Regular, 14sp
                                Text(
                                    text = "Sector rewards unlocked",
                                    fontFamily = Exo2,
                                    fontWeight = FontWeight.W400, // Regular
                                    fontSize = 14.sp,
                                    lineHeight = 18.sp, // Same line height as paragraphs in ShipDetailsScreen
                                    color = Color(0xFFFFFFFF),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    
                    // Right column: Rectangle JSON aligned towards the center component, stretches to fill remaining space
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart // Align rectangle towards the center (left side of right column)
                    ) {
                        val rightRectangleComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.rectangle))
                        LottieAnimation(
                            composition = rightRectangleComposition,
                            iterations = 1,
                            modifier = Modifier
                                .height(8.dp) // 8dp height, maintaining aspect ratio
                                .graphicsLayer { rotationZ = 180f }, // Rotate 180 degrees
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
            
            // 24dp spacing between stats section and button
            Spacer(modifier = Modifier.height(24.dp))
            
            // "VIEW SECTOR DETAILS" button
            ViewSectorDetailsButton(
                onClick = {}, // TODO: Add callback for viewing sector details
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            
            // 24dp spacing between button and divider
            Spacer(modifier = Modifier.height(24.dp))
            
            // Horizontal divider: Separates button from content below
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }
    }
}

/**
 * HorizontalDivider composable - displays a horizontal white line with side padding.
 * 
 * Creates a 1dp height white line with 16dp horizontal padding on both sides.
 * Used to separate sections in the VaultScreen.
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
 * ViewSectorDetailsButton composable - displays the "VIEW SECTOR DETAILS" button with secondary style.
 * 
 * Uses the same visual format as ViewButton but with dynamic width:
 * - Transparent background with white border
 * - White text
 * - Rounded corners (80dp radius)
 * - Dynamic width: text width + 16dp padding on each side
 * - Fixed height: 32dp
 * - 16sp font size, regular weight
 * 
 * @param onClick Callback when button is clicked
 * @param modifier Modifier for the button
 */
@Composable
private fun ViewSectorDetailsButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val buttonText = "VIEW SECTOR DETAILS"
    
    // Measure text width
    val textLayoutResult = textMeasurer.measure(
        text = buttonText,
        style = TextStyle(
            fontFamily = Exo2,
            fontSize = 16.sp
        )
    )
    val textWidth = with(LocalDensity.current) { textLayoutResult.size.width.toDp() }
    val buttonWidth = textWidth + 48.dp // 24dp padding on each side
    
    Box(
        modifier = modifier
            .width(buttonWidth)
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
            text = buttonText,
            fontFamily = Exo2,
            fontSize = 16.sp,
            color = Color(0xFFFFFFFF), // White text
            textAlign = TextAlign.Center
        )
    }
}

/**
 * SectorExplorationProgress composable - displays the sector exploration progress with decorative SVG side elements.
 * 
 * Layout:
 * - Row with decorative SVG elements on left, center content, and decorative SVG elements on right
 * - Left decorative: SVG image (sidedecoration)
 * - Right decorative: SVG image (sidedecoration, mirrored)
 * - Center: Percentage (56sp bold), "Completed" label (14sp regular), and progress bar
 * - Progress bar width is 56% of screen width, which defines the container width inside the JSONs
 * - Center content auto-scales down if it exceeds available space
 * 
 * Spacing:
 * - 16dp padding from screen edges (for SVGs)
 * - 12dp internal padding between decorative elements and center content
 * 
 * @param progress Progress value between 0f and 1f (0 = 0%, 1 = 100%)
 * @param modifier Modifier for the component
 */
@Composable
private fun SectorExplorationProgress(
    progress: Float = 0.25f, // TODO: Replace with dynamic value
    modifier: Modifier = Modifier
) {
    // #region agent log
    try { java.io.FileWriter("c:\\Users\\Jorge\\AndroidStudioProjects\\FarGalaxy\\.cursor\\debug.log", true).use { it.write("{\"id\":\"log_${System.currentTimeMillis()}_sector_entry\",\"timestamp\":${System.currentTimeMillis()},\"location\":\"VaultScreen.kt:201\",\"message\":\"SectorExplorationProgress entry\",\"data\":{\"progress\":$progress},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"A\"}\n") } } catch(e: Exception) {}
    // #endregion
    
    val density = LocalDensity.current
    
    // Track SVG width to calculate available space for center content
    var leftSvgWidth by remember { mutableStateOf(0.dp) }
    var rightSvgWidth by remember { mutableStateOf(0.dp) }
    
    // Track center content height to match sidedecoration height
    var centerContentHeight by remember { mutableStateOf(0.dp) }
    
    // Animate progress value for smooth transitions
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500),
        label = "sector_progress_animation"
    )
    
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val totalWidth = maxWidth
        val sidePadding = 16.dp
        val decorationSpacing = 8.dp // 8dp spacing between rectangle and sidedecoration
        
        // Progress bar width is 50% of screen width
        val progressBarWidth = totalWidth * 0.50f
        
        // Calculate available width based on SVG widths (recalculates when SVG widths change)
        val availableWidth = derivedStateOf {
            (totalWidth - sidePadding.times(2) - leftSvgWidth - rightSvgWidth - decorationSpacing.times(4)) // 4 spacings: 2 rectangles + 2 sidedecorations
                .coerceAtLeast(0.dp)
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 6.dp), // Move all JSONs downwards by 6dp
            horizontalArrangement = Arrangement.spacedBy(0.dp), // No spacing, we'll use Spacers
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Outer left decorative element: rectangle - positioned at left edge with 16dp padding
            Box(
                modifier = Modifier
                    .padding(start = sidePadding) // 16dp padding from left edge
            ) {
                val outerLeftComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.rectangle))
                LottieAnimation(
                    composition = outerLeftComposition,
                    iterations = 1, // Play once
                    modifier = Modifier.height(88.dp), // Match sidedecoration height
                    contentScale = ContentScale.Fit // Maintain original aspect ratio
                )
            }
            
            // 8dp spacing between outer left rectangle and inner left sidedecoration
            Spacer(modifier = Modifier.width(decorationSpacing))
            
            // Left decorative element: Lottie animation
            // Height matches center content height (from top of percentage to bottom of progress bar)
            Box(
                modifier = Modifier
                    .onSizeChanged { size ->
                        // #region agent log
                        try { java.io.FileWriter("c:\\Users\\Jorge\\AndroidStudioProjects\\FarGalaxy\\.cursor\\debug.log", true).use { it.write("{\"id\":\"log_${System.currentTimeMillis()}_left_svg\",\"timestamp\":${System.currentTimeMillis()},\"location\":\"VaultScreen.kt:242\",\"message\":\"Left SVG size changed\",\"data\":{\"width\":${size.width},\"height\":${size.height}},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"A\"}\n") } } catch(e: Exception) {}
                        // #endregion
                        with(density) {
                            leftSvgWidth = size.width.toDp()
                        }
                        // #region agent log
                        try { java.io.FileWriter("c:\\Users\\Jorge\\AndroidStudioProjects\\FarGalaxy\\.cursor\\debug.log", true).use { it.write("{\"id\":\"log_${System.currentTimeMillis()}_left_svg_after\",\"timestamp\":${System.currentTimeMillis()},\"location\":\"VaultScreen.kt:244\",\"message\":\"Left SVG width after conversion\",\"data\":{\"leftSvgWidth\":${leftSvgWidth.value}},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"A\"}\n") } } catch(e: Exception) {}
                        // #endregion
                    }
            ) {
                val leftComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.sidedecoration))
                LottieAnimation(
                    composition = leftComposition,
                    iterations = 1, // Play once
                    modifier = Modifier.height(
                        if (centerContentHeight > 0.dp) centerContentHeight * 1.20f else 88.dp
                    ), // 20% taller than center content height, fallback to 88dp initially
                    contentScale = ContentScale.Fit // Maintain original aspect ratio
                )
            }
            
            // Spacer: Stretches to fill available space between inner left decoration and center content
            Spacer(modifier = Modifier.weight(1f))
            
            // Center content: Percentage + "Completed" label + progress bar
            // Container width is defined by progress bar width (50% of screen width)
            // This ensures the container fits the progress bar and the JSONs are positioned around it
            Box(
                modifier = Modifier
                    .width(progressBarWidth.coerceAtMost(availableWidth.value)) // Use progress bar width, but don't exceed available space
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { size ->
                            with(density) {
                                centerContentHeight = size.height.toDp()
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(0.dp) // Manual spacing control
                ) {
                    // Percentage: Bold, 56sp
                    // TODO: Replace with dynamic value
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        fontFamily = Exo2,
                        fontWeight = FontWeight.Bold,
                        fontSize = 56.sp,
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center
                    )
                    
                    // Spacing between percentage and "Completed" label
                    Spacer(modifier = Modifier.height((-8).dp))
                    
                    // "Completed" label: Regular, 14sp
                    Text(
                        text = "Completed",
                        fontFamily = Exo2,
                        fontWeight = FontWeight.W400, // Regular
                        fontSize = 14.sp,
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center
                    )
                    
                    // Spacing between "Completed" label and progress bar
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Progress bar: 50% of screen width
                    ProgressBar(
                        progress = animatedProgress,
                        backgroundColor = Color(0x33FFFFFF), // White with 20% opacity
                        foregroundColor = Color(0xFFFFFFFF), // White
                        modifier = Modifier
                            .fillMaxWidth() // Fill the container width (which is progressBarWidth)
                            .height(8.dp)
                    )
                }
            }
            
            // Spacer: Stretches to fill available space between center content and inner right decoration
            Spacer(modifier = Modifier.weight(1f))
            
            // Right decorative element: Lottie animation (mirrored)
            // Height matches center content height (from top of percentage to bottom of progress bar)
            Box(
                modifier = Modifier
                    .onSizeChanged { size ->
                        // #region agent log
                        try { java.io.FileWriter("c:\\Users\\Jorge\\AndroidStudioProjects\\FarGalaxy\\.cursor\\debug.log", true).use { it.write("{\"id\":\"log_${System.currentTimeMillis()}_right_svg\",\"timestamp\":${System.currentTimeMillis()},\"location\":\"VaultScreen.kt:312\",\"message\":\"Right SVG size changed\",\"data\":{\"width\":${size.width},\"height\":${size.height}},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"A\"}\n") } } catch(e: Exception) {}
                        // #endregion
                        with(density) {
                            rightSvgWidth = size.width.toDp()
                        }
                        // #region agent log
                        try { java.io.FileWriter("c:\\Users\\Jorge\\AndroidStudioProjects\\FarGalaxy\\.cursor\\debug.log", true).use { it.write("{\"id\":\"log_${System.currentTimeMillis()}_right_svg_after\",\"timestamp\":${System.currentTimeMillis()},\"location\":\"VaultScreen.kt:314\",\"message\":\"Right SVG width after conversion\",\"data\":{\"rightSvgWidth\":${rightSvgWidth.value}},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"A\"}\n") } } catch(e: Exception) {}
                        // #endregion
                    }
            ) {
                val rightComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.sidedecoration))
                LottieAnimation(
                    composition = rightComposition,
                    iterations = 1, // Play once
                    modifier = Modifier
                        .height(
                            if (centerContentHeight > 0.dp) centerContentHeight * 1.20f else 88.dp
                        ) // 20% taller than center content height, fallback to 88dp initially
                        .scale(scaleX = -1f, scaleY = 1f), // Mirror horizontally
                    contentScale = ContentScale.Fit // Maintain original aspect ratio
                )
            }
            
            // 8dp spacing between inner right sidedecoration and outer right rectangle
            Spacer(modifier = Modifier.width(decorationSpacing))
            
            // Outer right decorative element: rectangle (rotated 180 degrees) - positioned at right edge with 16dp padding
            Box(
                modifier = Modifier
                    .padding(end = sidePadding) // 16dp padding from right edge
            ) {
                val outerRightComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.rectangle))
                LottieAnimation(
                    composition = outerRightComposition,
                    iterations = 1, // Play once
                    modifier = Modifier
                        .height(88.dp) // Match sidedecoration height
                        .graphicsLayer { rotationZ = 180f }, // Rotate 180 degrees
                    contentScale = ContentScale.Fit // Maintain original aspect ratio
                )
            }
        }
    }
}

