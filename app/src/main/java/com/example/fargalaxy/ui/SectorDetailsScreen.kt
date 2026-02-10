package com.example.fargalaxy.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.fargalaxy.R
import kotlinx.coroutines.delay

/**
 * SectorDetailsScreen composable - displays details about a sector.
 * This screen opens when the user taps on "VIEW SECTOR DETAILS" button in VaultScreen.
 * 
 * @param onBackClick Callback when the back button is clicked
 * @param modifier Modifier for the screen
 */
@Composable
fun SectorDetailsScreen(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Scroll state to track scrolling
    val scrollState = rememberScrollState()
    
    // Get density to convert dp to pixels
    val density = LocalDensity.current
    
    // Calculate if content is being scrolled (scroll position > 0 means scrolling has started)
    // Header bottom is at statusBarsPadding + 75dp, trim line appears when scrolling starts
    val isContentClipped = derivedStateOf {
        with(density) {
            scrollState.value > 0
        }
    }
    
    // Track image height to calculate padding for scrollable content
    var imageHeight by remember { mutableStateOf(0.dp) }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Solid background color behind everything
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF111419))
        )
        
        // Sector details image: 100% of screen width, aligned to top edge
        // Appears behind the header, offset down by 40dp
        Image(
            painter = painterResource(id = R.drawable.sectordetails1),
            contentDescription = "Sector details",
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(y = 40.dp) // Move image down by 40dp
                .fillMaxWidth() // 100% of screen width
                .onSizeChanged { size ->
                    with(density) {
                        imageHeight = size.height.toDp()
                    }
                },
            contentScale = ContentScale.FillWidth // Fill width, maintain aspect ratio
        )
        
        // Top gradient overlay: Covers 35% of screen height, creating a fade effect at the top.
        // Gradient transitions from solid black at the top to transparent at the bottom.
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF000000),
                            Color(0x00000000)
                        )
                    )
                )
        )
        
        // Top controls area: Title and back button
        // Positioned at statusBarsPadding + 24.dp from top, 51.dp height
        // Appears on top of the image
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 24.dp)
                .fillMaxWidth()
                .height(51.dp)
        ) {
            // Back button
            Image(
                painter = painterResource(id = R.drawable.backdefault),
                contentDescription = "Back",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 24.dp)
                    .height(51.dp)
                    .clickable(onClick = onBackClick),
                contentScale = ContentScale.Fit
            )
            
            // Title: "Sector details"
            Text(
                text = "Sector details",
                fontFamily = Exo2,
                fontSize = 18.sp,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        // Clip boundary container: Positioned at trim line to clip content that scrolls above it
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 91.dp) // Trim line position (header bottom 75dp + 16dp)
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
            // Top padding equals 45% of image height, offset down by 40dp (same as image)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .offset(y = 40.dp) // Move content down by 40dp (same as image)
                    .verticalScroll(scrollState)
                    .navigationBarsPadding()
                    .padding(top = imageHeight * 0.45f) // 45% of image height
                    .padding(bottom = 64.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Title section: Badge above "Astra Verge" with JSONs on sides
                // All horizontally center aligned
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp) // 4dp spacing between badge and title
                ) {
                    // Badge: "CURRENT SECTOR" - Same format as "CURRENT SHIP" badge
                    CurrentSectorBadge()
                    
                    // Title row: "Astra Verge" with small rectangle JSONs on sides
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left small rectangle JSON: 8dp spacing from title
                        val leftRectangleComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.rectangle))
                        LottieAnimation(
                            composition = leftRectangleComposition,
                            iterations = 1, // Play once then stay static
                            modifier = Modifier.size(8.dp),
                            contentScale = ContentScale.Fit
                        )
                        
                        // Spacing between left rectangle and title: 8dp (doubled from 4dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Title: "Astra Verge" - Bold, 24sp
                        Text(
                            text = "Astra Verge",
                            fontFamily = Exo2,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center
                        )
                        
                        // Spacing between title and right rectangle: 8dp (doubled from 4dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Right small rectangle JSON: Rotated 180 degrees
                        val rightRectangleComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.rectangle))
                        LottieAnimation(
                            composition = rightRectangleComposition,
                            iterations = 1, // Play once then stay static
                            modifier = Modifier
                                .size(8.dp)
                                .graphicsLayer {
                                    rotationZ = 180f
                                },
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                
                // Spacing from title section to factions section: 24dp
                Spacer(modifier = Modifier.height(24.dp))
                
                // Factions in this sector section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp), // 16dp side padding for title only
                    verticalArrangement = Arrangement.spacedBy(0.dp) // 0dp spacing between title and container
                ) {
                    // Title: "Factions in this sector" - Bold, 14sp
                    Text(
                        text = "Factions in this sector",
                        fontFamily = Exo2,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFFFFF)
                    )
                }
                
                // Factions container: Full width with 16dp side padding, JSONs at edges
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp) // 16dp side padding
                        .padding(top = 0.dp) // 0dp below title
                ) {
                    var factionContainerHeight by remember { mutableStateOf(0.dp) }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left small rectangle JSON: At left edge of container (16dp from screen edge)
                        val leftRectangleComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.rectangle))
                        LottieAnimation(
                            composition = leftRectangleComposition,
                            iterations = 1,
                            modifier = Modifier.size(8.dp),
                            contentScale = ContentScale.Fit
                        )
                        
                        // Spacing between left rectangle and container: 12dp (4dp + 8dp additional)
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        // Factions container: 3 internal containers with automatic spacing based on screen width
                        // Animation states for staggered fade-in
                        var showFirstFaction by remember { mutableStateOf(false) }
                        var showSecondFaction by remember { mutableStateOf(false) }
                        var showThirdFaction by remember { mutableStateOf(false) }
                        
                        // Trigger animations with delays
                        LaunchedEffect(Unit) {
                            kotlinx.coroutines.delay(500) // 0.5s delay for first
                            showFirstFaction = true
                            kotlinx.coroutines.delay(500) // 0.5s delay for second (1.0s total)
                            showSecondFaction = true
                            kotlinx.coroutines.delay(500) // 0.5s delay for third (1.5s total)
                            showThirdFaction = true
                        }
                        
                        // Animated alpha values
                        val firstAlpha by animateFloatAsState(
                            targetValue = if (showFirstFaction) 1f else 0f,
                            animationSpec = tween(durationMillis = 500),
                            label = "first_faction_alpha"
                        )
                        val secondAlpha by animateFloatAsState(
                            targetValue = if (showSecondFaction) 1f else 0f,
                            animationSpec = tween(durationMillis = 500),
                            label = "second_faction_alpha"
                        )
                        val thirdAlpha by animateFloatAsState(
                            targetValue = if (showThirdFaction) 1f else 0f,
                            animationSpec = tween(durationMillis = 500),
                            label = "third_faction_alpha"
                        )
                        
                        Row(
                            modifier = Modifier.weight(1f), // Fill remaining space between JSONs
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // First faction: ASN - fades in 0.5s after screen opens
                            Box(
                                modifier = Modifier
                                    .alpha(firstAlpha)
                                    .onSizeChanged { size ->
                                        with(density) {
                                            factionContainerHeight = size.height.toDp()
                                        }
                                    }
                            ) {
                                FactionContainer(
                                    logoResId = R.drawable.alliancelogo,
                                    label = "ASN"
                                )
                            }
                            
                            // Spacer for automatic spacing (will expand on wider viewports)
                            Spacer(modifier = Modifier.weight(1f))
                            
                            // Vertical divider: 80% of container height
                            if (factionContainerHeight > 0.dp) {
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(factionContainerHeight * 0.80f)
                                        .background(Color(0x52FFFFFF)) // White with 32% opacity
                                )
                            }
                            
                            // Spacer for automatic spacing (will expand on wider viewports)
                            Spacer(modifier = Modifier.weight(1f))
                            
                            // Second faction: ISC - fades in 0.5s after first (1.0s total)
                            FactionContainer(
                                logoResId = R.drawable.isflogo,
                                label = "ISC",
                                modifier = Modifier.alpha(secondAlpha)
                            )
                            
                            // Spacer for automatic spacing (will expand on wider viewports)
                            Spacer(modifier = Modifier.weight(1f))
                            
                            // Vertical divider: 80% of container height
                            if (factionContainerHeight > 0.dp) {
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(factionContainerHeight * 0.80f)
                                        .background(Color(0x52FFFFFF)) // White with 32% opacity
                                )
                            }
                            
                            // Spacer for automatic spacing (will expand on wider viewports)
                            Spacer(modifier = Modifier.weight(1f))
                            
                            // Third faction: NSA - fades in 0.5s after second (1.5s total)
                            FactionContainer(
                                logoResId = R.drawable.navakeshilogo,
                                label = "NSA",
                                modifier = Modifier.alpha(thirdAlpha)
                            )
                        }
                        
                        // Spacing between container and right rectangle: 12dp (4dp + 8dp additional)
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        // Right small rectangle JSON: At right edge of screen
                        val rightRectangleComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.rectangle))
                        LottieAnimation(
                            composition = rightRectangleComposition,
                            iterations = 1,
                            modifier = Modifier
                                .size(8.dp)
                                .graphicsLayer {
                                    rotationZ = 180f
                                },
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                
                // Spacing from factions section to lore section: 24dp
                Spacer(modifier = Modifier.height(24.dp))
                
                // Sector lore section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp), // 16dp side padding
                    verticalArrangement = Arrangement.spacedBy(8.dp) // 8dp spacing between title and paragraph
                ) {
                    // Title: "Sector lore" - Bold, 14sp
                    Text(
                        text = "Sector lore",
                        fontFamily = Exo2,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFFFFF)
                    )
                    
                    // Paragraph: Same format as ship lore in ShipDetailsScreen
                    Text(
                        text = "When humanity first expanded beyond its home system, certain regions of space stood out as natural points of convergence. Astra Verge was one of them. Rich in habitable worlds and stable hyper routes, it became the first sector to be extensively colonized and mapped, serving as the foundation for human expansion across the galaxy.\n\nOver centuries, as settlements grew and technology advanced, humanity diverged into multiple starfaring factions. Many of these cultures would later find themselves at odds, and Astra Verge became a silent witness to the earliest political tensions and conflicts that shaped the modern galaxy.\n\nToday, Astra Verge is considered a relatively safe and well-charted sector, especially for new pilots and explorers. While its most prominent systems have been studied in depth, space is never truly static. Trade routes shift, anomalies emerge, and unexplored pockets still remain. Even here, every journey holds the potential for discovery.",
                        fontFamily = Exo2,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        color = Color(0xFFFFFFFF),
                        lineHeight = 18.sp
                    )
                }
            }
        }
        
        // Bottom gradient container: 10% screen height, right above Android bottom bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.10f) // 10% of screen height
                .navigationBarsPadding() // Right above Android bottom bar
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x00000000), // 0% opacity at top
                            Color(0xFF000000)  // 100% opacity at bottom
                        )
                    )
                )
        )
    }
}

/**
 * CurrentSectorBadge composable - displays the "CURRENT SECTOR" badge.
 * 
 * Badge specifications:
 * - Fixed height: 16dp
 * - Label: "CURRENT SECTOR", 10sp, Medium weight, color #010102
 * - Width: Adjusts to label + 16dp internal padding (8dp each side)
 * - Corner radius: 80dp
 * - Background: #FFFFFF (white)
 * - Label vertical offset: -1dp
 * 
 * @param modifier Modifier for the badge
 */
@Composable
private fun CurrentSectorBadge(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .wrapContentHeight()
            .wrapContentWidth()
            .clip(RoundedCornerShape(80.dp))
            .background(Color(0xFFFFFFFF)) // White background
            .padding(start = 8.dp, top = 2.dp, end = 8.dp, bottom = 3.dp), // 8dp padding on each side, 2dp top, 3dp bottom
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "CURRENT SECTOR",
            fontFamily = Exo2,
            fontSize = 10.sp,
            lineHeight = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF010102), // Very dark color
            textAlign = TextAlign.Center
        )
    }
}

/**
 * FactionContainer composable - displays a single faction container with logo and label.
 * 
 * Container specifications:
 * - Square container: 72dp size (1:1 aspect ratio)
 * - PNG logo inside: 40dp width, maintains aspect ratio, vertically and horizontally centered
 * - Label below: 14sp regular, 0dp spacing from container
 * - All elements center aligned
 * 
 * @param logoResId The drawable resource ID for the faction logo
 * @param label The label text to display below the logo
 * @param modifier Modifier for the container
 */
@Composable
private fun FactionContainer(
    logoResId: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp) // 0dp spacing between container and label
    ) {
        // Square container: 72dp size
        Box(
            modifier = Modifier
                .size(72.dp),
            contentAlignment = Alignment.Center // Center the logo inside
        ) {
            // PNG logo: 40dp width, maintains aspect ratio
            Image(
                painter = painterResource(id = logoResId),
                contentDescription = label,
                modifier = Modifier.width(40.dp), // 40dp width, maintains aspect ratio
                contentScale = ContentScale.Fit
            )
        }
        
        // Label: 14sp regular, 0dp spacing from container
        Text(
            text = label,
            fontFamily = Exo2,
            fontSize = 14.sp,
            fontWeight = FontWeight.W400, // Regular
            color = Color(0xFFFFFFFF),
            textAlign = TextAlign.Center
        )
    }
}
