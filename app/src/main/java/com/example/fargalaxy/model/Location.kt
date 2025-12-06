package com.example.fargalaxy.model

/**
 * Represents a location in the game with all its properties.
 */
data class Location(
    val id: String,
    val name: String,
    val type: String, // e.g., "Core World", "Horizon-class battlecruiser", etc.
    val classification: LocationClassification, // Planet, Capital Ship, or Space Station
    val rarity: LocationRarity,
    val selectionImageResId: Int, // Drawable resource ID for the selection screen image (e.g., location2selectionscreen.png)
    val detailImageResId: Int, // Drawable resource ID for the detail screen image (e.g., location2.png)
    val selectionTypeDisplay: String? = null, // Optional type to display in selection screen (e.g., "Ship" for capital ships)
    val isFullWidthImage: Boolean = false, // If true, detail image uses full width; if false, uses 90% width
    val description: String, // Additional description (e.g., "Major trade hub")
    // Planet-specific fields (nullable for non-planets)
    val dayDuration: String? = null, // Day duration (e.g., "1.3 Earth days")
    val diameter: String? = null, // Diameter description (e.g., "1.2 T.U. (large)")
    // Capital ship-specific fields (nullable for non-capital ships)
    val weight: String? = null, // Weight (e.g., "56 million tons")
    val length: String? = null, // Length (e.g., "1.5 km")
    // Common fields
    val population: String, // Population/crew description (e.g., "Billions (interstellar metropolis)" or "5.000 crew members")
    val lore: String // Location lore - paragraph explaining the story of the location
)

/**
 * Classification types for locations.
 */
enum class LocationClassification {
    PLANET,
    CAPITAL_SHIP,
    SPACE_STATION
}

/**
 * Rarity levels for locations.
 */
enum class LocationRarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY,
    MYTHICAL
}

