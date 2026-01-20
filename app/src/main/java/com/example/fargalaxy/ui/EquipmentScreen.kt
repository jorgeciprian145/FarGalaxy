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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

/**
 * EquipmentScreen composable - displays the equipment screen where users can purchase boosts.
 * 
 * Layout structure:
 * - Background: shipscreenbackground image (same as StaryardScreen)
 * - Top gradient: 20% height, black to transparent
 * - Bottom gradient: 25% height, transparent to black
 * - Top controls: Back button and "Equipment" title
 * - Scrollable content: (to be implemented)
 * - Clipping behavior: Content clips at 83dp from top when scrolling (same as StaryardScreen)
 * 
 * Scroll behavior:
 * - Same scrolling behavior as StaryardScreen
 * 
 * @param onBackClick Callback when the back button is clicked
 * @param modifier Modifier for the screen
 */
@Composable
fun EquipmentScreen(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Scroll state
    val scrollState = rememberScrollState()
    
    // Get density to convert dp to pixels
    val density = LocalDensity.current
    
    // Calculate if content is being clipped (scroll position >= 16dp means content moved up past initial spacer)
    // When scrolled 16dp or more, content reaches the clip boundary at 83dp
    val isContentClipped = derivedStateOf {
        with(density) {
            scrollState.value >= 16.dp.toPx().toInt()
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Background image: Same as StaryardScreen
        Image(
            painter = painterResource(id = R.drawable.shipscreenbackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Top gradient overlay: Covers 20% of screen height, creating a fade effect at the top.
        // Gradient transitions from solid black at the top to transparent at the bottom.
        // Same as StaryardScreen
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
        // Same as StaryardScreen
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
        // Positioned at the same location as the top controls in StaryardScreen
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
            // Back button: Same size and style as StaryardScreen
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
            
            // Title: "Equipment"
            // Same font style as "Staryard" in StaryardScreen
            // Horizontally centered on the screen
            Text(
                text = "Equipment",
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
            // Initially, content starts 4dp below clip line (via spacer)
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
                // Initial spacer: Push content down 4dp from clip line
                Spacer(modifier = Modifier.height(4.dp))
                
                // Label: "Purchase boosts to use when traveling" - center aligned, 14sp, regular weight
                Text(
                    text = "Purchase boosts to use when traveling",
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
                
                // Container: Items count on left, credits on right
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Items count label - bold, 20sp
                    // TODO: Replace with dynamic count when equipment items are added
                    Text(
                        text = "4 items available",
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
                            // TODO: Replace with dynamic value matching VaultScreen
                            Text(
                                text = "2600",
                                fontFamily = Exo2,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFFFFFFF) // White color
                            )
                        }
                    }
                }
                
                // 24dp spacing below the credits container
                Spacer(modifier = Modifier.height(24.dp))
                
                // Equipment grid: 2 columns, 16dp side padding, 8dp spacing between cards
                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Calculate single column width: (screen width - 32dp padding - 8dp spacing) / 2
                    val singleColumnWidth = (maxWidth - 32.dp - 8.dp) / 2
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp) // 8dp spacing between rows
                    ) {
                        // First row: Full-width card (156dp height)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp), // 16dp side padding
                            horizontalArrangement = Arrangement.spacedBy(0.dp) // No spacing for full-width card
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(156.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0x29FFFFFF), // White at 16% opacity (left)
                                                Color(0x00FFFFFF)  // White at 0% opacity (right)
                                            )
                                        )
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFF6B6C6F),
                                        shape = RoundedCornerShape(0.dp) // No corner radius for now
                                    )
                                    .padding(horizontal = 0.dp, vertical = 8.dp) // 0dp side padding, 8dp vertical padding
                            ) {
                                // Inner container: stroke, no fill, 32dp corner radius
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                        .border(
                                            width = 1.dp,
                                            color = Color(0xFF6B6C6F),
                                            shape = RoundedCornerShape(32.dp) // 32dp corner radius
                                        )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Left: 1:1 aspect ratio container
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .aspectRatio(1f) // 1:1 aspect ratio
                                        ) {
                                            // Equipment background SVG: fills available space, keeps aspect ratio,
                                            // centered horizontally and vertically within the square container.
                                            Image(
                                                painter = painterResource(id = R.drawable.equipmentback),
                                                contentDescription = "Equipment background",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .align(Alignment.Center),
                                                contentScale = ContentScale.Fit
                                            )
                                            
                                            // PNG on top of SVG: modulatorselection
                                            Image(
                                                painter = painterResource(id = R.drawable.modulatorselection),
                                                contentDescription = "Modulator selection",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .align(Alignment.Center),
                                                contentScale = ContentScale.Fit
                                            )
                                        }
                                        
                                        // Right: Remaining space container
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .weight(1f) // Stretches to fill remaining width
                                                .padding(
                                                    start = 0.dp,
                                                    top = 8.dp,
                                                    end = 8.dp,
                                                    bottom = 8.dp
                                                ),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            // Right content: title + price/button container
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .wrapContentHeight(),
                                                verticalArrangement = Arrangement.spacedBy(16.dp)
                                            ) {
                                                // Title row: text + chevron
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    // Title: 14sp, bold, 18sp line height
                                                    Text(
                                                        text = "Emergency modulators",
                                                        fontFamily = Exo2,
                                                        fontSize = 14.sp,
                                                        lineHeight = 18.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFFFFFFF),
                                                        modifier = Modifier.weight(1f)
                                                    )

                                                    Spacer(modifier = Modifier.width(8.dp))

                                                    // Chevron container with right padding
                                                    Box(
                                                        modifier = Modifier.padding(end = 8.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Image(
                                                            painter = painterResource(id = R.drawable.chevronsmall),
                                                            contentDescription = "Chevron",
                                                            modifier = Modifier.height(8.dp),
                                                            contentScale = ContentScale.Fit,
                                                            colorFilter = ColorFilter.tint(Color(0xFFFFFFFF))
                                                        )
                                                    }
                                                }

                                                // Bottom container: price label + BUY button
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .border(
                                                            width = 1.dp,
                                                            color = Color(0xFF6B6C6F),
                                                            shape = RoundedCornerShape(50.dp)
                                                        )
                                                        .background(
                                                            color = Color(0xFF373A3E),
                                                            shape = RoundedCornerShape(50.dp)
                                                        )
                                                        .padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        // Price label: creditsicon + value
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Image(
                                                                painter = painterResource(id = R.drawable.creditsicon),
                                                                contentDescription = "Credits",
                                                                modifier = Modifier.width(16.dp),
                                                                contentScale = ContentScale.Fit,
                                                                colorFilter = ColorFilter.tint(Color(0xFFFFFFFF))
                                                            )

                                                            Spacer(modifier = Modifier.width(4.dp))

                                                            Text(
                                                                text = "2500",
                                                                fontFamily = Exo2,
                                                                fontSize = 16.sp,
                                                                fontWeight = FontWeight.SemiBold,
                                                                color = Color(0xFFFFFFFF)
                                                            )
                                                        }

                                                        // BUY button
                                                        Box(
                                                            modifier = Modifier
                                                                .height(24.dp)
                                                                .border(
                                                                    width = 1.dp,
                                                                    color = Color(0xFFFFFFFF),
                                                                    shape = RoundedCornerShape(50.dp)
                                                                ),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = "BUY",
                                                                fontFamily = Exo2,
                                                                fontSize = 14.sp,
                                                                fontWeight = FontWeight.W400,
                                                                color = Color(0xFFFFFFFF),
                                                                modifier = Modifier
                                                                    .offset(y = (-1).dp)
                                                                    .padding(horizontal = 16.dp)
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
                        
                        // Second row: Full-width card (156dp height)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp), // 16dp side padding
                            horizontalArrangement = Arrangement.spacedBy(0.dp) // No spacing for full-width card
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(156.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0x29FFFFFF), // White at 16% opacity (left)
                                                Color(0x00FFFFFF)  // White at 0% opacity (right)
                                            )
                                        )
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFF6B6C6F),
                                        shape = RoundedCornerShape(0.dp) // No corner radius for now
                                    )
                                    .padding(horizontal = 0.dp, vertical = 8.dp) // 0dp side padding, 8dp vertical padding
                            ) {
                                // Inner container: stroke, no fill, 32dp corner radius
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                        .border(
                                            width = 1.dp,
                                            color = Color(0xFF6B6C6F),
                                            shape = RoundedCornerShape(32.dp) // 32dp corner radius
                                        )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Left: 1:1 aspect ratio container
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .aspectRatio(1f) // 1:1 aspect ratio
                                        ) {
                                            // Equipment background SVG: fills available space, keeps aspect ratio,
                                            // centered horizontally and vertically within the square container.
                                            Image(
                                                painter = painterResource(id = R.drawable.equipmentback),
                                                contentDescription = "Equipment background",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .align(Alignment.Center),
                                                contentScale = ContentScale.Fit
                                            )
                                            
                                            // PNG on top of SVG: scannerselection
                                            Image(
                                                painter = painterResource(id = R.drawable.scannerselection),
                                                contentDescription = "Scanner selection",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .align(Alignment.Center),
                                                contentScale = ContentScale.Fit
                                            )
                                        }
                                        
                                        // Right: Remaining space container
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .weight(1f) // Stretches to fill remaining width
                                                .padding(
                                                    start = 0.dp,
                                                    top = 8.dp,
                                                    end = 8.dp,
                                                    bottom = 8.dp
                                                ),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            // Right content: title + price/button container
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .wrapContentHeight(),
                                                verticalArrangement = Arrangement.spacedBy(16.dp)
                                            ) {
                                                // Title row: text + chevron
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    // Title: 14sp, bold, 18sp line height
                                                    Text(
                                                        text = "Deep space scanners",
                                                        fontFamily = Exo2,
                                                        fontSize = 14.sp,
                                                        lineHeight = 18.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFFFFFFF),
                                                        modifier = Modifier.weight(1f)
                                                    )

                                                    Spacer(modifier = Modifier.width(8.dp))

                                                    // Chevron container with right padding
                                                    Box(
                                                        modifier = Modifier.padding(end = 8.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Image(
                                                            painter = painterResource(id = R.drawable.chevronsmall),
                                                            contentDescription = "Chevron",
                                                            modifier = Modifier.height(8.dp),
                                                            contentScale = ContentScale.Fit,
                                                            colorFilter = ColorFilter.tint(Color(0xFFFFFFFF))
                                                        )
                                                    }
                                                }

                                                // Bottom container: price label + BUY button
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .border(
                                                            width = 1.dp,
                                                            color = Color(0xFF6B6C6F),
                                                            shape = RoundedCornerShape(50.dp)
                                                        )
                                                        .background(
                                                            color = Color(0xFF373A3E),
                                                            shape = RoundedCornerShape(50.dp)
                                                        )
                                                        .padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        // Price label: creditsicon + value
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Image(
                                                                painter = painterResource(id = R.drawable.creditsicon),
                                                                contentDescription = "Credits",
                                                                modifier = Modifier.width(16.dp),
                                                                contentScale = ContentScale.Fit,
                                                                colorFilter = ColorFilter.tint(Color(0xFFFFFFFF))
                                                            )

                                                            Spacer(modifier = Modifier.width(4.dp))

                                                            Text(
                                                                text = "2500",
                                                                fontFamily = Exo2,
                                                                fontSize = 16.sp,
                                                                fontWeight = FontWeight.SemiBold,
                                                                color = Color(0xFFFFFFFF)
                                                            )
                                                        }

                                                        // BUY button
                                                        Box(
                                                            modifier = Modifier
                                                                .height(24.dp)
                                                                .border(
                                                                    width = 1.dp,
                                                                    color = Color(0xFFFFFFFF),
                                                                    shape = RoundedCornerShape(50.dp)
                                                                ),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = "BUY",
                                                                fontFamily = Exo2,
                                                                fontSize = 14.sp,
                                                                fontWeight = FontWeight.W400,
                                                                color = Color(0xFFFFFFFF),
                                                                modifier = Modifier
                                                                    .offset(y = (-1).dp)
                                                                    .padding(horizontal = 16.dp)
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
                        
                        // Third row: Two single-column cards (236dp height each)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp), // 16dp side padding
                            horizontalArrangement = Arrangement.spacedBy(8.dp) // 8dp spacing between cards
                        ) {
                            // First single-column card
                            Box(
                                modifier = Modifier
                                    .width(singleColumnWidth)
                                    .height(236.dp)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0x29FFFFFF), // White at 16% opacity (top)
                                                Color(0x00FFFFFF)  // White at 0% opacity (bottom)
                                            )
                                        )
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFF6B6C6F),
                                        shape = RoundedCornerShape(0.dp) // No corner radius for now
                                    )
                                    .padding(0.dp) // 0dp padding on all sides
                            ) {
                                // Inner container: stroke, no fill, 32dp corner radius
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                        .border(
                                            width = 1.dp,
                                            color = Color(0xFF6B6C6F),
                                            shape = RoundedCornerShape(32.dp) // 32dp corner radius
                                        )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(),
                                        verticalArrangement = Arrangement.spacedBy(0.dp)
                                    ) {
                                        // Top: 1:1 aspect ratio container
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(1f) // 1:1 aspect ratio
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.equipmentback),
                                                contentDescription = "Equipment background",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .align(Alignment.Center),
                                                contentScale = ContentScale.Fit
                                            )
                                            
                                            // PNG on top of SVG: cargoselection
                                            Image(
                                                painter = painterResource(id = R.drawable.cargoselection),
                                                contentDescription = "Cargo selection",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .align(Alignment.Center),
                                                contentScale = ContentScale.Fit
                                            )
                                        }
                                        
                                        // Bottom: Remaining space container
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f) // Stretches to fill remaining height
                                        ) {
                                            // Content will be added here later
                                        }
                                    }
                                }
                            }
                            
                            // Second single-column card
                            Box(
                                modifier = Modifier
                                    .width(singleColumnWidth)
                                    .height(236.dp)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0x29FFFFFF), // White at 16% opacity (top)
                                                Color(0x00FFFFFF)  // White at 0% opacity (bottom)
                                            )
                                        )
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFF6B6C6F),
                                        shape = RoundedCornerShape(0.dp) // No corner radius for now
                                    )
                                    .padding(0.dp) // 0dp padding on all sides
                            ) {
                                // Inner container: stroke, no fill, 32dp corner radius
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                        .border(
                                            width = 1.dp,
                                            color = Color(0xFF6B6C6F),
                                            shape = RoundedCornerShape(32.dp) // 32dp corner radius
                                        )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(),
                                        verticalArrangement = Arrangement.spacedBy(0.dp)
                                    ) {
                                        // Top: 1:1 aspect ratio container
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(1f) // 1:1 aspect ratio
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.equipmentback),
                                                contentDescription = "Equipment background",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .align(Alignment.Center),
                                                contentScale = ContentScale.Fit
                                            )
                                            
                                            // PNG on top of SVG: fuelselection
                                            Image(
                                                painter = painterResource(id = R.drawable.fuelselection),
                                                contentDescription = "Fuel selection",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .align(Alignment.Center),
                                                contentScale = ContentScale.Fit
                                            )
                                        }
                                        
                                        // Bottom: Remaining space container
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f) // Stretches to fill remaining height
                                        ) {
                                            // Content will be added here later
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
