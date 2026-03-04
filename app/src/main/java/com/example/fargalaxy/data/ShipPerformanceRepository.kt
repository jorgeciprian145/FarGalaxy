package com.example.fargalaxy.data

/**
 * Provides access to ship performance stats (acceleration, speed, stability)
 * for gameplay calculations such as reward bonuses.
 *
 * The values are on a 0–100 scale and are based on the design stats used in the
 * ship details screens.
 */
object ShipPerformanceRepository {

    /**
     * Returns the (acceleration, speed, stability) stats for the given ship.
     *
     * Values are Ints in the 0–100 range.
     */
    fun getStatsForShip(shipId: String): Triple<Int, Int, Int> {
        val acceleration = when (shipId) {
            "b14_phantom" -> 24
            "type45c_shooting_star" -> 35
            "navakeshi_star_pouncer" -> 29
            "a300_albatross" -> 28
            "p7h_skyblazer" -> 32
            "b7f_starforce" -> 38
            "navakeshi_star_crusher" -> 24
            "asn_ag94_centurion" -> 35
            "b15_specter" -> 32
            "n6_98_melina" -> 25
            "model3_tortoise_ccp" -> 14
            "h98_valkyrie" -> 38
            "navakeshi_star_ravager" -> 40
            "isc_m450_phoenix" -> 40
            "a450_sparrow" -> 32
            "t47_dolphin" -> 22
            "asn_h99_dragoon" -> 54
            "silver_lightning" -> 62
            "vulcani_legenda_f1" -> 68
            "force_of_nature" -> 80
            "dying_star" -> 68
            "ship22" -> 61
            "ship23" -> 16
            else -> 0
        }

        val speed = when (shipId) {
            "b14_phantom" -> 25
            "type45c_shooting_star" -> 32
            "navakeshi_star_pouncer" -> 32
            "a300_albatross" -> 28
            "p7h_skyblazer" -> 32
            "b7f_starforce" -> 38
            "navakeshi_star_crusher" -> 30
            "asn_ag94_centurion" -> 32
            "b15_specter" -> 30
            "n6_98_melina" -> 34
            "model3_tortoise_ccp" -> 16
            "h98_valkyrie" -> 34
            "navakeshi_star_ravager" -> 40
            "isc_m450_phoenix" -> 36
            "a450_sparrow" -> 40
            "t47_dolphin" -> 21
            "asn_h99_dragoon" -> 58
            "silver_lightning" -> 60
            "vulcani_legenda_f1" -> 72
            "force_of_nature" -> 72
            "dying_star" -> 64
            "ship22" -> 61
            "ship23" -> 20
            else -> 0
        }

        val stability = when (shipId) {
            "b14_phantom" -> 29
            "type45c_shooting_star" -> 16
            "navakeshi_star_pouncer" -> 18
            "a300_albatross" -> 38
            "p7h_skyblazer" -> 38
            "b7f_starforce" -> 19
            "navakeshi_star_crusher" -> 35
            "asn_ag94_centurion" -> 30
            "b15_specter" -> 36
            "n6_98_melina" -> 42
            "model3_tortoise_ccp" -> 74
            "h98_valkyrie" -> 49
            "navakeshi_star_ravager" -> 30
            "isc_m450_phoenix" -> 32
            "a450_sparrow" -> 30
            "t47_dolphin" -> 68
            "asn_h99_dragoon" -> 65
            "silver_lightning" -> 57
            "vulcani_legenda_f1" -> 18
            "force_of_nature" -> 45
            "dying_star" -> 60
            "ship22" -> 68
            "ship23" -> 69
            else -> 0
        }

        return Triple(acceleration, speed, stability)
    }

    /**
     * Calculates the performance bonus percentage based on the average of the three stats.
     *
     * Formula:
     *   avgStat = (acceleration + speed + stability) / 3
     *   performanceBonusPercent = (avgStat / 100) * 20
     *
     * The result is an Int in the range 0–20.
     */
    fun getPerformanceBonusPercent(shipId: String): Int {
        val (acceleration, speed, stability) = getStatsForShip(shipId)
        val avgStat = (acceleration + speed + stability) / 3f
        val bonus = (avgStat / 100f) * 20f
        return bonus.toInt()
    }
}

