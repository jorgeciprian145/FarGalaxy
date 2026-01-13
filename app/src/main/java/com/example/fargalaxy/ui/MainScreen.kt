package com.example.fargalaxy.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fargalaxy.R
import com.example.fargalaxy.data.ShipRepository
import com.example.fargalaxy.model.Ship
import kotlinx.coroutines.delay
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
    
    // Track if ShipSelectionScreen should be shown
    var showShipSelection by remember { mutableStateOf(false) }
    
    // Track if LocationsScreen should be shown
    var showLocations by remember { mutableStateOf(false) }
    
    // Track if LocationDetailsScreen should be shown
    var showLocationDetails by remember { mutableStateOf(false) }
    
    // Track if FactionDetailsScreen should be shown
    var showFactionDetails by remember { mutableStateOf(false) }
    
    // Track the selected faction for details
    var selectedFactionForDetails by remember { mutableStateOf<String?>(null) }
    
    // Track if we should reset scroll in ShipSelectionScreen (true when opening from CareerScreen)
    var shouldResetShipSelectionScroll by remember { mutableStateOf(false) }
    
    // Track if we should reset scroll in LocationsScreen (true when opening from CareerScreen)
    var shouldResetLocationsScroll by remember { mutableStateOf(false) }
    
    // Track the selected location for details
    var selectedLocationForDetails by remember { mutableStateOf<com.example.fargalaxy.model.Location?>(null) }
    
    // Track the current ship
    var currentShip by remember { mutableStateOf<Ship>(ShipRepository.getCurrentShip()) }
    
    // Track the selected ship for details (separate from currentShip)
    var selectedShipForDetails by remember { mutableStateOf<Ship?>(null) }
    
    // Track scroll to top trigger for CareerScreen (incremented to trigger scroll)
    var scrollToTopTrigger by remember { mutableStateOf(0) }
    
    // Get activity context for exiting app
    val context = LocalContext.current
    val activity = context as? Activity
    
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
    
    // Track if ShipDetailsScreen was opened from CareerScreen (for "CHANGE SHIP" button)
    var isFromCareerScreen by remember { mutableStateOf(false) }
    
    // Handle ship details navigation from CareerScreen
    val onViewShipClick: () -> Unit = {
        isFromCareerScreen = true
        selectedShipForDetails = null // Clear any previous selection
        showShipDetails = true
    }
    
    // Handle back from ship details
    val onBackFromShipDetails: () -> Unit = {
        showShipDetails = false
    }
    
    // Handle ship selection navigation
    val onShipSelectionClick: () -> Unit = {
        shouldResetShipSelectionScroll = true // Reset scroll when opening from CareerScreen
        showShipSelection = true
    }
    
    // Handle back from ship selection
    val onBackFromShipSelection: () -> Unit = {
        showShipSelection = false
    }
    
    // Handle locations navigation
    val onLocationsClick: () -> Unit = {
        shouldResetLocationsScroll = true // Reset scroll when opening from CareerScreen
        showLocations = true
    }
    
    // Handle back from locations
    val onBackFromLocations: () -> Unit = {
        showLocations = false
    }
    
    // Handle location click from LocationsScreen
    val onLocationClick: (com.example.fargalaxy.model.Location) -> Unit = { location ->
        selectedLocationForDetails = location
        showLocationDetails = true
        // Don't hide LocationsScreen - keep it in composition to preserve scroll position
    }
    
    // Handle back from location details
    val onBackFromLocationDetails: () -> Unit = {
        showLocationDetails = false
        selectedLocationForDetails = null
        // LocationsScreen is still in composition, so scroll position is preserved
    }
    
    // Reset the scroll flag after it's been used
    LaunchedEffect(shouldResetShipSelectionScroll, showShipSelection) {
        if (shouldResetShipSelectionScroll && showShipSelection) {
            // Flag has been passed to ShipSelectionScreen, reset it after a brief delay
            kotlinx.coroutines.delay(100)
            shouldResetShipSelectionScroll = false
        }
    }
    
    // Reset the locations scroll flag after it's been used
    LaunchedEffect(shouldResetLocationsScroll, showLocations) {
        if (shouldResetLocationsScroll && showLocations) {
            // Flag has been passed to LocationsScreen, reset it after a brief delay
            kotlinx.coroutines.delay(100)
            shouldResetLocationsScroll = false
        }
    }
    
    // Handle ship click from ShipSelectionScreen
    val onShipClick: (Ship) -> Unit = { ship ->
        isFromCareerScreen = false // Not from CareerScreen
        selectedShipForDetails = ship
        showShipDetails = true
        // Don't hide ShipSelectionScreen - keep it in composition to preserve scroll position
    }
    
    // Handle back from ship details - return to previous screen
    val onBackFromShipDetailsUpdated: () -> Unit = {
        showShipDetails = false
        isFromCareerScreen = false
        // If we came from ShipSelectionScreen, it's still in composition so scroll position is preserved
        // If we came from CareerScreen, just hide details (returns to CareerScreen)
        if (selectedShipForDetails != null) {
            selectedShipForDetails = null
            // ShipSelectionScreen is still in composition, so scroll position is preserved
            // No need to show it again or reset scroll
        }
    }
    
    // Handle "CHANGE SHIP" button click - navigate to ship selection screen
    val onChangeShip: () -> Unit = {
        showShipDetails = false
        isFromCareerScreen = false
        shouldResetShipSelectionScroll = false // Don't reset scroll when coming from ShipDetailsScreen
        showShipSelection = true
    }
    
    // Handle ship selection - set the ship as current and navigate to CareerScreen
    val onSelectShip: () -> Unit = {
        val shipToSelect = selectedShipForDetails
        if (shipToSelect != null) {
            // Update the current ship in repository
            ShipRepository.setCurrentShip(shipToSelect.id)
            // Update local state
            currentShip = shipToSelect
            // Close details screen and selection screen
            showShipDetails = false
            showShipSelection = false
            selectedShipForDetails = null
            // Navigate to CareerScreen
            navigateToPage(0) // Navigate to CareerScreen (page 0)
        }
    }
    
    // Disable user scrolling when not idle
    val userScrollEnabled = isGalaxyIdle
    
    // Handle back button press
    BackHandler(enabled = true) {
        when {
            // If FactionDetailsScreen is shown, close it
            showFactionDetails -> {
                showFactionDetails = false
                selectedFactionForDetails = null
            }
            // If LocationDetailsScreen is shown, close it
            showLocationDetails -> {
                onBackFromLocationDetails()
            }
            // If ShipDetailsScreen is shown, close it (check before ShipSelectionScreen since it's on top)
            showShipDetails -> {
                onBackFromShipDetailsUpdated()
            }
            // If LocationsScreen is shown, close it
            showLocations -> {
                onBackFromLocations()
            }
            // If ShipSelectionScreen is shown, close it
            showShipSelection -> {
                onBackFromShipSelection()
            }
            // If on CareerScreen (page 0), scroll to top then navigate to GalaxyScreen
            pagerState.currentPage == 0 -> {
                coroutineScope.launch {
                    // Trigger scroll to top
                    scrollToTopTrigger++
                    // Wait a bit for scroll animation to complete
                    delay(300)
                    // Navigate to GalaxyScreen (page 1)
                    navigateToPage(1)
                }
            }
            // If on VaultScreen (page 2), navigate to GalaxyScreen
            pagerState.currentPage == 2 -> {
                navigateToPage(1)
            }
            // If on GalaxyScreen (page 1), exit the app
            pagerState.currentPage == 1 -> {
                activity?.finish()
            }
        }
    }
    
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
                        onViewShipClick = onViewShipClick,
                        onShipSelectionClick = onShipSelectionClick,
                        onLocationsClick = onLocationsClick,
                        onBackClick = { navigateToPage(1) }, // Navigate to GalaxyScreen
                        totalTravelMinutes = 45, // TODO: Connect to actual data source
                        isPageActive = pagerState.currentPage == 0 && !showShipDetails && !showShipSelection && !showLocations && !showLocationDetails, // Track when page is active and overlays are closed
                        scrollToTopTrigger = scrollToTopTrigger
                    )
                }
                1 -> {
                    // GalaxyScreen - content only (no background/noise/indicator)
                    GalaxyScreen(
                        currentShip = currentShip,
                        isIdleCallback = { idle -> isGalaxyIdle = idle },
                        activeScreen = activeScreen,
                        onCareerClick = onCareerClick,
                        onCollectionClick = onCollectionClick
                    )
                }
                2 -> {
                    // VaultScreen - content only (no background/noise/indicator)
                    VaultScreen(
                        onBackClick = { navigateToPage(1) } // Navigate to GalaxyScreen
                    )
                }
            }
        }
        
        // Static noise overlay - doesn't move when swiping (above content, below indicator)
        // Hide noise when ShipDetailsScreen, ShipSelectionScreen, LocationsScreen, or LocationDetailsScreen is shown (they have their own backgrounds)
        if (!showShipDetails && !showShipSelection && !showLocations && !showLocationDetails) {
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
        // Also hide when ShipDetailsScreen, ShipSelectionScreen, LocationsScreen, or LocationDetailsScreen is shown
        if (!showShipDetails && !showShipSelection && !showLocations && !showLocationDetails && (pagerState.currentPage == 0 || pagerState.currentPage == 2 || isGalaxyIdle)) {
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
        
        // LocationsScreen overlay - shown on top of everything when showLocations is true
        if (showLocations) {
            Box(modifier = Modifier.fillMaxSize()) {
                LocationsScreen(
                    onBackClick = onBackFromLocations,
                    onLocationClick = onLocationClick,
                    shouldResetScroll = shouldResetLocationsScroll
                )
                
                // Block pointer events when LocationDetailsScreen is shown
                if (showLocationDetails) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                // Consume all pointer events to prevent interaction with LocationsScreen
                                detectTapGestures { }
                            }
                    )
                }
            }
        }
        
        // LocationDetailsScreen overlay - shown on top of everything when showLocationDetails is true
        if (showLocationDetails && selectedLocationForDetails != null) {
            LocationDetailsScreen(
                location = selectedLocationForDetails!!,
                onBackClick = onBackFromLocationDetails,
                onFactionBadgeClick = { faction ->
                    selectedFactionForDetails = faction
                    showFactionDetails = true
                }
            )
            
            // Block pointer events when FactionDetailsScreen is shown
            if (showFactionDetails) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            // Consume all pointer events to prevent interaction with LocationDetailsScreen
                            detectTapGestures { }
                        }
                )
            }
        }
        
        // FactionDetailsScreen overlay - shown on top of everything when showFactionDetails is true
        if (showFactionDetails && selectedFactionForDetails != null) {
            FactionDetailsScreen(
                faction = selectedFactionForDetails!!,
                onBackClick = {
                    showFactionDetails = false
                    selectedFactionForDetails = null
                }
            )
        }
        
        // ShipSelectionScreen overlay - shown on top of everything when showShipSelection is true
        if (showShipSelection) {
            Box(modifier = Modifier.fillMaxSize()) {
                ShipSelectionScreen(
                    onBackClick = onBackFromShipSelection,
                    onShipClick = onShipClick,
                    shouldResetScroll = shouldResetShipSelectionScroll
                )
                
                // Block pointer events when ShipDetailsScreen is shown
                if (showShipDetails) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                // Consume all pointer events to prevent interaction with ShipSelectionScreen
                                detectTapGestures { }
                            }
                    )
                }
            }
        }
        
        // ShipDetailsScreen overlay - shown on top of everything when showShipDetails is true
        if (showShipDetails) {
            ShipDetailsScreen(
                ship = selectedShipForDetails ?: currentShip,
                currentShip = currentShip,
                onBackClick = onBackFromShipDetailsUpdated,
                onSelectShip = onSelectShip,
                onChangeShip = if (isFromCareerScreen) onChangeShip else null
            )
        }
    }
}

