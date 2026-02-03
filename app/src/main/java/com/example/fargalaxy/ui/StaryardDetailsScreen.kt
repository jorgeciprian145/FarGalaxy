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
import com.example.fargalaxy.model.Ship
import com.example.fargalaxy.model.ShipRarity
import com.example.fargalaxy.ui.ProgressBar

/**
 * Helper function to get the manufacturer logo drawable resource ID based on manufacturer name.
 * 
 * @param manufacturer The manufacturer name
 * @return The drawable resource ID for the manufacturer logo
 */
private fun getManufacturerLogoResId(manufacturer: String): Int {
    return when (manufacturer.lowercase()) {
        "soren shipworks", "soren" -> R.drawable.sorenlogo
        "valketh industries", "valketh" -> R.drawable.valkethlogo
        "marakeshi space technologies", "marakeshi" -> R.drawable.marakeshilogo
        "karnyx armory division", "karnyx" -> R.drawable.karnyxlogo
        "tiona", "tiona spaceworks", "tiona starworks" -> R.drawable.tionalogo
        "aurellian atelier works", "aurellian", "aurelian atelier works", "aurelian" -> R.drawable.aurelianlogo
        "kel'varra star systems", "kel'varra", "kelvarra star systems", "kelvarra" -> R.drawable.kelvarralogo
        "eternal infinitum (according to what was deciphered)", "eternal infinitum", "infinitum" -> R.drawable.infinitumlogo
        else -> R.drawable.valkethlogo // Fallback to Valketh logo
    }
}

/**
 * Helper function to get the gradient color based on ship rarity.
 * 
 * @param rarity The ship's rarity
 * @return The gradient color (with 32% opacity for top of gradient)
 */
private fun getGradientColor(rarity: ShipRarity): Color {
    return when (rarity) {
        ShipRarity.UNCOMMON -> Color(0x5245E031) // #45E031 at 32% opacity (0x52 = ~32%)
        ShipRarity.RARE -> Color(0x5235D9F6) // #35D9F6 at 32% opacity (0x52 = ~32%)
        ShipRarity.EPIC -> Color(0x52E06BEA) // #E06BEA at 32% opacity (0x52 = ~32%)
        ShipRarity.LEGENDARY -> Color(0x52E7CC52) // #E7CC52 at 32% opacity (0x52 = ~32%)
        ShipRarity.MYTHICAL -> Color(0x52F6823A) // #F6823A at 32% opacity (0x52 = ~32%)
        else -> Color(0x52FFFFFF) // White at 32% opacity (default for COMMON and others)
    }
}

/**
 * Helper function to get the badge text color based on ship rarity.
 * 
 * @param rarity The ship's rarity
 * @return The badge text color
 */
private fun getBadgeTextColor(rarity: ShipRarity): Color {
    return when (rarity) {
        ShipRarity.UNCOMMON -> Color(0xFF45E031) // #45E031 at 100% opacity
        ShipRarity.RARE -> Color(0xFF35D9F6) // #35D9F6 at 100% opacity
        ShipRarity.EPIC -> Color(0xFFE06BEA) // #E06BEA at 100% opacity
        ShipRarity.LEGENDARY -> Color(0xFFE7CC52) // #E7CC52 at 100% opacity
        ShipRarity.MYTHICAL -> Color(0xFFF6823A) // #F6823A at 100% opacity
        else -> Color(0xFFFFFFFF) // White (default for COMMON and others)
    }
}

/**
 * Helper function to get the badge container color based on ship rarity.
 * 
 * @param rarity The ship's rarity
 * @return The badge container color (with 16% opacity)
 */
private fun getBadgeContainerColor(rarity: ShipRarity): Color {
    return when (rarity) {
        ShipRarity.UNCOMMON -> Color(0x2945E031) // #45E031 at 16% opacity (0x29 = ~16%)
        ShipRarity.RARE -> Color(0x2935D9F6) // #35D9F6 at 16% opacity (0x29 = ~16%)
        ShipRarity.EPIC -> Color(0x29E06BEA) // #E06BEA at 16% opacity (0x29 = ~16%)
        ShipRarity.LEGENDARY -> Color(0x29E7CC52) // #E7CC52 at 16% opacity (0x29 = ~16%)
        ShipRarity.MYTHICAL -> Color(0x29F6823A) // #F6823A at 16% opacity (0x29 = ~16%)
        else -> Color(0x29FFFFFF) // White at 16% opacity (default for COMMON and others)
    }
}

/**
 * Helper function to format ship name for display, with line break for ship9 on smaller screens.
 * 
 * @param shipId The ship's ID
 * @param shipName The ship's full name
 * @param screenWidthDp The screen width in dp
 * @return Pair of strings (first line, second line) or null if single line
 */
private fun formatShipNameForDisplay(shipId: String, shipName: String, screenWidthDp: Float): Pair<String?, String> {
    // For ship9 on smaller screens (< 400dp), split the name after "Compact"
    if (shipId == "model3_tortoise_ccp" && screenWidthDp < 400f) {
        // Split "Model 3 "Tortoise" Compact cargo platform" after "Compact "
        // Result: Line 1: "Model 3 "Tortoise" Compact", Line 2: "cargo platform"
        val splitPoint = shipName.indexOf("Compact ")
        if (splitPoint > 0) {
            val afterCompact = splitPoint + "Compact ".length
            return Pair(
                shipName.substring(0, afterCompact).trim(), // "Model 3 "Tortoise" Compact"
                shipName.substring(afterCompact).trim()     // "cargo platform"
            )
        }
    }
    // Return single line for other cases
    return Pair(null, shipName)
}

/**
 * StaryardDetailsScreen composable - displays details about a ship in the staryard.
 * This screen opens when the user taps on a ship in the StaryardScreen.
 * 
 * @param ship The ship to display details for
 * @param price The price of the ship in credits
 * @param userCredits The user's current credits
 * @param onBackClick Callback when the back button is clicked
 * @param onPurchaseClick Callback when the purchase button is clicked
 * @param modifier Modifier for the screen
 */
@Composable
fun StaryardDetailsScreen(
    ship: Ship,
    price: Int,
    onBackClick: () -> Unit = {},
    onPurchaseClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Read credits from global repository
    val userCredits = com.example.fargalaxy.data.UserDataRepository.userCredits
    
    // Determine if user can afford the ship
    val canAfford = price <= userCredits
    // Scroll state to track when content is being clipped
    val scrollState = rememberScrollState()
    
    // Calculate if content is being scrolled (scroll position > 0 means scrolling has started)
    val isScrolling = derivedStateOf {
        scrollState.value > 0
    }
    
    // Get density to convert dp to pixels
    val density = LocalDensity.current
    
    // Track ship image size to calculate clip boundary position
    var shipImageSize by remember { mutableStateOf(IntSize.Zero) }
    
    // Track the actual height of the badge/name/price Column (includes badge, name, spacing, and price container)
    var badgeNamePriceHeight by remember { mutableStateOf(0.dp) }
    
    // Convert ship image height to dp for calculation
    val shipImageHeightDp = with(density) {
        shipImageSize.height.toDp()
    }
    
    // Calculate clip boundary position:
    // Fixed content Box starts at: statusBarsPadding + 64.dp
    // Ship image: variable height (shipImageHeightDp)
    // Badge/name/price Column: tracked via onSizeChanged (badgeNamePriceHeight)
    // Column has -28dp offset, so visual position is adjusted
    // Spacing to divider/trim line: 24dp (divider should be 24dp below price container)
    // Additional offset: 40dp for most ships, but B15 specter needs 12dp less (28dp adjustment)
    // Total offset from statusBarsPadding: 64.dp + shipImageHeightDp + badgeNamePriceHeight + 24.dp + adjustment
    val additionalOffset = if (ship.id == "b15_specter") 12.dp else 40.dp
    val clipBoundaryTop = 64.dp + shipImageHeightDp + badgeNamePriceHeight + 24.dp + additionalOffset
    
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
        // Gradient transitions from 32% opacity color (based on rarity) at the top to transparent at the bottom.
        // Positioned behind all elements but on top of the background image.
        // Color is dynamic based on ship.rarity (UNCOMMON uses #45E031, others use white)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.30f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            getGradientColor(ship.rarity),
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
            
            // Title: "Ship details"
            // Same font style as "Your career" in CareerScreen
            // Horizontally centered on the screen
            Text(
                text = "Ship details",
                fontFamily = Exo2,
                fontSize = 18.sp,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        // Fixed content above clip boundary: Ship image, badge, and name
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
            // Ship image container: Multi-layered component
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
                    // Logo selection is dynamic based on ship.manufacturer
                    Image(
                        painter = painterResource(id = getManufacturerLogoResId(ship.manufacturer)),
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
                    
                    // Lightning effect layer: Only for ship14 (Force of nature)
                    // Same dimensions, positioning, and scaling behavior as ship render image
                    // Positioned on top of the ship image
                    if (ship.id == "force_of_nature") {
                        val lightningComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ship14lightningeffect))
                        LottieAnimation(
                            composition = lightningComposition,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            contentScale = ContentScale.Fit
                        )
                    }
                    
                    // Solar flare effect layer: Only for ship15 (Dying Star)
                    // Same dimensions, positioning, and scaling behavior as ship render image
                    // Positioned on top of the ship image
                    if (ship.id == "dying_star") {
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
            
            // Rarity badge and ship name container
            // Positioned below the ship image container
            // Uses Column to stack badge and ship name vertically
            // Applied vertical offset of -28dp to move upwards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-28).dp)
                    .onSizeChanged { size ->
                        // Measure the badge/name/price Column height (includes badge, name, spacing, and price container)
                        badgeNamePriceHeight = with(density) { size.height.toDp() }
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Spacing between ship image and badge: 8dp
                Spacer(modifier = Modifier.height(8.dp))
                
                // Rarity badge: Container with rarity text (e.g., "COMMON SHIP", "UNCOMMON SHIP")
                // Height hugs content, width adjusts to content, 4px padding inside (horizontal and vertical)
                // Text: 10px, Medium weight, color based on rarity
                // Container: color based on rarity with 16% opacity
                Box(
                    modifier = Modifier
                        .wrapContentHeight()
                        .wrapContentWidth()
                        .background(
                            color = getBadgeContainerColor(ship.rarity)
                        )
                        .padding(start = 4.dp, top = 2.dp, end = 4.dp, bottom = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getRarityDisplayText(ship.rarity),
                        fontFamily = Exo2,
                        fontSize = 10.sp,
                        lineHeight = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = getBadgeTextColor(ship.rarity),
                        textAlign = TextAlign.Center
                    )
                }
                
                // Ship name: 21sp, Bold, positioned 4dp below badge
                // For ship9 on smaller screens, display with line break
                Spacer(modifier = Modifier.height(4.dp))
                
                // Get screen width for responsive formatting
                val configuration = LocalConfiguration.current
                val screenWidthDp = configuration.screenWidthDp.toFloat()
                val (firstLine, secondLine) = formatShipNameForDisplay(ship.id, ship.name, screenWidthDp)
                
                // Display ship name with conditional line break for smaller screens
                if (firstLine != null) {
                    // Two-line display for ship9 on smaller screens
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = firstLine,
                            fontFamily = Exo2,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = secondLine,
                            fontFamily = Exo2,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Single-line display for normal cases
                    Text(
                        text = secondLine,
                        fontFamily = Exo2,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center
                    )
                }
                
                // Price container: 16dp below ship name, above trim line
                // Contains: creditsicon (24dp width) + 8dp spacing + price label (32sp bold)
                // Container: 16dp horizontal padding, 8dp vertical padding, 1dp white stroke at 32% opacity
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
        
        // Clip boundary container: Positioned at the trim line, 24dp below ship name
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
            // Positioned at the very top of the clip boundary container (24dp below ship name)
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
                // Spacing from divider to SPECS content: 24dp (removed toggle, so content starts here)
                Spacer(modifier = Modifier.height(24.dp))
                
                // SPECS content (no toggle, no DETAILS tab)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // Spacing from divider to badge: 4dp (was spacing from toggle to badge)
                    Spacer(modifier = Modifier.height(4.dp))

                    // Badge: "Requires: Space license level X"
                    // Container: 32dp tall, 24dp internal side padding, no stroke
                    // Contains: Left rectangle JSON, label with bold "Space license level X", right rectangle JSON (rotated 180°)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .padding(horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // TODO: Replace with dynamic required level from ship model
                        val requiredLevel = when (ship.id) {
                            "b14_phantom" -> 1
                            "type45c_shooting_star" -> 2
                            "navakeshi_star_pouncer" -> 2
                            "a300_albatross" -> 3
                            "p7h_skyblazer" -> 5
                            "b7f_starforce" -> 4
                            "navakeshi_star_crusher" -> 5
                            "asn_ag94_centurion" -> 3
                            "b15_specter" -> 6
                            "n6_98_melina" -> 6
                            "model3_tortoise_ccp" -> 8
                            "h98_valkyrie" -> 9
                            "navakeshi_star_ravager" -> 9
                            "isc_m450_phoenix" -> 9
                            "silver_lightning" -> 12
                            "vulcani_legenda_f1" -> 12
                            "force_of_nature" -> 15
                            "dying_star" -> 15
                            "ship22" -> 15
                            "ship23" -> 8
                            else -> 1 // Default placeholder
                        }

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

                            // Label: "Requires: " (regular) + "Space license level X" (bold)
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
                                    text = "Space license level $requiredLevel",
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
                        // TODO: Replace with dynamic values from ship model
                        val accelerationValue = when (ship.id) {
                            "b14_phantom" -> 24
                            "type45c_shooting_star" -> 35
                            "navakeshi_star_pouncer" -> 32
                            "a300_albatross" -> 28
                            "p7h_skyblazer" -> 32
                            "b7f_starforce" -> 38
                            "navakeshi_star_crusher" -> 35
                            "asn_ag94_centurion" -> 35
                            "b15_specter" -> 32
                            "n6_98_melina" -> 25
                            "model3_tortoise_ccp" -> 14
                            "h98_valkyrie" -> 38
                            "navakeshi_star_ravager" -> 40
                            "isc_m450_phoenix" -> 40
                            "silver_lightning" -> 62
                            "vulcani_legenda_f1" -> 68
                            "force_of_nature" -> 80
                            "dying_star" -> 68
                            "ship22" -> 61
                            "ship23" -> 16
                            else -> 0
                        }
                        val speedValue = when (ship.id) {
                            "b14_phantom" -> 25
                            "type45c_shooting_star" -> 32
                            "navakeshi_star_pouncer" -> 30
                            "a300_albatross" -> 28
                            "p7h_skyblazer" -> 32
                            "b7f_starforce" -> 38
                            "navakeshi_star_crusher" -> 30
                            "asn_ag94_centurion" -> 32
                            "b15_specter" -> 30
                            "n6_98_melina" -> 34
                            "model3_tortoise_ccp" -> 16
                            "h98_valkyrie" -> 34
                            "navakeshi_star_ravager" -> 40
                            "isc_m450_phoenix" -> 36
                            "silver_lightning" -> 60
                            "vulcani_legenda_f1" -> 72
                            "force_of_nature" -> 72
                            "dying_star" -> 64
                            "ship22" -> 61
                            "ship23" -> 20
                            else -> 0
                        }
                        val stabilityValue = when (ship.id) {
                            "b14_phantom" -> 29
                            "type45c_shooting_star" -> 16
                            "navakeshi_star_pouncer" -> 18
                            "a300_albatross" -> 38
                            "p7h_skyblazer" -> 38
                            "b7f_starforce" -> 19
                            "navakeshi_star_crusher" -> 24
                            "asn_ag94_centurion" -> 30
                            "b15_specter" -> 36
                            "n6_98_melina" -> 42
                            "model3_tortoise_ccp" -> 74
                            "h98_valkyrie" -> 49
                            "navakeshi_star_ravager" -> 30
                            "isc_m450_phoenix" -> 32
                            "silver_lightning" -> 57
                            "vulcani_legenda_f1" -> 18
                            "force_of_nature" -> 45
                            "dying_star" -> 60
                            "ship22" -> 68
                            "ship23" -> 69
                            else -> 0
                        }
                        
                        // Row 1: Acceleration
                        AttributeRow(
                            title = "Acceleration",
                            value = accelerationValue,
                            progress = accelerationValue / 100f // Convert 0-100 to 0-1 for progress bar
                        )
                        
                        // Row 2: Speed
                        AttributeRow(
                            title = "Speed",
                            value = speedValue,
                            progress = speedValue / 100f
                        )
                        
                        // Row 3: Stability
                        AttributeRow(
                            title = "Stability",
                            value = stabilityValue,
                            progress = stabilityValue / 100f
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
                                    "Buy ship for $price credits"
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
