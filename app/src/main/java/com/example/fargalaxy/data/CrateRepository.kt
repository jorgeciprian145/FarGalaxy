package com.example.fargalaxy.data

import android.content.Context
import android.content.SharedPreferences
import com.example.fargalaxy.model.ShipRarity
import kotlin.random.Random

/**
 * Types of crates available in the store.
 */
enum class CrateType {
    STANDARD,
    ADVANCED,
    ELITE
}

/**
 * Categories of rewards from crates.
 */
private enum class RewardCategory {
    CREDITS_50000,
    SHIP_CARD_UNCOMMON,
    SHIP_CARD_EPIC,
    SHIP_CARD_LEGENDARY,
    EQUIPMENT
}

/**
 * Sealed result of opening a crate.
 */
sealed class CrateReward {
    data class Credits(val amount: Int) : CrateReward()
    data class Equipment(val itemId: String) : CrateReward()
    data class ShipCard(
        val shipId: String,
        val cardIndex: Int,
        val rarity: ShipRarity
    ) : CrateReward()
}

/**
 * CrateOpenResult - returned by [openCrate] and consumed by the UI layer.
 */
data class CrateOpenResult(
    val crateType: CrateType,
    val reward: CrateReward,
    val wasForcedByPity: Boolean,
    val unlockedShipId: String? = null // Ship ID if a ship was unlocked by collecting all 6 cards
)

/**
 * Internal structure used to define probability tables per crate type.
 *
 * @param category Reward type
 * @param weight Relative weight (we treat the percentages you provided as weights)
 */
private data class RewardEntry(
    val category: RewardCategory,
    val weight: Int
)

/**
 * CrateRepository - core logic for crate inventory, pity system, and reward resolution.
 *
 * Responsibilities:
 * - Track how many crates of each type the user owns.
 * - Track a per‑crate‑type "pity" state that increases chances of ship cards
 *   when the user repeatedly opens crates without winning a ship card or 50k credits.
 * - Guarantee that the 4th crate since the last ship card will always award a card
 *   (if there is at least one eligible ship card).
 * - Apply rewards: credits, equipment items, or ship cards.
 *
 * Pity system (per crate type):
 * - If a crate is opened and the reward is NOT a ship card AND NOT 50k credits:
 *   - Increment "failsSinceLastShipCard" (max 3).
 *   - On the next open, the weight for specific ship‑card categories is doubled.
 *   - Each consecutive fail doubles again (2x, 4x, 8x, ...) until a ship card
 *     or 50k credits is obtained, or the 4th crate is reached.
 * - If the user wins 50k credits OR any ship card:
 *   - Reset the pity state (fails counter + multipliers) for that crate type.
 * - 4th crate since last ship card:
 *   - If there's any eligible ship card for that crate type, force a ship card reward.
 */
object CrateRepository {
    private const val PREFS_NAME = "crate_prefs"

    // Inventory keys (owned crates)
    private const val KEY_STANDARD_COUNT = "standard_crate_count"
    private const val KEY_ADVANCED_COUNT = "advanced_crate_count"
    private const val KEY_ELITE_COUNT = "elite_crate_count"

    // Pity state per crate type
    // Number of consecutive opens without ship card or 50k credits (0–3; 4th crate forces card)
    private const val KEY_FAILS_SINCE_CARD_STANDARD = "fails_since_card_standard"
    private const val KEY_FAILS_SINCE_CARD_ADVANCED = "fails_since_card_advanced"
    private const val KEY_FAILS_SINCE_CARD_ELITE = "fails_since_card_elite"

    private var prefs: SharedPreferences? = null

    /**
     * Initialize the repository with a context.
     * Should be called once from MainActivity.onCreate().
     */
    fun initialize(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    private fun getPrefs(): SharedPreferences {
        return prefs
            ?: throw IllegalStateException("CrateRepository.initialize(context) must be called before use")
    }

    // --- Inventory management ---

    fun getCrateCount(crateType: CrateType): Int {
        val key = when (crateType) {
            CrateType.STANDARD -> KEY_STANDARD_COUNT
            CrateType.ADVANCED -> KEY_ADVANCED_COUNT
            CrateType.ELITE -> KEY_ELITE_COUNT
        }
        return getPrefs().getInt(key, 0)
    }

    fun addCrate(crateType: CrateType, count: Int = 1) {
        val prefs = getPrefs()
        val key = when (crateType) {
            CrateType.STANDARD -> KEY_STANDARD_COUNT
            CrateType.ADVANCED -> KEY_ADVANCED_COUNT
            CrateType.ELITE -> KEY_ELITE_COUNT
        }
        val current = prefs.getInt(key, 0)
        prefs.edit().putInt(key, current + count).apply()
    }

    /**
     * Consume a crate of the given type.
     *
     * @return true if a crate was consumed, false if none were available.
     */
    fun consumeCrate(crateType: CrateType): Boolean {
        val prefs = getPrefs()
        val key = when (crateType) {
            CrateType.STANDARD -> KEY_STANDARD_COUNT
            CrateType.ADVANCED -> KEY_ADVANCED_COUNT
            CrateType.ELITE -> KEY_ELITE_COUNT
        }
        val current = prefs.getInt(key, 0)
        if (current <= 0) return false
        prefs.edit().putInt(key, current - 1).apply()
        return true
    }

    // --- Pity state helpers ---

    private fun getFailsSinceCard(crateType: CrateType): Int {
        val key = when (crateType) {
            CrateType.STANDARD -> KEY_FAILS_SINCE_CARD_STANDARD
            CrateType.ADVANCED -> KEY_FAILS_SINCE_CARD_ADVANCED
            CrateType.ELITE -> KEY_FAILS_SINCE_CARD_ELITE
        }
        return getPrefs().getInt(key, 0)
    }

    private fun setFailsSinceCard(crateType: CrateType, value: Int) {
        val key = when (crateType) {
            CrateType.STANDARD -> KEY_FAILS_SINCE_CARD_STANDARD
            CrateType.ADVANCED -> KEY_FAILS_SINCE_CARD_ADVANCED
            CrateType.ELITE -> KEY_FAILS_SINCE_CARD_ELITE
        }
        getPrefs().edit().putInt(key, value.coerceAtLeast(0)).apply()
    }

    private fun resetPity(crateType: CrateType) {
        setFailsSinceCard(crateType, 0)
    }

    // --- Reward tables (weights reflect your percentages) ---
    // Using base 200 for Standard and Advanced to allow 0.5% precision
    // Elite uses base 108 to maintain current proportions

    private val standardTable = listOf(
        RewardEntry(RewardCategory.CREDITS_50000, weight = 1),      // 0.5% (1/200)
        RewardEntry(RewardCategory.SHIP_CARD_LEGENDARY, weight = 6), // 3% (6/200)
        RewardEntry(RewardCategory.SHIP_CARD_EPIC, weight = 20),    // 10% (20/200)
        RewardEntry(RewardCategory.SHIP_CARD_UNCOMMON, weight = 70), // 35% (70/200)
        RewardEntry(RewardCategory.EQUIPMENT, weight = 103)          // 51.5% (103/200)
    )

    private val advancedTable = listOf(
        RewardEntry(RewardCategory.CREDITS_50000, weight = 2),      // 1% (2/200)
        RewardEntry(RewardCategory.SHIP_CARD_LEGENDARY, weight = 12), // 6% (12/200)
        RewardEntry(RewardCategory.SHIP_CARD_EPIC, weight = 40),    // 20% (40/200)
        RewardEntry(RewardCategory.SHIP_CARD_UNCOMMON, weight = 60), // 30% (60/200)
        RewardEntry(RewardCategory.EQUIPMENT, weight = 86)          // 43% (86/200)
    )

    private val eliteTable = listOf(
        RewardEntry(RewardCategory.CREDITS_50000, weight = 3),      // 3% (3/100)
        RewardEntry(RewardCategory.SHIP_CARD_LEGENDARY, weight = 20), // 20% (20/100)
        RewardEntry(RewardCategory.SHIP_CARD_EPIC, weight = 30),    // 30% (30/100)
        RewardEntry(RewardCategory.SHIP_CARD_UNCOMMON, weight = 22), // 22% (22/100)
        RewardEntry(RewardCategory.EQUIPMENT, weight = 25)          // 25% (25/100)
    )

    private fun getTable(crateType: CrateType): List<RewardEntry> {
        return when (crateType) {
            CrateType.STANDARD -> standardTable
            CrateType.ADVANCED -> advancedTable
            CrateType.ELITE -> eliteTable
        }
    }

    // Equipment pool – simple IDs that map to InventoryRepository items
    // (weights are uniform for now).
    private val equipmentPool = listOf(
        "emergency_modulator",
        "experimental_fuel",
        "unstable_cargo",
        "deep_space_scanner"
    )

    // Ships that can be unlocked via ship cards, grouped by rarity.
    private val cardShipsByRarity: Map<ShipRarity, List<String>> = mapOf(
        ShipRarity.UNCOMMON to listOf("asn_ag94_centurion"), // ship16
        ShipRarity.EPIC to listOf("isc_m450_phoenix"),       // ship17
        ShipRarity.LEGENDARY to listOf("asn_h99_dragoon")    // ship18
    )

    /**
     * Main entry point to open a crate.
     *
     * Handles:
     * - Consuming one crate of the given type.
     * - Applying the pity system and 4th‑crate guarantee.
     * - Choosing and applying the reward.
     */
    fun openCrate(crateType: CrateType, isTestMode: Boolean): CrateOpenResult? {
        // First, ensure we actually have a crate to open.
        if (!consumeCrate(crateType)) {
            return null
        }

        // Increment global test counter (for any future test‑mode specific logic).
        if (isTestMode) {
            ShipCardRepository.incrementTestCratesOpened()
        }

        val fails = getFailsSinceCard(crateType)
        val table = getTable(crateType)

        // 4th crate since last card must give a card if any is available.
        val forceShipCard = fails >= 3 && hasAnyEligibleShipCard(crateType)

        val (rewardCategory, wasForcedByPity) = if (forceShipCard) {
            chooseForcedShipCardCategory(crateType) to true
        } else {
            chooseCategoryWithPity(crateType, table, fails) to false
        }

        val reward = when (rewardCategory) {
            RewardCategory.CREDITS_50000 -> {
                // Give credits and reset pity
                UserDataRepository.addCredits(50_000)
                resetPity(crateType)
                CrateReward.Credits(50_000)
            }

            RewardCategory.EQUIPMENT -> {
                val equipmentId = equipmentPool.random()
                InventoryRepository.addItem(equipmentId, 1)
                // This counts as a fail for pity
                setFailsSinceCard(crateType, (fails + 1).coerceAtMost(3))
                CrateReward.Equipment(equipmentId)
            }

            RewardCategory.SHIP_CARD_UNCOMMON,
            RewardCategory.SHIP_CARD_EPIC,
            RewardCategory.SHIP_CARD_LEGENDARY -> {
                val rarity = when (rewardCategory) {
                    RewardCategory.SHIP_CARD_UNCOMMON -> ShipRarity.UNCOMMON
                    RewardCategory.SHIP_CARD_EPIC -> ShipRarity.EPIC
                    RewardCategory.SHIP_CARD_LEGENDARY -> ShipRarity.LEGENDARY
                    else -> ShipRarity.UNCOMMON
                }
                val (shipCardReward, unlockedShipId) = giveShipCard(crateType, rarity)
                if (shipCardReward != null) {
                    // Successful card – reset pity
                    resetPity(crateType)
                    // Return reward with unlocked ship ID if ship was just unlocked
                    return CrateOpenResult(
                        crateType = crateType,
                        reward = shipCardReward,
                        wasForcedByPity = wasForcedByPity,
                        unlockedShipId = unlockedShipId
                    )
                } else {
                    // No eligible ship card for this rarity – fallback to credits
                    UserDataRepository.addCredits(50_000)
                    resetPity(crateType)
                    CrateReward.Credits(50_000)
                }
            }
        }

        return CrateOpenResult(
            crateType = crateType,
            reward = reward,
            wasForcedByPity = wasForcedByPity,
            unlockedShipId = null // Only ship cards can unlock ships, handled above
        )
    }

    /**
     * Choose a reward category applying the pity multipliers to ship card entries.
     *
     * Only ship card entries are boosted; 50k credits and equipment keep base weights.
     * - Standard: boost UNCOMMON only.
     * - Advanced: boost UNCOMMON + EPIC.
     * - Elite: boost all ship card categories.
     */
    private fun chooseCategoryWithPity(
        crateType: CrateType,
        baseTable: List<RewardEntry>,
        failsSinceCard: Int
    ): RewardCategory {
        if (failsSinceCard <= 0) {
            return chooseRandomCategory(baseTable)
        }

        // Each fail doubles the weight for the eligible ship card entries.
        val multiplier = 1 shl failsSinceCard // 2^fails

        val boostedTable = baseTable.map { entry ->
            val shouldBoost = when (crateType) {
                CrateType.STANDARD ->
                    entry.category == RewardCategory.SHIP_CARD_UNCOMMON

                CrateType.ADVANCED ->
                    entry.category == RewardCategory.SHIP_CARD_UNCOMMON ||
                        entry.category == RewardCategory.SHIP_CARD_EPIC

                CrateType.ELITE ->
                    entry.category == RewardCategory.SHIP_CARD_UNCOMMON ||
                        entry.category == RewardCategory.SHIP_CARD_EPIC ||
                        entry.category == RewardCategory.SHIP_CARD_LEGENDARY
            }

            if (shouldBoost) {
                entry.copy(weight = entry.weight * multiplier)
            } else {
                entry
            }
        }

        return chooseRandomCategory(boostedTable)
    }

    /** Basic weighted random choice helper. */
    private fun chooseRandomCategory(table: List<RewardEntry>): RewardCategory {
        val totalWeight = table.sumOf { it.weight }
        val roll = Random.nextInt(totalWeight)
        var cumulative = 0
        for (entry in table) {
            cumulative += entry.weight
            if (roll < cumulative) {
                return entry.category
            }
        }
        // Fallback – shouldn't happen
        return table.last().category
    }

    /**
     * For the forced 4th crate, pick a ship‑card category that:
     * - Is allowed for this crate type, and
     * - Still has at least one eligible ship with missing cards.
     */
    private fun chooseForcedShipCardCategory(crateType: CrateType): RewardCategory {
        val candidates = when (crateType) {
            CrateType.STANDARD -> listOf(RewardCategory.SHIP_CARD_UNCOMMON)
            CrateType.ADVANCED -> listOf(
                RewardCategory.SHIP_CARD_UNCOMMON,
                RewardCategory.SHIP_CARD_EPIC
            )
            CrateType.ELITE -> listOf(
                RewardCategory.SHIP_CARD_UNCOMMON,
                RewardCategory.SHIP_CARD_EPIC,
                RewardCategory.SHIP_CARD_LEGENDARY
            )
        }

        // Filter to only those rarities that still have at least one incomplete ship.
        val eligible = candidates.filter { category ->
            val rarity = when (category) {
                RewardCategory.SHIP_CARD_UNCOMMON -> ShipRarity.UNCOMMON
                RewardCategory.SHIP_CARD_EPIC -> ShipRarity.EPIC
                RewardCategory.SHIP_CARD_LEGENDARY -> ShipRarity.LEGENDARY
                else -> null
            }
            rarity != null && hasEligibleShipForRarity(rarity)
        }

        // If for some reason none are eligible, fall back to the crate's default boosted choice.
        if (eligible.isEmpty()) {
            return candidates.first()
        }

        return eligible.random()
    }

    private fun hasAnyEligibleShipCard(crateType: CrateType): Boolean {
        val rarities = when (crateType) {
            CrateType.STANDARD -> listOf(ShipRarity.UNCOMMON)
            CrateType.ADVANCED -> listOf(ShipRarity.UNCOMMON, ShipRarity.EPIC)
            CrateType.ELITE -> listOf(ShipRarity.UNCOMMON, ShipRarity.EPIC, ShipRarity.LEGENDARY)
        }
        return rarities.any { hasEligibleShipForRarity(it) }
    }

    private fun hasEligibleShipForRarity(rarity: ShipRarity): Boolean {
        val ships = cardShipsByRarity[rarity] ?: return false
        return ships.any { shipId ->
            !ShipCardRepository.isFullyCollected(shipId)
        }
    }

    /**
     * Award a ship card for one of the ships in [cardShipsByRarity] for the given rarity.
     * Picks a ship that still has missing cards, then lets ShipCardRepository choose a missing index.
     * 
     * @return Pair of (ShipCard reward, unlockedShipId if ship was just unlocked, or null)
     */
    private fun giveShipCard(crateType: CrateType, rarity: ShipRarity): Pair<CrateReward.ShipCard?, String?> {
        val candidateShips = cardShipsByRarity[rarity].orEmpty()
            .filter { !ShipCardRepository.isFullyCollected(it) }

        if (candidateShips.isEmpty()) {
            return null to null
        }

        // Uniform choice among candidate ships for now.
        val shipId = candidateShips.random()
        
        // Get current card count before adding the card
        val cardCountBefore = ShipCardRepository.getOwnedCardCount(shipId)
        
        val cardIndex = ShipCardRepository.addCard(shipId, rarity) ?: return null to null

        // Check if ship is now fully collected (was just unlocked - went from 5 to 6 cards)
        val cardCountAfter = ShipCardRepository.getOwnedCardCount(shipId)
        val wasJustUnlocked = cardCountBefore == 5 && cardCountAfter == 6
        val unlockedShipId = if (wasJustUnlocked) shipId else null

        val shipCard = CrateReward.ShipCard(
            shipId = shipId,
            cardIndex = cardIndex,
            rarity = rarity
        )
        
        return shipCard to unlockedShipId
    }

    /**
     * Reset all crate inventory and pity state (for testing/debugging).
     */
    fun resetAll() {
        val prefs = getPrefs()
        prefs.edit()
            .remove(KEY_STANDARD_COUNT)
            .remove(KEY_ADVANCED_COUNT)
            .remove(KEY_ELITE_COUNT)
            .remove(KEY_FAILS_SINCE_CARD_STANDARD)
            .remove(KEY_FAILS_SINCE_CARD_ADVANCED)
            .remove(KEY_FAILS_SINCE_CARD_ELITE)
            .apply()
    }
}

