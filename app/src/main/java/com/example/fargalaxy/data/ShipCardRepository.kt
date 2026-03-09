package com.example.fargalaxy.data

import android.content.Context
import android.content.SharedPreferences
import com.example.fargalaxy.model.ShipRarity

/**
 * ShipCardRepository - manages ship card collection and ship unlocks.
 *
 * Each ship that can be unlocked via cards has 6 unique cards (card indices 1–6).
 * We store a 6‑bit bitmask per ship:
 * - Bit 0 -> card 1
 * - Bit 1 -> card 2
 * - ...
 * - Bit 5 -> card 6
 *
 * Once all 6 bits are set, the ship is considered fully collected and the ship
 * is unlocked/owned via GameStateRepository + ShipRepository.
 */
object ShipCardRepository {
    private const val PREFS_NAME = "ship_card_prefs"
    private const val KEY_PREFIX_MASK = "ship_card_mask_" // + shipId
    private const val KEY_TEST_CRATES_OPENED = "test_crates_opened"

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
            ?: throw IllegalStateException("ShipCardRepository.initialize(context) must be called before use")
    }

    /** Get the raw bitmask for a ship's collected cards. */
    fun getMask(shipId: String): Int {
        return getPrefs().getInt(KEY_PREFIX_MASK + shipId, 0)
    }

    /** Persist the raw bitmask for a ship's collected cards. */
    private fun setMask(shipId: String, mask: Int) {
        getPrefs().edit().putInt(KEY_PREFIX_MASK + shipId, mask).apply()
    }

    /** Returns how many unique cards are owned for the ship (0–6). */
    fun getOwnedCardCount(shipId: String): Int {
        val mask = getMask(shipId)
        return Integer.bitCount(mask and 0b0011_1111)
    }

    /** Returns true if the user already owns the specific card index (1–6). */
    fun hasCard(shipId: String, cardIndex: Int): Boolean {
        if (cardIndex !in 1..6) return false
        val bit = 1 shl (cardIndex - 1)
        return (getMask(shipId) and bit) != 0
    }

    /** Returns a list of missing card indices (1–6) for the given ship. */
    fun getMissingCardIndices(shipId: String): List<Int> {
        val mask = getMask(shipId)
        val missing = mutableListOf<Int>()
        for (i in 1..6) {
            val bit = 1 shl (i - 1)
            if ((mask and bit) == 0) {
                missing.add(i)
            }
        }
        return missing
    }

    /**
     * Returns true if all 6 unique cards have been collected for the ship.
     */
    fun isFullyCollected(shipId: String): Boolean {
        val mask = getMask(shipId)
        // Lower 6 bits all set
        return (mask and 0b0011_1111) == 0b0011_1111
    }

    /**
     * Add a new card for the given ship.
     *
     * - Selects a random missing card index (1–6).
     * - Updates the bitmask so cards never duplicate.
     * - If this completes the collection (6/6), automatically unlocks and owns the ship.
     *
     * @return the card index that was added (1–6), or null if the ship is already complete.
     */
    fun addCard(shipId: String, rarity: ShipRarity): Int? {
        // If already fully collected, do nothing
        if (isFullyCollected(shipId)) {
            return null
        }

        val missing = getMissingCardIndices(shipId)
        if (missing.isEmpty()) {
            return null
        }

        val cardIndex = missing.random()
        val currentMask = getMask(shipId)
        val newMask = currentMask or (1 shl (cardIndex - 1))
        setMask(shipId, newMask)

        // If now fully collected, unlock ship (makes it available in staryard to be purchased)
        // Don't automatically own it - user needs to purchase it in the staryard
        if (isFullyCollected(shipId)) {
            GameStateRepository.unlockShip(shipId)
        }

        return cardIndex
    }

    /**
     * Reset all ship card data (for testing/debugging).
     */
    fun resetAllCards() {
        val prefs = getPrefs()
        val editor = prefs.edit()
        prefs.all.keys
            .filter { it.startsWith(KEY_PREFIX_MASK) || it == KEY_TEST_CRATES_OPENED }
            .forEach { editor.remove(it) }
        editor.apply()
    }

    // --- Test mode helpers (for crate opening guarantees) ---

    fun getTestCratesOpened(): Int {
        return getPrefs().getInt(KEY_TEST_CRATES_OPENED, 0)
    }

    fun incrementTestCratesOpened() {
        val prefs = getPrefs()
        val current = prefs.getInt(KEY_TEST_CRATES_OPENED, 0)
        prefs.edit().putInt(KEY_TEST_CRATES_OPENED, current + 1).apply()
    }

    fun resetTestCratesCounter() {
        getPrefs().edit().putInt(KEY_TEST_CRATES_OPENED, 0).apply()
    }
}

