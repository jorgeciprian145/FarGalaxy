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
        // Add more ships here as needed
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

