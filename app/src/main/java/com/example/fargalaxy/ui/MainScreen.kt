package com.example.fargalaxy.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fargalaxy.R
import com.example.fargalaxy.data.ShipRepository
import com.example.fargalaxy.model.Ship
import kotlinx.coroutines.launch

/**
 * MainScreen composable - manages navigation between CareerScreen, GalaxyScreen, and VaultScreen.
 * Uses HorizontalPager for smooth swipe transitions.
 * Static layers (background, noise, indicator) are placed outside the pager so they remain fixed.
 * 
 * Pages:
 * - Index 0: CareerScreen (left)
 * - Index 1: GalaxyScreen (center)
 * - Index 2: VaultScreen (right)
 */
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    // Pager state - initial page is 1 (GalaxyScreen)
    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { 3 }
    )
    
    val coroutineScope = rememberCoroutineScope()
    
    // Track if GalaxyScreen is idle (not preparing or traveling)
    // This will be updated by GalaxyScreen
    var isGalaxyIdle by remember { mutableStateOf(true) }
    
    // Track active screen based on current page
    var activeScreen by remember { mutableStateOf(ActiveScreen.CENTER) }
    
    // Track if ShipDetailsScreen should be shown
    var showShipDetails by remember { mutableStateOf(false) }
    
    // Track the current ship
    var currentShip by remember { mutableStateOf<Ship>(ShipRepository.getCurrentShip()) }
    
    // Update active screen when page changes
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            activeScreen = when (page) {
                0 -> ActiveScreen.LEFT   // CareerScreen
                1 -> ActiveScreen.CENTER // GalaxyScreen
                2 -> ActiveScreen.RIGHT  // VaultScreen
                else -> ActiveScreen.CENTER
            }
        }
    }
    
    // Navigate to a specific page
    fun navigateToPage(page: Int) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(page)
        }
    }
    
    // Handle career icon click - only navigate if idle
    val onCareerClick: () -> Unit = {
        if (isGalaxyIdle) {
            navigateToPage(0) // Navigate to CareerScreen
        }
    }
    
    // Handle collection icon click - navigate to VaultScreen (only if idle)
    val onCollectionClick: () -> Unit = {
        if (isGalaxyIdle) {
            navigateToPage(2) // Navigate to VaultScreen
        }
    }
    
    // Handle ship details navigation
    val onViewShipClick: () -> Unit = {
        showShipDetails = true
    }
    
    // Handle back from ship details
    val onBackFromShipDetails: () -> Unit = {
        showShipDetails = false
    }
    
    // Disable user scrolling when not idle
    val userScrollEnabled = isGalaxyIdle
    
    Box(modifier = modifier.fillMaxSize()) {
        // Static background layer - doesn't move when swiping (bottom layer)
        Image(
            painter = painterResource(id = R.drawable.bg_galaxy),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Swipeable content - only this moves when swiping
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = userScrollEnabled
        ) { page ->
            when (page) {
                0 -> {
                    // CareerScreen - content only (no background/noise/indicator)
                    CareerScreen(
                        currentShip = currentShip,
                        onViewShipClick = onViewShipClick
                    )
                }
                1 -> {
                    // GalaxyScreen - content only (no background/noise/indicator)
                    GalaxyScreen(
                        isIdleCallback = { idle -> isGalaxyIdle = idle },
                        activeScreen = activeScreen,
                        onCareerClick = onCareerClick,
                        onCollectionClick = onCollectionClick
                    )
                }
                2 -> {
                    // VaultScreen - content only (no background/noise/indicator)
                    VaultScreen()
                }
            }
        }
        
        // Static noise overlay - doesn't move when swiping (above content, below indicator)
        // Hide noise when ShipDetailsScreen is shown (it has its own background)
        if (!showShipDetails) {
            Image(
                painter = painterResource(id = R.drawable.noise_8bit),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.085f),
                contentScale = ContentScale.Crop
            )
        }
        
        // Static indicator - positioned above everything (rendered last so it's on top)
        // Hide indicator when traveling or preparing (only show when idle or on CareerScreen/VaultScreen)
        // Also hide when ShipDetailsScreen is shown
        if (!showShipDetails && (pagerState.currentPage == 0 || pagerState.currentPage == 2 || isGalaxyIdle)) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 48.dp)
                    .fillMaxWidth()
                    .height(51.dp),
                contentAlignment = Alignment.Center
            ) {
                Indicator(activeScreen = activeScreen)
            }
        }
        
        // ShipDetailsScreen overlay - shown on top of everything when showShipDetails is true
        if (showShipDetails) {
            ShipDetailsScreen(
                ship = currentShip,
                onBackClick = onBackFromShipDetails
            )
        }
    }
}

