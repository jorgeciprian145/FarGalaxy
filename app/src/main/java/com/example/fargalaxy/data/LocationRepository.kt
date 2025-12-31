package com.example.fargalaxy.data

import com.example.fargalaxy.model.Location
import com.example.fargalaxy.R

/**
 * Repository for managing location data.
 * In the future, this could load locations from a database, JSON file, or API.
 */
import com.example.fargalaxy.model.LocationClassification
import com.example.fargalaxy.model.LocationRarity

object LocationRepository {
    private val locations = listOf(
        Location(
            id = "location1",
            name = "Aurelia",
            type = "Core world",
            classification = LocationClassification.PLANET,
            rarity = LocationRarity.COMMON,
            selectionImageResId = R.drawable.location1selectionscreen,
            detailImageResId = R.drawable.location1,
            description = "Cultural and academic center",
            dayDuration = "25 Earth hours",
            population = "Billions (interstellar metropolis)",
            diameter = "14,000 km",
            faction = "Alliance of Starfaring Nations",
            lore = "Aurelia is a peaceful and prosperous core world known for its luminous cities, refined culture, and advanced scientific institutions. It is home to the Alliance Flight Academy, one of the most respected pilot training centers in the galaxy. Many of the finest navigators, scouts, and starship captains begin their journey here before venturing into deeper space.\n\nThe planet's oceans and green regions surround elegant urban centers built with a focus on balance and beauty. Aurelia has no major conflicts and maintains diplomatic relations with most factions, serving as a symbol of stability and cooperation across the sector.\n\nFor new pilots, Aurelia represents home, safety, and the very beginning of their galactic story."
        ),
        Location(
            id = "location2",
            name = "Valthor",
            type = "Core world",
            classification = LocationClassification.PLANET,
            rarity = LocationRarity.COMMON,
            selectionImageResId = R.drawable.location2selectionscreen,
            detailImageResId = R.drawable.location2,
            description = "Major trade hub",
            dayDuration = "31 Earth hours",
            population = "Billions (interstellar metropolis)",
            diameter = "15,200 km",
            faction = "None",
            lore = "Valthor is one of the most influential worlds in the entire galactic network. Known for its strict political neutrality, it naturally evolved into a meeting point for travelers, merchants, and diplomatic envoys. Its strategic position along several key hyperspace routes turned it into a central node of commerce where almost anything can be traded or found.\n\nThe planet's cities are famous for their floating boulevards, suspended gardens, and a vibrant cultural life that never slows down. Architectural light structures blend with expansive green spaces, giving Valthor a unique balance between advanced technology and natural beauty. Its diverse population creates a cosmopolitan atmosphere that attracts visitors who want to rest, explore, or prepare for long journeys.\n\nFor most pilots, Valthor is the place to refuel, repair, trade, and recharge. It is a world that always offers something new, no matter how many times someone returns."
        ),
        Location(
            id = "location3",
            name = "ASN Folkshore",
            type = "Light Frigate",
            classification = LocationClassification.CAPITAL_SHIP,
            rarity = LocationRarity.COMMON,
            selectionImageResId = R.drawable.location3selectionscreen,
            detailImageResId = R.drawable.location3,
            selectionTypeDisplay = "Ship",
            description = "",
            weight = "6 million tons",
            length = "420 m",
            population = "1040",
            faction = "Alliance of Star Nations",
            lore = "Alliance light frigates are among the smallest capital vessels in active service, designed to balance autonomy, endurance, and operational flexibility. Compact compared to cruisers and battlecruisers, these ships form the backbone of routine Alliance naval operations across populated systems and established trade corridors. The overall profile of an Alliance light frigate favors a streamlined, reinforced hull with clearly separated functional sections. Most vessels of this class feature limited hangar capacity for auxiliary craft, including shuttles, drones, and small escort vessels. This allows them to conduct inspection operations, search and rescue missions, and logistical support tasks without relying on nearby fleet assets. Crew facilities are optimized for extended deployments, with redundant life support and command systems intended to maintain operational capability under partial system failure. Within the Alliance of Starfaring Nations, light frigates represent presence rather than dominance. They are the ships most commonly encountered near civilian worlds, orbital infrastructure, and developing colonies, serving as a steady and familiar symbol of Alliance coordination and collective security."
        )
    )
    
    /**
     * Get all discovered locations.
     * TODO: Filter to only show discovered locations when discovery system is implemented
     */
    fun getDiscoveredLocations(): List<Location> = locations
    
    /**
     * Get a location by its ID.
     */
    fun getLocationById(id: String): Location? = locations.find { it.id == id }
}

