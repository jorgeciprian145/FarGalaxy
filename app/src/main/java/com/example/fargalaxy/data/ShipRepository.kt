package com.example.fargalaxy.data

import com.example.fargalaxy.model.CrewCapacity
import com.example.fargalaxy.model.Ship
import com.example.fargalaxy.model.ShipDimensions
import com.example.fargalaxy.model.ShipRarity
import com.example.fargalaxy.R

/**
 * Repository for managing ship data.
 * In the future, this could load ships from a database, JSON file, or API.
 */
object ShipRepository {
    private val ships = listOf(
        Ship(
            id = "b14_phantom",
            name = "B14 Phantom",
            manufacturer = "Valketh Industries",
            type = "Light Hypertravel Starcraft",
            rarity = ShipRarity.COMMON,
            imageResId = R.drawable.ship1,
            renderImageResId = R.drawable.ship1render,
            dimensions = ShipDimensions(
                lengthMeters = 32f,
                lengthFeet = 104f,
                widthMeters = 12f,
                widthFeet = 39f
            ),
            crewCapacity = CrewCapacity(
                pilots = 2,
                crewMembers = 4
            ),
            lore = "After countless prototypes and more failures than the engineers cared to admit, Valketh Industries finally released a ship that changed the way new pilots entered the galaxy. The B14 Phantom became the standard entry-level vessel, combining reliability with just enough performance to make interstellar travel accessible to the masses.",
            // Gameplay properties
            speed = 100f,
            acceleration = 50f,
            warpConverters = 75f,
            uniqueTrait = null // No unique trait for this ship
        ),
        Ship(
            id = "common_ship_2",
            name = "Common Ship 2",
            manufacturer = "Valketh Industries",
            type = "Light Hypertravel Starcraft",
            rarity = ShipRarity.COMMON,
            imageResId = R.drawable.ship1,
            renderImageResId = R.drawable.ship1render,
            dimensions = ShipDimensions(
                lengthMeters = 32f,
                lengthFeet = 104f,
                widthMeters = 12f,
                widthFeet = 39f
            ),
            crewCapacity = CrewCapacity(
                pilots = 2,
                crewMembers = 4
            ),
            lore = "A common ship for testing.",
            speed = 100f,
            acceleration = 50f,
            warpConverters = 75f,
            uniqueTrait = null
        ),
        Ship(
            id = "uncommon_ship_1",
            name = "Uncommon Ship 1",
            manufacturer = "Valketh Industries",
            type = "Medium Hypertravel Starcraft",
            rarity = ShipRarity.UNCOMMON,
            imageResId = R.drawable.ship1,
            renderImageResId = R.drawable.ship1render,
            dimensions = ShipDimensions(
                lengthMeters = 40f,
                lengthFeet = 130f,
                widthMeters = 15f,
                widthFeet = 49f
            ),
            crewCapacity = CrewCapacity(
                pilots = 2,
                crewMembers = 6
            ),
            lore = "An uncommon ship for testing.",
            speed = 120f,
            acceleration = 60f,
            warpConverters = 85f,
            uniqueTrait = null
        ),
        Ship(
            id = "uncommon_ship_2",
            name = "Uncommon Ship 2",
            manufacturer = "Valketh Industries",
            type = "Medium Hypertravel Starcraft",
            rarity = ShipRarity.UNCOMMON,
            imageResId = R.drawable.ship1,
            renderImageResId = R.drawable.ship1render,
            dimensions = ShipDimensions(
                lengthMeters = 40f,
                lengthFeet = 130f,
                widthMeters = 15f,
                widthFeet = 49f
            ),
            crewCapacity = CrewCapacity(
                pilots = 2,
                crewMembers = 6
            ),
            lore = "Another uncommon ship for testing.",
            speed = 120f,
            acceleration = 60f,
            warpConverters = 85f,
            uniqueTrait = null
        ),
        Ship(
            id = "legendary_ship",
            name = "Legendary Ship",
            manufacturer = "Valketh Industries",
            type = "Heavy Hypertravel Starcraft",
            rarity = ShipRarity.LEGENDARY,
            imageResId = R.drawable.ship1,
            renderImageResId = R.drawable.ship1render,
            dimensions = ShipDimensions(
                lengthMeters = 60f,
                lengthFeet = 197f,
                widthMeters = 20f,
                widthFeet = 66f
            ),
            crewCapacity = CrewCapacity(
                pilots = 4,
                crewMembers = 12
            ),
            lore = "A legendary ship for testing.",
            speed = 200f,
            acceleration = 100f,
            warpConverters = 150f,
            uniqueTrait = null
        )
    )
    
    /**
     * Get all available ships.
     */
    fun getAllShips(): List<Ship> = ships
    
    /**
     * Get a ship by its ID.
     */
    fun getShipById(id: String): Ship? = ships.find { it.id == id }
    
    /**
     * Get the currently selected ship.
     * TODO: Replace with actual current ship logic (e.g., from SharedPreferences or DataStore)
     */
    fun getCurrentShip(): Ship = ships.first()
}

