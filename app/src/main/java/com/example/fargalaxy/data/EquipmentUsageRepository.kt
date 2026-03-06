package com.example.fargalaxy.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

/**
 * EquipmentUsageRepository - tracks usage state for equipped equipment items.
 * 
 * Tracks:
 * - Emergency modulator: whether it has been used (0/1)
 * - Unstable cargo: whether a penalty occurred during travel
 * - Experimental fuel: remaining travels (starts at 3, decrements each travel)
 * 
 * Usage state is reset when equipment is equipped or when travel starts.
 */
object EquipmentUsageRepository {
    private const val PREFS_NAME = "equipment_usage_prefs"
    private const val KEY_EMERGENCY_MODULATOR_USED = "emergency_modulator_used" // Boolean
    private const val KEY_UNSTABLE_CARGO_PENALTY = "unstable_cargo_penalty" // Boolean
    private const val KEY_EXPERIMENTAL_FUEL_REMAINING = "experimental_fuel_remaining" // Int
    
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
    
    /**
     * Initialize usage state when equipment is equipped.
     * 
     * @param itemId The item ID that was equipped
     */
    fun initializeUsage(itemId: String) {
        when (itemId) {
            "emergency_modulator" -> {
                prefs?.edit()?.putBoolean(KEY_EMERGENCY_MODULATOR_USED, false)?.apply()
            }
            "unstable_cargo" -> {
                prefs?.edit()?.putBoolean(KEY_UNSTABLE_CARGO_PENALTY, false)?.apply()
            }
            "experimental_fuel" -> {
                // Only initialize if not already set (don't reset if already in use)
                if (prefs?.contains(KEY_EXPERIMENTAL_FUEL_REMAINING) != true) {
                    prefs?.edit()?.putInt(KEY_EXPERIMENTAL_FUEL_REMAINING, 3)?.apply()
                }
            }
        }
    }
    
    /**
     * Initialize usage state for a new travel (resets single-use equipment, but preserves multi-use equipment state).
     * 
     * @param itemId The item ID that is equipped
     */
    fun initializeUsageForNewTravel(itemId: String) {
        when (itemId) {
            "emergency_modulator" -> {
                // Reset for new travel (single use per travel)
                prefs?.edit()?.putBoolean(KEY_EMERGENCY_MODULATOR_USED, false)?.apply()
            }
            "unstable_cargo" -> {
                // Reset for new travel (single use per travel)
                prefs?.edit()?.putBoolean(KEY_UNSTABLE_CARGO_PENALTY, false)?.apply()
            }
            "experimental_fuel" -> {
                // Don't reset - preserve remaining travels count across travels
                // Experimental fuel lasts 3 travels total, not per travel
            }
        }
    }
    
    /**
     * Reset usage state when equipment is unequipped.
     */
    fun resetUsage() {
        prefs?.edit()
            ?.remove(KEY_EMERGENCY_MODULATOR_USED)
            ?.remove(KEY_UNSTABLE_CARGO_PENALTY)
            ?.remove(KEY_EXPERIMENTAL_FUEL_REMAINING)
            ?.apply()
    }
    
    // Emergency Modulator
    
    /**
     * Check if emergency modulator has been used.
     */
    fun isEmergencyModulatorUsed(): Boolean {
        return prefs?.getBoolean(KEY_EMERGENCY_MODULATOR_USED, false) ?: false
    }
    
    /**
     * Mark emergency modulator as used.
     */
    fun markEmergencyModulatorUsed() {
        prefs?.edit()?.putBoolean(KEY_EMERGENCY_MODULATOR_USED, true)?.apply()
    }
    
    // Unstable Cargo
    
    /**
     * Check if unstable cargo penalty occurred.
     */
    fun hasUnstableCargoPenalty(): Boolean {
        return prefs?.getBoolean(KEY_UNSTABLE_CARGO_PENALTY, false) ?: false
    }
    
    /**
     * Mark that unstable cargo penalty occurred.
     */
    fun markUnstableCargoPenalty() {
        prefs?.edit()?.putBoolean(KEY_UNSTABLE_CARGO_PENALTY, true)?.apply()
    }
    
    // Experimental Fuel
    
    /**
     * Get remaining travels for experimental fuel.
     */
    fun getExperimentalFuelRemaining(): Int {
        return prefs?.getInt(KEY_EXPERIMENTAL_FUEL_REMAINING, 3) ?: 3
    }
    
    /**
     * Decrement experimental fuel remaining travels.
     * Returns the new remaining count.
     */
    fun decrementExperimentalFuel(): Int {
        val current = getExperimentalFuelRemaining()
        val newValue = (current - 1).coerceAtLeast(0)
        prefs?.edit()?.putInt(KEY_EXPERIMENTAL_FUEL_REMAINING, newValue)?.apply()
        return newValue
    }
    
    /**
     * Set experimental fuel remaining travels.
     */
    fun setExperimentalFuelRemaining(count: Int) {
        prefs?.edit()?.putInt(KEY_EXPERIMENTAL_FUEL_REMAINING, count)?.apply()
    }
}
