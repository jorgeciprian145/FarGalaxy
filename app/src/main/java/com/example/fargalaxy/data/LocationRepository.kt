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
        ),
        Location(
            id = "location4",
            name = "Verdantis II",
            type = "Colony world",
            classification = LocationClassification.PLANET,
            rarity = LocationRarity.COMMON,
            selectionImageResId = R.drawable.location4selectionscreen,
            detailImageResId = R.drawable.location4,
            description = "",
            dayDuration = "26 Earth hours",
            population = "950,000",
            diameter = "13,150 km",
            faction = "Alliance of Star Nations",
            lore = "Verdantis II is a world where nature is dominant. The planet's surface is covered by an immense rainforest canopy so thick that sunlight rarely reaches the ground. Below the canopy lies a dim, humid realm of bioluminescent plants, predatory creatures, and ancient organisms that have evolved in near-total darkness.\n\nHuman civilization exists high above the ground on towering platforms, forming elevated cities interconnected by suspension rails and maglev bridges. These settlements support nearly a million inhabitants, including researchers, biotech specialists, climate engineers, and workers involved in extracting and synthesizing rare organic compounds.\n\nVerdantis II is a biological goldmine. Plants produce fibers stronger than steel, spores with rapid genetic adaptation, and enzymes with medicinal properties. Corporations and scientific institutions maintain strict conservation protocols, recognizing that the ecosystem's fragility makes it both invaluable and vulnerable."
        ),
        Location(
            id = "location5",
            name = "Krython B-2",
            type = "Colony moon",
            classification = LocationClassification.PLANET,
            rarity = LocationRarity.COMMON,
            selectionImageResId = R.drawable.location5selectionscreen,
            detailImageResId = R.drawable.location5,
            description = "",
            dayDuration = "19 Earth hours",
            population = "340",
            diameter = "3,200 km",
            faction = "None",
            lore = "Krython B-2 is a cold, mineral-rich moon orbiting Sylthara. Its rugged terrain consists of fractured rock fields, deep impact scars, and pockets of ice that accumulate in permanently shadowed regions. Embedded within its crust are faint concentrations of pre-Sylthium crystalline material, deposited over centuries as particles drifted from Sylthara's ring system and settled across the moon's surface.\n\nThe moon's role in the system is modest but steady. While it is not crucial to the sector's economy, Krython B-2 provides a reliable source of supporting minerals and offers an ideal, stable platform for operations tied to Sylthara's mining industry. For this reason, a large corporate outpost was established here decades ago.\n\nThe outpost is jointly operated by Kalyx Extraction Group and Auricon Dynamics, housing around 340 rotating personnel including miners, technicians, researchers, drone operators, medical staff, and geologists. Its purpose is twofold: to extract and refine small quantities of pre-Sylthium, and to support the scientific study of the mineral interactions between Krython B-2 and Sylthara.\n\nThough its work is technically demanding, the moon itself holds no broader political or military importance. The Alliance maintains no permanent presence here, visiting only for inspections or when coordinating with transit operations. For most workers, Krython B-2 is viewed as a stable, quiet assignment: isolated, harsh, and unremarkable—but undeniably valuable to the corporations that depend on it."
        ),
        Location(
            id = "location6",
            name = "Primeway Station",
            type = "Orbital transit station",
            classification = LocationClassification.SPACE_STATION,
            rarity = LocationRarity.COMMON,
            selectionImageResId = R.drawable.location6selectionscreen,
            detailImageResId = R.drawable.location6,
            selectionTypeDisplay = "Space station",
            description = "",
            weight = "88 million tons",
            diameter = "8.6 km (outer arms)",
            population = "8000",
            faction = "None",
            lore = "Primeway Station is a mid-sized orbital facility positioned along several well-traveled transport routes in the region. Built by the Primeway Transit Consortium decades ago, the station was designed to support steady commercial movement rather than to impress visitors. Its structure consists of a tall central spine surrounded by layered rings, each dedicated to a different function: refueling, maintenance, cargo handling, and short-stay habitation.\n\nTraffic flows constantly through the station's three exterior docking arms, where small and mid-sized ships anchor for quick refueling or routine maintenance. Cargo drones and automated lifts operate around the lower rings, transferring sealed containers between freighters and storage modules before redirecting them toward nearby systems. Upper levels house control centers, life-support systems, navigation offices, and the administrative branches of several transport companies that rely on the station for day-to-day operations.\n\nInside, Primeway hosts a compact but practical mix of services: parts vendors, contract brokers, rest pods, supply counters, and scattered eateries that serve crews passing through on tight schedules. Pilots rarely stay long—just enough to refuel, restock, file transit data, or pick up minor jobs before returning to their route.\n\nAlthough unassuming compared to major hubs in the galaxy, Primeway Station remains an essential waypoint for travelers who depend on reliable infrastructure to keep moving. Its constant movement of vessels, cargo, and personnel gives it a quiet life of its own: always active, always useful, and always ready to serve the next arrival."
        ),
        Location(
            id = "location7",
            name = "Elyrion",
            type = "Colony world",
            classification = LocationClassification.PLANET,
            rarity = LocationRarity.COMMON,
            selectionImageResId = R.drawable.location7selectionscreen,
            detailImageResId = R.drawable.location7,
            description = "",
            dayDuration = "26 Earth hours",
            population = "600,000",
            diameter = "5,600 km",
            faction = "Independent Systems Federation",
            lore = "Elyrion is a large habitable moon known for its lush landscapes, turquoise waters, and diverse ecosystem. It has a mild climate and oxygen-rich atmosphere, supporting dense forests, vibrant wetlands, and coastal regions that change color with the sun. Bioluminescent vegetation creates a soft glow visible from orbit at night.\n\nElyrion Haven, the main settlement, is home to roughly 420,000 residents. It evolved from a scientific outpost into a colony focused on agriculture, ecological research, and light tourism. The moon's fertile soils produce high-yield crops and unique bioplants for export, and research institutes study its flora for medical and biotechnological applications.\n\nElyrion is a welcoming destination with modest cities integrated into the environment, connected by lightweight magrails, and surrounded by preserved natural reserves. Inhabitants prioritize quality of life and a peaceful atmosphere over economic opportunity.\n\nWhile not strategically important, Elyrion plays a steady role in supporting trade, science, and culture. It stands out in a galaxy of industrial hubs and harsh frontier worlds as a place where life thrives."
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

