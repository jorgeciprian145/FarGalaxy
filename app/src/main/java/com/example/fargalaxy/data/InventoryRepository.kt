package com.example.fargalaxy.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

/**
 * InventoryRepository - manages user's inventory of boost items.
 * 
 * Stores inventory as a JSON object mapping item IDs to quantities.
 * Example: {"emergency_modulator": 3, "unstable_cargo": 2, "experimental_fuel": 1}
 * 
 * Item IDs:
 * - "emergency_modulator" - Emergency modulators
 * - "unstable_cargo" - Unstable cargo
 * - "experimental_fuel" - Experimental fuel
 * - "deep_space_scanner" - Deep space scanners
 */
object InventoryRepository {
    private const val PREFS_NAME = "inventory_prefs"
    private const val KEY_INVENTORY = "inventory" // JSON object: {"itemId": count}
    
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
     * Get the quantity of a specific item in the inventory.
     * 
     * @param itemId The item ID (e.g., "emergency_modulator")
     * @return The quantity of the item (defaults to 0 if not found)
     */
    fun getItemQuantity(itemId: String): Int {
        val inventoryJson = prefs?.getString(KEY_INVENTORY, null) ?: return 0
        
        return try {
            val inventory = JSONObject(inventoryJson)
            if (inventory.has(itemId)) {
                inventory.getInt(itemId)
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Add items to the inventory.
     * 
     * @param itemId The item ID (e.g., "emergency_modulator")
     * @param quantity The quantity to add (defaults to 1)
     */
    fun addItem(itemId: String, quantity: Int = 1) {
        val currentQuantity = getItemQuantity(itemId)
        setItemQuantity(itemId, currentQuantity + quantity)
    }
    
    /**
     * Remove items from the inventory.
     * 
     * @param itemId The item ID (e.g., "emergency_modulator")
     * @param quantity The quantity to remove (defaults to 1)
     * @return true if items were removed successfully, false if not enough items
     */
    fun removeItem(itemId: String, quantity: Int = 1): Boolean {
        val currentQuantity = getItemQuantity(itemId)
        if (currentQuantity < quantity) {
            return false
        }
        setItemQuantity(itemId, currentQuantity - quantity)
        return true
    }
    
    /**
     * Set the quantity of a specific item in the inventory.
     * 
     * @param itemId The item ID (e.g., "emergency_modulator")
     * @param quantity The new quantity
     */
    private fun setItemQuantity(itemId: String, quantity: Int) {
        val inventoryJson = prefs?.getString(KEY_INVENTORY, null)
        val inventory = if (inventoryJson != null) {
            try {
                JSONObject(inventoryJson)
            } catch (e: Exception) {
                JSONObject()
            }
        } else {
            JSONObject()
        }
        
        if (quantity > 0) {
            inventory.put(itemId, quantity)
        } else {
            inventory.remove(itemId)
        }
        
        prefs?.edit()?.putString(KEY_INVENTORY, inventory.toString())?.apply()
    }
    
    /**
     * Get all items in the inventory as a map.
     * 
     * @return Map of itemId to quantity
     */
    fun getAllItems(): Map<String, Int> {
        val inventoryJson = prefs?.getString(KEY_INVENTORY, null) ?: return emptyMap()
        
        return try {
            val inventory = JSONObject(inventoryJson)
            val items = mutableMapOf<String, Int>()
            val keys = inventory.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                items[key] = inventory.getInt(key)
            }
            items
        } catch (e: Exception) {
            emptyMap()
        }
    }
    
    /**
     * Reset inventory (for testing/debugging).
     */
    fun resetInventory() {
        prefs?.edit()?.remove(KEY_INVENTORY)?.apply()
    }
}
