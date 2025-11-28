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
                widthMeters = 17f,
                widthFeet = 56f
            ),
            crewCapacity = CrewCapacity(
                pilots = 2,
                crewMembers = 4
            ),
            lore = "After countless prototypes and more failures than the engineers cared to admit, Valketh Industries finally released a ship that changed the way new pilots entered the galaxy. The Phantom was never designed to be the fastest or the strongest. It was built to endure, to forgive mistakes, and to carry beginners through their first uncertain steps into deep space.\n\nIts frame is simple, its systems modest, and its performance unremarkable when compared to the elite vessels flown by veteran crews. Yet the Phantom earned its reputation through grit rather than glory. It survives rough landings, unstable jump routes, and long stretches of travel where more advanced ships would demand repairs. For generations of cadets, its hum has been the first sound they heard before taking off into the void.\n\nMost pilots eventually outgrow the Phantom once they gain skill and confidence, trading it for ships that push the limits of speed, firepower, or exploration range. But the Phantom stays with them. It becomes the memory of their first real flight, the craft that caught their mistakes and carried their victories, the starting point of every career that ever reached the stars.",
            // Gameplay properties
            speed = 100f,
            acceleration = 50f,
            warpConverters = 75f,
            uniqueTrait = null // No unique trait for this ship
        ),
        Ship(
            id = "type45c_shooting_star",
            name = "Type 45C Shooting Star",
            manufacturer = "Soren Shipworks",
            type = "Interceptor Starcraft",
            rarity = ShipRarity.COMMON,
            imageResId = R.drawable.ship2,
            renderImageResId = R.drawable.ship2render,
            dimensions = ShipDimensions(
                lengthMeters = 32f,
                lengthFeet = 105f,
                widthMeters = 31f,
                widthFeet = 102f
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 2
            ),
            lore = "Not every mission calls for long stretches in deep space. Some require beds, rations and days of quiet travel. Others simply demand speed. The Shooting Star was built for the second kind.\n\nThis agile, lightweight craft excels at quick planetary hops and rapid interception runs, especially when suspicious vessels drop out of hyperspace without warning. Its compact frame may suggest a single-pilot fighter, yet the cockpit is cleverly engineered to hold a three-person crew: a pilot at the helm, a navigator charting optimal routes, and a communications specialist who stays linked to central command, feeding real-time intel as the situation unfolds.\n\nThe Shooting Star isn't a ship you live in. It's a ship you launch when time matters and hesitation isn't an option.",
            speed = 100f,
            acceleration = 50f,
            warpConverters = 75f,
            uniqueTrait = null
        ),
        Ship(
            id = "a300_albatross",
            name = "A-300 Albatross",
            manufacturer = "Valketh Industries",
            type = "Multi-purpose Hypertravel Starcraft",
            rarity = ShipRarity.UNCOMMON,
            imageResId = R.drawable.ship4,
            renderImageResId = R.drawable.ship4render,
            dimensions = ShipDimensions(
                lengthMeters = 33f,
                lengthFeet = 108f,
                widthMeters = 45f,
                widthFeet = 148f
            ),
            crewCapacity = CrewCapacity(
                pilots = 2,
                crewMembers = 4
            ),
            lore = "The Albatross is a reliable, well-rounded craft built for almost any assignment the Alliance can throw at it. Pilots trust it for its steady handling and sturdy frame, and it has become a common sight on missions that span a few days. Its interior isn't luxurious, but it's surprisingly comfortable, letting crews stay out in the field longer than the mission log originally intended.\n\nAmong pilots, there's an old joke that the Albatross mechanic is usually the most relaxed member of the team. The ship rarely complains, rarely needs emergency fixes, and often returns from long routes without a single warning light. It may not be the flashiest vessel in the hangar, but it has proven time and time again that dependability is its greatest weapon.",
            speed = 120f,
            acceleration = 60f,
            warpConverters = 85f,
            uniqueTrait = null
        ),
        Ship(
            id = "b7f_starforce",
            name = "B7F Starforce",
            manufacturer = "Marakeshi Space Technologies",
            type = "Medium Hypertravel Starcraft",
            rarity = ShipRarity.UNCOMMON,
            imageResId = R.drawable.ship5,
            renderImageResId = R.drawable.ship5render,
            dimensions = ShipDimensions(
                lengthMeters = 38f,
                lengthFeet = 125f,
                widthMeters = 45f,
                widthFeet = 148f
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 3
            ),
            lore = "The Starforce is the sports car of hyperspace travel. Sleek lines, smooth handling and a punchy performance made it an instant icon from the moment Marakeshi unveiled their first mid-size model. It didn't take long for thrill-seeking civilian pilots to fall in love with it.\n\nIt does demand more maintenance than the simpler ships in its class, but anyone who has pushed its twin double-engines to full throttle knows the trade-off is worth it. The Starforce was built for pilots who enjoy the ride as much as the destination.\n\nJust remember to ease off the drive when passing through regulated lanes. Hyperspace patrols have very little patience for speed enthusiasts.",
            speed = 120f,
            acceleration = 60f,
            warpConverters = 85f,
            uniqueTrait = null
        ),
        Ship(
            id = "h98_valkyrie",
            name = "H-98 Valkyrie",
            manufacturer = "Karnyx Armory Division",
            type = "Multi-purpose Hypertravel Starcraft",
            rarity = ShipRarity.EPIC,
            imageResId = R.drawable.ship10,
            renderImageResId = R.drawable.ship10render,
            dimensions = ShipDimensions(
                lengthMeters = 43f,
                lengthFeet = 141f,
                widthMeters = 41f,
                widthFeet = 135f
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 3
            ),
            lore = "If toughness is what you're after, few ships embody it as completely as the Valkyrie. Originally built as a military starcraft, it was designed to launch from cruisers and carry out orbital patrols and strike missions. Its armor proved so durable that, after years of service, engineers eventually refitted the model with hypertravel capabilities and released a civilian-approved variant.\n\nEven without its cannons and missile racks, the Valkyrie still looks unmistakably intimidating. Many pilots embrace that legacy and repaint their vessels in the original military scheme, keeping the ship's combat heritage alive in spirit.\n\nJust remember to announce your peaceful intentions when approaching populated zones. For some of the outer colonies, the sight of a Valkyrie blazing down through orbit still carries memories of darker times.",
            speed = 200f,
            acceleration = 100f,
            warpConverters = 150f,
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
    
    // Store the current ship ID (defaults to first ship)
    private var currentShipId: String = ships.first().id
    
    /**
     * Get the currently selected ship.
     */
    fun getCurrentShip(): Ship = ships.find { it.id == currentShipId } ?: ships.first()
    
    /**
     * Set the currently selected ship by ID.
     */
    fun setCurrentShip(shipId: String) {
        if (ships.any { it.id == shipId }) {
            currentShipId = shipId
        }
    }
}

