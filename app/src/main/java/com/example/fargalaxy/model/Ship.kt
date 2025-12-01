package com.example.fargalaxy.model

/**
 * Represents a ship in the game with all its properties.
 */
data class Ship(
    val id: String,
    val name: String,
    val manufacturer: String,
    val type: String, // e.g., "Light Hypertravel Starcraft"
    val rarity: ShipRarity,
    val imageResId: Int, // Drawable resource ID for the ship image
    val renderImageResId: Int, // Drawable resource ID for the 3D render
    val dimensions: ShipDimensions,
    val crewCapacity: CrewCapacity,
    val lore: String, // Ship lore - paragraph explaining the story of the ship
    // Gameplay properties
    val speed: Float, // How fast the ship goes
    val acceleration: Float, // The acceleration of the ship
    val warpConverters: Float, // How much energy the ship creates
    val uniqueTrait: UniqueTrait? = null // Optional bonus effect (only for some ships)
)

/**
 * Rarity levels for ships.
 */
enum class ShipRarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY,
    MYTHICAL
}

/**
 * Physical dimensions of the ship.
 */
data class ShipDimensions(
    val lengthMeters: Float,
    val lengthFeet: Float,
    val widthMeters: Float,
    val widthFeet: Float
)

/**
 * Crew capacity of the ship.
 */
data class CrewCapacity(
    val pilots: Int,
    val crewMembers: Int
)

/**
 * Represents a unique trait that provides a bonus effect.
 * Only certain ships will have this property.
 */
sealed class UniqueTrait {
    abstract val name: String
    abstract val description: String
    
    // You can add specific trait types here as needed
    // Example:
    // data class SpeedBoost(val multiplier: Float) : UniqueTrait() {
    //     override val name = "Speed Boost"
    //     override val description = "Increases speed by ${multiplier}x"
    // }
}

