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
                lengthFeet = 105f, // 32m * 3.28084 = 104.99ft ≈ 105ft
                widthMeters = 17f,
                widthFeet = 56f // 17m * 3.28084 = 55.77ft ≈ 56ft
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 3
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
            id = "navakeshi_star_pouncer",
            name = "Navakeshi Star Pouncer",
            manufacturer = "Kel'Varra Star Systems",
            type = "Interceptor Starcraft",
            rarity = ShipRarity.COMMON,
            imageResId = R.drawable.ship3,
            renderImageResId = R.drawable.ship3render,
            dimensions = ShipDimensions(
                lengthMeters = 36f,
                lengthFeet = 118f, // 36m * 3.28084 = 118.11ft ≈ 118ft
                widthMeters = 18f,
                widthFeet = 59f // 18m * 3.28084 = 59.06ft ≈ 59ft
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 1
            ),
            lore = "A fairly common sight whenever you enter Navakeshi controlled space. This light vessel forms the lowest tier of their space fleet and has been a constant presence on near planetary patrol routes for generations. Most units lack hyperspace capability entirely, though a limited number are equipped with basic hypertravel engines and assigned to short interplanetary duties when required.\n\nFor many young Navakeshi pilots, this interceptor marks the beginning of their service. It is not impressive in speed or firepower, but its mass produced design has trained generations of recruits and helped safeguard local worlds with quiet consistency. A basic interceptor that has proven its worth over time and a respectable force when encountered in numbers.",
            speed = 100f,
            acceleration = 50f,
            warpConverters = 75f,
            uniqueTrait = null
        ),
        Ship(
            id = "p7h_skyblazer",
            name = "P7H Skyblazer",
            manufacturer = "Marakeshi Space Technologies",
            type = "Multi-purpose Hypertravel Starcraft",
            rarity = ShipRarity.UNCOMMON,
            imageResId = R.drawable.ship19,
            renderImageResId = R.drawable.ship19render,
            dimensions = ShipDimensions(
                lengthMeters = 50f,
                lengthFeet = 140f, // 50m * 3.28084 = 164.04ft, but image shows 140ft
                widthMeters = 40f,
                widthFeet = 39f // 40m * 3.28084 = 131.23ft, but image shows 39ft
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 4
            ),
            lore = "The P7H Skyblazer was the first design developed by Marakeshi Space Technologies for the military sector, although later variants were adapted for civilian use. Built around a robust and flexible frame, the Skyblazer was engineered to perform across a wide range of roles, from long-range hypertravel jumps to sustained operations in low-orbit environments.\n\nAlthough commonly deployed for air-to-ground support and near-orbit patrols, the Skyblazer has also been adopted by several private armed groups. Its ability to quickly disengage from conflict, combined with enough firepower to defend itself when escape is not an option, has made it a popular choice for interplanetary light cargo runs and high-risk VIP transport missions.",
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
            id = "navakeshi_star_crusher",
            name = "Navakeshi Star Crusher",
            manufacturer = "Kel'Varra Star Systems",
            type = "Medium Interceptor, Hypertravel Starcraft",
            rarity = ShipRarity.UNCOMMON,
            imageResId = R.drawable.ship6,
            renderImageResId = R.drawable.ship6render,
            dimensions = ShipDimensions(
                lengthMeters = 36f,
                lengthFeet = 118f, // 36m * 3.28084 = 118.11ft ≈ 118ft
                widthMeters = 33f,
                widthFeet = 108f // 33m * 3.28084 = 108.27ft ≈ 108ft
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 1
            ),
            lore = "The natural evolution of the Pouncer, the Crusher improves in almost every way. This ship is reserved for advanced pilots who have proven themselves capable. It is equipped with larger engines, reinforced fuselage armor, expanded weapon hardpoints and, unlike its predecessor, every unit comes with full hypertravel capability.\n\nThe Crusher is built to reach conflict zones no matter where they arise. It can respond to distress calls across distant systems, push through hostile territory and hold its ground long enough for heavier Navakeshi forces to arrive. It is still a light interceptor at its core, but its versatility and reach make it one of the most relied upon ships in the mid-tier ranks of the Navakeshi fleet.",
            speed = 120f,
            acceleration = 60f,
            warpConverters = 85f,
            uniqueTrait = null
        ),
        Ship(
            id = "b15_specter",
            name = "B15 Specter",
            manufacturer = "Valketh Industries",
            type = "Medium Hypertravel Starcraft",
            rarity = ShipRarity.RARE,
            imageResId = R.drawable.ship7,
            renderImageResId = R.drawable.ship7render,
            dimensions = ShipDimensions(
                lengthMeters = 38f,
                lengthFeet = 125f, // 38m * 3.28084 = 124.67ft ≈ 125ft
                widthMeters = 36f,
                widthFeet = 118f // 36m * 3.28084 = 118.11ft ≈ 118ft
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 4
            ),
            lore = "After the tremendous success and widespread popularity of the Phantom, Valketh set out to create a more advanced successor. The Specter builds on everything its predecessor did right. It handles well, remains reliable under pressure and is still simple and inexpensive to maintain. It also expands its capabilities with a more powerful engine and significantly improved hyperspace performance.\n\nPilots who trained with the Phantom are naturally drawn to this refined evolution of their first vessel. The Specter keeps the familiar feel of the original while offering a more capable and responsive platform for pilots ready to grow beyond entry level ships.\n\nAmong independent crews, the Specter has already earned a solid place as a dependable workhorse for long range missions and frontier travel. Its balance of power, efficiency and low upkeep costs makes it well suited for those who operate without the support of a formal fleet. Many freelancers regard it as an ideal partner for life between systems, versatile enough to take wherever the next job leads.",
            speed = 135f,
            acceleration = 67f,
            warpConverters = 92f,
            uniqueTrait = null
        ),
        Ship(
            id = "n6_98_melina",
            name = "N6-98 Melina",
            manufacturer = "Marakeshi Space Technologies",
            type = "Multi-purpose Hypertravel Starcraft",
            rarity = ShipRarity.RARE,
            imageResId = R.drawable.ship8,
            renderImageResId = R.drawable.ship8render,
            dimensions = ShipDimensions(
                lengthMeters = 42f,
                lengthFeet = 138f, // 42m * 3.28084 = 137.8ft ≈ 138ft
                widthMeters = 44f,
                widthFeet = 144f // 44m * 3.28084 = 144.4ft ≈ 144ft
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 3
            ),
            lore = "The Melina is Marakeshi's answer to the competing Albatross. The goal was to create a well rounded starship that would appeal both to pilots seeking a dependable workhorse and to travelers who enjoy a more spirited flying experience.\n\nAlthough it lacks the endurance of its rival and tends to require slightly more maintenance, the Melina has secured a strong following among pilots who value a faster vessel with higher overall performance. It offers a more energetic flight profile without sacrificing the practicality expected from a ship in its class.\n\nOverall, the Melina has proved to be a capable and versatile starship, able to hold its own on day to day travel as well as longer system hops. For many pilots, it strikes the right balance between functionality and enjoyment, making it one of Marakeshi's most appreciated civilian designs.",
            speed = 140f,
            acceleration = 70f,
            warpConverters = 95f,
            uniqueTrait = null
        ),
        Ship(
            id = "model3_tortoise_ccp",
            name = "Model 3 \"Tortoise\" Compact cargo platform",
            manufacturer = "Karnyx Armory Division",
            type = "Hypertravel cargo starcraft",
            rarity = ShipRarity.RARE,
            imageResId = R.drawable.ship9,
            renderImageResId = R.drawable.ship9render,
            dimensions = ShipDimensions(
                lengthMeters = 54f,
                lengthFeet = 177f, // 54m * 3.28084 = 177.17ft ≈ 177ft
                widthMeters = 40f,
                widthFeet = 131f // 40m * 3.28084 = 131.23ft ≈ 131ft
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 1
            ),
            lore = "Both in life and in space travel, looks are not everything, especially if your goal is to move cargo through hyperspace lanes. That is exactly what this heavy cargo starcraft was designed to do. Affectionately called \"Tortoise\" by Alliance Fleet pilots, this quadruple-engined workhorse does not waste a single inch of interior space on anything that is not part of its cargo hold.\n\nIt can only seat two crew members in its cockpit, and that is the only section with room for seating at all. The nickname comes not only from the way it looks from above, but also as a reminder of the old fable \"The Tortoise and the Hare,\" where slow and steady carries the day. And aboard this ship, you will definitely be going slow and steady.\n\nDespite its bulky appearance, the Tortoise can land almost anywhere, from cramped colony pads to improvised dirt clearings. Pilots often praise it for its stubborn determination to complete a run, even when conditions turn unpredictable or outright hostile.",
            speed = 130f,
            acceleration = 65f,
            warpConverters = 90f,
            uniqueTrait = null
        ),
        Ship(
            id = "navakeshi_star_ravager",
            name = "Navakeshi Star Ravager",
            manufacturer = "Kel'Varra Star Systems",
            type = "Assault Hypertravel Starcraft",
            rarity = ShipRarity.EPIC,
            imageResId = R.drawable.ship11,
            renderImageResId = R.drawable.ship11render,
            dimensions = ShipDimensions(
                lengthMeters = 36f,
                lengthFeet = 118f, // 36m * 3.28084 = 118.11ft ≈ 118ft
                widthMeters = 35f,
                widthFeet = 115f // 35m * 3.28084 = 114.83ft ≈ 115ft
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 1
            ),
            lore = "When a situation calls for a decisive response, attack formations of these vessels are often the first to blaze toward their target. The Ravager is the most complete starfighter in the Navakeshi fleet, combining exceptional speed, precise maneuverability, and formidable offensive power.\n\nReserved exclusively for veteran pilots, the Ravager is assigned only to those who have proven themselves through years of service. Flying one is considered a significant honor, as controlling the immense output of its four engines demands discipline, experience, and absolute confidence. Only the most skilled pilots in the fleet are entrusted with a ship of this caliber.\n\nTo many rival factions, the sudden appearance of a Ravager wing is reason enough to reconsider an attack and break away at full burn.",
            speed = 150f,
            acceleration = 75f,
            warpConverters = 100f,
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
            id = "silver_lightning",
            name = "Silver Lightning",
            manufacturer = "Tiona Starworks",
            type = "Custom built",
            rarity = ShipRarity.LEGENDARY,
            imageResId = R.drawable.ship12,
            renderImageResId = R.drawable.ship12render,
            dimensions = ShipDimensions(
                lengthMeters = 50f,
                lengthFeet = 164f,
                widthMeters = 32f,
                widthFeet = 105f
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 5
            ),
            lore = "If you've made it this far, you're standing before one of the rarest sights in the entire sector. Few pilots ever get the chance to lay eyes on the Silver Lightning, let alone fly it. Hand-built by a small circle of master engineers and designers, this vessel is truly one of a kind.\n\nDespite its graceful silhouette, the Silver Lightning handles with a precision that feels almost unreal. It boasts far more power than any civilian ship of comparable size, and its interior is nothing short of luxurious. The cabin comfortably accommodates a crew of six, complete with private quarters reserved for the captain.\n\nIts iconic silver sheen comes from a mysterious alloy used in the fuselage, a material whose origin is still debated among collectors and enthusiasts. Beyond the rumors and the craftsmanship, one thing is certain: if you ever find yourself sitting behind the controls of the Silver Lightning, consider yourself among the very fortunate few.",
            speed = 200f,
            acceleration = 100f,
            warpConverters = 150f,
            uniqueTrait = null
        ),
        Ship(
            id = "vulcani_legenda_f1",
            name = "Vulcani Legenda F-1",
            manufacturer = "Aurellian Atelier Works",
            type = "Custom built",
            rarity = ShipRarity.LEGENDARY,
            imageResId = R.drawable.ship13,
            renderImageResId = R.drawable.ship13render,
            dimensions = ShipDimensions(
                lengthMeters = 52f,
                lengthFeet = 171f, // 52m * 3.28084 = 170.60ft ≈ 171ft
                widthMeters = 24f,
                widthFeet = 79f // 24m * 3.28084 = 78.74ft ≈ 79ft
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 3
            ),
            lore = "Probably one of the most exquisite and desired starships in the entire sector, and by far one of the best high-performing vessels ever built. The Vulcani Legenda was assembled almost entirely by hand, crafted by a small circle of builders whose dedication borders on obsession. Every line of its frame evokes a sense of motion, a tribute to an old Earth motorsport tradition that faded long ago but was never forgotten.\n\nPowered by an engine that is almost absurd for a ship of its size, the Legenda delivers a level of performance that truly matches its appearance. It is not a vessel for casual pilots. Only those who have mastered the finer instincts of spaceflight can hope to control it at full output, and even then it demands constant precision.\n\nAs a sign of respect for their craft, Aurelian imposes a strict selection process before granting ownership of a Legenda. The ship is not sold to the highest bidder, but entrusted to pilots who can appreciate the discipline, skill and heritage required to command it. Those who pass the trials and earn the privilege join a lineage of captains who treat the Vulcani Legenda not merely as a starship, but as a masterpiece.",
            speed = 200f,
            acceleration = 100f,
            warpConverters = 150f,
            uniqueTrait = null
        ),
        Ship(
            id = "force_of_nature",
            name = "Force of nature",
            manufacturer = "Eternal Infinitum (according to what was deciphered)",
            type = "Unknown",
            rarity = ShipRarity.MYTHICAL,
            imageResId = R.drawable.ship14,
            renderImageResId = R.drawable.ship14render,
            dimensions = ShipDimensions(
                lengthMeters = 52f,
                lengthFeet = 171f, // 52m * 3.28084 = 170.60ft ≈ 171ft
                widthMeters = 34f,
                widthFeet = 112f // 34m * 3.28084 = 111.55ft ≈ 112ft
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 3
            ),
            lore = "Even the brightest minds in the galaxy have yet to understand how this vessel is possible. Its origins and inner workings remain almost entirely unknown. What little has been uncovered points to a power core capable of generating enough output to sustain an entire colony on a small planet. The energy it produces is so overwhelming that the ship must constantly discharge the excess simply to remain stable.\n\nThe craft feels ancient in presence, yet its construction relies on a form of technology that has never been documented. Engineers admit they are centuries away from even beginning to replicate it. Every panel and conduit suggests a level of sophistication that defies anything currently built in the known systems.\n\nOnly the most skilled and disciplined pilots can be trusted with a vessel like this. Before stepping into the cockpit, make sure you understand what you are getting into. This is a vessel that feels alive, a ship that seems unwilling to be held back and always ready to leap into hyperspace the moment you loosen your grip.",
            speed = 250f,
            acceleration = 125f,
            warpConverters = 200f,
            uniqueTrait = null
        ),
        Ship(
            id = "dying_star",
            name = "Dying Star",
            manufacturer = "Eternal Infinitum (according to what was deciphered)",
            type = "Unknown",
            rarity = ShipRarity.MYTHICAL,
            imageResId = R.drawable.ship15,
            renderImageResId = R.drawable.ship15render,
            dimensions = ShipDimensions(
                lengthMeters = 43f,
                lengthFeet = 141f, // 43m * 3.28084 = 141.08ft ≈ 141ft
                widthMeters = 32f,
                widthFeet = 105f // 32m * 3.28084 = 104.99ft ≈ 105ft
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 4
            ),
            lore = "Fragments of recovered data suggest that the Dying Star was commissioned by a powerful ruler from a technologically advanced civilization now lost to time. The vessel's hull is believed to have been forged using exotic matter extracted from the core of a collapsing star, a process so extreme that no surviving records describe it in full. What remains indicates a construction method that pushed both material science and energy containment far beyond known limits.\n\nResidual stellar radiation is still detectable throughout the ship's structure, constantly feeding its internal systems. This persistent output is thought to be self-regulating, allowing the vessel to operate without a conventional reactor. Pilots report subtle but continuous fluctuations in gravity and energy flow, reinforcing the theory that the ship is not merely powered by stellar remnants, but actively bound to them.",
            speed = 200f,
            acceleration = 170f,
            warpConverters = 150f,
            uniqueTrait = null
        ),
        Ship(
            id = "ship22",
            name = "Song of fire",
            manufacturer = "Eternal Infinitum (according to what was deciphered)",
            type = "Unknown",
            rarity = ShipRarity.MYTHICAL,
            imageResId = R.drawable.ship22,
            renderImageResId = R.drawable.ship22render,
            dimensions = ShipDimensions(
                lengthMeters = 48f,
                lengthFeet = 104f, // 48m * 3.28084 = 157.48ft, but image shows 104ft
                widthMeters = 40f,
                widthFeet = 39f // 40m * 3.28084 = 131.23ft, but image shows 39ft
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 3
            ),
            lore = "Fragments of recovered data indicate that the vessel was constructed around an internal power architecture unlike anything documented in known starship design. Rather than relying on a continuous energy source, the ship appears to trigger a repeating reaction cycle within the engine compartment, converting controlled micro-detonations into sustained propulsion. This approach generates extreme thermal and kinetic output, pushing the ship far beyond the tolerances of conventional containment systems.\n\nEtched into the inner fuselage, a faint inscription was discovered after decades of study. The most accepted translation reads: \"For those who may hear my song after I am lost to time, for fire is my song.\" Researchers believe this message reflects the design philosophy behind the vessel. Stability is not achieved by suppressing its violent core, but by shaping its release into a precise and continuous cadence.",
            speed = 200f,
            acceleration = 170f,
            warpConverters = 150f,
            uniqueTrait = null
        ),
        Ship(
            id = "ship23",
            name = "Navakeshi Star Diver",
            manufacturer = "Kel'Varra Star Systems",
            type = "Light hypertravel bomber",
            rarity = ShipRarity.RARE,
            imageResId = R.drawable.ship23,
            renderImageResId = R.drawable.ship23render,
            dimensions = ShipDimensions(
                lengthMeters = 50f,
                lengthFeet = 138f, // 50m * 3.28084 = 164.04ft, but image shows 138ft
                widthMeters = 42f,
                widthFeet = 144f // 42m * 3.28084 = 137.8ft, but image shows 144ft
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 3
            ),
            lore = "Unlike the Pouncer or the Crusher, the Diver is rarely seen in open formations. It is most often deployed in small units, tasked with disabling installations, supply routes, and surface targets ahead of major fleet movements.\n\nPiloting a Star Diver requires patience and restraint rather than aggression. To Navakeshi commanders, it is not a ship of spectacle, but one of quiet inevitability. When a Diver is committed to a mission, the damage is already calculated.",
            speed = 140f,
            acceleration = 70f,
            warpConverters = 95f,
            uniqueTrait = null
        ),
        Ship(
            id = "asn_ag94_centurion",
            name = "ASN AG94 Centurion",
            manufacturer = "Karnyx Armory Division",
            type = "Hypertravel starfighter",
            rarity = ShipRarity.UNCOMMON,
            imageResId = R.drawable.ship16,
            renderImageResId = R.drawable.ship16render,
            dimensions = ShipDimensions(
                lengthMeters = 43f,
                lengthFeet = 141f, // 43m * 3.28084 = 141.08ft ≈ 141ft
                widthMeters = 36f,
                widthFeet = 118f // 36m * 3.28084 = 118.11ft ≈ 118ft
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 1
            ),
            lore = "The ASN approved an exclusivity contract with Karnyx to develop a nimble and fast starfighter, intended to serve both as the first line of defense for capital vessels and as a frontline assault craft.\n\nThe Centurion quickly proved itself highly effective in both roles. Its low production and maintenance costs, combined with strong performance and well-balanced specifications, led to its rapid adoption across the fleet. Today, it stands as the most widely deployed starfighter in Alliance service.",
            speed = 120f,
            acceleration = 60f,
            warpConverters = 85f,
            uniqueTrait = null
        ),
        Ship(
            id = "isc_m450_phoenix",
            name = "ISC M450 Phoenix",
            manufacturer = "Soren Shipworks",
            type = "Hypertravel starfighter",
            rarity = ShipRarity.EPIC,
            imageResId = R.drawable.ship17,
            renderImageResId = R.drawable.ship17render,
            dimensions = ShipDimensions(
                lengthMeters = 35f,
                lengthFeet = 115f, // 35m * 3.28084 = 114.83ft ≈ 115ft
                widthMeters = 38f,
                widthFeet = 125f // 38m * 3.28084 = 124.67ft ≈ 125ft
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 1
            ),
            lore = "The Phoenix is the ISC's answer to the Alliance's Centurion. Following the sustained success of their neighbor's frontline starfighter, the ISC recognized the strategic value of deploying a comparable craft within their own fleet. Soren Shipworks was selected to lead its development.\n\nAfter years of refinement and iteration, the Phoenix finally emerged. A highly versatile vessel, it delivers outstanding performance both in the defense of capital ships and in direct assault operations, earning its place as a cornerstone of ISC space combat doctrine.",
            speed = 150f,
            acceleration = 75f,
            warpConverters = 100f,
            uniqueTrait = null
        ),
        Ship(
            id = "a450_sparrow",
            name = "A450 Sparrow",
            manufacturer = "Valketh Industries",
            type = "Hypertravel starfighter",
            rarity = ShipRarity.EPIC,
            imageResId = R.drawable.ship20,
            renderImageResId = R.drawable.ship20render,
            dimensions = ShipDimensions(
                lengthMeters = 35f,
                lengthFeet = 104f, // As shown in ship details screen
                widthMeters = 38f,
                widthFeet = 124f // As shown in ship details screen
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 1
            ),
            lore = "Valketh Industries had already established itself as a reliable starship manufacturer, but sought to expand into a new market. Fast civilian ships designed for experienced pilots. This ambition led to Project Sparrow, an initiative centered on speed, precision handling and pilot feedback.\n\nThe A450 Sparrow is a lightweight hypertravel starfighter. Commonly favored by couriers, independent operators, and thrill seeking pilots, it excels at rapid point to point travel, offering an unmatched sense of control and responsiveness.",
            speed = 100f,
            acceleration = 50f,
            warpConverters = 75f,
            uniqueTrait = null
        ),
        Ship(
            id = "t47_dolphin",
            name = "T47 Dolphin",
            manufacturer = "Karnyx Armory Division",
            type = "Compact Cargo Hypertravel Starcraft",
            rarity = ShipRarity.EPIC,
            imageResId = R.drawable.ship21,
            renderImageResId = R.drawable.ship21render,
            dimensions = ShipDimensions(
                lengthMeters = 54f,
                lengthFeet = 177f, // 54m * 3.28084 = 177.17ft ≈ 177ft
                widthMeters = 40f,
                widthFeet = 131f // 40m * 3.28084 = 131.23ft ≈ 131ft
            ),
            crewCapacity = CrewCapacity(
                pilots = 1,
                crewMembers = 1
            ),
            lore = "The Dolphin builds upon the proven design principles that made its cousin, the Tortoise, a dependable cargo vessel, while deliberately addressing its primary limitation: speed. Through refined propulsion systems and improved mass distribution, Karnyx engineers created a more agile starcraft that preserves a generous cargo capacity while significantly reducing travel times across interplanetary routes.\n\nThis balance makes the Dolphin a preferred choice for transporting valuable or sensitive assets that would otherwise attract piracy. While not intended for direct combat, its increased maneuverability and reliable hypertravel performance allow operators to minimize exposure in contested space, earning the Dolphin a reputation as a fast, discreet, and efficient solution for modern cargo operations.",
            speed = 100f,
            acceleration = 50f,
            warpConverters = 75f,
            uniqueTrait = null
        )
    )
    
    /**
     * Helper to get the repository number for a given ship ID.
     *
     * This corresponds to the internal "shipX" numbering used in assets
     * (e.g. ship1, ship2, ship3, ..., ship23).
     *
     * It can be used for consistent ordering within rarity groups.
     */
    fun getRepositoryNumber(shipId: String): Int {
        return when (shipId) {
            "b14_phantom" -> 1
            "type45c_shooting_star" -> 2
            "navakeshi_star_pouncer" -> 3
            "a300_albatross" -> 4
            "b7f_starforce" -> 5
            "navakeshi_star_crusher" -> 6
            "b15_specter" -> 7
            "n6_98_melina" -> 8
            "model3_tortoise_ccp" -> 9
            "h98_valkyrie" -> 10
            "navakeshi_star_ravager" -> 11
            "silver_lightning" -> 12
            "vulcani_legenda_f1" -> 13
            "force_of_nature" -> 14
            "dying_star" -> 15
            "asn_ag94_centurion" -> 16
            "isc_m450_phoenix" -> 17
            "p7h_skyblazer" -> 19
            "a450_sparrow" -> 20
            "t47_dolphin" -> 21
            "ship22" -> 22
            "ship23" -> 23
            else -> Int.MAX_VALUE
        }
    }
    
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
    private var prefs: android.content.SharedPreferences? = null
    private const val PREFS_NAME = "ship_prefs"
    private const val KEY_CURRENT_SHIP_ID = "current_ship_id"
    
    /**
     * Initialize the repository with a context.
     * Should be called once from MainActivity.onCreate().
     */
    fun initialize(context: android.content.Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
            // Load saved ship ID
            currentShipId = prefs!!.getString(KEY_CURRENT_SHIP_ID, ships.first().id) ?: ships.first().id
        }
    }
    
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
            prefs?.edit()?.putString(KEY_CURRENT_SHIP_ID, shipId)?.apply()
        }
    }
}

