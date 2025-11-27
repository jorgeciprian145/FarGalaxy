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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import com.example.fargalaxy.model.Ship
import com.example.fargalaxy.model.ShipRarity

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
 * @param onBackClick Callback when the back button is clicked
 * @param modifier Modifier for the screen
 */
@Composable
fun ShipSelectionScreen(
    onBackClick: () -> Unit = {},
    onShipClick: (Ship) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Get list of available ships (for now, using all ships from repository)
    // TODO: Filter to only show unlocked ships
    val allShips = ShipRepository.getAllShips()
    val currentShip = ShipRepository.getCurrentShip()
    
    // Ensure current ship is always first, then add other ships
    val availableShips = if (allShips.isNotEmpty()) {
        val otherShips = allShips.filter { it.id != currentShip.id }
        listOf(currentShip) + otherShips
    } else {
        allShips
    }
    
    val shipsCount = availableShips.size
    
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
                    .padding(start = 24.dp)
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
        
        // Clip boundary container: Box positioned at 115dp to define clipping boundary
        // Same calculation as CareerScreen:
        // Indicator bottom is at: statusBarPadding + 48dp + 51dp = statusBarPadding + 99dp
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
                    .padding(bottom = 32.dp), // Allow last row to be 32dp above bottom bar
                verticalArrangement = Arrangement.spacedBy(0.dp) // Manual spacing control
            ) {
                // Initial spacer: Push content down 8dp from clip line (so it starts at 123dp visually)
                Spacer(modifier = Modifier.height(8.dp))
                
                // Spacing from counter/button container: 32px
                Spacer(modifier = Modifier.height(32.dp))
                
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
                                    ShipContainer(
                                        ship = ship,
                                        isCurrentShip = isCurrentShip,
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
 * - legendary_ship -> ship13selectionscreen
 * 
 * @param shipId The ship's ID
 * @return The drawable resource ID for the selection screen image
 */
private fun getSelectionScreenImageResId(shipId: String): Int {
    return when (shipId) {
        "b14_phantom" -> R.drawable.ship1selectionscreen
        "type45c_shooting_star" -> R.drawable.ship2selectionscreen
        "a300_albatross" -> R.drawable.ship4selectionscreen
        "b7f_starforce" -> R.drawable.ship5selectionscreen
        "h98_valkyrie" -> R.drawable.ship10selectionscreen
        "legendary_ship" -> R.drawable.ship13selectionscreen
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
    modifier: Modifier = Modifier
) {
    val backgroundResId = getSelectionBackgroundResId(ship.rarity, isCurrentShip)
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
            .height(16.dp)
            .wrapContentWidth()
            .clip(RoundedCornerShape(80.dp))
            .background(Color(0xFFFFFFFF)) // White background
            .padding(horizontal = 8.dp), // 8dp padding on each side (16dp total)
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "CURRENT SHIP",
            fontFamily = Exo2,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF010102), // Very dark color
            textAlign = TextAlign.Center,
            modifier = Modifier.offset(y = (-4).dp) // -1dp vertical offset
        )
    }
}

