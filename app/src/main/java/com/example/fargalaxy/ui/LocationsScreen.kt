package com.example.fargalaxy.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.fargalaxy.R
import com.example.fargalaxy.data.LocationRepository
import com.example.fargalaxy.data.UserDataRepository
import kotlinx.coroutines.delay
import com.example.fargalaxy.model.Location
import com.example.fargalaxy.model.LocationRarity

/**
 * LocationsScreen composable - displays the location selection screen where users can view discovered locations.
 * 
 * Layout structure:
 * - Background: shipscreenbackground image (same as ShipSelectionScreen)
 * - Top gradient: 20% height, black to transparent (same as ShipSelectionScreen)
 * - Bottom gradient: 25% height, transparent to black (same as ShipSelectionScreen)
 * - Top controls: Back button and "Locations" title
 * - Center-aligned counter: Shows "X Discovered" (no sort button)
 * - Scrollable content: Grid of location containers (to be implemented)
 * - Clipping behavior: Content clips at boundary when scrolling (same as ShipSelectionScreen)
 * 
 * Scroll behavior:
 * - Scroll position is preserved when navigating to LocationDetailsScreen and back
 * - Scroll position is reset when opening from CareerScreen (when shouldResetScroll is true)
 * 
 * @param onBackClick Callback when the back button is clicked
 * @param onLocationClick Callback when a location is clicked
 * @param shouldResetScroll Boolean flag to reset scroll position when opening from CareerScreen
 * @param modifier Modifier for the screen
 */
@Composable
fun LocationsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onLocationClick: (Location) -> Unit = {},
    shouldResetScroll: Boolean = false
) {
    // Get discovered locations from repository - filter to only show unlocked locations
    val allLocations = LocationRepository.getDiscoveredLocations()
    val discoveredLocations = allLocations.filter { 
        com.example.fargalaxy.data.GameStateRepository.isLocationUnlocked(it.id) 
    }
    val discoveredLocationsCount = discoveredLocations.size
    
    // Save scroll position to persist when navigating to LocationDetailsScreen and back
    var savedFirstVisibleItemIndex by rememberSaveable {
        mutableStateOf(0)
    }
    var savedFirstVisibleItemScrollOffset by rememberSaveable {
        mutableStateOf(0)
    }
    
    // LazyListState: Create new state, will be restored from saved position
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = if (shouldResetScroll) 0 else savedFirstVisibleItemIndex,
        initialFirstVisibleItemScrollOffset = if (shouldResetScroll) 0 else savedFirstVisibleItemScrollOffset
    )
    
    // Track previous reset flag for scroll reset logic
    var previousResetFlag by remember { mutableStateOf(false) }
    
    // Save scroll position when it changes (but not when resetting)
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        if (!shouldResetScroll) {
            savedFirstVisibleItemIndex = listState.firstVisibleItemIndex
            savedFirstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset
        }
    }
    
    // Reset scroll position when shouldResetScroll is true (opening from CareerScreen)
    LaunchedEffect(shouldResetScroll) {
        if (shouldResetScroll && !previousResetFlag) {
            // Only reset when flag changes from false to true (opening from CareerScreen)
            listState.animateScrollToItem(0)
            savedFirstVisibleItemIndex = 0
            savedFirstVisibleItemScrollOffset = 0
        }
        previousResetFlag = shouldResetScroll
    }
    
    // Get density to convert dp to pixels
    val density = LocalDensity.current
    
    // Calculate if content is being clipped (first visible item > 0 or scroll offset >= 16dp means content moved up past initial spacer)
    // When scrolled 16dp or more, content reaches the clip boundary at 147dp
    val isContentClipped = remember {
        derivedStateOf {
            with(density) {
                listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset >= 16.dp.toPx().toInt()
            }
        }
    }

    // First-time tutorial (not part of hasCompletedAllTutorials — does not gate interstitial ads)
    var showLocationsSelectionTutorial by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!UserDataRepository.hasSeenLocationsSelectionTutorial) {
            delay(1000)
            showLocationsSelectionTutorial = true
            UserDataRepository.markLocationsSelectionTutorialSeen()
        }
    }
    
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        // Background image: Same as ShipSelectionScreen (bottom layer)
        Image(
            painter = painterResource(id = R.drawable.shipscreenbackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Location galaxy background: Positioned above shipscreenbackground, behind everything else
        // Width: 140% of screen width (extends beyond screen edges, gets trimmed)
        // Horizontally centered
        // Vertical offset: moves up by 30% of screen height (30% of top gets trimmed)
        // Maintains original aspect ratio
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val imageWidth = screenWidth * 1.4f // 140% width
        val verticalOffset = screenHeight * 0.3f // 30% of screen height upward
        
        Image(
            painter = painterResource(id = R.drawable.locationgalaxybackground),
            contentDescription = null,
            modifier = Modifier
                .width(imageWidth)
                .fillMaxHeight()
                .alpha(0.8f) // 80% opacity
                .align(Alignment.TopCenter)
                .offset(y = -verticalOffset), // Move up by 30% of screen height
            contentScale = ContentScale.Crop // Maintains aspect ratio while filling space
        )
        
        // Top gradient overlay: Covers 35% of screen height, creating a fade effect at the top.
        // Gradient transitions from solid black at the top to transparent at the bottom.
        // Taller than ShipSelectionScreen for LocationsScreen
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

        // Bottom gradient overlay: Covers 25% of screen height, creating a fade effect at the bottom.
        // Gradient transitions from transparent at the top to solid black at the bottom.
        // Same as ShipSelectionScreen
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
        
        // Top controls area: Title and back button
        // Positioned at the same location as the top controls in ShipSelectionScreen
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
            // Back button: Same size and style as ShipSelectionScreen
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
            
            // Title: "Locations"
            // Same font style as "Ship selection" in ShipSelectionScreen
            // Horizontally centered on the screen
            Text(
                text = "Locations",
                fontFamily = Exo2,
                fontSize = 18.sp,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        // Counter container: Positioned 24dp below the back button
        // Back button bottom: statusBarsPadding + 24.dp + 51.dp = statusBarsPadding + 75.dp
        // Container top: statusBarsPadding + 75.dp + 24.dp = statusBarsPadding + 99.dp
        // Center-aligned labels: Sector name and "X Discovered" (no sort button)
        // Track labels container height to position clip line 8dp below it
        var labelsContainerHeight by remember { mutableStateOf(0.dp) }
        val density = LocalDensity.current
        
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 99.dp) // 24dp below back button (75.dp + 24.dp)
                .fillMaxWidth()
                .onSizeChanged { size ->
                    with(density) {
                        labelsContainerHeight = size.height.toDp()
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp) // 0dp spacing between labels
        ) {
            // Sector name: Center-aligned, Regular, 14sp
            Text(
                text = "Astra Verge", // TODO: Replace with dynamic sector name
                fontFamily = Exo2,
                fontWeight = FontWeight.W400, // Regular
                fontSize = 14.sp,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center
            )
            
            // Counter: Center-aligned, Dynamic number, Bold, 20sp
            Text(
                text = "$discoveredLocationsCount Discovered",
                fontFamily = Exo2,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center
            )
        }
        
        // Clip boundary container: Box positioned 8dp below the labels container
        // Labels container top: statusBarsPadding + 99.dp
        // Clip line is at: statusBarsPadding + 99.dp + labelsContainerHeight + 8.dp
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 99.dp + labelsContainerHeight + 8.dp) // Clip boundary position: 8dp below labels container
                .fillMaxWidth()
                .fillMaxHeight()
                .clipToBounds() // Clip content that goes above this boundary
        ) {
            if (discoveredLocationsCount == 0) {
                // Empty state: Show centered message when no locations are discovered
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp), // 24dp side padding
                    contentAlignment = Alignment.Center // Center horizontally and vertically
                ) {
                    Text(
                        text = "Discovered locations will appear here",
                        fontFamily = Exo2,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400, // Regular
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
            // LazyColumn: Content can scroll up and get clipped at the boundary
            // Only visible items (and a small buffer) are composed for better performance
            // Initially, content starts 16dp below clip line (via spacer)
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .navigationBarsPadding(), // Account for navigation bar height
                contentPadding = PaddingValues(
                    top = 32.dp, // 32dp spacing between bottom of "26 Discovered" label and first row
                    bottom = 32.dp // Allow last row to be 32dp above bottom bar
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp) // 8dp spacing between rows
            ) {
                // Location rows: Each row contains a location image container and text container
                // Rows alternate: first row has image on left, second on right, etc.
                itemsIndexed(
                    items = discoveredLocations,
                    key = { index, location -> location.id } // Use location ID as key for stable identity
                ) { index, location ->
                    val isImageOnLeft = index % 2 == 0 // Alternate: even index = left, odd index = right
                    LocationRow(
                        location = location,
                        isImageOnLeft = isImageOnLeft,
                        onLocationClick = { onLocationClick(location) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // White divider line: Only visible when content is being clipped
            // Positioned on top of the Column so it appears above clipped content
            if (isContentClipped.value) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFFFFFFF)) // White line, full width, 1px
                )
                }
            }
        }

        if (showLocationsSelectionTutorial) {
            TutorialModal(
                title = "Discovered locations",
                body = "View the locations you've discovered during your travels. New locations are added automatically.",
                buttonText = "CONTINUE",
                onButtonClick = { showLocationsSelectionTutorial = false }
            )
        }
    }
}

/**
 * LocationRow composable - displays a single location row with image and text containers.
 * 
 * Row structure:
 * - 16dp horizontal padding from screen edges
 * - Image container: 45% width, 1:1 aspect ratio
 *   - Background: planetback.json (Lottie animation)
 *   - Location image on top (e.g., location2.png)
 * - Text container: 55% width, adjusts vertically to content
 *   - Horizontal line above (12dp spacing, white, 1px, 56% opacity)
 *   - Name label (16sp, Bold)
 *   - Type label (14sp, regular, 0dp spacing below name)
 *   - Horizontal line below (12dp spacing, white, 1px, 56% opacity)
 * 
 * @param location The location to display
 * @param isImageOnLeft If true, image container is on the left; if false, on the right
 * @param onLocationClick Callback when the location is clicked
 * @param modifier Modifier for the row
 */
@Composable
private fun LocationRow(
    location: Location,
    isImageOnLeft: Boolean = true,
    onLocationClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp) // 16px horizontal padding
                    .clickable(onClick = onLocationClick),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalAlignment = Alignment.CenterVertically // Vertically center align containers
            ) {
            // Render containers in the correct order based on isImageOnLeft
            if (isImageOnLeft) {
                // Image container: 45% width, 1:1 aspect ratio (unless overflow is enabled)
                Box(
                    modifier = Modifier
                        .weight(0.45f) // Use weight to ensure proper distribution
                        .then(
                            if (location.shouldOverflowSelectionImage) {
                                Modifier.fillMaxHeight() // Full height for overflow images
                            } else {
                                Modifier.aspectRatio(1f) // 1:1 aspect ratio for normal images
                            }
                        ),
                    contentAlignment = Alignment.Center // Center align all content
                ) {
                    // Background: JSON animation based on location rarity - centered in container
                    val jsonResourceId = when (location.rarity) {
                        LocationRarity.COMMON -> R.raw.planetback
                        LocationRarity.UNCOMMON -> R.raw.planetbackuncommon
                        LocationRarity.RARE -> R.raw.planetbackrare
                        LocationRarity.EPIC -> R.raw.planetbackepic
                        LocationRarity.LEGENDARY -> R.raw.planetbacklegendary
                        LocationRarity.MYTHICAL -> R.raw.minimythicalradarlocation
                    }
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(jsonResourceId))
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.fillMaxSize(), // contentAlignment centers it automatically
                        contentScale = ContentScale.FillBounds
                    )
                    
                    // Location image on top: 80% of container for normal, 100% for location3, location6, location8, location11, location13, location14, location16, location19, location21, location22, location23, location25, and location26
                    // Overflow images (location4, location13, location22) are rendered outside the Row to allow overflow
                    // LOCATION3, LOCATION6, LOCATION8, LOCATION11, LOCATION13, LOCATION14, LOCATION16, LOCATION19, LOCATION21, LOCATION22, LOCATION23, LOCATION25 & LOCATION26: Rendered at 100% size to fill container both horizontally and vertically
                    if (!location.shouldOverflowSelectionImage) {
                        // Check if this is location3, location6, location8, location11, location13, location14, location16, location19, location21, location22, location23, location25, or location26 (100% size)
                        val isLocation3Or6Or8Or11Or13Or14Or16Or19Or21Or22Or23Or25Or26 = location.id == "location3" || location.id == "location6" || location.id == "location8" || location.id == "location11" || location.id == "location13" || location.id == "location14" || location.id == "location16" || location.id == "location19" || location.id == "location21" || location.id == "location22" || location.id == "location23" || location.id == "location25" || location.id == "location26"
                        
                        if (isLocation3Or6Or8Or11Or13Or14Or16Or19Or21Or22Or23Or25Or26) {
                            // LOCATION3, LOCATION6, LOCATION8, LOCATION11, LOCATION13, LOCATION14, LOCATION16, LOCATION19, LOCATION21, LOCATION22, LOCATION23, LOCATION25 & LOCATION26: 100% size - fill container both horizontally and vertically
                            // Rendered directly in same container as JSON to ensure exact same size
                            Image(
                                painter = painterResource(id = location.selectionImageResId),
                                contentDescription = location.name,
                                modifier = Modifier.fillMaxSize(), // 100% - fills container both horizontally and vertically, same size as JSON
                                contentScale = ContentScale.Fit // Maintain aspect ratio
                            )
                        } else {
                            // Normal images: 80% of container, maintains aspect ratio
                            BoxWithConstraints(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center // Center align - no need for .align() on Image
                            ) {
                                val containerSize = minOf(maxWidth, maxHeight)
                                Image(
                                    painter = painterResource(id = location.selectionImageResId),
                                    contentDescription = location.name,
                                    modifier = Modifier.size(containerSize * 0.8f), // 80% of container size
                                    contentScale = ContentScale.Fit // Maintain aspect ratio
                                )
                            }
                        }
                    }
                }
                
                // Text container: 55% width, adjusts vertically to content
                Box(
                    modifier = Modifier
                        .weight(0.55f) // Use weight to ensure proper distribution
                        .wrapContentHeight()
                        .padding(horizontal = 12.dp) // Internal padding for text
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally, // Center align content horizontally
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        // Horizontal line above labels (12dp spacing)
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0x8FFFFFFF)) // White, 1px, 56% opacity (0x8F = ~56%)
                        )
                        
                        // Spacing between top line and name label
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Name label: 16sp, Bold, center aligned
                        Text(
                            text = location.name,
                            fontFamily = Exo2,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center
                        )
                        
                        // Type label: 14sp, regular, 0dp spacing below name
                        // Use selectionTypeDisplay if available, otherwise use type
                        Text(
                            text = location.selectionTypeDisplay ?: location.type,
                            fontFamily = Exo2,
                            fontWeight = FontWeight.W400, // Regular
                            fontSize = 14.sp,
                            color = Color(0xFFFFFFFF),
                            modifier = Modifier.padding(top = 0.dp) // 0dp spacing from name
                        )
                        
                        // Horizontal line below labels (12dp spacing)
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0x8FFFFFFF)) // White, 1px, 56% opacity
                        )
                    }
                }
            } else {
                // Text container: 55% width, adjusts vertically to content (on left when image is on right)
                Box(
                    modifier = Modifier
                        .weight(0.55f) // Use weight to ensure proper distribution
                        .wrapContentHeight()
                        .padding(horizontal = 12.dp) // Internal padding for text
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally, // Center align content horizontally
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        // Horizontal line above labels (12dp spacing)
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0x8FFFFFFF)) // White, 1px, 56% opacity (0x8F = ~56%)
                        )
                        
                        // Spacing between top line and name label
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Name label: 16sp, Bold, center aligned
                        Text(
                            text = location.name,
                            fontFamily = Exo2,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center
                        )
                        
                        // Type label: 14sp, regular, 0dp spacing below name
                        // Use selectionTypeDisplay if available, otherwise use type
                        Text(
                            text = location.selectionTypeDisplay ?: location.type,
                            fontFamily = Exo2,
                            fontWeight = FontWeight.W400, // Regular
                            fontSize = 14.sp,
                            color = Color(0xFFFFFFFF),
                            modifier = Modifier.padding(top = 0.dp) // 0dp spacing from name
                        )
                        
                        // Horizontal line below labels (12dp spacing)
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0x8FFFFFFF)) // White, 1px, 56% opacity
                        )
                    }
                }
                
                // Image container: 45% width, 1:1 aspect ratio (on right, consistent for all to maintain row spacing)
                // CONSISTENT ROW SPACING: All containers use aspectRatio(1f) to maintain same row height
                // Overflow images (location4, location6) are rendered outside Row, so container height doesn't affect overflow
                Box(
                    modifier = Modifier
                        .weight(0.45f) // Use weight to ensure proper distribution
                        .aspectRatio(1f), // 1:1 aspect ratio for ALL images (maintains consistent row height)
                    contentAlignment = Alignment.Center // Center align all content
                ) {
                    // Background: JSON animation based on location rarity - centered in container
                    val jsonResourceId = when (location.rarity) {
                        LocationRarity.COMMON -> R.raw.planetback
                        LocationRarity.UNCOMMON -> R.raw.planetbackuncommon
                        LocationRarity.RARE -> R.raw.planetbackrare
                        LocationRarity.EPIC -> R.raw.planetbackepic
                        LocationRarity.LEGENDARY -> R.raw.planetbacklegendary
                        LocationRarity.MYTHICAL -> R.raw.minimythicalradarlocation
                    }
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(jsonResourceId))
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.fillMaxSize(), // contentAlignment centers it automatically
                        contentScale = ContentScale.FillBounds
                    )
                    
                    // Location image on top: 80% of container for normal, 100% for location3, location6, location8, location11, location13, location14, location16, location19, location21, location22, location23, location25, and location26
                    // Overflow images (location4, location13, location22) are rendered outside the Row to allow overflow
                    if (!location.shouldOverflowSelectionImage) {
                        // Check if this is location3, location6, location8, location11, location13, location14, location16, location19, location21, location22, location23, location25, or location26 (100% size)
                        val isLocation3Or6Or8Or11Or13Or14Or16Or19Or21Or22Or23Or25Or26 = location.id == "location3" || location.id == "location6" || location.id == "location8" || location.id == "location11" || location.id == "location13" || location.id == "location14" || location.id == "location16" || location.id == "location19" || location.id == "location21" || location.id == "location22" || location.id == "location23" || location.id == "location25" || location.id == "location26"
                        
                        if (isLocation3Or6Or8Or11Or13Or14Or16Or19Or21Or22Or23Or25Or26) {
                            // LOCATION3, LOCATION6, LOCATION8, LOCATION11, LOCATION13, LOCATION14, LOCATION16, LOCATION19, LOCATION21, LOCATION22, LOCATION23, LOCATION25 & LOCATION26: 100% size - fill container both horizontally and vertically
                            Image(
                                painter = painterResource(id = location.selectionImageResId),
                                contentDescription = location.name,
                                modifier = Modifier.fillMaxSize(), // 100% - fills container both horizontally and vertically, same size as JSON
                                contentScale = ContentScale.Fit // Maintain aspect ratio
                            )
                        } else {
                            // Normal images: 80% of container, maintains aspect ratio
                            BoxWithConstraints(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center // Center align - no need for .align() on Image
                            ) {
                                val containerSize = minOf(maxWidth, maxHeight)
                                Image(
                                    painter = painterResource(id = location.selectionImageResId),
                                    contentDescription = location.name,
                                    modifier = Modifier.size(containerSize * 0.8f), // 80% of container size
                                    contentScale = ContentScale.Fit // Maintain aspect ratio
                                )
                            }
                        }
                    }
                }
            }
            }
            
            // Overflow image: Positioned outside Row to allow overflow into padding area
            // This allows the image to extend beyond the Box constraints into the padding area
            // Positioned to align with the JSON center, then offset to allow overflow
            // HANDLES: location4 (shouldOverflowSelectionImage = true) only
            // LOCATION6: Now rendered inside container with clipping (see above), no longer in overflow section
            if (location.shouldOverflowSelectionImage) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight() // Match the Row's height for location4
                ) {
                    // Calculate container center position - same calculation as the JSON container
                    // The Row has 16dp padding, so available width is maxWidth - 32.dp
                    val availableRowWidth = maxWidth - 32.dp // 16dp padding on each side
                    val imageContainerWidth = availableRowWidth * 0.45f
                    
                    // Calculate the center X position of the image container (where JSON is centered)
                    val containerCenterX = if (isImageOnLeft) {
                        16.dp + imageContainerWidth / 2 // Center of left container (same as JSON)
                    } else {
                        maxWidth - 16.dp - imageContainerWidth / 2 // Center of right container (same as JSON)
                    }
                    
                    // LOCATION4 OVERFLOW IMPLEMENTATION: Full height overflow (fillMaxHeight)
                    // Location4 uses fillMaxHeight to extend beyond container bounds
                    // LOCATION13: Additional 30dp offset to the right (16dp + 12dp + 8dp - 4dp - 2dp)
                    // LOCATION22: Additional 15dp offset to the left (30dp - 8dp - 4dp - 2dp - 2dp - 1dp, inverted direction), 11dp down, and 5% bigger
                    val isLocation13 = location.id == "location13"
                    val isLocation22 = location.id == "location22"
                    val baseOffset = (containerCenterX - maxWidth / 2) + (if (isImageOnLeft) -16.dp else 16.dp)
                    val finalOffsetX = when {
                        isLocation13 -> baseOffset + 32.dp // Offset to the right
                        isLocation22 -> baseOffset - 27.dp // Offset to the left (inverted, 15dp less than 30dp)
                        else -> baseOffset
                    }
                    val finalOffsetY = if (isLocation22) 11.dp else 0.dp // 11dp down for location22
                    
                    Image(
                        painter = painterResource(id = location.selectionImageResId),
                        contentDescription = location.name,
                        modifier = Modifier
                            .fillMaxHeight() // Full height - width will scale proportionally
                            .then(if (isLocation22) Modifier.scale(1.15f) else Modifier) // 5% bigger for location22
                            .align(Alignment.Center) // Center vertically (same as JSON)
                            .offset(x = finalOffsetX, y = finalOffsetY),
                        contentScale = ContentScale.FillHeight // Fill height, maintain aspect ratio
                    )
                }
            }
        }
    }

