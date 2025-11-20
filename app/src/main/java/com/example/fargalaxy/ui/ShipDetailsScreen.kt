package com.example.fargalaxy.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.fargalaxy.R
import com.example.fargalaxy.model.Ship
import com.example.fargalaxy.model.ShipRarity

/**
 * Helper function to convert ShipRarity enum to display text.
 * Returns the rarity name in uppercase followed by " SHIP".
 */
fun getRarityDisplayText(rarity: ShipRarity): String {
    return "${rarity.name} SHIP"
}

/**
 * ShipDetailsScreen composable - displays details about the user's current ship.
 * This screen opens when the user taps "view" next to the ship name on the career screen.
 * 
 * @param ship The ship to display details for
 * @param onBackClick Callback when the back button is clicked
 * @param modifier Modifier for the screen
 */
@Composable
fun ShipDetailsScreen(
    ship: Ship,
    onBackClick: () -> Unit = {},
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
        // Background image
        Image(
            painter = painterResource(id = R.drawable.shipscreenbackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Top gradient overlay: Covers 20% of screen height, creating a fade effect at the top.
        // Gradient transitions from solid black at the top to transparent at the bottom.
        // Positioned behind all elements but on top of the background image.
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
        
        // Top controls area: Title and back button
        // Positioned at the same location as the top controls in GalaxyScreen
        // (statusBarsPadding + 48.dp from top, 51.dp height)
        // These elements remain static and above the clipping line
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 48.dp)
                .fillMaxWidth()
                .height(51.dp)
        ) {
            // Back button: Same size and style as CareerButton
            // Uses the backdefault SVG icon
            // Positioned on the left side
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
            
            // Title: "Your current ship"
            // Same font style as "Your career" in CareerScreen
            // Horizontally centered on the screen
            Text(
                text = "Your current ship",
                fontFamily = Exo2,
                fontSize = 18.sp,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        // Clip boundary container: Box positioned at 115dp to define clipping boundary
        // Title/back button bottom is at: statusBarPadding + 48dp + 51dp = statusBarPadding + 99dp
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
                    .padding(bottom = 188.dp), // Allow lore text to end 188px from bottom when scrolled all the way
                verticalArrangement = Arrangement.spacedBy(0.dp) // Manual spacing control
            ) {
                // Initial spacer: Push content down 8dp from clip line
                Spacer(modifier = Modifier.height(-16.dp))
                
                // Track ship image size to align other elements
                var shipImageSize by remember { mutableStateOf(IntSize.Zero) }
                
                // Track which tab is selected (DETAILS or SPECS)
                var selectedTab by remember { mutableStateOf("DETAILS") }
                
                // Ship image container: Multi-layered component
                // Positioned relative to the scrollable column start
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp) // This creates the 16dp padding container
                ) {
                    // Inner container to center all layers
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    ) {
                        // Get screen width to calculate logo size
                        val configuration = LocalConfiguration.current
                        val screenWidthDp = configuration.screenWidthDp.dp
                        val logoWidth = screenWidthDp * 0.48f // 48% of screen width
                        
                        // Backship animation layer: Lottie animation positioned behind the logo layer.
                        // Vertically and horizontally centered relative to the ship image.
                        // Full width of screen, maintains aspect ratio.
                        // Loops continuously at original speed (6 seconds per loop).
                        // Positioned behind the logo but centered with the ship.
                        val backshipComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.backship))
                        LottieAnimation(
                            composition = backshipComposition,
                            iterations = LottieConstants.IterateForever,
                            speed = 1.0f, // Original speed
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth(),
                            contentScale = ContentScale.Fit
                        )
                        
                        // Logo layer - behind ship image but in front of backship animation
                        // 48% of screen width, vertically and horizontally aligned with ship
                        // For now, using valkethlogo for Valketh Industries ships
                        // TODO: Make logo selection dynamic based on ship.manufacturer
                        Image(
                            painter = painterResource(id = R.drawable.valkethlogo),
                            contentDescription = "Manufacturer logo",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .width(logoWidth),
                            contentScale = ContentScale.Fit
                        )
                        
                        // Ship image (frontmost) - on top of everything, even noise
                        // Fills available width (already constrained by 16dp padding), maintains aspect ratio
                        // Height is calculated automatically based on aspect ratio
                        Image(
                            painter = painterResource(id = ship.renderImageResId),
                            contentDescription = ship.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                                .onSizeChanged { size ->
                                    shipImageSize = size
                                },
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                
                // Rarity badge and ship name container
                // Positioned 20px below the ship image container
                // Uses Column to stack badge and ship name vertically
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Spacing between ship image and badge: 32px
                    Spacer(modifier = Modifier.height(0.dp))
                    
                    // Rarity badge: Container with "COMMON SHIP" text
                    // 16px height, width adjusts to content, 4px padding inside
                    // Text: 10px, Medium weight, white color
                    // Container: white background with 16% opacity
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .wrapContentWidth()
                            .background(
                                color = Color(0xFFFFFFFF).copy(alpha = 0.16f)
                            )
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getRarityDisplayText(ship.rarity),
                            fontFamily = Exo2,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.offset(y = (-4).dp) // Adjust this value to move text up (negative) or down (positive)
                        )
                    }
                    
                    // Ship name: 18px, Bold, positioned 8px below badge
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ship.name,
                        fontFamily = Exo2,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center
                    )
                    
                    // Tab switcher: 20px below ship name
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Tab container: Full width with 16px horizontal padding, 40px height, 40px corner radius, 1px white border
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(40.dp)
                            .border(
                                width = 1.dp,
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(40.dp)
                            )
                    ) {
                        // Inner container with 4px padding from top and sides
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 4.dp, start = 4.dp, end = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // DETAILS tab
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(32.dp)
                                        .background(
                                            color = if (selectedTab == "DETAILS") Color(0xFFFFFFFF) else Color.Transparent,
                                            shape = RoundedCornerShape(40.dp)
                                        )
                                        .clickable { selectedTab = "DETAILS" },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "DETAILS",
                                        fontFamily = Exo2,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.W400,
                                        color = if (selectedTab == "DETAILS") Color(0xFF010102) else Color(0xFFFFFFFF),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.offset(y = (-1).dp) // Adjust this value to change vertical offset
                                    )
                                }
                                
                                // SPECS tab
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(32.dp)
                                        .background(
                                            color = if (selectedTab == "SPECS") Color(0xFFFFFFFF) else Color.Transparent,
                                            shape = RoundedCornerShape(40.dp)
                                        )
                                        .clickable { selectedTab = "SPECS" },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "SPECS",
                                        fontFamily = Exo2,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.W400,
                                        color = if (selectedTab == "SPECS") Color(0xFF010102) else Color(0xFFFFFFFF),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.offset(y = (-1).dp) // Adjust this value to change vertical offset
                                    )
                                }
                            }
                        }
                    }
                    
                    // Details section: 24px below tabs
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Two-row layout for ship details
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Row 1: Type and Manufacturer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Type
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Type",
                                    fontFamily = Exo2,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFFFFF)
                                )
                                Text(
                                    text = ship.type,
                                    fontFamily = Exo2,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W400,
                                    color = Color(0xFFFFFFFF),
                                    lineHeight = 18.sp
                                )
                            }
                            
                            // Manufacturer
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Manufacturer",
                                    fontFamily = Exo2,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFFFFF)
                                )
                                Text(
                                    text = ship.manufacturer,
                                    fontFamily = Exo2,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W400,
                                    color = Color(0xFFFFFFFF),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                        
                        // Row 2: Crew capacity and Dimensions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Crew capacity
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Crew capacity",
                                    fontFamily = Exo2,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFFFFF)
                                )
                                Text(
                                    text = "${ship.crewCapacity.pilots} pilots",
                                    fontFamily = Exo2,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W400,
                                    color = Color(0xFFFFFFFF),
                                    lineHeight = 18.sp
                                )
                                Text(
                                    text = "${ship.crewCapacity.crewMembers} crew members",
                                    fontFamily = Exo2,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W400,
                                    color = Color(0xFFFFFFFF),
                                    lineHeight = 18.sp
                                )
                            }
                            
                            // Dimensions
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Dimensions",
                                    fontFamily = Exo2,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFFFFF)
                                )
                                Text(
                                    text = "Length ${ship.dimensions.lengthMeters.toInt()} m (${ship.dimensions.lengthFeet.toInt()} feet)",
                                    fontFamily = Exo2,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W400,
                                    color = Color(0xFFFFFFFF),
                                    lineHeight = 18.sp
                                )
                                Text(
                                    text = "Width ${ship.dimensions.widthMeters.toInt()} m (${ship.dimensions.widthFeet.toInt()} feet)",
                                    fontFamily = Exo2,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W400,
                                    color = Color(0xFFFFFFFF),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                    
                    // Ship's lore: Full width, 24px spacing from above
                    Spacer(modifier = Modifier.height(24.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Ship's lore",
                            fontFamily = Exo2,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFFFFF)
                        )
                        Text(
                            text = "After countless prototypes and more failures than the engineers cared to admit, Valketh Industries finally released a ship that changed the way new pilots entered the galaxy. The Phantom was never designed to be the fastest or the strongest. It was built to endure, to forgive mistakes, and to carry beginners through their first uncertain steps into deep space.\n\nIts frame is simple, its systems modest, and its performance unremarkable when compared to the elite vessels flown by veteran crews. Yet the Phantom earned its reputation through grit rather than glory. It survives rough landings, unstable jump routes, and long stretches of travel where more advanced ships would demand repairs. For generations of cadets, its hum has been the first sound they heard before taking off into the void.\n\nMost pilots eventually outgrow the Phantom once they gain skill and confidence, trading it for ships that push the limits of speed, firepower, or exploration range. But the Phantom stays with them. It becomes the memory of their first real flight, the craft that caught their mistakes and carried their victories, the starting point of every career that ever reached the stars.",
                            fontFamily = Exo2,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W400,
                            color = Color(0xFFFFFFFF),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
        
        // Bottom fixed container: Contains gradient and button container
        // Both stay fixed at bottom when scrolling
        // navigationBarsPadding() ensures button container sits right above navigation bar with 0 spacing
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding() // Positions content above navigation bar (0 spacing between them)
        ) {
            // Gradient overlay container: 48dp height, full width
            // Gradient from 100% opacity black at bottom to 0% opacity black at top
            // Positioned right above the button container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x00000000), // 0% opacity at top
                                Color(0xFF000000)  // 100% opacity at bottom
                            )
                        )
                    )
            )
            
            // Button container: Full width, black background
            // Contains two buttons with 16dp padding on all sides, 8dp spacing between buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF000000)) // Black background at 100% opacity
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Left button: "CHANGE SHIP" - no fill, white stroke, white text
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .border(
                            width = 1.dp,
                            color = Color(0xFFFFFFFF),
                            shape = RoundedCornerShape(40.dp)
                        )
                        .clickable { /* TODO: Add onClick handler */ },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CHANGE SHIP",
                        fontFamily = Exo2,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W400,
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.offset(y = (-1).dp)
                    )
                }
                
                // Right button: "UPGRADE" - white fill, black text
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .background(
                            color = Color(0xFFFFFFFF),
                            shape = RoundedCornerShape(40.dp)
                        )
                        .clickable { /* TODO: Add onClick handler */ },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "UPGRADE",
                        fontFamily = Exo2,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W400,
                        color = Color(0xFF010102),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.offset(y = (-1).dp)
                    )
                }
            }
            }
        }
    }
}

