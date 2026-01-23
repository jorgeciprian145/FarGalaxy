package com.example.fargalaxy.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * GameStateRepository - manages game state including unlocked ships, locations, and test mode.
 * 
 * Key features:
 * - Separate storage for real progress (unlockedShipsReal, unlockedLocationsReal)
 * - Test mode flag that only affects visibility, never overwrites real progress
 * - Test mode only available in debug builds (not in release)
 * - Reset progress function for developer use
 */
object GameStateRepository {
    private const val PREFS_NAME = "game_state_prefs"
    private const val KEY_TEST_MODE = "test_mode"
    private const val KEY_UNLOCKED_SHIPS_REAL = "unlocked_ships_real" // JSON array of ship IDs (available to buy)
    private const val KEY_UNLOCKED_LOCATIONS_REAL = "unlocked_locations_real" // JSON array of location IDs
    private const val KEY_OWNED_SHIPS_REAL = "owned_ships_real" // JSON array of ship IDs (purchased and selectable)
    
    private var prefs: SharedPreferences? = null
    
    /**
     * Initialize the repository with a context.
     * Should be called once from MainActivity.onCreate().
     */
    fun initialize(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            loadRealProgress()
            
            // Test mode is only available in debug builds
            // In release builds, test mode is always false
            if (isDebugBuild()) {
                // Load test mode flag (defaults to false)
                _isTestMode = mutableStateOf(prefs!!.getBoolean(KEY_TEST_MODE, false))
            } else {
                // Release build: test mode is always false
                _isTestMode = mutableStateOf(false)
            }
            
            // Sync unlocked ships and locations based on current focus time (in case user already has enough focus time)
            syncUnlockedShipsFromFocusTime()
            syncUnlockedLocationsFromFocusTime()
        }
    }
    
    /**
     * Check if this is a debug build.
     * Test mode should only be available in debug builds.
     */
    private fun isDebugBuild(): Boolean {
        return try {
            // Access BuildConfig.DEBUG directly (will be available after buildConfig = true in build.gradle.kts)
            com.example.fargalaxy.BuildConfig.DEBUG
        } catch (e: Exception) {
            // If BuildConfig doesn't exist or DEBUG field is missing, assume release
            false
        }
    }
    
    // Test mode flag (only affects visibility, never overwrites real progress)
    private var _isTestMode = mutableStateOf(false)
    var isTestMode: Boolean
        get() = if (isDebugBuild()) _isTestMode.value else false
        set(value) {
            if (!isDebugBuild()) {
                // Test mode not available in release builds
                return
            }
            _isTestMode.value = value
            prefs?.edit()?.putBoolean(KEY_TEST_MODE, value)?.apply()
        }
    
    // Real unlocked ships (Set of ship IDs) - persisted separately
    private var _unlockedShipsReal = mutableStateOf<Set<String>>(emptySet())
    private var unlockedShipsReal: Set<String>
        get() = _unlockedShipsReal.value
        set(value) {
            _unlockedShipsReal.value = value
            saveRealProgress()
        }
    
    // Real unlocked locations (Set of location IDs) - persisted separately
    private var _unlockedLocationsReal = mutableStateOf<Set<String>>(emptySet())
    private var unlockedLocationsReal: Set<String>
        get() = _unlockedLocationsReal.value
        set(value) {
            _unlockedLocationsReal.value = value
            saveRealProgress()
        }
    
    // Real owned ships (Set of ship IDs) - persisted separately
    // Owned ships are purchased and can be selected in ShipSelectionScreen
    private var _ownedShipsReal = mutableStateOf<Set<String>>(emptySet())
    private var ownedShipsReal: Set<String>
        get() = _ownedShipsReal.value
        set(value) {
            _ownedShipsReal.value = value
            saveRealProgress()
        }
    
    /**
     * Get the focus time requirement (in minutes) for a ship to be unlocked.
     * Returns null if the ship doesn't have a focus time requirement.
     */
    fun getShipFocusTimeRequirement(shipId: String): Int? {
        return when (shipId) {
            "type45c_shooting_star" -> 15 // ship2
            "navakeshi_star_pouncer" -> 25 // ship3
            "a300_albatross" -> 35 // ship4
            "b7f_starforce" -> 50 // ship5
            "navakeshi_star_crusher" -> 80 // ship6
            "b15_specter" -> 105 // ship7
            "n6_98_melina" -> 150 // ship8
            "model3_tortoise_ccp" -> 200 // ship9
            "h98_valkyrie" -> 250 // ship10
            "navakeshi_star_ravager" -> 300 // ship11
            "silver_lightning" -> 350 // ship12
            "vulcani_legenda_f1" -> 450 // ship13
            "force_of_nature" -> 600 // ship14
            else -> null // No focus time requirement (e.g., b14_phantom starting ship)
        }
    }
    
    /**
     * Check if a ship is unlocked.
     * In test mode: always returns true (without modifying real progress).
     * In real mode: checks if ship is in unlockedShipsReal OR if focus time requirement is met.
     */
    fun isShipUnlocked(shipId: String): Boolean {
        if (isTestMode) {
            return true // Test mode: show all ships
        }
        
        // Check if already manually unlocked
        if (unlockedShipsReal.contains(shipId)) {
            return true
        }
        
        // Check focus time requirement
        val focusTimeRequirement = getShipFocusTimeRequirement(shipId)
        if (focusTimeRequirement != null) {
            val currentFocusTime = UserDataRepository.totalFocusTimeMinutes
            return currentFocusTime >= focusTimeRequirement
        }
        
        // No requirement means not unlocked (except starting ship b14_phantom)
        return shipId == "b14_phantom" // Starting ship is always unlocked
    }
    
    /**
     * Sync unlocked ships based on current focus time.
     * Automatically unlocks ships when focus time requirements are met.
     * Should be called whenever focus time changes.
     */
    fun syncUnlockedShipsFromFocusTime() {
        if (isTestMode) {
            return // Don't modify real progress in test mode
        }
        
        val currentFocusTime = UserDataRepository.totalFocusTimeMinutes
        val allShips = ShipRepository.getAllShips()
        
        // Check each ship and unlock if focus time requirement is met
        allShips.forEach { ship ->
            val focusTimeRequirement = getShipFocusTimeRequirement(ship.id)
            if (focusTimeRequirement != null && currentFocusTime >= focusTimeRequirement) {
                // Ship should be unlocked, add it if not already in the set
                if (!unlockedShipsReal.contains(ship.id)) {
                    unlockedShipsReal = unlockedShipsReal + ship.id
                }
            }
        }
    }
    
    /**
     * Sync unlocked locations based on current focus time.
     * Automatically unlocks locations when focus time requirements are met.
     * Should be called whenever focus time changes.
     */
    fun syncUnlockedLocationsFromFocusTime() {
        if (isTestMode) {
            return // Don't modify real progress in test mode
        }
        
        val currentFocusTime = UserDataRepository.totalFocusTimeMinutes
        val allLocations = LocationRepository.getAllLocations()
        
        // Check each location and unlock if focus time requirement is met
        allLocations.forEach { location ->
            val focusTimeRequirement = getLocationFocusTimeRequirement(location.id)
            if (focusTimeRequirement != null && currentFocusTime >= focusTimeRequirement) {
                // Location should be unlocked, add it if not already in the set
                if (!unlockedLocationsReal.contains(location.id)) {
                    unlockedLocationsReal = unlockedLocationsReal + location.id
                }
            }
        }
    }
    
    /**
     * Get the focus time requirement (in minutes) for a location to be discovered.
     * Returns null if the location doesn't have a focus time requirement.
     */
    fun getLocationFocusTimeRequirement(locationId: String): Int? {
        return when (locationId) {
            "location1" -> 10
            "location2" -> 25
            "location3" -> 40
            "location4" -> 60
            "location5" -> 85
            "location6" -> 100
            "location7" -> 135
            "location8" -> 190
            "location9" -> 300
            "location10" -> 450
            "location11" -> 750
            "location12" -> 950
            "location13" -> 1150
            "location14" -> 1300
            "location15" -> 1500
            "location16" -> 1750
            "location17" -> 1950
            "location18" -> 2200
            "location19" -> 2450
            "location20" -> 2700
            "location21" -> 2950
            "location22" -> 3200
            "location23" -> 3500
            "location24" -> 3800
            "location25" -> 4100
            "location26" -> 4600
            else -> null // No focus time requirement
        }
    }
    
    /**
     * Check if a location is unlocked.
     * In test mode: always returns true (without modifying real progress).
     * In real mode: checks if location is in unlockedLocationsReal OR if focus time requirement is met.
     */
    fun isLocationUnlocked(locationId: String): Boolean {
        if (isTestMode) {
            return true // Test mode: show all locations
        }
        
        // Check if already manually unlocked
        if (unlockedLocationsReal.contains(locationId)) {
            return true
        }
        
        // Check focus time requirement
        val focusTimeRequirement = getLocationFocusTimeRequirement(locationId)
        if (focusTimeRequirement != null) {
            val currentFocusTime = UserDataRepository.totalFocusTimeMinutes
            return currentFocusTime >= focusTimeRequirement
        }
        
        // No requirement means not unlocked
        return false
    }
    
    /**
     * Unlock a ship (adds to real progress).
     * This function modifies real progress, so it should only be called
     * when the user actually unlocks something (not in test mode).
     */
    fun unlockShip(shipId: String) {
        unlockedShipsReal = unlockedShipsReal + shipId
    }
    
    /**
     * Unlock a location (adds to real progress).
     * This function modifies real progress, so it should only be called
     * when the user actually unlocks something (not in test mode).
     */
    fun unlockLocation(locationId: String) {
        unlockedLocationsReal = unlockedLocationsReal + locationId
    }
    
    /**
     * Check if a ship is owned (purchased and selectable).
     * In test mode: always returns true (without modifying real progress).
     * In real mode: checks if ship is in ownedShipsReal.
     * The starting ship (b14_phantom) is always owned.
     */
    fun isShipOwned(shipId: String): Boolean {
        if (isTestMode) {
            return true // Test mode: show all ships as owned
        }
        
        // Starting ship is always owned
        if (shipId == "b14_phantom") {
            return true
        }
        
        // Check if ship is in owned ships
        return ownedShipsReal.contains(shipId)
    }
    
    /**
     * Mark a ship as owned (purchased).
     * This function modifies real progress, so it should only be called
     * when the user actually purchases a ship (not in test mode).
     */
    fun ownShip(shipId: String) {
        if (isTestMode) {
            return // Don't modify real progress in test mode
        }
        ownedShipsReal = ownedShipsReal + shipId
    }
    
    /**
     * Reset real progress to starting state.
     * Only resets real progress, does not affect test mode.
     * This is a developer function for testing purposes.
     * Also resets UserDataRepository and ShipRepository.
     */
    fun resetProgress() {
        // Reset user data first (all values to 0)
        UserDataRepository.resetProgress()
        
        // Reset to starting state: only B14 Phantom unlocked and owned, no locations
        unlockedShipsReal = setOf("b14_phantom")
        ownedShipsReal = setOf("b14_phantom") // Starting ship is owned
        unlockedLocationsReal = emptySet()
        
        // Reset current ship to default
        ShipRepository.setCurrentShip(ShipRepository.getAllShips().first().id)
    }
    
    /**
     * Get all unlocked ships (for display purposes).
     * In test mode: returns all ships.
     * In real mode: returns only unlocked ships.
     */
    fun getUnlockedShips(): List<String> {
        if (isTestMode) {
            return ShipRepository.getAllShips().map { it.id }
        }
        return unlockedShipsReal.toList()
    }
    
    /**
     * Get all unlocked locations (for display purposes).
     * In test mode: returns all locations.
     * In real mode: returns only unlocked locations.
     */
    fun getUnlockedLocations(): List<String> {
        if (isTestMode) {
            return try {
                LocationRepository.getAllLocations().map { it.id }
            } catch (e: Exception) {
                // Fallback if getAllLocations doesn't exist yet
                LocationRepository.getDiscoveredLocations().map { it.id }
            }
        }
        return unlockedLocationsReal.toList()
    }
    
    /**
     * Load real progress from SharedPreferences.
     */
    private fun loadRealProgress() {
        val shipsJson = prefs?.getString(KEY_UNLOCKED_SHIPS_REAL, null)
        val locationsJson = prefs?.getString(KEY_UNLOCKED_LOCATIONS_REAL, null)
        val ownedShipsJson = prefs?.getString(KEY_OWNED_SHIPS_REAL, null)
        
        if (shipsJson != null) {
            unlockedShipsReal = parseStringSet(shipsJson)
        } else {
            // First launch: initialize with starting state
            unlockedShipsReal = setOf("b14_phantom")
        }
        
        if (ownedShipsJson != null) {
            ownedShipsReal = parseStringSet(ownedShipsJson)
        } else {
            // First launch: starting ship is owned
            ownedShipsReal = setOf("b14_phantom")
        }
        
        if (locationsJson != null) {
            unlockedLocationsReal = parseStringSet(locationsJson)
        }
    }
    
    /**
     * Save real progress to SharedPreferences.
     */
    private fun saveRealProgress() {
        prefs?.edit()
            ?.putString(KEY_UNLOCKED_SHIPS_REAL, stringSetToJson(unlockedShipsReal))
            ?.putString(KEY_UNLOCKED_LOCATIONS_REAL, stringSetToJson(unlockedLocationsReal))
            ?.putString(KEY_OWNED_SHIPS_REAL, stringSetToJson(ownedShipsReal))
            ?.apply()
    }
    
    /**
     * Parse a JSON array string into a Set of Strings.
     * Example: ["id1","id2"] -> Set("id1", "id2")
     */
    private fun parseStringSet(json: String): Set<String> {
        if (json.isEmpty() || json == "[]") return emptySet()
        
        return json.removeSurrounding("[", "]")
            .split(",")
            .map { it.trim().removeSurrounding("\"") }
            .filter { it.isNotEmpty() }
            .toSet()
    }
    
    /**
     * Convert a Set of Strings to a JSON array string.
     * Example: Set("id1", "id2") -> ["id1","id2"]
     */
    private fun stringSetToJson(set: Set<String>): String {
        if (set.isEmpty()) return "[]"
        return set.joinToString(",", "[", "]") { "\"$it\"" }
    }
}
