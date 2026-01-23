package com.example.fargalaxy.data

/**
 * Helper object to calculate and display level XP requirements.
 * Used for verification and testing purposes.
 */
object LevelXPCalculator {
    /**
     * Calculate XP required to go from level N to level N+1.
     * Each level requires 20% more XP than the previous level.
     * Results are rounded to the nearest 10 for cleaner numbers.
     */
    fun getXPRequiredForLevel(fromLevel: Int): Int {
        if (fromLevel < 1) return 600
        
        // Level 1 -> 2: 600 XP
        if (fromLevel == 1) return 600
        
        // For levels 2+, calculate: previous XP * 1.2, rounded to nearest 10
        var xpRequired = 600f
        for (level in 2..fromLevel) {
            xpRequired *= 1.2f
        }
        
        // Round to nearest 10
        return ((xpRequired + 5) / 10).toInt() * 10
    }
    
    /**
     * Calculate total XP required to reach a specific level.
     */
    fun getTotalXPForLevel(targetLevel: Int): Int {
        if (targetLevel <= 1) return 0
        
        var totalXP = 0
        for (level in 1 until targetLevel) {
            totalXP += getXPRequiredForLevel(level)
        }
        return totalXP
    }
    
    /**
     * Generate a list of the first N levels with their XP requirements.
     */
    fun generateLevelList(upToLevel: Int): List<String> {
        val levels = mutableListOf<String>()
        levels.add("Level 1: Starting level (0 XP required)")
        
        for (level in 1 until upToLevel) {
            val xpRequired = getXPRequiredForLevel(level)
            val totalXP = getTotalXPForLevel(level + 1)
            levels.add("Level ${level} -> ${level + 1}: $xpRequired XP (Total: $totalXP XP)")
        }
        
        return levels
    }
}
