package com.example.fargalaxy.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

/**
 * EquipmentRepository - manages equipped equipment items globally (not per ship).
 * 
 * Stores equipped equipment as a single item ID (only one equipment can be equipped at a time).
 * 
 * Item IDs:
 * - "emergency_modulator" - Emergency modulators
 * - "unstable_cargo" - Unstable cargo
 * - "experimental_fuel" - Experimental fuel
 */
object EquipmentRepository {
    private const val PREFS_NAME = "equipment_prefs"
    private const val KEY_EQUIPPED = "equipped" // String: itemId or null
    
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
     * Get the currently equipped item ID.
     * 
     * @return The equipped item ID, or null if no item is equipped
     */
    fun getEquippedItem(): String? {
        return prefs?.getString(KEY_EQUIPPED, null)
    }
    
    /**
     * Equip an item globally.
     * 
     * @param itemId The item ID to equip (e.g., "emergency_modulator")
     */
    fun equipItem(itemId: String) {
        prefs?.edit()?.putString(KEY_EQUIPPED, itemId)?.apply()
    }
    
    /**
     * Unequip the currently equipped item.
     */
    fun unequipItem() {
        prefs?.edit()?.remove(KEY_EQUIPPED)?.apply()
    }
    
    /**
     * Check if a specific item is equipped.
     * 
     * @param itemId The item ID to check
     * @return true if the item is equipped, false otherwise
     */
    fun isItemEquipped(itemId: String): Boolean {
        return getEquippedItem() == itemId
    }
}
