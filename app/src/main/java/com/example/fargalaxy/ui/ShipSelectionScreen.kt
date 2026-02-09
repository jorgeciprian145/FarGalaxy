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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fargalaxy.R
import com.example.fargalaxy.data.ShipRepository
import com.example.fargalaxy.data.UserDataRepository
import com.example.fargalaxy.data.GameStateRepository
import com.example.fargalaxy.model.Ship
import com.example.fargalaxy.model.ShipRarity
import androidx.compose.ui.draw.alpha

/**
 * Helper function to get the required space license level for a ship.
 * 
 * @param shipId The ship's ID
 * @return The required space license level
 */
private fun getRequiredSpaceLicenseLevel(shipId: String): Int {
    return when (shipId) {
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
        "a450_sparrow" -> 9
        "t47_dolphin" -> 9
        "asn_h99_dragoon" -> 12
        "silver_lightning" -> 12
        "vulcani_legenda_f1" -> 12
        "force_of_nature" -> 15
        "dying_star" -> 15
        "ship22" -> 15
        "ship23" -> 8
        else -> 1 // Default placeholder
    }
}

/**
 * ShipSelectionScreen composable - displays the ship selection screen where users can view and select ships.
 * 
 * Layout structure:
 * - Background: shipscreenbackground image (same as ShipDetailsScreen)
 * - Top gradient: 20% height, black to transparent (same as CareerScreen)
 * - Bottom gradient: 25% height, transparent to black (same as CareerScreen)
 * - Top controls: Back button and "Ship selection" title
 * - Scrollable content: Grid of ship containers (to be implemented)
 * - Clipping behavior: Content clips at 115dp from top when scrolling (same as CareerScreen)
 * 
 * Scroll behavior:
 * - Scroll position is preserved when navigating to ShipDetailsScreen and back
 * - Scroll position is reset when opening from CareerScreen (when shouldResetScroll is true)
 * 
 * @param onBackClick Callback when the back button is clicked
 * @param onShipClick Callback when a ship is clicked
 * @param shouldResetScroll Boolean flag to reset scroll position when opening from CareerScreen
 * @param modifier Modifier for the screen
 */
@Composable
fun ShipSelectionScreen(
    onBackClick: () -> Unit = {},
    onShipClick: (Ship) -> Unit = {},
    shouldResetScroll: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Get list of available ships - filter to only show unlocked ships
    val allShips = ShipRepository.getAllShips()
    val currentShip = ShipRepository.getCurrentShip()
    
    // Filter to only unlocked ships (test mode shows all, real mode shows only unlocked)
    // Filter to show only owned ships (purchased and selectable)
    val unlockedShips = allShips.filter { 
        com.example.fargalaxy.data.GameStateRepository.isShipOwned(it.id) 
    }
    
    // Group ships by rarity, with current ship first
    // Rarity order: COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHICAL
    // Within each rarity group, sort by ascending repository number
    val rarityOrder = listOf(
        ShipRarity.COMMON,
        ShipRarity.UNCOMMON,
        ShipRarity.RARE,
        ShipRarity.EPIC,
        ShipRarity.LEGENDARY,
        ShipRarity.MYTHICAL
    )
    
    val availableShips = if (unlockedShips.isNotEmpty()) {
        val otherShips = unlockedShips.filter { it.id != currentShip.id }
        // Group other ships by rarity, sorting by repository number within each group
        val groupedByRarity = rarityOrder.map { rarity ->
            otherShips.filter { it.rarity == rarity }
                .sortedBy { ShipRepository.getRepositoryNumber(it.id) } // Sort by repository number
        }.flatten()
        // Current ship first, then grouped by rarity
        listOf(currentShip) + groupedByRarity
    } else {
        unlockedShips
    }
    
    val shipsCount = availableShips.size
    
    // Save scroll position to persist when navigating to ShipDetailsScreen and back
    var savedScrollPosition by rememberSaveable {
        mutableStateOf(0)
    }
    
    // Scroll state: Create new state, will be restored from saved position
    val scrollState = rememberScrollState()
    
    // Restore scroll position when composable is first created (if not resetting)
    var previousResetFlag by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (savedScrollPosition > 0 && !shouldResetScroll) {
            scrollState.scrollTo(savedScrollPosition)
        }
    }
    
    // Save scroll position when it changes (but not when resetting)
    LaunchedEffect(scrollState.value) {
        if (!shouldResetScroll) {
            savedScrollPosition = scrollState.value
        }
    }
    
    // Reset scroll position when shouldResetScroll is true (opening from CareerScreen)
    LaunchedEffect(shouldResetScroll) {
        if (shouldResetScroll && !previousResetFlag) {
            // Only reset when flag changes from false to true (opening from CareerScreen)
            scrollState.animateScrollTo(0)
            savedScrollPosition = 0 // Also clear saved position
        }
        previousResetFlag = shouldResetScroll
    }
    
    // Get density to convert dp to pixels
    val density = LocalDensity.current
    
    // Calculate if content is being clipped (scroll position >= 16dp means content moved up past initial spacer)
    // When scrolled 16dp or more, content reaches the clip boundary at 147dp
    val isContentClipped = derivedStateOf {
        with(density) {
            scrollState.value >= 16.dp.toPx().toInt()
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Background image: Same as ShipDetailsScreen
        Image(
            painter = painterResource(id = R.drawable.shipscreenbackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Top gradient overlay: Covers 20% of screen height, creating a fade effect at the top.
        // Gradient transitions from solid black at the top to transparent at the bottom.
        // Same as CareerScreen
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
        // Same as CareerScreen
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
        // Positioned at the same location as the top controls in ShipDetailsScreen
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
                    .padding(start = 12.dp)
                    .height(51.dp)
                    .clickable(onClick = onBackClick),
                contentScale = ContentScale.Fit
            )
            
            // Title: "Ship selection"
            // Same font style as "Your current ship" in ShipDetailsScreen
            // Horizontally centered on the screen
            Text(
                text = "Ship selection",
                fontFamily = Exo2,
                fontSize = 18.sp,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        // Counter and button container: Positioned 24dp below the back button
        // Back button bottom: statusBarsPadding + 24.dp + 51.dp = statusBarsPadding + 75.dp
        // Container top: statusBarsPadding + 75.dp + 24.dp = statusBarsPadding + 99.dp
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 99.dp) // 24dp below back button (75.dp + 24.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp) // 16px horizontal padding
                .wrapContentHeight() // Height determined by tallest element
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Counter: Dynamic number, Bold, 20sp
                Text(
                    text = "$shipsCount Owned",
                    fontFamily = Exo2,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFFFFFFFF)
                )
                
                // Sort button: Fixed height 32dp, label 14sp regular, width based on content + 16px padding
                SortButton(
                    text = "SORT: BY RARITY",
                    onClick = { /* TODO: Implement sort functionality */ }
                )
            }
        }
        
        // Clip boundary container: Box positioned 16dp below the counter/button container
        // Counter/button container top: statusBarsPadding + 99.dp
        // Counter/button container height: 32.dp (SortButton height)
        // Counter/button container bottom: statusBarsPadding + 99.dp + 32.dp = statusBarsPadding + 131.dp
        // Clip line is at: statusBarsPadding + 131.dp + 16.dp = statusBarsPadding + 147.dp
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 147.dp) // Clip boundary position: 16dp below counter/button container
                .fillMaxWidth()
                .fillMaxHeight()
                .clipToBounds() // Clip content that goes above this boundary
        ) {
            // Scrollable content column: Content can scroll up and get clipped at the boundary
            // Initially, content starts 16dp below clip line (via spacer)
            // Column fills available height to enable proper scrolling
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .verticalScroll(scrollState)
                    .navigationBarsPadding() // Account for navigation bar height
                    .padding(bottom = 32.dp), // Allow last row to be 32dp above bottom bar
                verticalArrangement = Arrangement.spacedBy(0.dp) // Manual spacing control
            ) {
                // Initial spacer: Push content down 16dp from clip line
                Spacer(modifier = Modifier.height(16.dp))
                
                // Ship grid: Rows of 1:1 containers, 2 per row, 8px gap between containers
                // 16px side padding, containers maintain 1:1 ratio even when alone in a row
                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val containerWidth = (maxWidth - 32.dp - 8.dp) / 2 // (screen width - 32dp padding - 8dp spacing) / 2
                    
                    // Group ships into rows (chunks of 2)
                    val rows = availableShips.chunked(2)
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp) // 8dp spacing between rows
                    ) {
                        rows.forEach { rowShips ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp), // 16px side padding
                                horizontalArrangement = Arrangement.spacedBy(8.dp) // 8px spacing between containers
                            ) {
                                rowShips.forEach { ship ->
                                    val isCurrentShip = ship.id == currentShip.id
                                    // Check license level requirement
                                    val requiredLevel = getRequiredSpaceLicenseLevel(ship.id)
                                    val currentLevel = UserDataRepository.getCurrentLevel()
                                    val isTestMode = GameStateRepository.isTestMode
                                    val canSelectShip = isTestMode || currentLevel >= requiredLevel
                                    
                                    ShipContainer(
                                        ship = ship,
                                        isCurrentShip = isCurrentShip,
                                        containerWidth = containerWidth,
                                        onShipClick = { onShipClick(ship) },
                                        enabled = canSelectShip,
                                        modifier = Modifier
                                            .width(containerWidth)
                                            .alpha(if (canSelectShip) 1f else 0.5f) // Reduce opacity when disabled
                                    )
                                }
                            }
                        }
                    }
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
}

/**
 * SortButton composable - displays the sort button with dynamic width.
 * 
 * Button specifications:
 * - Fixed height: 32dp
 * - Label: 14sp, regular weight
 * - Vertical offset: -1dp (moves text slightly upward)
 * - Width: Determined by label width + 16dp internal padding (8dp on each side)
 * - White border, rounded corners (80dp radius)
 * 
 * @param text The button label text
 * @param onClick Callback when button is clicked
 * @param modifier Modifier for the button
 */
@Composable
private fun SortButton(
    text: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(32.dp)
            .wrapContentWidth() // Width determined by content
            .clip(RoundedCornerShape(80.dp))
            .border(
                width = 1.dp,
                color = Color(0xFFFFFFFF), // White border
                shape = RoundedCornerShape(80.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp), // 16dp padding on each side (32dp total)
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = Exo2,
            fontSize = 14.sp,
            fontWeight = FontWeight.W400, // Regular weight
            color = Color(0xFFFFFFFF), // White text
            textAlign = TextAlign.Center,
            modifier = Modifier.offset(y = (-1).dp) // -1dp vertical offset
        )
    }
}

/**
 * Helper function to get the background drawable resource ID based on ship rarity and selection state.
 * 
 * @param rarity The ship's rarity (COMMON, UNCOMMON, RARE, EPIC, LEGENDARY)
 * @param isActive Whether the ship is currently selected
 * @return The drawable resource ID for the background
 */
private fun getSelectionBackgroundResId(rarity: ShipRarity, isActive: Boolean): Int {
    val rarityName = when (rarity) {
        ShipRarity.COMMON -> "common"
        ShipRarity.UNCOMMON -> "uncommon"
        ShipRarity.RARE -> "rare"
        ShipRarity.EPIC -> "epic"
        ShipRarity.LEGENDARY -> "legendary"
        ShipRarity.MYTHICAL -> "mythical"
    }
    val stateName = if (isActive) "active" else "default"
    val drawableName = "selectionbackground${rarityName}${stateName}"
    
    return when (drawableName) {
        "selectionbackgroundcommonactive" -> R.drawable.selectionbackgroundcommonactive
        "selectionbackgroundcommondefault" -> R.drawable.selectionbackgroundcommondefault
        "selectionbackgrounduncommonactive" -> R.drawable.selectionbackgrounduncommonactive
        "selectionbackgrounduncommondefault" -> R.drawable.selectionbackgrounduncommondefault
        "selectionbackgroundrareactive" -> R.drawable.selectionbackgroundrareactive
        "selectionbackgroundraredefault" -> R.drawable.selectionbackgroundraredefault
        "selectionbackgroundepicactive" -> R.drawable.selectionbackgroundepicactive
        "selectionbackgroundepicdefault" -> R.drawable.selectionbackgroundepicdefault
        "selectionbackgroundlegendaryactive" -> R.drawable.selectionbackgroundlegendaryactive
        "selectionbackgroundlegendarydefault" -> R.drawable.selectionbackgroundlegendarydefault
        "selectionbackgroundmythicalactive" -> R.drawable.selectionbackgroundmythicalactive
        "selectionbackgroundmythicaldefault" -> R.drawable.selectionbackgroundmythicaldefault
        else -> R.drawable.selectionbackgroundcommondefault // Fallback
    }
}

/**
 * Helper function to get the selection screen image resource ID based on ship ID.
 * 
 * Maps ship IDs to their corresponding selection screen images:
 * - b14_phantom -> ship1selectionscreen
 * - common_ship_2 -> ship2selectionscreen
 * - uncommon_ship_1 -> ship4selectionscreen
 * - uncommon_ship_2 -> ship5selectionscreen
 * - legendary_ship -> ship12selectionscreen
 * 
 * @param shipId The ship's ID
 * @return The drawable resource ID for the selection screen image
 */
private fun getSelectionScreenImageResId(shipId: String): Int {
    return when (shipId) {
        "b14_phantom" -> R.drawable.ship1selectionscreen
        "type45c_shooting_star" -> R.drawable.ship2selectionscreen
        "navakeshi_star_pouncer" -> R.drawable.ship3selectionscreen
        "a300_albatross" -> R.drawable.ship4selectionscreen
        "p7h_skyblazer" -> R.drawable.ship19selectionscreen
        "b7f_starforce" -> R.drawable.ship5selectionscreen
        "navakeshi_star_crusher" -> R.drawable.ship6selectionscreen
        "b15_specter" -> R.drawable.ship7selectionscreen
        "n6_98_melina" -> R.drawable.ship8selectionscreen
        "model3_tortoise_ccp" -> R.drawable.ship9selectionscreen
        "navakeshi_star_ravager" -> R.drawable.ship11selectionscreen
        "h98_valkyrie" -> R.drawable.ship10selectionscreen
        "silver_lightning" -> R.drawable.ship12selectionscreen
        "vulcani_legenda_f1" -> R.drawable.ship13selectionscreen
        "force_of_nature" -> R.drawable.ship14selectionscreen
        "dying_star" -> R.drawable.ship15selectionscreen
        "asn_ag94_centurion" -> R.drawable.ship16selectionscreen
        "isc_m450_phoenix" -> R.drawable.ship17selectionscreen
        "asn_h99_dragoon" -> R.drawable.ship18selectionscreen
        "a450_sparrow" -> R.drawable.ship20selectionscreen
        "t47_dolphin" -> R.drawable.ship21selectionscreen
        "ship22" -> R.drawable.ship22selectionscreen
        "ship23" -> R.drawable.ship23selectionscreen
        "legendary_ship" -> R.drawable.ship12selectionscreen
        else -> R.drawable.ship1selectionscreen // Fallback
    }
}

/**
 * ShipContainer composable - displays a single ship container with 1:1 aspect ratio.
 * 
 * Container specifications:
 * - 1:1 aspect ratio (maintained via aspectRatio modifier)
 * - Background SVG based on ship rarity and selection state
 * - Ship image on top of background, full width and height
 * - Width determined by calculation: (screen width - 32dp padding - 8dp spacing) / 2
 * 
 * @param ship The ship to display
 * @param isCurrentShip Whether this is the currently selected ship
 * @param containerWidth The calculated width for the container
 * @param modifier Modifier for the container
 */
@Composable
private fun ShipContainer(
    ship: Ship,
    isCurrentShip: Boolean,
    containerWidth: androidx.compose.ui.unit.Dp,
    onShipClick: () -> Unit = {},
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val backgroundResId = getSelectionBackgroundResId(ship.rarity, isCurrentShip)
    val shipImageResId = getSelectionScreenImageResId(ship.id)
    
    Box(
        modifier = modifier
            .width(containerWidth)
            .aspectRatio(1f) // Maintain 1:1 aspect ratio
            .clickable(
                enabled = enabled,
                onClick = onShipClick
            )
    ) {
        // Background SVG: Full width and height, grows proportionally
        Image(
            painter = painterResource(id = backgroundResId),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.FillBounds // Fill the entire container
        )
        
        // Ship image: Full width and height, appears on top of background SVG
        // Grows proportionally with the container
        Image(
            painter = painterResource(id = shipImageResId),
            contentDescription = ship.name,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.FillBounds // Fill the entire container
        )
        
        // "CURRENT SHIP" badge: Only shown for the currently active ship
        // Positioned at bottom center with 16dp bottom padding
        if (isCurrentShip) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                CurrentShipBadge()
            }
        }
    }
}

/**
 * CurrentShipBadge composable - displays the "CURRENT SHIP" badge.
 * 
 * Badge specifications:
 * - Fixed height: 16dp
 * - Label: "CURRENT SHIP", 10sp, Medium weight, color #010102
 * - Width: Adjusts to label + 16dp internal padding (8dp each side)
 * - Corner radius: 80dp
 * - Background: #FFFFFF (white)
 * - Label vertical offset: -1dp
 * 
 * @param modifier Modifier for the badge
 */
@Composable
private fun CurrentShipBadge(
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
            text = "CURRENT SHIP",
            fontFamily = Exo2,
            fontSize = 10.sp,
            lineHeight = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF010102), // Very dark color
            textAlign = TextAlign.Center
        )
    }
}

