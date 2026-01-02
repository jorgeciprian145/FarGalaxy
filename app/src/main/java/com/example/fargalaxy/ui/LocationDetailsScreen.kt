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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.material3.Text
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import android.graphics.RenderEffect as AndroidRenderEffect
import android.graphics.Shader
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.fargalaxy.R
import com.example.fargalaxy.model.Location
import com.example.fargalaxy.model.LocationClassification
import com.example.fargalaxy.model.LocationRarity

/**
 * Helper function to convert LocationRarity enum to display text.
 * Returns the rarity name in uppercase followed by " LOCATION".
 */
fun getLocationRarityDisplayText(rarity: LocationRarity): String {
    return "${rarity.name} LOCATION"
}

/**
 * Helper function to get the badge text color based on location rarity.
 * 
 * @param rarity The location's rarity
 * @return The badge text color
 */
private fun getLocationBadgeTextColor(rarity: LocationRarity): Color {
    return when (rarity) {
        LocationRarity.UNCOMMON -> Color(0xFF45E031) // #45E031 at 100% opacity
        LocationRarity.RARE -> Color(0xFF35D9F6) // #35D9F6 at 100% opacity
        LocationRarity.EPIC -> Color(0xFFE06BEA) // #E06BEA at 100% opacity
        LocationRarity.LEGENDARY -> Color(0xFFE7CC52) // #E7CC52 at 100% opacity
        LocationRarity.MYTHICAL -> Color(0xFFF6823A) // #F6823A at 100% opacity
        else -> Color(0xFFFFFFFF) // White (default for COMMON and others)
    }
}

/**
 * Helper function to get the badge container color based on location rarity.
 * 
 * @param rarity The location's rarity
 * @return The badge container color (with 16% opacity)
 */
private fun getLocationBadgeContainerColor(rarity: LocationRarity): Color {
    return when (rarity) {
        LocationRarity.UNCOMMON -> Color(0x2945E031) // #45E031 at 16% opacity (0x29 = ~16%)
        LocationRarity.RARE -> Color(0x2935D9F6) // #35D9F6 at 16% opacity (0x29 = ~16%)
        LocationRarity.EPIC -> Color(0x29E06BEA) // #E06BEA at 16% opacity (0x29 = ~16%)
        LocationRarity.LEGENDARY -> Color(0x29E7CC52) // #E7CC52 at 16% opacity (0x29 = ~16%)
        LocationRarity.MYTHICAL -> Color(0x29F6823A) // #F6823A at 16% opacity (0x29 = ~16%)
        else -> Color(0x29FFFFFF) // White at 16% opacity (default for COMMON and others)
    }
}

/**
 * Helper function to get the faction badge resource ID based on faction name.
 * 
 * @param faction The faction name
 * @return The drawable resource ID for the badge, or null if faction is "None"
 */
private fun getFactionBadgeResId(faction: String): Int? {
    return when (faction) {
        "Alliance of Starfaring Nations", "Alliance of Star Nations" -> R.drawable.alliancebadge
        "Independent Systems Federation" -> R.drawable.isfbadge
        "Navakeshi Star Armada" -> R.drawable.navakeshibadge
        else -> null // "None" or unknown faction
    }
}

/**
 * Helper function to get the faction logo resource ID based on faction name.
 * 
 * @param faction The faction name
 * @return The drawable resource ID for the logo, or null if faction is "None"
 */
private fun getFactionLogoResId(faction: String): Int? {
    return when (faction) {
        "Alliance of Starfaring Nations", "Alliance of Star Nations" -> R.drawable.alliancelogo
        "Independent Systems Federation" -> R.drawable.isflogo
        "Navakeshi Star Armada" -> R.drawable.navakeshilogo
        else -> null // "None" or unknown faction
    }
}

/**
 * Helper function to format population string based on location classification.
 * Adds "crew members" for ships, "personnel" for space stations, and leaves planets as-is.
 * 
 * @param population The population string
 * @param classification The location classification
 * @return The formatted population string
 */
private fun formatPopulation(population: String, classification: LocationClassification): String {
    return when (classification) {
        LocationClassification.CAPITAL_SHIP -> "$population crew members"
        LocationClassification.SPACE_STATION -> "$population personnel"
        else -> population // Planets remain as-is
    }
}

/**
 * LocationDetailsScreen composable - displays details about a location.
 * This screen opens when the user taps on a location row in the LocationsScreen.
 * 
 * @param location The location to display details for
 * @param onBackClick Callback when the back button is clicked
 * @param modifier Modifier for the screen
 */
@Composable
fun LocationDetailsScreen(
    location: Location,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Scroll state to track when content is being clipped
    val scrollState = rememberScrollState()
    
    // Calculate if content is being scrolled (scroll position > 0 means scrolling has started)
    val isScrolling = derivedStateOf {
        scrollState.value > 0
    }
    
    // Get density to convert dp to pixels
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp
    
    // Track the actual measured width of the image container to ensure full width
    var containerWidth by remember { mutableStateOf(0.dp) }
    
    // Track planet detail back JSON size to calculate clip boundary position
    var planetDetailBackSize by remember { mutableStateOf(IntSize.Zero) }
    
    // Track the actual height of the badge/name Column (without the 24dp spacer)
    var badgeNameHeight by remember { mutableStateOf(0.dp) }
    
    // Animated alpha for location image fade-in
    var imageVisible by remember { mutableStateOf(false) }
    val imageAlpha by animateFloatAsState(
        targetValue = if (imageVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000), // 1 second animation
        label = "locationImageFadeIn"
    )
    
    // Trigger fade-in animation when screen opens
    LaunchedEffect(Unit) {
        imageVisible = true
    }
    
    // Convert planet detail back height to dp for calculation
    val planetDetailBackHeightDp = with(density) {
        planetDetailBackSize.height.toDp()
    }
    
    // Calculate clip boundary position:
    // Header bottom: statusBarsPadding + 24dp + 51dp = statusBarsPadding + 75dp
    // Spacing from header to JSON: variable (0dp for full-width, 16dp for 90% width)
    // Planet detail back: variable height (planetDetailBackHeightDp)
    // Spacing from JSON to badge: 8dp
    // Badge/name Column: tracked via onSizeChanged (badgeNameHeight)
    // Spacing to divider: 24dp (added separately, not included in Column measurement)
    // Total offset from statusBarsPadding: 75dp + spacing + planetDetailBackHeightDp + 8dp + badgeNameHeight + 24dp
    val headerToJsonSpacing = if (location.isFullWidthImage) 0.dp else 16.dp
    val clipBoundaryTop = 75.dp + headerToJsonSpacing + planetDetailBackHeightDp + 8.dp + badgeNameHeight + 24.dp
    
    Box(modifier = modifier.fillMaxSize()) {
        // Background image: Same as ShipDetailsScreen
        Image(
            painter = painterResource(id = R.drawable.shipscreenbackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Location details galaxy background: Behind JSON but outside image container
        // 60% of screen height, maintains original aspect ratio, horizontally centered, top-aligned
        // 64% opacity for mythical locations, 32% opacity for others
        // Positioned at the absolute top of the screen (no padding)
        // Use mythicalgalaxybackground for mythical locations, locationdetailsgalaxy for others
        val galaxyBackgroundResId = if (location.rarity == LocationRarity.MYTHICAL) {
            R.drawable.mythicalgalaxybackground
        } else {
            R.drawable.locationdetailsgalaxy
        }
        val galaxyBackgroundOpacity = if (location.rarity == LocationRarity.MYTHICAL) {
            0.64f // 64% opacity for mythical locations
        } else {
            0.32f // 32% opacity for others
        }
        val imageHeight = screenHeightDp * 0.60f // 60% of screen height
        BoxWithConstraints(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(imageHeight)
        ) {
            Image(
                painter = painterResource(id = galaxyBackgroundResId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(galaxyBackgroundOpacity)
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.Crop // Fill entire container, maintain aspect ratio (excess trimmed from sides)
            )
        }
        
        // Top gradient overlay: Covers 35% of screen height, creating a fade effect at the top.
        // Gradient transitions from solid black at the top to transparent at the bottom.
        // Taller than ShipDetailsScreen (which is 30%)
        // Rendered after galaxy background to ensure it appears on top
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
        // Positioned at the same location as ShipDetailsScreen
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
            // Back button: Same size and style as ShipDetailsScreen
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
            
            // Title: "Location details"
            // Same font style as ShipDetailsScreen
            // Horizontally centered on the screen
            Text(
                text = "Location details",
                fontFamily = Exo2,
                fontSize = 18.sp,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        // Fixed content above clip boundary: Planet detail back, location image, badge, and name
        // Positioned to span edge-to-edge - statusBarsPadding only affects top, not horizontal width
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding() // Match header's statusBarsPadding for consistent spacing
                .padding(top = 75.dp) // Header bottom (24dp + 51dp = 75dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Spacing between header and planet detail back: 
                // 16px for 90% width images, 0px for full-width images (since JSON is scaled larger)
                if (!location.isFullWidthImage) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Planet detail back JSON: Full width, maintains aspect ratio
                // Positioned 8px below header
                // Ensure this container spans full width edge-to-edge - no padding constraints
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { size ->
                            planetDetailBackSize = size
                            containerWidth = with(density) { size.width.toDp() }
                        }
                ) {
                    // Use planetradarmythical for mythical locations, planetradarlegendary for legendary locations, planetdetailback for others
                    val jsonResourceId = when (location.rarity) {
                        LocationRarity.MYTHICAL -> R.raw.planetradarmythical
                        LocationRarity.LEGENDARY -> R.raw.planetradarlegendary
                        else -> R.raw.planetdetailback
                    }
                    val planetDetailBackComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(jsonResourceId))
                    LottieAnimation(
                        composition = planetDetailBackComposition,
                        iterations = LottieConstants.IterateForever,
                        speed = 0.65f, // Play at 0.65x speed
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (location.isFullWidthImage) {
                                    // For full-width images, make JSON 7% bigger using scale
                                    Modifier.scale(1.07f)
                                } else {
                                    Modifier
                                }
                            )
                            .align(Alignment.Center), // Center both vertically and horizontally
                        contentScale = ContentScale.Fit // Maintain aspect ratio
                    )
                    
                    // Faction logo: Only for Capital Ships, positioned behind location image
                    // 80% of container size, centered, 8% opacity, maintains aspect ratio
                    if (location.classification == LocationClassification.CAPITAL_SHIP) {
                        getFactionLogoResId(location.faction)?.let { logoResId ->
                            val containerSize = minOf(maxWidth, maxHeight)
                            Image(
                                painter = painterResource(id = logoResId),
                                contentDescription = "Faction logo",
                                modifier = Modifier
                                    .size(containerSize * 0.80f) // 80% of container size
                                    .alpha(0.08f) // 8% opacity
                                    .align(Alignment.Center), // Center both vertically and horizontally
                                contentScale = ContentScale.Fit // Maintain aspect ratio
                            )
                        }
                    }
                    
                    // Location image on top: 90% or full width based on location.isFullWidthImage, maintains aspect ratio
                    // Vertically and horizontally centered relative to the JSON
                    // Fades in over 1 second when screen opens
                    // For full width, use fillMaxWidth to ensure edge-to-edge display
                    if (location.isFullWidthImage) {
                        // Full width image - centered both vertically and horizontally, fills container width
                        Image(
                            painter = painterResource(id = location.detailImageResId),
                            contentDescription = location.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(imageAlpha) // Fade-in animation
                                .align(Alignment.Center), // Center both vertically and horizontally
                            contentScale = ContentScale.FillWidth // Fill width to reach edges
                        )
                    } else {
                        // 90% width image - centered both vertically and horizontally
                        Image(
                            painter = painterResource(id = location.detailImageResId),
                            contentDescription = location.name,
                            modifier = Modifier
                                .width(maxWidth * 0.90f) // 90% of measured container width
                                .alpha(imageAlpha) // Fade-in animation
                                .align(Alignment.Center), // Center both vertically and horizontally
                            contentScale = ContentScale.Fit // Maintain aspect ratio for 90% width images
                        )
                    }
                    
                    // Faction badge button: Positioned based on location classification
                    // Only shown if faction is not "None"
                    // Badge size: 119.7504dp (10% bigger than 108.864dp)
                    // Backdrop blur effect applied to area behind badge
                    getFactionBadgeResId(location.faction)?.let { badgeResId ->
                        if (location.classification == LocationClassification.CAPITAL_SHIP) {
                            // For Capital Ship: Horizontally aligned with location image, at the bottom edge of the image container, no padding
                            // Badge size: 83.82528dp (30% smaller than 119.7504dp for ships)
                            // Backdrop blur layer positioned behind badge area
                            Box(
                                modifier = Modifier
                                    .size(83.82528.dp)
                                    .align(Alignment.BottomCenter)
                                    .graphicsLayer {
                                        renderEffect = AndroidRenderEffect.createBlurEffect(
                                            35f, // Blur radius
                                            35f,
                                            Shader.TileMode.CLAMP
                                        ).asComposeRenderEffect()
                                    }
                                    .alpha(0.5f) // Semi-transparent to show blurred content behind
                            )
                            // Badge image on top
                            Image(
                                painter = painterResource(id = badgeResId),
                                contentDescription = "Faction badge",
                                modifier = Modifier
                                    .size(83.82528.dp)
                                    .align(Alignment.BottomCenter)
                                    .clickable(onClick = { /* TODO: Handle badge click */ }),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            // For Planet/Space Station: Vertically centered relative to location image, positioned to the right with 10% screen width padding
                            // Backdrop blur layer positioned behind badge area
                            Box(
                                modifier = Modifier
                                    .size(119.7504.dp)
                                    .align(Alignment.CenterEnd)
                                    .padding(end = maxWidth * 0.10f) // 10% of screen width padding from right
                                    .graphicsLayer {
                                        renderEffect = AndroidRenderEffect.createBlurEffect(
                                            35f, // Blur radius
                                            35f,
                                            Shader.TileMode.CLAMP
                                        ).asComposeRenderEffect()
                                    }
                                    .alpha(0.5f) // Semi-transparent to show blurred content behind
                            )
                            // Badge image on top
                            Image(
                                painter = painterResource(id = badgeResId),
                                contentDescription = "Faction badge",
                                modifier = Modifier
                                    .size(119.7504.dp)
                                    .align(Alignment.CenterEnd)
                                    .padding(end = maxWidth * 0.10f) // 10% of screen width padding from right
                                    .clickable(onClick = { /* TODO: Handle badge click */ }),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
                
                // Spacing from planet detail back to badge: 12dp (8dp + 4dp)
                Spacer(modifier = Modifier.height(12.dp))
                
                // Rarity badge and location name container
                // Positioned 16dp below planet detail back
                // Uses Column to stack badge and location name vertically
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .onSizeChanged { size ->
                            // Measure the badge/name Column height (without the 24dp Spacer)
                            badgeNameHeight = with(density) { size.height.toDp() }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Rarity badge: Container with rarity text (e.g., "COMMON LOCATION", "UNCOMMON LOCATION")
                    // Height hugs content, width adjusts to content, 4px padding inside (horizontal and vertical)
                    // Text: 10px, Medium weight, color based on rarity
                    // Container: color based on rarity with 16% opacity
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .wrapContentWidth()
                            .background(
                                color = getLocationBadgeContainerColor(location.rarity)
                            )
                            .padding(start = 4.dp, top = 2.dp, end = 4.dp, bottom = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getLocationRarityDisplayText(location.rarity),
                            fontFamily = Exo2,
                            fontSize = 10.sp,
                            lineHeight = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = getLocationBadgeTextColor(location.rarity),
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    // Location name: 21sp, Bold, positioned 4dp below badge
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = location.name,
                        fontFamily = Exo2,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center
                    )
                }
                
                // Spacing from location name to divider line: 24px
                // Moved outside inner Column to be a direct child of outer Column
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        
        // Clip boundary container: Positioned at the horizontal divider line
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = clipBoundaryTop)
                .fillMaxWidth()
                .fillMaxHeight()
                .clipToBounds()
        ) {
            // Horizontal divider line: Fixed at clip boundary, 32% opacity
            // Initially has 16px padding on sides, expands to full width when scrolling
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .then(
                        if (isScrolling.value) {
                            Modifier.fillMaxWidth()
                        } else {
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
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
                    .padding(bottom = 64.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Initial spacer: Push content down 24dp from divider line
                Spacer(modifier = Modifier.height(24.dp))
                
                // Two-row layout for location details
                // Different fields shown based on classification
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    when (location.classification) {
                        LocationClassification.PLANET -> {
                            // Row 1: Type and Description
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Type
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                    Text(
                                        text = "Type",
                                        fontFamily = Exo2,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFFFFF)
                                    )
                                    Text(
                                        text = location.type,
                                        fontFamily = Exo2,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W400,
                                        color = Color(0xFFFFFFFF),
                                        lineHeight = 18.sp
                                    )
                                    if (location.description.isNotEmpty()) {
                                        Text(
                                            text = location.description,
                                            fontFamily = Exo2,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.W400,
                                            color = Color(0xFFFFFFFF),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                                
                                // Population
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                    Text(
                                        text = "Population",
                                        fontFamily = Exo2,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFFFFF)
                                    )
                                    Text(
                                        text = formatPopulation(location.population, location.classification),
                                        fontFamily = Exo2,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W400,
                                        color = Color(0xFFFFFFFF),
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                            
                            // Row 2: Day duration and Diameter
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Day duration
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                    Text(
                                        text = "Day duration",
                                        fontFamily = Exo2,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFFFFF)
                                    )
                                    location.dayDuration?.let {
                                        Text(
                                            text = it,
                                            fontFamily = Exo2,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.W400,
                                            color = Color(0xFFFFFFFF),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                                
                                // Diameter
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                    Text(
                                        text = "Diameter",
                                        fontFamily = Exo2,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFFFFF)
                                    )
                                    location.diameter?.let {
                                        Text(
                                            text = it,
                                            fontFamily = Exo2,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.W400,
                                            color = Color(0xFFFFFFFF),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                            
                            // Row 3: Faction (full width)
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                Text(
                                    text = "Faction",
                                    fontFamily = Exo2,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFFFFF)
                                )
                                Text(
                                    text = location.faction,
                                    fontFamily = Exo2,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W400,
                                    color = Color(0xFFFFFFFF),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                        
                        LocationClassification.CAPITAL_SHIP -> {
                            // Row 1: Type and Weight
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Type
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                    Text(
                                        text = "Type",
                                        fontFamily = Exo2,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFFFFF)
                                    )
                                    Text(
                                        text = location.type,
                                        fontFamily = Exo2,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W400,
                                        color = Color(0xFFFFFFFF),
                                        lineHeight = 18.sp
                                    )
                                }
                                
                                // Population (crew)
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                    Text(
                                        text = "Population",
                                        fontFamily = Exo2,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFFFFF)
                                    )
                                    Text(
                                        text = formatPopulation(location.population, location.classification),
                                        fontFamily = Exo2,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W400,
                                        color = Color(0xFFFFFFFF),
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                            
                            // Row 2: Weight and Length
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Weight
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                    Text(
                                        text = "Weight",
                                        fontFamily = Exo2,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFFFFF)
                                    )
                                    location.weight?.let {
                                        Text(
                                            text = it,
                                            fontFamily = Exo2,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.W400,
                                            color = Color(0xFFFFFFFF),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                                
                                // Length
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                    Text(
                                        text = "Length",
                                        fontFamily = Exo2,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFFFFF)
                                    )
                                    location.length?.let {
                                        Text(
                                            text = it,
                                            fontFamily = Exo2,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.W400,
                                            color = Color(0xFFFFFFFF),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                            
                            // Row 3: Faction (full width)
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                Text(
                                    text = "Faction",
                                    fontFamily = Exo2,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFFFFF)
                                )
                                Text(
                                    text = location.faction,
                                    fontFamily = Exo2,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W400,
                                    color = Color(0xFFFFFFFF),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                        
                        LocationClassification.SPACE_STATION -> {
                            // Row 1: Type and Population
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Type
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                    Text(
                                        text = "Type",
                                        fontFamily = Exo2,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFFFFF)
                                    )
                                    Text(
                                        text = location.type,
                                        fontFamily = Exo2,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W400,
                                        color = Color(0xFFFFFFFF),
                                        lineHeight = 18.sp
                                    )
                                }
                                
                                // Population
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                    Text(
                                        text = "Population",
                                        fontFamily = Exo2,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFFFFF)
                                    )
                                    Text(
                                        text = formatPopulation(location.population, location.classification),
                                        fontFamily = Exo2,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W400,
                                        color = Color(0xFFFFFFFF),
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                            
                            // Row 2: Weight and Diameter
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Weight
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                    Text(
                                        text = "Weight",
                                        fontFamily = Exo2,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFFFFF)
                                    )
                                    location.weight?.let {
                                        Text(
                                            text = it,
                                            fontFamily = Exo2,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.W400,
                                            color = Color(0xFFFFFFFF),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                                
                                // Diameter
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                    Text(
                                        text = "Diameter",
                                        fontFamily = Exo2,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFFFFF)
                                    )
                                    location.diameter?.let {
                                        Text(
                                            text = it,
                                            fontFamily = Exo2,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.W400,
                                            color = Color(0xFFFFFFFF),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                            
                            // Row 3: Faction (full width)
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                Text(
                                    text = "Faction",
                                    fontFamily = Exo2,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFFFFF)
                                )
                                Text(
                                    text = location.faction,
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
                
                // Location's lore: Full width, 24px spacing from above
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Text(
                        text = "Location's lore",
                        fontFamily = Exo2,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFFFFF)
                    )
                    Text(
                        text = location.lore,
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
        // Gradient from 0% opacity at top to 100% opacity black at bottom
        // NO button (unlike ShipDetailsScreen)
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

