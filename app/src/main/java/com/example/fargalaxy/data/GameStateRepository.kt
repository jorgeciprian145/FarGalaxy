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
    private const val KEY_CONSUMED_TRAVELS = "consumed_travels" // JSON object: {"shipId": count, "date": "yyyy-MM-dd"}
    private const val KEY_CONSUMED_TRAVELS_DATE = "consumed_travels_date" // Date when consumed travels were last updated
    private const val KEY_MAINTENANCE_START_TIME = "maintenance_start_time" // JSON object: {"shipId": timestamp}
    private const val KEY_MAINTENANCE_DURATION = "maintenance_duration" // JSON object: {"shipId": minutes}
    
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
            // Note: Consumed travels and maintenance state persist across app sessions
            // They are only reset when resetProgress() is called manually
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
        
        // Reset inventory items
        InventoryRepository.resetInventory()
        // Reset flight environment scanner usage so environment returns to default state
        FlightEnvironmentRepository.resetScannerUsage()
        
        // Reset equipped equipment
        com.example.fargalaxy.data.EquipmentRepository.unequipItem()
        com.example.fargalaxy.data.EquipmentUsageRepository.resetUsage()
        
        // Reset consumed travels and maintenance status
        prefs?.edit()
            ?.remove(KEY_CONSUMED_TRAVELS)
            ?.remove(KEY_CONSUMED_TRAVELS_DATE)
            ?.remove(KEY_MAINTENANCE_START_TIME)
            ?.remove(KEY_MAINTENANCE_DURATION)
            ?.apply()
        
        // Reset to starting state: only B14 Phantom unlocked and owned, no locations
        unlockedShipsReal = setOf("b14_phantom")
        ownedShipsReal = setOf("b14_phantom") // Starting ship is owned
        unlockedLocationsReal = emptySet()
        
        // Reset current ship to default
        ShipRepository.setCurrentShip(ShipRepository.getAllShips().first().id)
        
        // Reapply test mode credits if test mode is enabled (so credits are always 50000 in test mode)
        UserDataRepository.setTestModeCreditsIfEnabled()
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
    
    /**
     * Get the current date in "yyyy-MM-dd" format.
     */
    private fun getCurrentDateString(): String {
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH) + 1 // Month is 0-based
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        return String.format("%04d-%02d-%02d", year, month, day)
    }
    
    /**
     * Get consumed travels count for a ship.
     * Returns 0 if it's a new day (travels reset daily).
     * 
     * @param shipId The ship ID
     * @return The number of consumed travels for today
     */
    fun getConsumedTravels(shipId: String): Int {
        val prefs = prefs ?: return 0
        val currentDate = getCurrentDateString()
        val storedDate = prefs.getString(KEY_CONSUMED_TRAVELS_DATE, null)
        
        // If it's a new day, reset consumed travels
        if (storedDate != currentDate) {
            return 0
        }
        
        // Load consumed travels map
        val consumedTravelsJson = prefs.getString(KEY_CONSUMED_TRAVELS, null) ?: return 0
        if (consumedTravelsJson.isEmpty() || consumedTravelsJson == "{}") return 0
        
        // Parse JSON object: {"shipId": count}
        try {
            val json = consumedTravelsJson.removeSurrounding("{", "}")
            val pairs = json.split(",")
            for (pair in pairs) {
                val keyValue = pair.split(":")
                if (keyValue.size == 2) {
                    val key = keyValue[0].trim().removeSurrounding("\"")
                    val value = keyValue[1].trim().toIntOrNull() ?: 0
                    if (key == shipId) {
                        return value
                    }
                }
            }
        } catch (e: Exception) {
            // If parsing fails, return 0
        }
        
        return 0
    }
    
    /**
     * Consume one travel for a ship.
     * This should be called after each trip (completed or cancelled).
     * 
     * @param shipId The ship ID
     */
    fun consumeTravel(shipId: String) {
        val prefs = prefs ?: return
        val currentDate = getCurrentDateString()
        val storedDate = prefs.getString(KEY_CONSUMED_TRAVELS_DATE, null)
        
        // Load current consumed travels map
        val consumedTravelsJson = prefs.getString(KEY_CONSUMED_TRAVELS, null) ?: "{}"
        val consumedTravels = mutableMapOf<String, Int>()
        
        // If it's a new day, reset the map
        if (storedDate != currentDate) {
            consumedTravels.clear()
        } else if (consumedTravelsJson.isNotEmpty() && consumedTravelsJson != "{}") {
            // Parse existing consumed travels
            try {
                val json = consumedTravelsJson.removeSurrounding("{", "}")
                val pairs = json.split(",")
                for (pair in pairs) {
                    val keyValue = pair.split(":")
                    if (keyValue.size == 2) {
                        val key = keyValue[0].trim().removeSurrounding("\"")
                        val value = keyValue[1].trim().toIntOrNull() ?: 0
                        consumedTravels[key] = value
                    }
                }
            } catch (e: Exception) {
                // If parsing fails, start fresh
                consumedTravels.clear()
            }
        }
        
        // Increment consumed travels for this ship
        val currentCount = consumedTravels[shipId] ?: 0
        consumedTravels[shipId] = currentCount + 1
        
        // Save back to SharedPreferences
        val jsonString = consumedTravels.entries.joinToString(",", "{", "}") { 
            "\"${it.key}\":${it.value}" 
        }
        
        prefs.edit()
            .putString(KEY_CONSUMED_TRAVELS, jsonString)
            .putString(KEY_CONSUMED_TRAVELS_DATE, currentDate)
            .apply()
        
        // Check if all travels are consumed - if so, start maintenance
        val durability = getShipMaintenanceTime(shipId) // We'll use this to check if maintenance is needed
        // Actually, we need to check if consumedTravels >= durability
        // But we don't have durability here, so we'll check in GalaxyScreen
    }
    
    /**
     * Get maintenance time requirement (in minutes) for a ship.
     * This is the time a ship needs to spend in maintenance after all travels are consumed.
     * Based on ship rarity:
     * - COMMON: 60 mins
     * - UNCOMMON: 120 mins
     * - RARE: 180 mins
     * - EPIC: 240 mins
     * - LEGENDARY: 300 mins
     * - MYTHICAL: 360 mins
     * 
     * @param shipId The ship ID
     * @return Maintenance time in minutes
     */
    fun getShipMaintenanceTime(shipId: String): Int {
        val ship = ShipRepository.getShipById(shipId) ?: return 60 // Default to COMMON if ship not found
        return when (ship.rarity) {
            com.example.fargalaxy.model.ShipRarity.COMMON -> 60
            com.example.fargalaxy.model.ShipRarity.UNCOMMON -> 120
            com.example.fargalaxy.model.ShipRarity.RARE -> 180
            com.example.fargalaxy.model.ShipRarity.EPIC -> 240
            com.example.fargalaxy.model.ShipRarity.LEGENDARY -> 300
            com.example.fargalaxy.model.ShipRarity.MYTHICAL -> 360
        }
    }
    
    /**
     * Get the repair cost in credits for a ship.
     * Based on ship rarity:
     * - COMMON: 1500 credits
     * - UNCOMMON: 3000 credits
     * - RARE: 4500 credits
     * - EPIC: 6000 credits
     * - LEGENDARY: 7500 credits
     * - MYTHICAL: 9000 credits
     * 
     * @param shipId The ship ID
     * @return The repair cost in credits
     */
    fun getShipRepairCost(shipId: String): Int {
        val ship = ShipRepository.getShipById(shipId) ?: return 1500 // Default to COMMON if ship not found
        return when (ship.rarity) {
            com.example.fargalaxy.model.ShipRarity.COMMON -> 1500
            com.example.fargalaxy.model.ShipRarity.UNCOMMON -> 3000
            com.example.fargalaxy.model.ShipRarity.RARE -> 4500
            com.example.fargalaxy.model.ShipRarity.EPIC -> 6000
            com.example.fargalaxy.model.ShipRarity.LEGENDARY -> 7500
            com.example.fargalaxy.model.ShipRarity.MYTHICAL -> 9000
        }
    }
    
    /**
     * Get the proportional repair cost in credits for a ship based on remaining maintenance time.
     * The cost is proportional to the remaining maintenance time:
     * - If 100% of time remains, cost = full repair cost
     * - If 40% of time remains, cost = 40% of full repair cost
     * 
     * @param shipId The ship ID
     * @return The proportional repair cost in credits (rounded to nearest integer)
     */
    fun getShipRepairCostProportional(shipId: String): Int {
        val baseCost = getShipRepairCost(shipId)
        val totalTimeMinutes = getShipMaintenanceTime(shipId)
        val remainingTimeSeconds = getRemainingMaintenanceTime(shipId)
        
        // If maintenance is complete or not in maintenance, return 0
        if (remainingTimeSeconds <= 0 || totalTimeMinutes <= 0) {
            return 0
        }
        
        val totalTimeSeconds = totalTimeMinutes * 60
        val proportion = remainingTimeSeconds.toFloat() / totalTimeSeconds.toFloat()
        
        // Calculate proportional cost and round to nearest integer
        return (baseCost * proportion).toInt()
    }
    
    /**
     * Start maintenance for a ship.
     * This should be called when all travels are consumed.
     * 
     * @param shipId The ship ID
     */
    fun startMaintenance(shipId: String) {
        val prefs = prefs ?: return
        val maintenanceDuration = getShipMaintenanceTime(shipId)
        val startTime = System.currentTimeMillis()
        
        // Load current maintenance map
        val maintenanceStartJson = prefs.getString(KEY_MAINTENANCE_START_TIME, null) ?: "{}"
        val maintenanceDurationJson = prefs.getString(KEY_MAINTENANCE_DURATION, null) ?: "{}"
        val startTimes = mutableMapOf<String, Long>()
        val durations = mutableMapOf<String, Int>()
        
        // Parse existing maintenance data
        if (maintenanceStartJson.isNotEmpty() && maintenanceStartJson != "{}") {
            try {
                val json = maintenanceStartJson.removeSurrounding("{", "}")
                val pairs = json.split(",")
                for (pair in pairs) {
                    val keyValue = pair.split(":")
                    if (keyValue.size == 2) {
                        val key = keyValue[0].trim().removeSurrounding("\"")
                        val value = keyValue[1].trim().toLongOrNull() ?: 0L
                        startTimes[key] = value
                    }
                }
            } catch (e: Exception) {
                // If parsing fails, start fresh
            }
        }
        
        if (maintenanceDurationJson.isNotEmpty() && maintenanceDurationJson != "{}") {
            try {
                val json = maintenanceDurationJson.removeSurrounding("{", "}")
                val pairs = json.split(",")
                for (pair in pairs) {
                    val keyValue = pair.split(":")
                    if (keyValue.size == 2) {
                        val key = keyValue[0].trim().removeSurrounding("\"")
                        val value = keyValue[1].trim().toIntOrNull() ?: 0
                        durations[key] = value
                    }
                }
            } catch (e: Exception) {
                // If parsing fails, start fresh
            }
        }
        
        // Set maintenance for this ship
        startTimes[shipId] = startTime
        durations[shipId] = maintenanceDuration
        
        // Save back to SharedPreferences
        val startTimesJson = startTimes.entries.joinToString(",", "{", "}") { 
            "\"${it.key}\":${it.value}" 
        }
        val durationsJson = durations.entries.joinToString(",", "{", "}") { 
            "\"${it.key}\":${it.value}" 
        }
        
        prefs.edit()
            .putString(KEY_MAINTENANCE_START_TIME, startTimesJson)
            .putString(KEY_MAINTENANCE_DURATION, durationsJson)
            .apply()
    }
    
    /**
     * Get remaining maintenance time (in seconds) for a ship.
     * Returns 0 if maintenance is complete or not in maintenance.
     * 
     * @param shipId The ship ID
     * @return Remaining maintenance time in seconds
     */
    fun getRemainingMaintenanceTime(shipId: String): Int {
        val prefs = prefs ?: return 0
        
        val maintenanceStartJson = prefs.getString(KEY_MAINTENANCE_START_TIME, null) ?: return 0
        val maintenanceDurationJson = prefs.getString(KEY_MAINTENANCE_DURATION, null) ?: return 0
        
        if (maintenanceStartJson.isEmpty() || maintenanceStartJson == "{}") return 0
        if (maintenanceDurationJson.isEmpty() || maintenanceDurationJson == "{}") return 0
        
        var startTime: Long = 0
        var durationMinutes: Int = 0
        
        // Parse start time
        try {
            val json = maintenanceStartJson.removeSurrounding("{", "}")
            val pairs = json.split(",")
            for (pair in pairs) {
                val keyValue = pair.split(":")
                if (keyValue.size == 2) {
                    val key = keyValue[0].trim().removeSurrounding("\"")
                    val value = keyValue[1].trim().toLongOrNull() ?: 0L
                    if (key == shipId) {
                        startTime = value
                        break
                    }
                }
            }
        } catch (e: Exception) {
            return 0
        }
        
        // Parse duration
        try {
            val json = maintenanceDurationJson.removeSurrounding("{", "}")
            val pairs = json.split(",")
            for (pair in pairs) {
                val keyValue = pair.split(":")
                if (keyValue.size == 2) {
                    val key = keyValue[0].trim().removeSurrounding("\"")
                    val value = keyValue[1].trim().toIntOrNull() ?: 0
                    if (key == shipId) {
                        durationMinutes = value
                        break
                    }
                }
            }
        } catch (e: Exception) {
            return 0
        }
        
        if (startTime == 0L || durationMinutes == 0) return 0
        
        // Calculate remaining time
        val elapsedMillis = System.currentTimeMillis() - startTime
        val totalDurationMillis = durationMinutes * 60 * 1000L
        val remainingMillis = totalDurationMillis - elapsedMillis
        
        return (remainingMillis / 1000).toInt().coerceAtLeast(0)
    }
    
    /**
     * Check if a ship is currently in maintenance.
     * 
     * @param shipId The ship ID
     * @return True if ship is in maintenance, false otherwise
     */
    fun isShipInMaintenance(shipId: String): Boolean {
        return getRemainingMaintenanceTime(shipId) > 0
    }
    
    /**
     * Check if maintenance data exists for a ship (regardless of whether time has expired).
     * This is useful to determine if maintenance should be completed when time expires.
     * 
     * @param shipId The ship ID
     * @return True if maintenance data exists, false otherwise
     */
    fun hasMaintenanceData(shipId: String): Boolean {
        val prefs = prefs ?: return false
        
        val maintenanceStartJson = prefs.getString(KEY_MAINTENANCE_START_TIME, null) ?: return false
        if (maintenanceStartJson.isEmpty() || maintenanceStartJson == "{}") return false
        
        // Check if this ship's start time exists in the JSON
        try {
            val json = maintenanceStartJson.removeSurrounding("{", "}")
            val pairs = json.split(",")
            for (pair in pairs) {
                val keyValue = pair.split(":")
                if (keyValue.size == 2) {
                    val key = keyValue[0].trim().removeSurrounding("\"")
                    if (key == shipId) {
                        return true // Maintenance data exists for this ship
                    }
                }
            }
        } catch (e: Exception) {
            return false
        }
        
        return false
    }
    
    /**
     * Complete maintenance for a ship (called when maintenance time expires or user repairs).
     * This resets consumed travels and clears maintenance state.
     * 
     * @param shipId The ship ID
     */
    fun completeMaintenance(shipId: String) {
        val prefs = prefs ?: return
        
        // Clear consumed travels for this ship
        val consumedTravelsJson = prefs.getString(KEY_CONSUMED_TRAVELS, null) ?: "{}"
        val consumedTravels = mutableMapOf<String, Int>()
        
        if (consumedTravelsJson.isNotEmpty() && consumedTravelsJson != "{}") {
            try {
                val json = consumedTravelsJson.removeSurrounding("{", "}")
                val pairs = json.split(",")
                for (pair in pairs) {
                    val keyValue = pair.split(":")
                    if (keyValue.size == 2) {
                        val key = keyValue[0].trim().removeSurrounding("\"")
                        val value = keyValue[1].trim().toIntOrNull() ?: 0
                        if (key != shipId) { // Keep other ships' data
                            consumedTravels[key] = value
                        }
                    }
                }
            } catch (e: Exception) {
                // If parsing fails, clear all
            }
        }
        
        // Save consumed travels (without this ship)
        val jsonString = consumedTravels.entries.joinToString(",", "{", "}") { 
            "\"${it.key}\":${it.value}" 
        }
        
        // Clear maintenance data for this ship
        val maintenanceStartJson = prefs.getString(KEY_MAINTENANCE_START_TIME, null) ?: "{}"
        val maintenanceDurationJson = prefs.getString(KEY_MAINTENANCE_DURATION, null) ?: "{}"
        val startTimes = mutableMapOf<String, Long>()
        val durations = mutableMapOf<String, Int>()
        
        // Parse and remove this ship
        if (maintenanceStartJson.isNotEmpty() && maintenanceStartJson != "{}") {
            try {
                val json = maintenanceStartJson.removeSurrounding("{", "}")
                val pairs = json.split(",")
                for (pair in pairs) {
                    val keyValue = pair.split(":")
                    if (keyValue.size == 2) {
                        val key = keyValue[0].trim().removeSurrounding("\"")
                        val value = keyValue[1].trim().toLongOrNull() ?: 0L
                        if (key != shipId) {
                            startTimes[key] = value
                        }
                    }
                }
            } catch (e: Exception) {
                // If parsing fails, clear all
            }
        }
        
        if (maintenanceDurationJson.isNotEmpty() && maintenanceDurationJson != "{}") {
            try {
                val json = maintenanceDurationJson.removeSurrounding("{", "}")
                val pairs = json.split(",")
                for (pair in pairs) {
                    val keyValue = pair.split(":")
                    if (keyValue.size == 2) {
                        val key = keyValue[0].trim().removeSurrounding("\"")
                        val value = keyValue[1].trim().toIntOrNull() ?: 0
                        if (key != shipId) {
                            durations[key] = value
                        }
                    }
                }
            } catch (e: Exception) {
                // If parsing fails, clear all
            }
        }
        
        val startTimesJson = startTimes.entries.joinToString(",", "{", "}") { 
            "\"${it.key}\":${it.value}" 
        }
        val durationsJson = durations.entries.joinToString(",", "{", "}") { 
            "\"${it.key}\":${it.value}" 
        }
        
        prefs.edit()
            .putString(KEY_CONSUMED_TRAVELS, jsonString)
            .putString(KEY_MAINTENANCE_START_TIME, startTimesJson)
            .putString(KEY_MAINTENANCE_DURATION, durationsJson)
            .apply()
    }
}
