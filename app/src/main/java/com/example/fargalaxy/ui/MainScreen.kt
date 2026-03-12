package com.example.fargalaxy.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fargalaxy.R
import com.example.fargalaxy.data.CrateOpenResult
import com.example.fargalaxy.data.CrateRepository
import com.example.fargalaxy.data.CrateType
import com.example.fargalaxy.data.GameStateRepository
import com.example.fargalaxy.data.ShipRepository
import com.example.fargalaxy.model.Ship
import com.example.fargalaxy.model.ShipRarity
import com.example.fargalaxy.utils.playMouseClickSound
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
    
    // Track if rewards screen is shown (to hide indicator)
    var isRewardsScreenShown by remember { mutableStateOf(false) }
    
    // Track if ship unlocked screen is shown (to hide indicator and block pager)
    var isShipUnlockedScreenShown by remember { mutableStateOf(false) }
    
    // Track if location discovered screen is shown (to hide indicator and block pager)
    var isLocationDiscoveredScreenShown by remember { mutableStateOf(false) }
    
    // Track if ship acquired screen is shown (to hide indicator and block pager)
    var showShipAcquiredScreen by remember { mutableStateOf(false) }
    var acquiredShipId by remember { mutableStateOf<String?>(null) }
    
    // Track if boost selection bottom sheet is shown (to hide indicator)
    var isBoostSelectionBottomSheetShown by remember { mutableStateOf(false) }
    
    // Toast state
    var toastMessage by remember { mutableStateOf<String?>(null) }
    
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
    
    // Track if StaryardScreen should be shown
    var showStaryard by remember { mutableStateOf(false) }
    
    // Track if StaryardDetailsScreen should be shown
    var showStaryardDetails by remember { mutableStateOf(false) }
    
    // Track if ShipCardProgressModal should be shown (shipcards detail overlay)
    var showShipCardProgressModal by remember { mutableStateOf(false) }
    var shipCardProgressShipId by remember { mutableStateOf("asn_ag94_centurion") }
    var shipCardProgressRarity by remember { mutableStateOf(ShipRarity.UNCOMMON) }
    var shipCardOwnedCards by remember { mutableStateOf(0) }
    
    // Track if EquipmentScreen should be shown
    var showEquipment by remember { mutableStateOf(false) }
    
    // Track if EquipmentDetailsScreen should be shown
    var showEquipmentDetails by remember { mutableStateOf(false) }
    
    // Track the selected equipment for details
    var selectedEquipmentName by remember { mutableStateOf("") }
    var selectedEquipmentImageResId by remember { mutableStateOf(0) }
    var selectedEquipmentPrice by remember { mutableStateOf(0) }
    var selectedEquipmentDescription by remember { mutableStateOf("") }
    
    // Track if StoreScreen should be shown
    var showStore by remember { mutableStateOf(false) }
    
    // Track if StoreDetailsScreen should be shown
    var showStoreDetails by remember { mutableStateOf(false) }
    
    // Track the selected store item for details
    var selectedStoreItemName by remember { mutableStateOf("") }
    var selectedStoreItemImageResId by remember { mutableStateOf(0) }
    var selectedStoreItemPrice by remember { mutableStateOf("") }
    var selectedStoreItemPriceType by remember { mutableStateOf("credits") }
    var selectedStoreItemDescription by remember { mutableStateOf("") }
    
    // Track the selected ship for staryard details
    var selectedShipForStaryardDetails by remember { mutableStateOf<Ship?>(null) }
    
    // Track the price of the selected ship for staryard details
    var selectedShipPrice by remember { mutableStateOf(0) }
    
    // User credits - read from global repository
    val userCredits = com.example.fargalaxy.data.UserDataRepository.userCredits
    
    // Ship prices for testing (matching StaryardScreen)
    // Ship prices based on focus time unlock requirements (must match StaryardScreen.kt)
    val shipPrices = mapOf(
        "type45c_shooting_star" to 2500, // ship2: 15 mins
        "navakeshi_star_pouncer" to 5000, // ship3: 40 mins
        "a300_albatross" to 8500, // ship4: 60 mins
        "b7f_starforce" to 12000, // ship5: 90 mins
        "navakeshi_star_crusher" to 15000, // ship6: 150 mins
        "b15_specter" to 20000, // ship7: 240 mins
        "n6_98_melina" to 26000, // ship8: 360 mins
        "model3_tortoise_ccp" to 32000, // ship9: 500 mins
        "h98_valkyrie" to 38000, // ship10: 700 mins
        "navakeshi_star_ravager" to 45000, // ship11: 1000 mins
        "silver_lightning" to 60000, // ship12: 1400 mins
        "vulcani_legenda_f1" to 65000, // ship13: 2000 mins
        "force_of_nature" to 80000 // ship14: 3000 mins
    )
    
    // Track if FactionDetailsScreen should be shown
    var showFactionDetails by remember { mutableStateOf(false) }
    
    // Track the selected faction for details
    var selectedFactionForDetails by remember { mutableStateOf<String?>(null) }
    
    // Track if SectorDetailsScreen should be shown
    var showSectorDetails by remember { mutableStateOf(false) }
    
    // Track if we should reset scroll in ShipSelectionScreen (true when opening from CareerScreen)
    var shouldResetShipSelectionScroll by remember { mutableStateOf(false) }
    
    // Track if we should reset scroll in LocationsScreen (true when opening from CareerScreen)
    var shouldResetLocationsScroll by remember { mutableStateOf(false) }
    
    // Track the selected location for details
    var selectedLocationForDetails by remember { mutableStateOf<com.example.fargalaxy.model.Location?>(null) }
    
    // Track the current ship - always read fresh from repository to ensure it's up to date
    // This ensures that after resetProgress() or other repository changes, we get the latest ship
    var currentShip by remember { mutableStateOf<Ship>(ShipRepository.getCurrentShip()) }
    
    // Update currentShip when repository changes (e.g., after resetProgress or app restart)
    // Use a small delay to ensure repositories are fully initialized after resetProgress()
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100) // Delay to ensure resetProgress() has completed
        currentShip = ShipRepository.getCurrentShip() // Always update to ensure we have the latest ship
    }
    
    // Track the selected ship for details (separate from currentShip)
    var selectedShipForDetails by remember { mutableStateOf<Ship?>(null) }
    
    // Track scroll to top trigger for CareerScreen (incremented to trigger scroll)
    var scrollToTopTrigger by remember { mutableStateOf(0) }
    
    // Crate inventory state (backed by CrateRepository, but mirrored here for Compose state)
    var standardCrates by remember { mutableStateOf(CrateRepository.getCrateCount(CrateType.STANDARD)) }
    var advancedCrates by remember { mutableStateOf(CrateRepository.getCrateCount(CrateType.ADVANCED)) }
    var eliteCrates by remember { mutableStateOf(CrateRepository.getCrateCount(CrateType.ELITE)) }

    // Crate opening overlay state
    var showCrateOpeningOverlay by remember { mutableStateOf(false) }
    var openingCrateType by remember { mutableStateOf<CrateType?>(null) }
    var crateOpenResult by remember { mutableStateOf<CrateOpenResult?>(null) }
    
    // Track ship unlocked via crate cards
    var shipUnlockedFromCrate by remember { mutableStateOf<String?>(null) }

    // Get activity context for exiting app and for showing interstitial ads
    val context = LocalContext.current
    val activity = context as? Activity
    
    // Update active screen when page changes and trigger interstitials for Career/Vault visits
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            activeScreen = when (page) {
                0 -> ActiveScreen.LEFT   // CareerScreen
                1 -> ActiveScreen.CENTER // GalaxyScreen
                2 -> ActiveScreen.RIGHT  // VaultScreen
                else -> ActiveScreen.CENTER
            }

            // Trigger interstitial logic when user navigates to Career (0) or Vault (2)
            // The second lifetime visit to either screen will be the first opportunity to show an ad.
            val act = activity
            if (act != null && (page == 0 || page == 2)) {
                com.example.fargalaxy.ads.AdManager.maybeShowInterstitialOnCareerOrVaultVisit(act)
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
        playMouseClickSound(context, coroutineScope)
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
        playMouseClickSound(context, coroutineScope)
        shouldResetShipSelectionScroll = true // Reset scroll when opening from CareerScreen
        showShipSelection = true
    }
    
    // Handle back from ship selection
    val onBackFromShipSelection: () -> Unit = {
        showShipSelection = false
    }
    
    // Handle locations navigation
    val onLocationsClick: () -> Unit = {
        playMouseClickSound(context, coroutineScope)
        shouldResetLocationsScroll = true // Reset scroll when opening from CareerScreen
        showLocations = true
    }
    
    // Handle back from locations
    val onBackFromLocations: () -> Unit = {
        showLocations = false
    }
    
    // Handle staryard navigation
    val onStaryardClick: () -> Unit = {
        playMouseClickSound(context, coroutineScope)
        showStaryard = true
    }
    
    // Handle back from staryard
    val onBackFromStaryard: () -> Unit = {
        showStaryard = false
    }
    
    // Handle equipment navigation
    val onEquipmentClick: () -> Unit = {
        playMouseClickSound(context, coroutineScope)
        showEquipment = true
    }
    
    // Handle back from equipment
    val onBackFromEquipment: () -> Unit = {
        showEquipment = false
    }
    
    // Handle store navigation
    val onStoreClick: () -> Unit = {
        playMouseClickSound(context, coroutineScope)
        showStore = true
    }
    
    // Handle back from store
    val onBackFromStore: () -> Unit = {
        showStore = false
    }
    
    // Handle store item click from StoreScreen
    val onStoreItemClick: (String, Int, Int, String) -> Unit = { name, imageResId, price, description ->
        playMouseClickSound(context, coroutineScope)
        selectedStoreItemName = name
        selectedStoreItemImageResId = imageResId
        // Convert price Int to String and determine priceType
        // Cards with price < 1000 are dollars ($1.99), others are credits
        if (name == "Dying Star" || name == "Interstellar credits pack") {
            selectedStoreItemPrice = "$1.99"
            selectedStoreItemPriceType = "dollars"
        } else {
            selectedStoreItemPrice = price.toString()
            selectedStoreItemPriceType = "credits"
        }
        selectedStoreItemDescription = description
        showStoreDetails = true
    }
    
    // Handle back from store details
    val onBackFromStoreDetails: () -> Unit = {
        showStoreDetails = false
        selectedStoreItemName = ""
        selectedStoreItemImageResId = 0
        selectedStoreItemPrice = ""
        selectedStoreItemPriceType = "credits"
        selectedStoreItemDescription = ""
    }
    
    // Helper function to handle store purchases (used by both detail view and direct BUY buttons)
    fun handleStorePurchase(itemName: String, price: Int) {
        // Determine if it's a credit purchase or dollar purchase
        val isCreditPurchase = price >= 1000 // Credits are >= 1000, dollars are < 1000 (199 = $1.99)
        
        if (isCreditPurchase) {
            // Credit purchase (paid with in‑game credits)
            if (price > 0 && price <= com.example.fargalaxy.data.UserDataRepository.userCredits) {
                // Deduct credits
                com.example.fargalaxy.data.UserDataRepository.addCredits(-price)

                when (itemName) {
                    "Elite Spacer's crate" -> {
                        CrateRepository.addCrate(CrateType.ELITE)
                        eliteCrates = CrateRepository.getCrateCount(CrateType.ELITE)
                    }
                    "Advanced Spacer's crate" -> {
                        CrateRepository.addCrate(CrateType.ADVANCED)
                        advancedCrates = CrateRepository.getCrateCount(CrateType.ADVANCED)
                    }
                    "Standard Spacer's crate" -> {
                        CrateRepository.addCrate(CrateType.STANDARD)
                        standardCrates = CrateRepository.getCrateCount(CrateType.STANDARD)
                    }
                    else -> {
                        // Non‑crate credit‑priced items (if any) can be handled here later.
                    }
                }

                // Toast in same style as equipment purchases
                toastMessage = "Bought $itemName for $price credits"
            }
        } else {
            // Dollar purchase: route through Google Play Billing
            val act = activity
            if (act != null) {
                when (itemName) {
                    "Interstellar credits pack" -> {
                        com.example.fargalaxy.billing.BillingManager.launchPurchase(
                            act,
                            com.example.fargalaxy.billing.PremiumProduct.CREDITS_PACK
                        )
                    }
                    "Dying Star" -> {
                        com.example.fargalaxy.billing.BillingManager.launchPurchase(
                            act,
                            com.example.fargalaxy.billing.PremiumProduct.DYING_STAR
                        )
                    }
                }
            } else {
                // Fallback if activity is not available
                toastMessage = "Unable to start purchase flow"
            }
        }
    }
    
    // Handle purchase / select click from StoreDetailsScreen
    val onStorePurchaseClick: () -> Unit = {
        // Note: Dying Star button is disabled when already owned, so this path won't be called
        if (selectedStoreItemName.isNotEmpty()) {
            if (selectedStoreItemPriceType == "credits") {
                val priceInt = selectedStoreItemPrice.toIntOrNull() ?: 0
                handleStorePurchase(selectedStoreItemName, priceInt)
            } else {
                // Dollar‑priced items (Dying Star / credits pack)
                val priceInt = 199 // $1.99 in cents
                handleStorePurchase(selectedStoreItemName, priceInt)
            }
        }

        // Close the details screen
        showStoreDetails = false
        selectedStoreItemName = ""
        selectedStoreItemImageResId = 0
        selectedStoreItemPrice = ""
        selectedStoreItemPriceType = "credits"
        selectedStoreItemDescription = ""
    }
    
    // Handle direct purchase from StoreScreen BUY buttons
    val onStoreDirectPurchaseClick: (String, Int) -> Unit = { name, price ->
        playMouseClickSound(context, coroutineScope)
        handleStorePurchase(name, price)
    }
    
    // Handle equipment click from EquipmentScreen
    val onEquipmentItemClick: (String, Int, Int, String) -> Unit = { name, imageResId, price, description ->
        playMouseClickSound(context, coroutineScope)
        selectedEquipmentName = name
        selectedEquipmentImageResId = imageResId
        selectedEquipmentPrice = price
        selectedEquipmentDescription = description
        showEquipmentDetails = true
    }
    
    // Handle back from equipment details
    val onBackFromEquipmentDetails: () -> Unit = {
        showEquipmentDetails = false
        selectedEquipmentName = ""
        selectedEquipmentImageResId = 0
        selectedEquipmentPrice = 0
        selectedEquipmentDescription = ""
    }
    
    // Handle purchase click from EquipmentDetailsScreen
    val onEquipmentPurchaseClick: () -> Unit = {
        // Check if user has enough credits
        if (selectedEquipmentPrice <= com.example.fargalaxy.data.UserDataRepository.userCredits) {
            // Deduct credits
            com.example.fargalaxy.data.UserDataRepository.addCredits(-selectedEquipmentPrice)
            
            // Map equipment name to item ID and add to inventory
            val itemId = when (selectedEquipmentName) {
                "Emergency modulators" -> "emergency_modulator"
                "Unstable cargo" -> "unstable_cargo"
                "Experimental fuel" -> "experimental_fuel"
                "Deep space scanners" -> "deep_space_scanner"
                else -> null
            }
            
            // Add item to inventory if mapping exists
            itemId?.let {
                com.example.fargalaxy.data.InventoryRepository.addItem(it, 1)
            }
            
            // Show toast message: "Bought (item name) for (amount of credits)"
            toastMessage = "Bought $selectedEquipmentName for $selectedEquipmentPrice credits"
        }
        
        // Close the details screen
        showEquipmentDetails = false
        selectedEquipmentName = ""
        selectedEquipmentImageResId = 0
        selectedEquipmentPrice = 0
        selectedEquipmentDescription = ""
    }
    
    // Handle ship click from StaryardScreen
    val onStaryardShipClick: (Ship) -> Unit = { ship ->
        playMouseClickSound(context, coroutineScope)
        selectedShipForStaryardDetails = ship
        selectedShipPrice = shipPrices[ship.id] ?: 0
        showStaryardDetails = true
    }
    
    // Handle back from staryard details
    val onBackFromStaryardDetails: () -> Unit = {
        showStaryardDetails = false
        selectedShipForStaryardDetails = null
        selectedShipPrice = 0
    }
    
    // Handle purchase click from StaryardDetailsScreen
    val onPurchaseClick: () -> Unit = {
        // Mark ship as owned when purchased and deduct credits
        selectedShipForStaryardDetails?.let { ship ->
            // Deduct credits
            com.example.fargalaxy.data.UserDataRepository.addCredits(-selectedShipPrice)
            // Mark ship as owned
            com.example.fargalaxy.data.GameStateRepository.ownShip(ship.id)
            // Show ship acquired modal
            acquiredShipId = ship.id
            showShipAcquiredScreen = true
        }
        // Close the details screen
        showStaryardDetails = false
        selectedShipForStaryardDetails = null
        selectedShipPrice = 0
    }
    
    // Handle continue click from ShipAcquiredScreen
    val onShipAcquiredContinueClick: () -> Unit = {
        playMouseClickSound(context, coroutineScope)
        showShipAcquiredScreen = false
        acquiredShipId = null
    }
    
    // Handle location click from LocationsScreen
    val onLocationClick: (com.example.fargalaxy.model.Location) -> Unit = { location ->
        playMouseClickSound(context, coroutineScope)
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
        playMouseClickSound(context, coroutineScope)
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
        playMouseClickSound(context, coroutineScope)
        showShipDetails = false
        isFromCareerScreen = false
        shouldResetShipSelectionScroll = false // Don't reset scroll when coming from ShipDetailsScreen
        showShipSelection = true
    }
    
    // Handle ship selection - set the ship as current and navigate to CareerScreen
    val onSelectShip: () -> Unit = {
        val shipToSelect = selectedShipForDetails
        if (shipToSelect != null) {
            playMouseClickSound(context, coroutineScope)
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
    
    // Disable user scrolling when not idle or when overlay screens are shown
    val userScrollEnabled = isGalaxyIdle && !isShipUnlockedScreenShown && !isLocationDiscoveredScreenShown && !showShipAcquiredScreen
    
    // Handle back button press
    BackHandler(enabled = true) {
        when {
            // If StoreDetailsScreen is shown, close it
            showStoreDetails -> {
                onBackFromStoreDetails()
            }
            // If EquipmentDetailsScreen is shown, close it
            showEquipmentDetails -> {
                onBackFromEquipmentDetails()
            }
            // If StaryardDetailsScreen is shown, close it
            showStaryardDetails -> {
                onBackFromStaryardDetails()
            }
            // If StaryardScreen is shown, close it
            showStaryard -> {
                onBackFromStaryard()
            }
            // If EquipmentScreen is shown, close it
            showEquipment -> {
                onBackFromEquipment()
            }
            // If StoreScreen is shown, close it
            showStore -> {
                onBackFromStore()
            }
            // If SectorDetailsScreen is shown, close it
            showSectorDetails -> {
                showSectorDetails = false
            }
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
            // If StaryardDetailsScreen is shown, close it
            showStaryardDetails -> {
                onBackFromStaryardDetails()
            }
            // If StaryardScreen is shown, close it
            showStaryard -> {
                onBackFromStaryard()
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
            // If ship unlocked from crate is shown, close it
            shipUnlockedFromCrate != null -> {
                playMouseClickSound(context, coroutineScope)
                shipUnlockedFromCrate = null
            }
            // If Crate opening overlay is shown, close it
            showCrateOpeningOverlay -> {
                showCrateOpeningOverlay = false
                openingCrateType = null
                crateOpenResult = null
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
                        totalTravelMinutes = 0, // Not used anymore, CareerScreen reads from UserDataRepository
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
                        onCollectionClick = onCollectionClick,
                        onRewardsScreenVisibilityChange = { isShown -> isRewardsScreenShown = isShown },
                        onShipUnlockedScreenVisibilityChange = { isShown -> isShipUnlockedScreenShown = isShown },
                        onLocationDiscoveredScreenVisibilityChange = { isShown -> isLocationDiscoveredScreenShown = isShown },
                        onBoostSelectionBottomSheetVisibilityChange = { isShown -> isBoostSelectionBottomSheetShown = isShown },
                        onShowToast = { message -> toastMessage = message }
                    )
                }
                2 -> {
                    // VaultScreen - content only (no background/noise/indicator)
                    VaultScreen(
                        onBackClick = { navigateToPage(1) }, // Navigate to GalaxyScreen
                        onStaryardClick = onStaryardClick,
                        onEquipmentClick = onEquipmentClick,
                        onStoreClick = onStoreClick,
                        onSectorDetailsClick = {
                            showSectorDetails = true
                        }
                    )
                }
            }
        }
        
        // Static noise overlay - doesn't move when swiping (above content, below indicator)
        // Hide noise when ShipDetailsScreen, ShipSelectionScreen, LocationsScreen, LocationDetailsScreen, StaryardScreen, StaryardDetailsScreen, EquipmentScreen, EquipmentDetailsScreen, StoreScreen, StoreDetailsScreen, SectorDetailsScreen, or FactionDetailsScreen is shown (they have their own backgrounds)
        if (!showShipDetails && !showShipSelection && !showLocations && !showLocationDetails && !showStaryard && !showStaryardDetails && !showEquipment && !showEquipmentDetails && !showStore && !showStoreDetails && !showSectorDetails && !showFactionDetails && !showCrateOpeningOverlay) {
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
        // Also hide when ShipDetailsScreen, ShipSelectionScreen, LocationsScreen, LocationDetailsScreen, StaryardScreen, StaryardDetailsScreen, EquipmentScreen, EquipmentDetailsScreen, StoreScreen, StoreDetailsScreen, SectorDetailsScreen, FactionDetailsScreen, RewardsScreen, ShipUnlockedScreen, LocationDiscoveredScreen, ShipAcquiredScreen, or BoostSelectionBottomSheet is shown
        if (!showShipDetails && !showShipSelection && !showLocations && !showLocationDetails && !showStaryard && !showStaryardDetails && !showEquipment && !showEquipmentDetails && !showStore && !showStoreDetails && !showSectorDetails && !showFactionDetails && !isRewardsScreenShown && !isShipUnlockedScreenShown && !isLocationDiscoveredScreenShown && !showShipAcquiredScreen && !isBoostSelectionBottomSheetShown && !showCrateOpeningOverlay && shipUnlockedFromCrate == null && (pagerState.currentPage == 0 || pagerState.currentPage == 2 || isGalaxyIdle)) {
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
                    playMouseClickSound(context, coroutineScope)
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
        
        // SectorDetailsScreen overlay - shown on top of everything when showSectorDetails is true
        if (showSectorDetails) {
            SectorDetailsScreen(
                onBackClick = {
                    showSectorDetails = false
                }
            )
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
        
        // StaryardScreen overlay - shown on top of everything when showStaryard is true
        if (showStaryard) {
            Box(modifier = Modifier.fillMaxSize()) {
                StaryardScreen(
                    onBackClick = onBackFromStaryard,
                    onShipClick = onStaryardShipClick,
                    onShipCardClick = { shipId ->
                        // Play click sound and open shipcard progress overlay
                        playMouseClickSound(context, coroutineScope)

                        shipCardProgressRarity = when (shipId) {
                            "ship16" -> ShipRarity.UNCOMMON
                            "ship17" -> ShipRarity.EPIC
                            "ship18" -> ShipRarity.LEGENDARY
                            else -> ShipRarity.UNCOMMON
                        }

                        // Map shipId to actual ship ID and get card count from repository
                        val actualShipId = when (shipId) {
                            "ship16" -> "asn_ag94_centurion"
                            "ship17" -> "isc_m450_phoenix"
                            "ship18" -> "asn_h99_dragoon"
                            else -> "asn_ag94_centurion"
                        }
                        shipCardProgressShipId = actualShipId
                        shipCardOwnedCards = com.example.fargalaxy.data.ShipCardRepository.getOwnedCardCount(actualShipId)
                        showShipCardProgressModal = true
                    }
                )
                
                // Block pointer events when StaryardDetailsScreen is shown
                if (showStaryardDetails) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                // Consume all pointer events to prevent interaction with StaryardScreen
                                detectTapGestures { }
                            }
                    )
                }
            }
        }
        
        // StaryardDetailsScreen overlay - shown on top of everything when showStaryardDetails is true
        if (showStaryardDetails && selectedShipForStaryardDetails != null) {
            StaryardDetailsScreen(
                ship = selectedShipForStaryardDetails!!,
                price = selectedShipPrice,
                onBackClick = onBackFromStaryardDetails,
                onPurchaseClick = onPurchaseClick
            )
        }

        // ShipCardProgressModal overlay - shown on top of everything when viewing shipcard maps
        if (showShipCardProgressModal) {
            ShipCardProgressModal(
                shipId = shipCardProgressShipId,
                rarity = shipCardProgressRarity,
                ownedCards = shipCardOwnedCards,
                onContinueClick = {
                    playMouseClickSound(context, coroutineScope)
                    showShipCardProgressModal = false
                }
            )
        }
        
        // EquipmentScreen overlay - shown on top of everything when showEquipment is true
        if (showEquipment) {
            Box(modifier = Modifier.fillMaxSize()) {
                EquipmentScreen(
                    onBackClick = onBackFromEquipment,
                    onEquipmentClick = onEquipmentItemClick,
                    onPurchaseClick = { itemName, price ->
                        // Handle purchase directly from EquipmentScreen
                        if (price <= com.example.fargalaxy.data.UserDataRepository.userCredits) {
                            // Deduct credits
                            com.example.fargalaxy.data.UserDataRepository.addCredits(-price)
                            
                            // Map equipment name to item ID and add to inventory
                            val itemId = when (itemName) {
                                "Emergency modulators" -> "emergency_modulator"
                                "Unstable cargo" -> "unstable_cargo"
                                "Experimental fuel" -> "experimental_fuel"
                                "Deep space scanners" -> "deep_space_scanner"
                                else -> null
                            }
                            
                            // Add item to inventory if mapping exists
                            itemId?.let {
                                com.example.fargalaxy.data.InventoryRepository.addItem(it, 1)
                            }
                            
                            // Show toast message: "Bought (item name) for (amount of credits)"
                            toastMessage = "Bought $itemName for $price credits"
                        }
                    }
                )
                
                // Block pointer events when EquipmentDetailsScreen is shown
                if (showEquipmentDetails) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                // Consume all pointer events to prevent interaction with EquipmentScreen
                                detectTapGestures { }
                            }
                    )
                }
            }
        }
        
        // StoreScreen overlay - shown on top of everything when showStore is true
        if (showStore) {
            Box(modifier = Modifier.fillMaxSize()) {
            StoreScreen(
                    onBackClick = onBackFromStore,
                    onStoreItemClick = onStoreItemClick,
                    onPurchaseClick = onStoreDirectPurchaseClick,
                    standardCrates = standardCrates,
                    advancedCrates = advancedCrates,
                    eliteCrates = eliteCrates,
                    onOpenCrateClick = { crateType ->
                        // Open crate through repository and show opening overlay
                        val result = CrateRepository.openCrate(crateType, GameStateRepository.isTestMode)
                        if (result != null) {
                            // Refresh counts after consumption
                            standardCrates = CrateRepository.getCrateCount(CrateType.STANDARD)
                            advancedCrates = CrateRepository.getCrateCount(CrateType.ADVANCED)
                            eliteCrates = CrateRepository.getCrateCount(CrateType.ELITE)

                            openingCrateType = crateType
                            crateOpenResult = result
                            showCrateOpeningOverlay = true
                        }
                    }
                )
                
                // Block pointer events when StoreDetailsScreen is shown
                if (showStoreDetails) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                // Consume all pointer events to prevent interaction with StoreScreen
                                detectTapGestures { }
                            }
                    )
                }
            }
        }
        
        // EquipmentDetailsScreen overlay - shown on top of everything when showEquipmentDetails is true
        if (showEquipmentDetails) {
            EquipmentDetailsScreen(
                equipmentName = selectedEquipmentName,
                equipmentImageResId = selectedEquipmentImageResId,
                price = selectedEquipmentPrice,
                description = selectedEquipmentDescription,
                onBackClick = onBackFromEquipmentDetails,
                onPurchaseClick = onEquipmentPurchaseClick
            )
        }
        
        // StoreDetailsScreen overlay - shown on top of everything when showStoreDetails is true
        if (showStoreDetails) {
            StoreDetailsScreen(
                itemName = selectedStoreItemName,
                itemImageResId = selectedStoreItemImageResId,
                price = selectedStoreItemPrice,
                priceType = selectedStoreItemPriceType,
                description = selectedStoreItemDescription,
                onBackClick = onBackFromStoreDetails,
                onPurchaseClick = onStorePurchaseClick
            )
        }

        // Crate opening overlay - crate animation + reward reveal
        if (showCrateOpeningOverlay && openingCrateType != null && crateOpenResult != null) {
            CrateOpeningScreen(
                crateType = openingCrateType!!,
                result = crateOpenResult!!,
                onContinue = {
                    playMouseClickSound(context, coroutineScope)
                    // Check if a ship was unlocked via crate cards
                    val unlockedShipId = crateOpenResult!!.unlockedShipId
                    showCrateOpeningOverlay = false
                    openingCrateType = null
                    val result = crateOpenResult
                    crateOpenResult = null
                    
                    // If a ship was unlocked, show the ship unlocked screen
                    if (unlockedShipId != null) {
                        shipUnlockedFromCrate = unlockedShipId
                    }
                }
            )
        }
        
        // Show ship unlocked screen if a ship was unlocked via crate cards
        if (shipUnlockedFromCrate != null) {
            ShipUnlockedScreen(
                shipId = shipUnlockedFromCrate!!,
                onContinueClick = {
                    playMouseClickSound(context, coroutineScope)
                    shipUnlockedFromCrate = null
                }
            )
        }
        
        // ShipAcquiredScreen overlay - shown on top of everything when showShipAcquiredScreen is true
        if (showShipAcquiredScreen && acquiredShipId != null) {
            ShipAcquiredScreen(
                shipId = acquiredShipId!!,
                onContinueClick = onShipAcquiredContinueClick
            )
        }
        
        // Black overlay at bottom to ensure navigation bar area is solid black
        // This covers the navigation bar area to prevent content from showing through
        val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues()
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(navigationBarPadding.calculateBottomPadding())
                .background(Color.Black)
        )
        
        // Custom Toast - shown when toastMessage is not null
        toastMessage?.let { message ->
            CustomToast(
                message = message,
                onDismiss = { toastMessage = null }
            )
        }
    }
}

