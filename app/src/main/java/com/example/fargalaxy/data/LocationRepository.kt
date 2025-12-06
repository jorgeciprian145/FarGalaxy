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
            dayDuration = "1.05 Earth days",
            population = "Billions (interstellar metropolis)",
            diameter = "14,000 km",
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
            dayDuration = "1.3 Earth days",
            population = "Billions (interstellar metropolis)",
            diameter = "15,200 km",
            lore = "Valthor is one of the most influential worlds in the entire galactic network. Known for its strict political neutrality, it naturally evolved into a meeting point for travelers, merchants, and diplomatic envoys. Its strategic position along several key hyperspace routes turned it into a central node of commerce where almost anything can be traded or found.\n\nThe planet's cities are famous for their floating boulevards, suspended gardens, and a vibrant cultural life that never slows down. Architectural light structures blend with expansive green spaces, giving Valthor a unique balance between advanced technology and natural beauty. Its diverse population creates a cosmopolitan atmosphere that attracts visitors who want to rest, explore, or prepare for long journeys.\n\nFor most pilots, Valthor is the place to refuel, repair, trade, and recharge. It is a world that always offers something new, no matter how many times someone returns."
        ),
        Location(
            id = "location3",
            name = "ASN Silver Meridian",
            type = "Horizon-class battlecruiser",
            classification = LocationClassification.CAPITAL_SHIP,
            rarity = LocationRarity.COMMON,
            selectionImageResId = R.drawable.location3selectionscreen,
            detailImageResId = R.drawable.location3,
            selectionTypeDisplay = "Ship", // Display "Ship" in selection screen
            description = "", // Not used for capital ships
            weight = "56 million tons",
            length = "1.5 km",
            population = "5.000 crew members",
                lore = "The ASN Silver Meridian is a Horizon-class battlecruiser operated by the Alliance Star Navy. Designed as a long-range defensive and command vessel, it plays a critical role in safeguarding the most important worlds aligned with the Alliance. Although it is not the largest ship in the fleet, its balanced combination of endurance, presence, and advanced technology makes it one of the most respected vessels in active service.\n\nThe Silver Meridian is most frequently stationed near Aurelia, home of the Alliance Flight Academy and one of the cultural centers of the region. Its presence above the planet is both symbolic and practical. For many young pilots beginning their training on Aurelia, the silhouette of the Silver Meridian hanging quietly in orbit represents the future they aspire to reach: service, mastery, and the responsibility of protecting peaceful worlds."
        ),
        Location(
            id = "location4",
            name = "Sylthara",
            type = "Gas giant",
            classification = LocationClassification.PLANET,
            rarity = LocationRarity.COMMON,
            selectionImageResId = R.drawable.location4selectionscreen,
            detailImageResId = R.drawable.location4,
            isFullWidthImage = true, // Image expands to full width
            shouldOverflowSelectionImage = true, // Selection image overflows container (full height, wider)
            description = "Industrial mining",
            dayDuration = "11 Earth hours",
            population = "None",
            diameter = "110,000 km",
            lore = "Sylthara is a massive violet gas giant whose distinctive color comes from microscopic Sylthium crystals suspended in its upper atmosphere. These particles, originally formed within the planet's luminous ring system, refract starlight into shades of deep purple and rose, giving Sylthara its unmistakable appearance.\n\nSylthium is a highly conductive mineral essential for long-range communication arrays and quantum sensor modules. The resource is extremely valuable and difficult to harvest, requiring specialized platforms capable of operating within the planet's turbulent upper layers. Violent electrical storms and shifting pressure gradients make mining operations dangerous, and workers typically serve in short rotational tours to reduce psychological and physical strain.\n\nBetween six and eight major mining platforms operate on Sylthara at any given time, each housing between 450 and 700 personnel. These massive structures are stabilized using magneto-gravitic anchors that lock onto Sylthium-rich clouds moving through the atmosphere."
        ),
        Location(
            id = "location5",
            name = "Krython B-2",
            type = "Rocky moon",
            classification = LocationClassification.PLANET,
            rarity = LocationRarity.COMMON,
            selectionImageResId = R.drawable.location5selectionscreen,
            detailImageResId = R.drawable.location5,
            description = "Mineral rich",
            dayDuration = "19 Earth hours",
            population = "340 personnel (outpost staff)",
            diameter = "3,200 km",
            lore = "Krython B-2 is a cold, mineral-rich moon orbiting Sylthara. Its rugged terrain consists of fractured rock fields, deep impact scars, and pockets of ice that accumulate in permanently shadowed regions. Embedded within its crust are faint concentrations of pre-Sylthium crystalline material, deposited over centuries as particles drifted from Sylthara's ring system and settled across the moon's surface.\n\nThe moon's role in the system is modest but steady. While it is not crucial to the sector's economy, Krython B-2 provides a reliable source of supporting minerals and offers an ideal, stable platform for operations tied to Sylthara's mining industry. For this reason, a large corporate outpost was established here decades ago.\n\nThe outpost is jointly operated by Kalyx Extraction Group and Auricon Dynamics, housing around 340 rotating personnel including miners, technicians, researchers, drone operators, medical staff, and geologists. Its purpose is twofold:  to extract and refine small quantities of pre-Sylthium, and to support the scientific study of the mineral interactions between Krython B-2 and Sylthara.\n\nThough its work is technically demanding, the moon itself holds no broader political or military importance. The Alliance maintains no permanent presence here, visiting only for inspections or when coordinating with transit operations. For most workers, Krython B-2 is viewed as a stable, quiet assignment: isolated, harsh, and unremarkable—but undeniably valuable to the corporations that depend on it."
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

