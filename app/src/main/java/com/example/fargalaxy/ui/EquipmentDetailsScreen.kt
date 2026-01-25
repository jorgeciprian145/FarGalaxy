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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
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

/**
 * EquipmentDetailsScreen composable - displays details about an equipment item.
 * This screen opens when the user taps on an equipment card in the EquipmentScreen.
 * 
 * @param equipmentName The name of the equipment item
 * @param equipmentImageResId The drawable resource ID for the equipment image
 * @param price The price of the equipment in credits
 * @param userCredits The user's current credits
 * @param description The description text for the equipment
 * @param onBackClick Callback when the back button is clicked
 * @param onPurchaseClick Callback when the purchase button is clicked
 * @param modifier Modifier for the screen
 */
@Composable
fun EquipmentDetailsScreen(
    equipmentName: String,
    equipmentImageResId: Int,
    price: Int,
    description: String,
    onBackClick: () -> Unit = {},
    onPurchaseClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Read credits from global repository
    val userCredits = com.example.fargalaxy.data.UserDataRepository.userCredits
    
    // Determine if user can afford the equipment
    val canAfford = price <= userCredits
    // Scroll state to track when content is being clipped
    val scrollState = rememberScrollState()
    
    // Calculate if content is being scrolled (scroll position > 0 means scrolling has started)
    val isScrolling = derivedStateOf {
        scrollState.value > 0
    }
    
    // Get density to convert dp to pixels
    val density = LocalDensity.current
    
    // Track equipment image size to calculate clip boundary position
    var equipmentImageSize by remember { mutableStateOf(IntSize.Zero) }
    
    // Track the actual height of the name/price Column (includes name, spacing, and price container)
    var namePriceHeight by remember { mutableStateOf(0.dp) }
    
    // Convert equipment image height to dp for calculation
    val equipmentImageHeightDp = with(density) {
        equipmentImageSize.height.toDp()
    }
    
    // Calculate clip boundary position:
    // Fixed content Box starts at: statusBarsPadding + 64.dp
    // Equipment image: variable height (equipmentImageHeightDp)
    // Name/price Column: tracked via onSizeChanged (namePriceHeight)
    // Column has -28dp offset, so visual position is adjusted
    // The namePriceHeight already includes the 24dp spacer, so we don't add it again
    // Additional offset: 40dp (same as StaryardDetailsScreen for most ships)
    val clipBoundaryTop = 64.dp + equipmentImageHeightDp + namePriceHeight + 8.dp // Moved up by 32dp (40dp - 32dp = 8dp)
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.shipscreenbackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Backlines layer: Full width, aligned to top, maintains aspect ratio
        // Positioned behind everything except the background
        Image(
            painter = painterResource(id = R.drawable.backlines),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
        
        // Top gradient overlay: Covers 30% of screen height, creating a fade effect at the top.
        // Gradient transitions from 32% opacity white at the top to transparent at the bottom.
        // Always uses common (white) gradient for equipment
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.30f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x52FFFFFF), // White at 32% opacity (common)
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
                .padding(top = 24.dp)
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
                    .padding(start = 12.dp)
                    .height(51.dp)
                    .clickable(onClick = onBackClick),
                contentScale = ContentScale.Fit
            )
            
            // Title: "Equipment details"
            // Same font style as "Your career" in CareerScreen
            // Horizontally centered on the screen
            Text(
                text = "Equipment details",
                fontFamily = Exo2,
                fontSize = 18.sp,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        // Fixed content above clip boundary: Equipment image, name, and price
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 64.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
            // Equipment image container: Multi-layered component
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Inner container to center all layers
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                ) {
                    // Backship animation layer: Lottie animation positioned behind the equipment image.
                    // Vertically and horizontally centered relative to the equipment image.
                    // Full width of screen, maintains aspect ratio.
                    // Loops continuously at original speed (6 seconds per loop).
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
                    
                    // Equipment image (frontmost) - on top of everything
                    // Fills available width (already constrained by 16dp padding), maintains aspect ratio
                    // Height is calculated automatically based on aspect ratio
                    Image(
                        painter = painterResource(id = equipmentImageResId),
                        contentDescription = equipmentName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .onSizeChanged { size ->
                                equipmentImageSize = size
                            },
                        contentScale = ContentScale.Fit
                    )
                }
            }
            
            // Name and price container
            // Positioned below the equipment image container
            // Uses Column to stack name and price vertically
            // Applied vertical offset of -28dp to move upwards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-28).dp)
                    .onSizeChanged { size ->
                        // Measure the name/price Column height (includes name, spacing, and price container)
                        namePriceHeight = with(density) { size.height.toDp() }
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Spacing between equipment image and name: 24dp
                Spacer(modifier = Modifier.height(24.dp))
                
                // Equipment name: 21sp, Bold
                Text(
                    text = equipmentName,
                    fontFamily = Exo2,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center
                )
                
                // Price container: 16dp below equipment name, above trim line
                // Contains: creditsicon (24dp width) + 8dp spacing + price label (24sp bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (canAfford) {
                        // When user has enough credits: Use sidedecoration2 JSON (same as total focus time counter)
                        // Left sidedecoration2: 24dp width, plays once then stays static
                        val leftDecorationComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.sidedecoration2))
                        LottieAnimation(
                            composition = leftDecorationComposition,
                            iterations = 1, // Play once then stays static
                            modifier = Modifier.width(24.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        // When user doesn't have enough credits: Use redrectangle JSON
                        // Left redrectangle: 24dp width (same size as sidedecoration2), plays once then stays static
                        val leftRedRectangleComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.redrectangle))
                        LottieAnimation(
                            composition = leftRedRectangleComposition,
                            iterations = 1, // Play once then stays static
                            modifier = Modifier.width(24.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    
                    // Spacing between left decoration and credits icon: 16dp
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Credits icon: 24dp width, maintaining aspect ratio
                    Image(
                        painter = painterResource(id = R.drawable.creditsicon),
                        contentDescription = "Credits",
                        modifier = Modifier.width(24.dp),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(
                            if (canAfford) Color(0xFFFFFFFF) else Color(0xFFF87F7F)
                        )
                    )
                    
                    // 8dp spacing between icon and label
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Price label: 24sp, bold
                    Text(
                        text = price.toString(),
                        fontFamily = Exo2,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (canAfford) Color(0xFFFFFFFF) else Color(0xFFF87F7F)
                    )
                    
                    // Spacing between price label and right decoration: 16dp
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    if (canAfford) {
                        // When user has enough credits: Use sidedecoration2 JSON (rotated 180 degrees)
                        // Right sidedecoration2: 24dp width, rotated 180 degrees, plays once then stays static
                        val rightDecorationComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.sidedecoration2))
                        LottieAnimation(
                            composition = rightDecorationComposition,
                            iterations = 1, // Play once then stays static
                            modifier = Modifier
                                .width(24.dp)
                                .graphicsLayer {
                                    rotationZ = 180f
                                },
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        // When user doesn't have enough credits: Use redrectangle JSON (rotated 180 degrees)
                        // Right redrectangle: 24dp width (same size as sidedecoration2), rotated 180 degrees, plays once then stays static
                        val rightRedRectangleComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.redrectangle))
                        LottieAnimation(
                            composition = rightRedRectangleComposition,
                            iterations = 1, // Play once then stays static
                            modifier = Modifier
                                .width(24.dp)
                                .graphicsLayer {
                                    rotationZ = 180f
                                },
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
            
            // Spacing from price container to divider/trim line: 24dp
            Spacer(modifier = Modifier.height(24.dp))
            }
        }
        
        // Clip boundary container: Positioned at the trim line, 24dp below price container
        // The divider/trim line is at the top of this container
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = clipBoundaryTop)
                .fillMaxWidth()
                .fillMaxHeight()
                .clipToBounds()
        ) {
            // Horizontal divider/trim line: Fixed at clip boundary, 32% opacity
            // Positioned at the very top of the clip boundary container (24dp below price container)
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .then(
                        if (isScrolling.value) {
                            Modifier
                        } else {
                            Modifier.padding(horizontal = 16.dp)
                        }
                    )
                    .height(1.dp)
                    .background(Color(0xFFFFFFFF).copy(alpha = 0.32f))
            )
            
            // Scrollable content column: Only content below the divider scrolls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .verticalScroll(scrollState)
                    .navigationBarsPadding()
                    .padding(bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Spacing from divider to content: 24dp
                Spacer(modifier = Modifier.height(24.dp))
                
                // Description content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // Heading: "Equipment effects"
                    Text(
                        text = "Equipment effects",
                        fontFamily = Exo2,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFFFFF)
                    )
                    
                    // Spacing between heading and description: 16dp (or adjust as needed)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Description text: Regular weight, 14sp
                    Text(
                        text = description,
                        fontFamily = Exo2,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        color = Color(0xFFFFFFFF),
                        lineHeight = 20.sp
                    )
                }
            }
        }
        
        // Bottom fixed container: Contains gradient and button container
        // Both stay fixed at bottom when scrolling
        // navigationBarsPadding() ensures button container sits right above navigation bar with 0 spacing
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding() // Positions content above navigation bar (0 spacing between them)
            ) {
                // Gradient overlay container: 48dp height, full width
                // Gradient from 100% opacity black at bottom to 0% opacity black at top
                // Positioned right above the credits label
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
                // Contains credits label and button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF000000)) // Black background at 100% opacity
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Credits available label: 16dp above button
                        // Format: "You have (amount) credits available"
                        // "You have" is regular, "(amount) credits available" is bold
                        // Left-aligned, respecting the 16dp horizontal padding
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                text = "You have ",
                                fontFamily = Exo2,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W400, // Regular
                                color = if (canAfford) Color(0xFFFFFFFF) else Color(0xFFF87F7F)
                            )
                            Text(
                                text = "$userCredits credits available",
                                fontFamily = Exo2,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (canAfford) Color(0xFFFFFFFF) else Color(0xFFF87F7F)
                            )
                        }
                        
                        // Purchase button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .border(
                                    width = 1.dp,
                                    color = if (canAfford) Color(0xFFFFFFFF) else Color(0xFFF87F7F),
                                    shape = RoundedCornerShape(40.dp)
                                )
                                .then(
                                    if (canAfford) {
                                        Modifier.clickable(onClick = onPurchaseClick)
                                    } else {
                                        Modifier // Disabled when can't afford
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (canAfford) {
                                    "Buy for $price credits"
                                } else {
                                    "NOT ENOUGH CREDITS"
                                },
                                fontFamily = Exo2,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W400,
                                color = if (canAfford) Color(0xFFFFFFFF) else Color(0xFFF87F7F),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.offset(y = (-1).dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
