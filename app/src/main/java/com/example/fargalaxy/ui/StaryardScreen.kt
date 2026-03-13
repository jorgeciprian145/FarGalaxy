 package com.example.fargalaxy.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fargalaxy.R
import com.example.fargalaxy.data.ShipRepository
import com.example.fargalaxy.model.Ship
import com.example.fargalaxy.model.ShipRarity

/**
 * Enum to represent the selected tab in StaryardScreen.
 */
private enum class StaryardTab {
    UNLOCKED_SHIPS,
    SHIPCARDS
}

/**
 * StaryardScreen composable - displays the staryard screen where users can purchase ships.
 * 
 * Layout structure:
 * - Background: shipscreenbackground image (same as ShipSelectionScreen)
 * - Top gradient: 20% height, black to transparent (same as ShipSelectionScreen)
 * - Bottom gradient: 25% height, transparent to black (same as ShipSelectionScreen)
 * - Top controls: Back button and "Staryard" title
 * - Scrollable content: (to be implemented)
 * - Clipping behavior: Content clips at 115dp from top when scrolling (same as ShipSelectionScreen)
 * 
 * Scroll behavior:
 * - Same scrolling behavior as ShipSelectionScreen
 * 
 * @param onBackClick Callback when the back button is clicked
 * @param onShipClick Callback when a ship is clicked
 * @param modifier Modifier for the screen
 */
@Composable
fun StaryardScreen(
    onBackClick: () -> Unit = {},
    onShipClick: (Ship) -> Unit = {},
    onShipCardClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Scroll state
    val scrollState = rememberScrollState()

    // Tab state: Track which tab is selected (Unlocked ships or Shipcards)
    var selectedTab by remember { mutableStateOf(StaryardTab.UNLOCKED_SHIPS) }
    
    // Get density to convert dp to pixels
    val density = LocalDensity.current
    
    // Get all ships and filter to only show unlocked ships that are NOT owned (excluding starting ship b14_phantom)
    // Ships that are already owned should not appear in the staryard
    val allShips = ShipRepository.getAllShips()
    val filteredShips = allShips.filter {
        it.id != "b14_phantom" && // Exclude starting ship
        com.example.fargalaxy.data.GameStateRepository.isShipUnlocked(it.id) &&
        !com.example.fargalaxy.data.GameStateRepository.isShipOwned(it.id)
    }

    // Group ships by rarity and, within each rarity, sort by ascending repository number
    val rarityOrder = listOf(
        ShipRarity.COMMON,
        ShipRarity.UNCOMMON,
        ShipRarity.RARE,
        ShipRarity.EPIC,
        ShipRarity.LEGENDARY,
        ShipRarity.MYTHICAL
    )
    val availableShips = filteredShips.sortedWith(
        compareBy<com.example.fargalaxy.model.Ship> { rarityOrder.indexOf(it.rarity) }
            .thenBy { ShipRepository.getRepositoryNumber(it.id) }
    )
    
    // Read credits from global repository
    val userCredits = com.example.fargalaxy.data.UserDataRepository.userCredits
    
    // Ship prices based on focus time unlock requirements
    // All prices except Type 45C Shooting Star have been increased by 75%
    val shipPrices = mapOf(
        "type45c_shooting_star" to 2500,   // ship2: 15 mins (unchanged)
        "navakeshi_star_pouncer" to 8750,   // ship3: 40 mins (was 5000)
        "a300_albatross" to 14875,          // ship4: 60 mins (was 8500)
        "b7f_starforce" to 21000,           // ship5: 90 mins (was 12000)
        "navakeshi_star_crusher" to 26250,  // ship6: 150 mins (was 15000)
        "b15_specter" to 35000,             // ship7: 240 mins (was 20000)
        "n6_98_melina" to 45500,            // ship8: 360 mins (was 26000)
        "model3_tortoise_ccp" to 56000,     // ship9: 500 mins (was 32000)
        "h98_valkyrie" to 66500,            // ship10: 700 mins (was 38000)
        "navakeshi_star_ravager" to 78750,  // ship11: 1000 mins (was 45000)
        "silver_lightning" to 105000,       // ship12: 1400 mins (was 60000)
        "vulcani_legenda_f1" to 113750,     // ship13: 2000 mins (was 65000)
        "force_of_nature" to 140000         // ship14: 3000 mins (was 80000)
    )
    
    // Calculate if content is being clipped (scroll position >= 16dp means content moved up past initial spacer)
    // When scrolled 16dp or more, content reaches the clip boundary at 147dp
    val isContentClipped = derivedStateOf {
        with(density) {
            scrollState.value >= 16.dp.toPx().toInt()
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Background image: Same as ShipSelectionScreen
        Image(
            painter = painterResource(id = R.drawable.shipscreenbackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Top gradient overlay: Covers 20% of screen height, creating a fade effect at the top.
        // Gradient transitions from solid black at the top to transparent at the bottom.
        // Same as ShipSelectionScreen
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
                    .padding(start = 12.dp)
                    .height(51.dp)
                    .clickable(onClick = onBackClick),
                contentScale = ContentScale.Fit
            )
            
            // Title: "Staryard"
            // Same font style as "Ship selection" in ShipSelectionScreen
            // Horizontally centered on the screen
            Text(
                text = "Staryard",
                fontFamily = Exo2,
                fontSize = 18.sp,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        // Clip boundary container: Box positioned 8dp below the back button
        // Back button top: statusBarsPadding + 24.dp
        // Back button height: 51.dp
        // Back button bottom: statusBarsPadding + 24.dp + 51.dp = statusBarsPadding + 75.dp
        // Clip line is at: statusBarsPadding + 75.dp + 8.dp = statusBarsPadding + 83.dp
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 83.dp) // Clip boundary position: 8dp below back button
                .fillMaxWidth()
                .fillMaxHeight()
                .clipToBounds() // Clip content that goes above this boundary
        ) {
            // Scrollable content column: Content can scroll up and get clipped at the boundary
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .verticalScroll(scrollState)
                    .navigationBarsPadding() // Account for navigation bar height
                    .padding(bottom = 32.dp), // Allow last row to be 32dp above bottom bar
                verticalArrangement = Arrangement.spacedBy(0.dp) // Manual spacing control
            ) {
                // Tab toggle: Positioned with extra spacing below the header (clip boundary)
                // and 16dp above the label. Same visual style as the ShipDetailsScreen tabs,
                // with 16dp side padding.
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(60.dp))
                        .border(
                            width = 1.dp,
                            color = Color(0xFFFFFFFF),
                            shape = RoundedCornerShape(60.dp)
                        )
                        .padding(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        // UNLOCKED SHIPS tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(60.dp))
                                .then(
                                    if (selectedTab == StaryardTab.UNLOCKED_SHIPS) {
                                        Modifier.background(Color(0xFFFFFFFF))
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable { selectedTab = StaryardTab.UNLOCKED_SHIPS },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Unlocked ships",
                                fontFamily = Exo2,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W400,
                                color = if (selectedTab == StaryardTab.UNLOCKED_SHIPS) {
                                    Color(0xFF010102)
                                } else {
                                    Color(0xFFFFFFFF)
                                },
                                textAlign = TextAlign.Center
                            )
                        }

                        // SHIPCARDS tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(60.dp))
                                .then(
                                    if (selectedTab == StaryardTab.SHIPCARDS) {
                                        Modifier.background(Color(0xFFFFFFFF))
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable { selectedTab = StaryardTab.SHIPCARDS },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Shipcards",
                                fontFamily = Exo2,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W400,
                                color = if (selectedTab == StaryardTab.SHIPCARDS) {
                                    Color(0xFF010102)
                                } else {
                                    Color(0xFFFFFFFF)
                                },
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // 16dp spacing below the tabs and above the section labels
                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == StaryardTab.UNLOCKED_SHIPS) {
                    if (availableShips.isEmpty()) {
                        // Empty state: message when there are no unlocked ships to buy
                        // Use wrapContentHeight instead of fillMaxHeight to avoid layout issues
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(horizontal = 24.dp, vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Unlocked ships will appear here for you to buy",
                                fontFamily = Exo2,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W400,
                                color = Color(0xFFFFFFFF),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // Label: "Purchase unlocked starships" - center aligned, 14sp, regular weight
                        Text(
                            text = "Purchase unlocked starships",
                            fontFamily = Exo2,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W400, // Regular
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        // 16dp spacing below label
                        Spacer(modifier = Modifier.height(16.dp))

                        // Container: Ships count on left, credits on right
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left: Ships count label - bold, 20sp
                            Text(
                                text = "${availableShips.size} unlocked ships",
                                fontFamily = Exo2,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFFFFF)
                            )

                            // Right: Credits container
                            // Height: 32dp, padding: 16dp, border: 1dp #6B6C6F, background: #373A3E, corner radius: 8dp
                            Box(
                                modifier = Modifier
                                    .height(32.dp)
                                    .background(
                                        color = Color(0xFF373A3E), // Background fill
                                        shape = RoundedCornerShape(8.dp) // 8dp corner radius
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFF6B6C6F), // Border color
                                        shape = RoundedCornerShape(8.dp) // 8dp corner radius
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp), // 16dp internal padding
                                    horizontalArrangement = Arrangement.spacedBy(8.dp), // 8dp spacing between icon and label
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Credits icon: 24dp width, maintaining aspect ratio (on the left)
                                    Image(
                                        painter = painterResource(id = R.drawable.creditsicon),
                                        contentDescription = "Credits",
                                        modifier = Modifier.width(24.dp), // 24dp width, maintaining aspect ratio
                                        contentScale = ContentScale.Fit,
                                        colorFilter = ColorFilter.tint(Color(0xFFFFFFFF)) // White tint for icon
                                    )

                                    // Credits amount label: 16sp, medium weight, white color
                                    Text(
                                        text = userCredits.toString(),
                                        fontFamily = Exo2,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFFFFFFF) // White color
                                    )
                                }
                            }
                        }

                        // 16dp spacing below the ships count/credits row
                        Spacer(modifier = Modifier.height(16.dp))

                        // Ship grid: Rows of 1:1 containers, 2 per row, 8dp gap between containers
                        // 16dp side padding, containers maintain 1:1 ratio even when alone in a row
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
                                            .padding(horizontal = 16.dp), // 16dp side padding
                                        horizontalArrangement = Arrangement.spacedBy(8.dp) // 8dp spacing between containers
                                    ) {
                                        rowShips.forEach { ship ->
                                            val shipPrice = shipPrices[ship.id] ?: 0
                                            StaryardShipContainer(
                                                ship = ship,
                                                price = shipPrice,
                                                userCredits = userCredits,
                                                containerWidth = containerWidth,
                                                onShipClick = { onShipClick(ship) },
                                                modifier = Modifier.width(containerWidth)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Shipcards tab: Intro label + 3 empty shipcard containers (ship16, ship17, ship18)
                    Text(
                        text = "View your progress towards unlocking ships with shipcards",
                        fontFamily = Exo2,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400, // Regular
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Three shipcard items using same container style as ships but empty for now
                    val shipCardConfigs = listOf(
                        "ship16" to ShipRarity.UNCOMMON,
                        "ship17" to ShipRarity.EPIC,
                        "ship18" to ShipRarity.LEGENDARY
                    )

                    BoxWithConstraints(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val containerWidth = (maxWidth - 32.dp - 8.dp) / 2
                        val rows = shipCardConfigs.chunked(2)

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rows.forEach { rowItems ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowItems.forEach { (shipId, rarity) ->
                                        ShipCardPlaceholderContainer(
                                            rarity = rarity,
                                            containerWidth = containerWidth,
                                            onClick = { onShipCardClick(shipId) },
                                            modifier = Modifier.width(containerWidth)
                                        )
                                    }
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
 * StaryardShipContainer composable - displays a single ship container with price badge.
 * 
 * Container specifications:
 * - 1:1 aspect ratio (maintained via aspectRatio modifier)
 * - Background SVG based on ship rarity
 * - Ship image on top of background, full width and height
 * - Price badge at bottom center with 16dp bottom padding
 * 
 * @param ship The ship to display
 * @param price The price of the ship
 * @param userCredits The user's current credits
 * @param containerWidth The calculated width for the container
 * @param modifier Modifier for the container
 */
@Composable
private fun StaryardShipContainer(
    ship: Ship,
    price: Int,
    userCredits: Int,
    containerWidth: androidx.compose.ui.unit.Dp,
    onShipClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val backgroundResId = getStaryardBackgroundResId(ship.rarity)
    val shipImageResId = getSelectionScreenImageResId(ship.id)
    
    Box(
        modifier = modifier
            .width(containerWidth)
            .aspectRatio(1f) // Maintain 1:1 aspect ratio
            .clickable(onClick = onShipClick)
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
        
        // Price badge: Always shown, positioned at bottom center with 16dp bottom padding
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            PriceBadge(
                price = price,
                canAfford = price <= userCredits
            )
        }
    }
}

/**
 * ShipCardPlaceholderContainer composable - displays an empty 1:1 container
 * using the same background style as ships, for shipcard progress entries.
 *
 * @param rarity The rarity of the target ship (controls background style)
 * @param containerWidth The calculated width for the container
 * @param onClick Callback when the container is clicked
 * @param modifier Modifier for the container
 */
@Composable
private fun ShipCardPlaceholderContainer(
    rarity: ShipRarity,
    containerWidth: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val backgroundResId = getStaryardBackgroundResId(rarity)

    Box(
        modifier = modifier
            .width(containerWidth)
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    ) {
        // Background SVG only – no inner content yet
        Image(
            painter = painterResource(id = backgroundResId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

/**
 * PriceBadge composable - displays the ship price with credits icon.
 * 
 * Badge specifications:
 * - Fixed height: 24dp
 * - Label: Price value, 14sp, Medium weight
 * - Credits icon: 16dp width, 8dp spacing from label
 * - Internal padding: 8dp for both elements
 * - Background fill: #373A3E
 * - Border: 1dp stroke
 * - Colors:
 *   - If price > user credits: stroke, label, and icon in #F87F7F (red)
 *   - If price <= user credits: stroke, label, and icon in white (#FFFFFF)
 * 
 * @param price The price to display
 * @param canAfford Whether the user can afford this ship
 * @param modifier Modifier for the badge
 */
@Composable
private fun PriceBadge(
    price: Int,
    canAfford: Boolean,
    modifier: Modifier = Modifier
) {
    val textColor = if (canAfford) {
        Color(0xFFFFFFFF) // White if affordable
    } else {
        Color(0xFFF87F7F) // Red if not affordable
    }
    
    val borderColor = if (canAfford) {
        Color(0xFF6B6C6F) // #6B6C6F border if affordable
    } else {
        Color(0xFFF87F7F) // Red border if not affordable
    }
    
    Box(
        modifier = modifier
            .height(24.dp)
            .wrapContentWidth()
            .clip(RoundedCornerShape(8.dp)) // 8dp corner radius
            .background(Color(0xFF373A3E)) // Background fill
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp) // 8dp corner radius
            )
            .padding(horizontal = 8.dp), // 8dp internal padding
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp), // 8dp spacing between icon and label
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Credits icon: 16dp width, maintaining aspect ratio
            Image(
                painter = painterResource(id = R.drawable.creditsicon),
                contentDescription = "Credits",
                modifier = Modifier.width(16.dp), // 16dp width, maintaining aspect ratio
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(textColor) // Use text color for icon
            )
            
            // Price label: 14sp, medium weight
            Text(
                text = price.toString(),
                fontFamily = Exo2,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Helper function to get the background drawable resource ID based on ship rarity.
 * Uses default (non-active) background for all ships in staryard.
 * 
 * @param rarity The ship's rarity (COMMON, UNCOMMON, RARE, EPIC, LEGENDARY)
 * @return The drawable resource ID for the background
 */
private fun getStaryardBackgroundResId(rarity: ShipRarity): Int {
    val rarityName = when (rarity) {
        ShipRarity.COMMON -> "common"
        ShipRarity.UNCOMMON -> "uncommon"
        ShipRarity.RARE -> "rare"
        ShipRarity.EPIC -> "epic"
        ShipRarity.LEGENDARY -> "legendary"
        ShipRarity.MYTHICAL -> "mythical"
    }
    val drawableName = "selectionbackground${rarityName}default"
    
    return when (drawableName) {
        "selectionbackgroundcommondefault" -> R.drawable.selectionbackgroundcommondefault
        "selectionbackgrounduncommondefault" -> R.drawable.selectionbackgrounduncommondefault
        "selectionbackgroundraredefault" -> R.drawable.selectionbackgroundraredefault
        "selectionbackgroundepicdefault" -> R.drawable.selectionbackgroundepicdefault
        "selectionbackgroundlegendarydefault" -> R.drawable.selectionbackgroundlegendarydefault
        "selectionbackgroundmythicaldefault" -> R.drawable.selectionbackgroundmythicaldefault
        else -> R.drawable.selectionbackgroundcommondefault // Fallback
    }
}

/**
 * Helper function to get the selection screen image resource ID based on ship ID.
 * 
 * Maps ship IDs to their corresponding selection screen images.
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
        "ship23" -> R.drawable.ship23selectionscreen
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
        "asn_h99_dragoon" -> R.drawable.ship18selectionscreen
        "a450_sparrow" -> R.drawable.ship20selectionscreen
        "t47_dolphin" -> R.drawable.ship21selectionscreen
        "legendary_ship" -> R.drawable.ship12selectionscreen
        else -> R.drawable.ship1selectionscreen // Fallback
    }
}
