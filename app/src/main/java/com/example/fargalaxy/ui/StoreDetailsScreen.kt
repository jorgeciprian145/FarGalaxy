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
import com.example.fargalaxy.ui.ProgressBar

/**
 * Helper function to get the gradient color for mythical items.
 * 
 * @return The gradient color (with 32% opacity for top of gradient)
 */
private fun getMythicalGradientColor(): Color {
    return Color(0x52F6823A) // #F6823A at 32% opacity (0x52 = ~32%)
}

/**
 * Helper function to get the badge text color for mythical items.
 * 
 * @return The badge text color
 */
private fun getMythicalBadgeTextColor(): Color {
    return Color(0xFFF6823A) // #F6823A at 100% opacity
}

/**
 * Helper function to get the badge container color for mythical items.
 * 
 * @return The badge container color (with 16% opacity)
 */
private fun getMythicalBadgeContainerColor(): Color {
    return Color(0x29F6823A) // #F6823A at 16% opacity (0x29 = ~16%)
}

/**
 * StoreDetailsScreen composable - displays details about a store item.
 * This screen opens when the user taps on a store card in the StoreScreen.
 * 
 * @param itemName The name of the store item
 * @param itemImageResId The drawable resource ID for the item image
 * @param price The price of the item (can be in credits or dollars)
 * @param priceType The type of price ("credits" or "dollars")
 * Note: userCredits is read from UserDataRepository
 * @param description The description text for the item
 * @param onBackClick Callback when the back button is clicked
 * @param onPurchaseClick Callback when the purchase button is clicked
 * @param modifier Modifier for the screen
 */
@Composable
fun StoreDetailsScreen(
    itemName: String,
    itemImageResId: Int,
    price: String,
    priceType: String = "credits", // "credits" or "dollars"
    description: String = "Placeholder description text. This is where the item description will be displayed.",
    onBackClick: () -> Unit = {},
    onPurchaseClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Read credits from global repository
    val userCredits = com.example.fargalaxy.data.UserDataRepository.userCredits
    // Determine if user can afford the item (only for credit-priced items)
    val canAfford = if (priceType == "credits") {
        price.toIntOrNull()?.let { it <= userCredits } ?: false
    } else {
        true // Dollar items are always "affordable" (no credit check)
    }
    
    // Scroll state to track when content is being clipped
    val scrollState = rememberScrollState()
    
    // Calculate if content is being scrolled (scroll position > 0 means scrolling has started)
    val isScrolling = derivedStateOf {
        scrollState.value > 0
    }
    
    // Get density to convert dp to pixels
    val density = LocalDensity.current
    
    // Track item image size to calculate clip boundary position
    var itemImageSize by remember { mutableStateOf(IntSize.Zero) }
    
    // Track the actual height of the name/price Column (includes name, spacing, and price container)
    var namePriceHeight by remember { mutableStateOf(0.dp) }
    
    // Convert item image height to dp for calculation
    val itemImageHeightDp = with(density) {
        itemImageSize.height.toDp()
    }
    
    // Calculate clip boundary position:
    // Fixed content Box starts at: statusBarsPadding + 64.dp
    // Item image: variable height (itemImageHeightDp)
    // Name/price Column: tracked via onSizeChanged (namePriceHeight)
    // Column has -28dp offset, so visual position is adjusted
    // Spacing to divider/trim line: 24dp (divider should be 24dp below price container)
    // For ship (Dying Star): Move divider down by 40dp
    // For other items (crates): Move divider down by 40dp - 32dp = 8dp (move up by 32dp relative to ship)
    val isShip = itemName == "Dying Star"
    val dividerOffset = if (isShip) {
        40.dp // Ship: keep current position
    } else {
        8.dp // Other items: move up by 32dp (40dp - 32dp = 8dp)
    }
    val clipBoundaryTop = 64.dp + itemImageHeightDp + namePriceHeight + 24.dp + dividerOffset
    
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
        // Gradient transitions from 32% opacity color at the top to transparent at the bottom.
        // For "Dying Star" (mythical ship), use mythical color (#F6823A); otherwise use white
        val gradientColor = if (itemName == "Dying Star") {
            getMythicalGradientColor()
        } else {
            Color(0x52FFFFFF) // White at 32% opacity
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.30f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            gradientColor,
                            Color(0x00000000)
                        )
                    )
                )
        )
        
        // Top controls area: Title and back button
        // Positioned at the same location as the top controls in EquipmentDetailsScreen
        // (statusBarsPadding + 24.dp from top, 51.dp height)
        // These elements remain static and above the clipping line
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 24.dp)
                .fillMaxWidth()
                .height(51.dp)
        ) {
            // Back button: Same size and style as EquipmentDetailsScreen
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
            
            // Title: "Store details"
            // Same font style as "Equipment details" in EquipmentDetailsScreen
            // Horizontally centered on the screen
            Text(
                text = "Store details",
                fontFamily = Exo2,
                fontSize = 18.sp,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        // Fixed content above clip boundary: Item image, name, and price
        // For now, no image is displayed - placeholder space is reserved
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
            // Item image container: Multi-layered component
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
                    // Get screen width to calculate logo size (for mythical ships)
                    val configuration = LocalConfiguration.current
                    val screenWidthDp = configuration.screenWidthDp.dp
                    val logoWidth = screenWidthDp * 0.48f // 48% of screen width
                    
                    // Backship animation layer: Lottie animation positioned behind the item image.
                    // Vertically and horizontally centered relative to the item image.
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
                    
                    // Logo layer - behind item image but in front of backship animation (only for "Dying Star")
                    // 48% of screen width, vertically and horizontally aligned with item
                    if (itemName == "Dying Star") {
                        Image(
                            painter = painterResource(id = R.drawable.infinitumlogo),
                            contentDescription = "Manufacturer logo",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .width(logoWidth),
                            contentScale = ContentScale.Fit
                        )
                    }
                    
                    // Item image (frontmost) - on top of everything
                    // Fills available width (already constrained by 16dp padding), maintains aspect ratio
                    // Height is calculated automatically based on aspect ratio
                    // For Interstellar credits pack, use creditsrender; for crates, use their specific render images; otherwise use the passed image
                    val imageResId = when (itemName) {
                        "Interstellar credits pack" -> R.drawable.creditsrender
                        "Elite Spacer's crate" -> R.drawable.elitecraterender
                        "Advanced Spacer's crate" -> R.drawable.advancedcraterender
                        "Standard Spacer's crate" -> R.drawable.standardcraterender
                        else -> itemImageResId
                    }
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = itemName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .onSizeChanged { size ->
                                itemImageSize = size
                            },
                        contentScale = ContentScale.Fit
                    )
                    
                    // Solar flare effect layer: Only for "Dying Star" (ship15)
                    // Same dimensions, positioning, and scaling behavior as item render image
                    // Positioned on top of the item image
                    if (itemName == "Dying Star") {
                        val solarFlareComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.shipsolarflare))
                        LottieAnimation(
                            composition = solarFlareComposition,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
            
            // Name and price container
            // Positioned below the item image container
            // Uses Column to stack name and price vertically
            // Applied vertical offset of -28dp to move upwards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-28).dp)
                    .onSizeChanged { size ->
                        // Measure the name/price Column height (includes badge, name, spacing, and price container)
                        namePriceHeight = with(density) { size.height.toDp() }
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Spacing between item image and badge/name: 8dp
                Spacer(modifier = Modifier.height(8.dp))
                
                // Rarity badge: Only for "Dying Star" (mythical ship)
                // Container with rarity text "MYTHICAL SHIP"
                // Height hugs content, width adjusts to content, 4px padding inside (horizontal and vertical)
                // Text: 10px, Medium weight, color based on rarity
                // Container: color based on rarity with 16% opacity
                if (itemName == "Dying Star") {
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .wrapContentWidth()
                            .background(
                                color = getMythicalBadgeContainerColor()
                            )
                            .padding(start = 4.dp, top = 2.dp, end = 4.dp, bottom = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "MYTHICAL SHIP",
                            fontFamily = Exo2,
                            fontSize = 10.sp,
                            lineHeight = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = getMythicalBadgeTextColor(),
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    // Spacing between badge and item name: 4dp
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                // Item name: 21sp, Bold
                Text(
                    text = itemName,
                    fontFamily = Exo2,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center
                )
                
                // Price container: 16dp below item name, above trim line
                // Format depends on priceType: credits icon + value, or just dollar amount
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (priceType == "credits") {
                        // Credits format: decoration + icon + price + decoration
                        // Left sidedecoration2: 24dp width, plays once then stays static
                        val leftDecorationComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.sidedecoration2))
                        LottieAnimation(
                            composition = leftDecorationComposition,
                            iterations = 1, // Play once then stays static
                            modifier = Modifier.width(24.dp),
                            contentScale = ContentScale.Fit
                        )
                        
                        // Spacing between left decoration and credits icon: 16dp
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // Credits icon: 24dp width, maintaining aspect ratio
                        Image(
                            painter = painterResource(id = R.drawable.creditsicon),
                            contentDescription = "Credits",
                            modifier = Modifier.width(24.dp),
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(Color(0xFFFFFFFF))
                        )
                        
                        // 8dp spacing between icon and label
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Price label: 24sp, bold
                        Text(
                            text = price,
                            fontFamily = Exo2,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFFFFF)
                        )
                        
                        // Spacing between price label and right decoration: 16dp
                        Spacer(modifier = Modifier.width(16.dp))
                        
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
                        // Dollar format: decoration + price + decoration (no icon)
                        // Left sidedecoration2: 24dp width, plays once then stays static
                        val leftDecorationComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.sidedecoration2))
                        LottieAnimation(
                            composition = leftDecorationComposition,
                            iterations = 1, // Play once then stays static
                            modifier = Modifier.width(24.dp),
                            contentScale = ContentScale.Fit
                        )
                        
                        // Spacing between left decoration and price: 16dp
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // Price label: 24sp, bold
                        Text(
                            text = price,
                            fontFamily = Exo2,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFFFFF)
                        )
                        
                        // Spacing between price label and right decoration: 16dp
                        Spacer(modifier = Modifier.width(16.dp))
                        
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
                
                // Content: Attributes for "Dying Star" ship, description for other items
                if (itemName == "Dying Star") {
                    // SPECS content (attributes) for Dying Star ship
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        // Spacing from divider to badge: 4dp
                        Spacer(modifier = Modifier.height(4.dp))

                        // Badge: "Requires: Space license level 15"
                        // Container: 32dp tall, 24dp internal side padding, no stroke
                        // Contains: Left rectangle JSON, label with bold "Space license level 15", right rectangle JSON (rotated 180°)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .padding(horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left rectangle JSON: 8dp size, plays once then stays static
                                val leftRectangleComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.rectangle))
                                LottieAnimation(
                                    composition = leftRectangleComposition,
                                    iterations = 1, // Play once then stay static
                                    modifier = Modifier.size(8.dp),
                                    contentScale = ContentScale.Fit
                                )

                                // Spacing between left rectangle and label: 16dp
                                Spacer(modifier = Modifier.width(16.dp))

                                // Label: "Requires: " (regular) + "Space license level 15" (bold)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Requires: ",
                                        fontFamily = Exo2,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W400,
                                        color = Color(0xFFFFFFFF),
                                        modifier = Modifier.offset(y = (-1).dp)
                                    )
                                    Text(
                                        text = "Space license level 15",
                                        fontFamily = Exo2,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFFFFF),
                                        modifier = Modifier.offset(y = (-1).dp)
                                    )
                                }

                                // Spacing between label and right rectangle: 16dp
                                Spacer(modifier = Modifier.width(16.dp))

                                // Right rectangle JSON: 8dp size, rotated 180 degrees, plays once then stays static
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
                        
                        // Spacing between badge and first attribute row: 16dp
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Attribute rows: Acceleration, Speed, Stability
                        // Each row spaced 16dp apart
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Row 1: Acceleration
                            AttributeRow(
                                title = "Acceleration",
                                value = 68,
                                progress = 68 / 100f
                            )
                            
                            // Row 2: Speed
                            AttributeRow(
                                title = "Speed",
                                value = 64,
                                progress = 64 / 100f
                            )
                            
                            // Row 3: Stability
                            AttributeRow(
                                title = "Stability",
                                value = 60,
                                progress = 60 / 100f
                            )
                        }
                    }
                } else {
                    // Description content for other items
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        // Heading: "Item description"
                        Text(
                            text = "Item description",
                            fontFamily = Exo2,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFFFFF)
                        )
                        
                        // Spacing between heading and description: 16dp
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
                
                // Extra spacing at bottom: 40dp to allow more scrolling
                Spacer(modifier = Modifier.height(40.dp))
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
                        // Hidden for Interstellar credits pack
                        if (itemName != "Interstellar credits pack") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = "You have ",
                                    fontFamily = Exo2,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.W400, // Regular
                                    color = Color(0xFFFFFFFF)
                                )
                                Text(
                                    text = "$userCredits credits available",
                                    fontFamily = Exo2,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFFFFF)
                                )
                            }
                        }
                        
                        // Purchase button
                        // For credit items: show red when can't afford, for dollar items: always white
                        val buttonColor = if (priceType == "credits" && !canAfford) {
                            Color(0xFFF87F7F) // Red when can't afford credits
                        } else {
                            Color(0xFFFFFFFF) // White otherwise
                        }
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .border(
                                    width = 1.dp,
                                    color = buttonColor,
                                    shape = RoundedCornerShape(40.dp)
                                )
                                .then(
                                    if (priceType == "credits" && !canAfford) {
                                        Modifier // Disabled when can't afford credits
                                    } else {
                                        Modifier.clickable(onClick = onPurchaseClick)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (itemName == "Dying Star") {
                                    // For Dying Star ship: "Buy ship for "$VALUE""
                                    "Buy ship for \"$price\""
                                } else if (priceType == "credits") {
                                    if (canAfford) {
                                        "BUY FOR $price CREDITS"
                                    } else {
                                        "NOT ENOUGH CREDITS"
                                    }
                                } else {
                                    "BUY PACK FOR $price"
                                },
                                fontFamily = Exo2,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W400,
                                color = buttonColor,
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

/**
 * AttributeRow composable - displays a single attribute row with title, value, and progress bar.
 * 
 * Structure:
 * - Top container: Label (left, 14sp bold) and value (right, 14sp regular)
 * - Bottom container: Progress bar (stretches full width, filled portion represents value/100)
 * - Spacing between containers: 4dp
 * 
 * @param title The attribute title (e.g., "Acceleration", "Speed", "Stability")
 * @param value The attribute value (0-100)
 * @param progress Progress value between 0f and 1f (value / 100f)
 */
@Composable
private fun AttributeRow(
    title: String,
    value: Int,
    progress: Float
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Top container: Label and value
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Label (left): 14sp, bold
            Text(
                text = title,
                fontFamily = Exo2,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFFFFF)
            )
            
            // Value (right): 14sp, regular
            Text(
                text = "$value",
                fontFamily = Exo2,
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                color = Color(0xFFFFFFFF)
            )
        }
        
        // Bottom container: Progress bar
        // Progress bar stretches full width, filled portion represents value/100
        ProgressBar(
            progress = progress.coerceIn(0f, 1f),
            backgroundColor = Color(0x33FFFFFF), // White at 20% opacity
            foregroundColor = Color(0xFFFFFFFF), // White
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
    }
}
