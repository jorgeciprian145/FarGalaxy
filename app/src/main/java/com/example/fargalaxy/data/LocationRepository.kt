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
            weight = "12 million tons",
            length = "1.1 km",
            population = "2,000 crew members",
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
        ),
        Location(
            id = "location6",
            name = "Primeway Station",
            type = "Orbital transit station",
            classification = LocationClassification.SPACE_STATION,
            rarity = LocationRarity.COMMON,
            selectionImageResId = R.drawable.location6selectionscreen,
            detailImageResId = R.drawable.location6,
            selectionTypeDisplay = "Space station", // Display "Space station" in selection screen
            description = "",
            weight = "88 million tons",
            diameter = "8.6 km (outer arms)",
            population = "8,000 personnel",
            lore = "Primeway Station is a mid-sized orbital facility positioned along several well-traveled transport routes in the region. Built by the Primeway Transit Consortium decades ago, the station was designed to support steady commercial movement rather than to impress visitors. Its structure consists of a tall central spine surrounded by layered rings, each dedicated to a different function: refueling, maintenance, cargo handling, and short-stay habitation.\n\nTraffic flows constantly through the station's three exterior docking arms, where small and mid-sized ships anchor for quick refueling or routine maintenance. Cargo drones and automated lifts operate around the lower rings, transferring sealed containers between freighters and storage modules before redirecting them toward nearby systems. Upper levels house control centers, life-support systems, navigation offices, and the administrative branches of several transport companies that rely on the station for day-to-day operations.\n\nInside, Primeway hosts a compact but practical mix of services: parts vendors, contract brokers, rest pods, supply counters, and scattered eateries that serve crews passing through on tight schedules. Pilots rarely stay long—just enough to refuel, restock, file transit data, or pick up minor jobs before returning to their route.\n\nAlthough unassuming compared to major hubs in the galaxy, Primeway Station remains an essential waypoint for travelers who depend on reliable infrastructure to keep moving. Its constant movement of vessels, cargo, and personnel gives it a quiet life of its own: always active, always useful, and always ready to serve the next arrival."
        ),
        Location(
            id = "location7",
            name = "Elyrion",
            type = "Habitable moon",
            classification = LocationClassification.PLANET,
            rarity = LocationRarity.COMMON,
            selectionImageResId = R.drawable.location7selectionscreen,
            detailImageResId = R.drawable.location7,
            description = "Flora and fauna rich",
            dayDuration = "26 Earth hours",
            population = "600,000 (Colony)",
            diameter = "5,600 km",
            lore = "Elyrion is a large habitable moon known for its lush landscapes, turquoise waters, and remarkably diverse ecosystem. Its mild climate and oxygen-rich atmosphere allowed life to flourish long before human settlement, giving rise to dense forests, vibrant wetlands, and wide coastal regions that shift in color depending on the angle of the sun. At night, patches of bioluminescent vegetation shimmer across the shadowed side, creating a soft glow visible even from orbit.\n\nThe main settlement, Elyrion Haven, is home to roughly 600,000 residents. What began as a small scientific outpost grew into a stable colony centered around agriculture, ecological research, and light tourism. The moon's fertile soils support high-yield crops and unique bioplants that are exported to nearby systems, while research institutes study Elyrion's flora for medical and biotechnological applications.\n\nTravelers describe Elyrion as one of the most welcoming destinations in the region. Its cities are modest but well-integrated into the environment, connected by lightweight magrails and surrounded by carefully preserved natural reserves. Most inhabitants choose Elyrion for its quality of life and its peaceful atmosphere rather than economic opportunity.\n\nThough not considered strategically important, the moon plays a steady role in supporting trade, science, and culture. In a galaxy filled with industrial hubs and harsh frontier worlds, Elyrion stands out simply for being a place where life thrives."
        ),
        Location(
            id = "location8",
            name = "KEG Stoneweld",
            type = "Heavy industrial mining barge",
            classification = LocationClassification.CAPITAL_SHIP,
            rarity = LocationRarity.COMMON,
            selectionImageResId = R.drawable.location8selectionscreen,
            detailImageResId = R.drawable.location8,
            isFullWidthImage = true, // Image expands to full width
            selectionTypeDisplay = "Ship", // Display "Ship" in selection screen
            description = "", // Not used for capital ships
            weight = "8 million tons",
            length = "920 m",
            population = "640 personnel",
            lore = "The KEG Stoneweld is one of Kalyx Extraction Group's flagship heavy mining barges, engineered for deep-resource extraction in some of the most hostile environments in known space. Built around a reinforced rectangular superstructure, the Stoneweld prioritizes durability over aesthetics: thick armored plating, exposed industrial frameworks, and modular cargo blocks form a silhouette that resembles a mobile refinery more than a starship.\n\nPowered by redundant reactor arrays and supported by a fleet of over three hundred autonomous mining drones, the barge can bore into dense asteroid clusters, skim mineral-rich cloud bands in gas giants, or anchor above unstable lunar surfaces. Its advanced magnetic collectors and onboard nano-refiners allow the Stoneweld to process raw material as it works, drastically reducing hauling costs.\n\nA typical mission lasts several months, during which its crew of engineers, technicians, and extraction specialists live inside long, cramped industrial corridors. Despite its harsh conditions, the Stoneweld is valued for its reliability: stories among KEG teams describe barges surviving impacts that would tear lighter ships apart."
        ),
        Location(
            id = "location9",
            name = "Hjorn-4",
            type = "Frozen planet",
            classification = LocationClassification.PLANET,
            rarity = LocationRarity.COMMON,
            selectionImageResId = R.drawable.location9selectionscreen,
            detailImageResId = R.drawable.location9,
            isFullWidthImage = true, // Image expands to full width
            description = "Ice extraction",
            dayDuration = "1.3 Earth days",
            population = "11,500 inhabitants (small colony)",
            diameter = "8,900 km",
            lore = "Hjorn-4 is a remote frozen planet defined by towering glacial ridges and endless plains of ancient ice. Temperatures rarely rise above freezing, and seasonal storms can bury entire outposts under meters of drifting snow. Despite its harsh environment, the world holds a valuable resource beneath its surface: deep layers of extremely pure ice formed over millions of years, untouched by contaminants or volcanic activity.\n\nThe planet's only major settlement, Frosthaven Station, sits inside a natural canyon carved long ago by shifting ice flows. The walls of the canyon shield the colony from the worst of Hjorn-4's storms, allowing refineries, storage silos, and habitation domes to function year-round. Most residents are technicians, engineers, or transport crews who rotate in and out of the world on fixed contracts, though a small group of long-term settlers call the station home.\n\nEvery day, extraction crews descend into the subglacial tunnels to harvest blocks of pristine ice, which are then melted, filtered, and condensed into ultrapure water. This resource is in high demand across nearby systems, supporting everything from high-efficiency reactor coolant loops to biotechnological manufacturing processes. A secondary byproduct, Hjornite, is occasionally recovered from deeper layers—an unremarkable mineral on its own but useful as a stabilizer for low-grade energy cells."
        ),
        Location(
            id = "location10",
            name = "Tavros-IV",
            type = "Gas giant",
            classification = LocationClassification.PLANET,
            rarity = LocationRarity.COMMON,
            selectionImageResId = R.drawable.location10selectionscreen,
            detailImageResId = R.drawable.location10,
            isFullWidthImage = true, // Image expands to full width on LocationDetailsScreen
            description = "Navigation reference",
            dayDuration = "9.8 hours",
            population = "None",
            diameter = "108,000 km",
            lore = "Tavros-IV is one of the sector's most stable and visually distinct gas giants, defined by its warm palette of amber, brown, and muted orange clouds. Unlike more turbulent giants, the atmosphere of Tavros-IV flows in wide, gentle bands sculpted by its rapid rotation. The result is a planet that appears serene from orbit—an enormous sphere of soft gradients and layered textures drifting through the void.\n\nWhile the planet has no solid surface and cannot support any form of settlement, Tavros-IV plays an important role in local navigation. Its immense gravity well and easily identifiable coloration make it a natural reference point for ships traversing the outer systems. Pilots frequently use Tavros-IV as a timing marker or orbital slingshot when plotting long-range jumps.\n\nIndustrial corporations have shown interest in the planet's upper atmosphere, which contains small concentrations of helium-3 and other reactive isotopes. Extraction efforts remain limited to automated drones and experimental collectors. The density and heat of the cloud layers make large-scale harvesting impractical.\n\nThree small icy moons orbit the giant, none of them large enough or stable enough to support permanent colonies. Instead, they host rotating research platforms that study Tavros-IV's atmospheric dynamics and magnetic field."
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

