package com.example.fargalaxy.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

/**
 * EquipmentRepository - manages equipped equipment items per ship.
 * 
 * Stores equipped equipment as a JSON object mapping ship IDs to item IDs.
 * Example: {"b14_phantom": "emergency_modulator", "crusher": "unstable_cargo"}
 * 
 * Only one equipment item can be equipped per ship at a time.
 * 
 * Item IDs:
 * - "emergency_modulator" - Emergency modulators
 * - "unstable_cargo" - Unstable cargo
 * - "experimental_fuel" - Experimental fuel
 */
object EquipmentRepository {
    private const val PREFS_NAME = "equipment_prefs"
    private const val KEY_EQUIPPED = "equipped" // JSON object: {"shipId": "itemId"}
    
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
     * Get the equipped item ID for a specific ship.
     * 
     * @param shipId The ship ID (e.g., "b14_phantom")
     * @return The equipped item ID, or null if no item is equipped
     */
    fun getEquippedItem(shipId: String): String? {
        val equippedJson = prefs?.getString(KEY_EQUIPPED, null) ?: return null
        
        return try {
            val equipped = JSONObject(equippedJson)
            if (equipped.has(shipId)) {
                equipped.getString(shipId)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Equip an item to a ship.
     * 
     * @param shipId The ship ID (e.g., "b14_phantom")
     * @param itemId The item ID to equip (e.g., "emergency_modulator")
     */
    fun equipItem(shipId: String, itemId: String) {
        val equippedJson = prefs?.getString(KEY_EQUIPPED, null)
        val equipped = if (equippedJson != null) {
            try {
                JSONObject(equippedJson)
            } catch (e: Exception) {
                JSONObject()
            }
        } else {
            JSONObject()
        }
        
        equipped.put(shipId, itemId)
        prefs?.edit()?.putString(KEY_EQUIPPED, equipped.toString())?.apply()
    }
    
    /**
     * Unequip the item from a ship.
     * 
     * @param shipId The ship ID (e.g., "b14_phantom")
     */
    fun unequipItem(shipId: String) {
        val equippedJson = prefs?.getString(KEY_EQUIPPED, null) ?: return
        val equipped = try {
            JSONObject(equippedJson)
        } catch (e: Exception) {
            return
        }
        
        equipped.remove(shipId)
        prefs?.edit()?.putString(KEY_EQUIPPED, equipped.toString())?.apply()
    }
    
    /**
     * Check if a specific item is equipped to a ship.
     * 
     * @param shipId The ship ID
     * @param itemId The item ID to check
     * @return true if the item is equipped, false otherwise
     */
    fun isItemEquipped(shipId: String, itemId: String): Boolean {
        return getEquippedItem(shipId) == itemId
    }
}
