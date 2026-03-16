package com.example.fargalaxy.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Calendar
import java.util.Locale

/**
 * Global state repository for user data (credits and XP).
 * This is a simple singleton that holds the current user state.
 * Persists data using SharedPreferences.
 */
object UserDataRepository {
    private const val PREFS_NAME = "user_data_prefs"
    private const val KEY_CREDITS = "user_credits"
    private const val KEY_XP = "user_xp"
    private const val KEY_TOTAL_FOCUS_TIME = "total_focus_time_minutes"
    private const val KEY_TOTAL_FOCUS_TIME_IN_SECTOR = "total_focus_time_in_sector_minutes"
    private const val KEY_CURRENT_STREAK = "current_streak_days"
    private const val KEY_SESSIONS_THIS_MONTH = "sessions_this_month"
    private const val KEY_TOTAL_SESSIONS = "total_sessions"
    private const val KEY_LAST_SESSION_DATE = "last_session_date" // Format: "yyyy-MM-dd"
    private const val KEY_LAST_SESSION_MONTH = "last_session_month" // Format: "yyyy-MM"
    private const val KEY_LAST_SESSION_WEEK = "last_session_week" // Format: "yyyy-Www" (ISO week)
    private const val KEY_SESSIONS_THIS_WEEK = "sessions_this_week"
    private const val KEY_SESSION_DURATIONS = "session_durations" // Comma-separated list of session durations in minutes
    private const val KEY_LONGEST_SESSION = "longest_session_minutes" // Longest session duration in minutes
    private const val KEY_LAST_SESSION_DURATION = "last_session_duration_minutes" // Last session duration in minutes

    // Tutorial flags
    private const val KEY_TUTORIAL_MAIN_WELCOME = "tutorial_main_welcome"
    private const val KEY_TUTORIAL_MAIN_LAUNCH = "tutorial_main_launch"
    private const val KEY_TUTORIAL_TRAVEL = "tutorial_travel"
    private const val KEY_TUTORIAL_CAREER = "tutorial_career"
    private const val KEY_TUTORIAL_VAULT = "tutorial_vault"
    
    private var prefs: SharedPreferences? = null
    
    /**
     * Initialize the repository with a context.
     * Should be called once from MainActivity.onCreate().
     */
    fun initialize(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            // Load saved values
            _userCredits = mutableStateOf(prefs!!.getInt(KEY_CREDITS, 0))
            _userXP = mutableStateOf(prefs!!.getInt(KEY_XP, 0))
            _totalFocusTimeMinutes = mutableStateOf(prefs!!.getInt(KEY_TOTAL_FOCUS_TIME, 0))
            _totalFocusTimeInSectorMinutes = mutableStateOf(prefs!!.getInt(KEY_TOTAL_FOCUS_TIME_IN_SECTOR, 0))
            _currentStreakDays = mutableStateOf(prefs!!.getInt(KEY_CURRENT_STREAK, 0))
            _sessionsThisMonth = mutableStateOf(prefs!!.getInt(KEY_SESSIONS_THIS_MONTH, 0))
            _totalSessions = mutableStateOf(prefs!!.getInt(KEY_TOTAL_SESSIONS, 0))

            // Load tutorial flags
            hasSeenMainWelcomeTutorial = prefs!!.getBoolean(KEY_TUTORIAL_MAIN_WELCOME, false)
            hasSeenMainLaunchTutorial = prefs!!.getBoolean(KEY_TUTORIAL_MAIN_LAUNCH, false)
            hasSeenTravelTutorial = prefs!!.getBoolean(KEY_TUTORIAL_TRAVEL, false)
            hasSeenCareerTutorial = prefs!!.getBoolean(KEY_TUTORIAL_CAREER, false)
            hasSeenVaultTutorial = prefs!!.getBoolean(KEY_TUTORIAL_VAULT, false)
            
            // Check if we need to reset sessions this month (new month)
            checkAndResetMonthlySessions()
            // Check if we need to reset sessions this week (new week)
            checkAndResetWeeklySessions()
        }
    }
    
    /**
     * Set credits to 50000 in test mode (for testing purposes).
     * Should be called after GameStateRepository is initialized.
     */
    fun setTestModeCreditsIfEnabled() {
        if (GameStateRepository.isTestMode) {
            userCredits = 50000
        }
    }

    // --- Tutorial flag helpers ---

    fun markMainWelcomeTutorialSeen() {
        hasSeenMainWelcomeTutorial = true
        prefs?.edit()?.putBoolean(KEY_TUTORIAL_MAIN_WELCOME, true)?.apply()
    }

    fun markMainLaunchTutorialSeen() {
        hasSeenMainLaunchTutorial = true
        prefs?.edit()?.putBoolean(KEY_TUTORIAL_MAIN_LAUNCH, true)?.apply()
    }

    fun markTravelTutorialSeen() {
        hasSeenTravelTutorial = true
        prefs?.edit()?.putBoolean(KEY_TUTORIAL_TRAVEL, true)?.apply()
    }

    fun markCareerTutorialSeen() {
        hasSeenCareerTutorial = true
        prefs?.edit()?.putBoolean(KEY_TUTORIAL_CAREER, true)?.apply()
    }

    fun markVaultTutorialSeen() {
        hasSeenVaultTutorial = true
        prefs?.edit()?.putBoolean(KEY_TUTORIAL_VAULT, true)?.apply()
    }
    
    private var _userCredits = mutableStateOf(0)
    private var _userXP = mutableStateOf(0)
    private var _totalFocusTimeMinutes = mutableStateOf(0)
    private var _totalFocusTimeInSectorMinutes = mutableStateOf(0)
    private var _currentStreakDays = mutableStateOf(0)
    private var _sessionsThisMonth = mutableStateOf(0)
    private var _totalSessions = mutableStateOf(0)

    // Tutorial flags (in-memory)
    var hasSeenMainWelcomeTutorial: Boolean = false
        private set
    var hasSeenMainLaunchTutorial: Boolean = false
        private set
    var hasSeenTravelTutorial: Boolean = false
        private set
    var hasSeenCareerTutorial: Boolean = false
        private set
    var hasSeenVaultTutorial: Boolean = false
        private set

    /**
     * Returns true when the user has seen all onboarding/tutorial modals.
     * Used to gate certain features (e.g., interstitial ads) until onboarding is complete.
     */
    fun hasCompletedAllTutorials(): Boolean {
        return hasSeenMainWelcomeTutorial &&
                hasSeenMainLaunchTutorial &&
                hasSeenTravelTutorial &&
                hasSeenCareerTutorial &&
                hasSeenVaultTutorial
    }
    
    // Current user credits (persisted)
    var userCredits: Int
        get() = _userCredits.value
        set(value) {
            _userCredits.value = value
            prefs?.edit()?.putInt(KEY_CREDITS, value)?.apply()
        }
    
    // Current user XP (persisted)
    var userXP: Int
        get() = _userXP.value
        set(value) {
            _userXP.value = value
            prefs?.edit()?.putInt(KEY_XP, value)?.apply()
        }
    
    // Total focus time in minutes (persisted) - tracks all focus sessions regardless of completion
    var totalFocusTimeMinutes: Int
        get() = _totalFocusTimeMinutes.value
        set(value) {
            _totalFocusTimeMinutes.value = value
            prefs?.edit()?.putInt(KEY_TOTAL_FOCUS_TIME, value)?.apply()
        }
    
    // Total focus time in current sector in minutes (persisted) - for now, same as total focus time
    var totalFocusTimeInSectorMinutes: Int
        get() = _totalFocusTimeInSectorMinutes.value
        set(value) {
            _totalFocusTimeInSectorMinutes.value = value
            prefs?.edit()?.putInt(KEY_TOTAL_FOCUS_TIME_IN_SECTOR, value)?.apply()
        }
    
    /**
     * Add focus time to both counters.
     * This is called regardless of whether the session was completed or cancelled.
     * 
     * @param minutes The number of minutes to add
     */
    fun addFocusTime(minutes: Int) {
        if (minutes > 0) {
            totalFocusTimeMinutes += minutes
            totalFocusTimeInSectorMinutes += minutes // For now, same as total (will be sector-specific later)
            // Sync unlocked ships and locations based on new focus time
            GameStateRepository.syncUnlockedShipsFromFocusTime()
            GameStateRepository.syncUnlockedLocationsFromFocusTime()
        }
    }
    
    /**
     * Add credits to user account.
     * Only called when a travel session is completed (not cancelled).
     */
    fun addCredits(amount: Int) {
        userCredits += amount
    }
    
    /**
     * Add XP to user account.
     * Only called when a travel session is completed (not cancelled).
     */
    fun addXp(amount: Int) {
        userXP += amount
    }
    
    // Current streak in days (persisted)
    var currentStreakDays: Int
        get() = _currentStreakDays.value
        private set(value) {
            _currentStreakDays.value = value
            prefs?.edit()?.putInt(KEY_CURRENT_STREAK, value)?.apply()
        }
    
    // Sessions this month (persisted, resets each month)
    var sessionsThisMonth: Int
        get() = _sessionsThisMonth.value
        private set(value) {
            _sessionsThisMonth.value = value
            prefs?.edit()?.putInt(KEY_SESSIONS_THIS_MONTH, value)?.apply()
        }
    
    // Total sessions (persisted, only successful travels)
    var totalSessions: Int
        get() = _totalSessions.value
        private set(value) {
            _totalSessions.value = value
            prefs?.edit()?.putInt(KEY_TOTAL_SESSIONS, value)?.apply()
        }
    
    /**
     * Record a completed session (successful travel).
     * Updates streak, sessions this month, sessions this week, and total sessions.
     * Tracks session duration for average and longest session calculations.
     * Should only be called when a travel session completes successfully (not cancelled).
     * 
     * @param sessionDurationMinutes The duration of the session in minutes
     */
    fun recordCompletedSession(sessionDurationMinutes: Int = 0) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-based
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        val todayString = String.format(Locale.US, "%04d-%02d-%02d", year, month, day) // Format: "yyyy-MM-dd"
        val currentMonth = String.format(Locale.US, "%04d-%02d", year, month) // Format: "yyyy-MM"
        
        // Get last session date
        val lastSessionDateString = prefs?.getString(KEY_LAST_SESSION_DATE, null)
        val lastSessionMonth = prefs?.getString(KEY_LAST_SESSION_MONTH, null)
        
        // Check if we need to reset sessions this month (new month)
        if (lastSessionMonth != currentMonth) {
            sessionsThisMonth = 0
        }
        
        // Update streak logic
        if (lastSessionDateString != null) {
            try {
                // Parse last session date
                val parts = lastSessionDateString.split("-")
                if (parts.size == 3) {
                    val lastYear = parts[0].toInt()
                    val lastMonth = parts[1].toInt()
                    val lastDay = parts[2].toInt()
                    
                    val lastCalendar = Calendar.getInstance()
                    lastCalendar.set(lastYear, lastMonth - 1, lastDay) // Calendar.MONTH is 0-based
                    lastCalendar.set(Calendar.HOUR_OF_DAY, 0)
                    lastCalendar.set(Calendar.MINUTE, 0)
                    lastCalendar.set(Calendar.SECOND, 0)
                    lastCalendar.set(Calendar.MILLISECOND, 0)
                    
                    val todayCalendar = Calendar.getInstance()
                    todayCalendar.set(year, month - 1, day) // Calendar.MONTH is 0-based
                    todayCalendar.set(Calendar.HOUR_OF_DAY, 0)
                    todayCalendar.set(Calendar.MINUTE, 0)
                    todayCalendar.set(Calendar.SECOND, 0)
                    todayCalendar.set(Calendar.MILLISECOND, 0)
                    
                    val daysBetween = ((todayCalendar.timeInMillis - lastCalendar.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                    
                    if (daysBetween == 1) {
                        // Consecutive day: increment streak
                        currentStreakDays += 1
                    } else if (daysBetween > 1) {
                        // Gap in days: reset streak to 1
                        currentStreakDays = 1
                    }
                    // If daysBetween == 0, same day: don't change streak
                } else {
                    // Invalid format: start fresh
                    currentStreakDays = 1
                }
            } catch (e: Exception) {
                // If parsing fails, start fresh
                currentStreakDays = 1
            }
        } else {
            // First session ever: start streak at 1
            currentStreakDays = 1
        }
        
        // Calculate current week (ISO week format: yyyy-Www)
        val weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentWeek = String.format(Locale.US, "%04d-W%02d", year, weekOfYear)
        
        // Get last session week
        val lastSessionWeek = prefs?.getString(KEY_LAST_SESSION_WEEK, null)
        
        // Check if we need to reset sessions this week (new week)
        if (lastSessionWeek != currentWeek) {
            prefs?.edit()?.putInt(KEY_SESSIONS_THIS_WEEK, 0)?.apply()
        }
        
        // Increment counters
        sessionsThisMonth += 1
        totalSessions += 1
        
        // Increment sessions this week
        val currentSessionsThisWeek = prefs?.getInt(KEY_SESSIONS_THIS_WEEK, 0) ?: 0
        prefs?.edit()?.putInt(KEY_SESSIONS_THIS_WEEK, currentSessionsThisWeek + 1)?.apply()
        
        // Track session duration
        if (sessionDurationMinutes > 0) {
            // Save last session duration
            prefs?.edit()?.putInt(KEY_LAST_SESSION_DURATION, sessionDurationMinutes)?.apply()
            
            // Update longest session if this is longer
            val currentLongest = prefs?.getInt(KEY_LONGEST_SESSION, 0) ?: 0
            if (sessionDurationMinutes > currentLongest) {
                prefs?.edit()?.putInt(KEY_LONGEST_SESSION, sessionDurationMinutes)?.apply()
            }
            
            // Add to session durations list (keep last 100 sessions for average calculation)
            val durationsString = prefs?.getString(KEY_SESSION_DURATIONS, "") ?: ""
            val durationsList = if (durationsString.isEmpty()) {
                mutableListOf<Int>()
            } else {
                durationsString.split(",").mapNotNull { it.toIntOrNull() }.toMutableList()
            }
            
            durationsList.add(sessionDurationMinutes)
            
            // Keep only last 100 sessions
            if (durationsList.size > 100) {
                durationsList.removeAt(0)
            }
            
            prefs?.edit()?.putString(KEY_SESSION_DURATIONS, durationsList.joinToString(","))?.apply()
        }
        
        // Save last session date, month, and week
        prefs?.edit()
            ?.putString(KEY_LAST_SESSION_DATE, todayString)
            ?.putString(KEY_LAST_SESSION_MONTH, currentMonth)
            ?.putString(KEY_LAST_SESSION_WEEK, currentWeek)
            ?.apply()
    }
    
    /**
     * Check if we need to reset sessions this week (when week changes).
     * Called on app initialization.
     */
    private fun checkAndResetWeeklySessions() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentWeek = String.format(Locale.US, "%04d-W%02d", year, weekOfYear)
        
        val lastSessionWeek = prefs?.getString(KEY_LAST_SESSION_WEEK, null)
        
        if (lastSessionWeek != null && lastSessionWeek != currentWeek) {
            // Week changed: reset sessions this week
            prefs?.edit()?.putInt(KEY_SESSIONS_THIS_WEEK, 0)?.apply()
        }
    }
    
    /**
     * Check if we need to reset sessions this month (when month changes).
     * Called on app initialization.
     */
    private fun checkAndResetMonthlySessions() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-based
        val currentMonth = String.format(Locale.US, "%04d-%02d", year, month) // Format: "yyyy-MM"
        
        val lastSessionMonth = prefs?.getString(KEY_LAST_SESSION_MONTH, null)
        
        if (lastSessionMonth != null && lastSessionMonth != currentMonth) {
            // Month changed: reset sessions this month
            sessionsThisMonth = 0
        }
        
        // Check streak: if last session was more than 1 day ago, reset streak
        val lastSessionDateString = prefs?.getString(KEY_LAST_SESSION_DATE, null)
        if (lastSessionDateString != null) {
            try {
                // Parse last session date
                val parts = lastSessionDateString.split("-")
                if (parts.size == 3) {
                    val lastYear = parts[0].toInt()
                    val lastMonth = parts[1].toInt()
                    val lastDay = parts[2].toInt()
                    
                    val lastCalendar = Calendar.getInstance()
                    lastCalendar.set(lastYear, lastMonth - 1, lastDay) // Calendar.MONTH is 0-based
                    lastCalendar.set(Calendar.HOUR_OF_DAY, 0)
                    lastCalendar.set(Calendar.MINUTE, 0)
                    lastCalendar.set(Calendar.SECOND, 0)
                    lastCalendar.set(Calendar.MILLISECOND, 0)
                    
                    val todayCalendar = Calendar.getInstance()
                    todayCalendar.set(year, month - 1, calendar.get(Calendar.DAY_OF_MONTH)) // Calendar.MONTH is 0-based
                    todayCalendar.set(Calendar.HOUR_OF_DAY, 0)
                    todayCalendar.set(Calendar.MINUTE, 0)
                    todayCalendar.set(Calendar.SECOND, 0)
                    todayCalendar.set(Calendar.MILLISECOND, 0)
                    
                    val daysBetween = ((todayCalendar.timeInMillis - lastCalendar.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                    
                    if (daysBetween > 1) {
                        // More than 1 day gap: reset streak
                        currentStreakDays = 0
                    }
                }
            } catch (e: Exception) {
                // If parsing fails, keep current streak
            }
        }
    }
    
    // Level 1 to Level 2 requires 600 XP
    private const val BASE_XP_FOR_LEVEL_2 = 600
    
    /**
     * Calculate XP required to go from level N to level N+1.
     * Each level requires 20% more XP than the previous level.
     * Results are rounded to the nearest 10 for cleaner numbers.
     * 
     * @param fromLevel The level you're starting from (1-based)
     * @return XP required to reach the next level
     */
    fun getXPRequiredForLevel(fromLevel: Int): Int {
        if (fromLevel < 1) return BASE_XP_FOR_LEVEL_2
        
        // Level 1 -> 2: 600 XP
        if (fromLevel == 1) return BASE_XP_FOR_LEVEL_2
        
        // For levels 2+, calculate: previous XP * 1.2, rounded to nearest 10
        var xpRequired = BASE_XP_FOR_LEVEL_2.toFloat()
        for (level in 2..fromLevel) {
            xpRequired *= 1.2f
        }
        
        // Round to nearest 10
        return ((xpRequired + 5) / 10).toInt() * 10
    }
    
    /**
     * Calculate total XP required to reach a specific level.
     * 
     * @param targetLevel The target level (1-based)
     * @return Total XP required to reach that level
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
     * Get current level based on total XP.
     * Starts at level 1 and increases as XP accumulates.
     * 
     * @return Current level (1-based)
     */
    fun getCurrentLevel(): Int {
        var level = 1
        var totalRequired = 0
        
        while (totalRequired <= userXP) {
            level++
            val xpForNextLevel = getXPRequiredForLevel(level - 1)
            totalRequired += xpForNextLevel
            
            // Safety check to prevent infinite loop
            if (level > 100) break
        }
        
        return level - 1
    }
    
    /**
     * Calculate progress for current level (0f to 1f)
     * Progress is calculated based on XP within the current level range.
     */
    fun getCurrentLevelProgress(): Float {
        val currentLevel = getCurrentLevel()
        
        // If at level 1 with 0 XP, progress is 0
        if (currentLevel == 1 && userXP == 0) return 0f
        
        // Calculate total XP required for current level
        val xpForCurrentLevel = getTotalXPForLevel(currentLevel)
        
        // Calculate XP within current level
        val xpInCurrentLevel = userXP - xpForCurrentLevel
        
        // Calculate XP required to reach next level from current level
        val xpRequiredForNext = getXPRequiredForLevel(currentLevel)
        
        // Calculate progress (0f to 1f)
        return (xpInCurrentLevel.toFloat() / xpRequiredForNext).coerceIn(0f, 1f)
    }
    
    /**
     * Calculate XP needed to reach next level
     * 
     * @return XP remaining to reach next level
     */
    fun getXPToNextLevel(): Int {
        val currentLevel = getCurrentLevel()
        val xpRequiredForNext = getXPRequiredForLevel(currentLevel)
        val xpForCurrentLevel = getTotalXPForLevel(currentLevel)
        val xpInCurrentLevel = userXP - xpForCurrentLevel
        
        return (xpRequiredForNext - xpInCurrentLevel).coerceAtLeast(0)
    }
    
    /**
     * Get sessions this week.
     * 
     * @return Number of sessions completed this week
     */
    fun getSessionsThisWeek(): Int {
        return prefs?.getInt(KEY_SESSIONS_THIS_WEEK, 0) ?: 0
    }
    
    /**
     * Get formatted last session date.
     * Returns "Today", "Yesterday", or the formatted date.
     * 
     * @return Formatted last session date string
     */
    fun getLastSessionFormattedDate(): String {
        val lastSessionDateString = prefs?.getString(KEY_LAST_SESSION_DATE, null) ?: return "Never"
        
        try {
            val parts = lastSessionDateString.split("-")
            if (parts.size == 3) {
                val lastYear = parts[0].toInt()
                val lastMonth = parts[1].toInt()
                val lastDay = parts[2].toInt()
                
                val lastCalendar = Calendar.getInstance()
                lastCalendar.set(lastYear, lastMonth - 1, lastDay)
                lastCalendar.set(Calendar.HOUR_OF_DAY, 0)
                lastCalendar.set(Calendar.MINUTE, 0)
                lastCalendar.set(Calendar.SECOND, 0)
                lastCalendar.set(Calendar.MILLISECOND, 0)
                
                val todayCalendar = Calendar.getInstance()
                todayCalendar.set(Calendar.HOUR_OF_DAY, 0)
                todayCalendar.set(Calendar.MINUTE, 0)
                todayCalendar.set(Calendar.SECOND, 0)
                todayCalendar.set(Calendar.MILLISECOND, 0)
                
                val daysBetween = ((todayCalendar.timeInMillis - lastCalendar.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                
                return when (daysBetween) {
                    0 -> "Today"
                    1 -> "Yesterday"
                    else -> {
                        // Format as "MMM d" (e.g., "Jan 15")
                        val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                        "${monthNames[lastMonth - 1]} $lastDay"
                    }
                }
            }
        } catch (e: Exception) {
            // If parsing fails, return the raw string
        }
        
        return lastSessionDateString
    }
    
    /**
     * Get average session time in minutes.
     * 
     * @return Average session time in minutes, or 0 if no sessions
     */
    fun getAverageSessionTimeMinutes(): Int {
        val durationsString = prefs?.getString(KEY_SESSION_DURATIONS, "") ?: ""
        if (durationsString.isEmpty()) return 0
        
        val durations = durationsString.split(",").mapNotNull { it.toIntOrNull() }
        if (durations.isEmpty()) return 0
        
        val sum = durations.sum()
        return sum / durations.size
    }
    
    /**
     * Get longest session time in minutes.
     * 
     * @return Longest session time in minutes, or 0 if no sessions
     */
    fun getLongestSessionMinutes(): Int {
        return prefs?.getInt(KEY_LONGEST_SESSION, 0) ?: 0
    }
    
    /**
     * Format minutes as a readable string (e.g., "15 m", "1 h 30 m", "20 mins").
     * 
     * @param minutes The number of minutes
     * @return Formatted string
     */
    fun formatSessionTime(minutes: Int): String {
        if (minutes == 0) return "0 m"
        
        if (minutes < 60) {
            return "$minutes m"
        } else {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            return if (remainingMinutes == 0) {
                "$hours h"
            } else {
                "$hours h $remainingMinutes m"
            }
        }
    }
    
    /**
     * Reset all user data to starting state.
     * Sets all values to 0 (starting state for new users).
     */
    fun resetProgress() {
        userCredits = 0
        userXP = 0
        totalFocusTimeMinutes = 0
        totalFocusTimeInSectorMinutes = 0
        currentStreakDays = 0
        sessionsThisMonth = 0
        totalSessions = 0
        
        // Clear last session date/month/week and session data
        prefs?.edit()
            ?.remove(KEY_LAST_SESSION_DATE)
            ?.remove(KEY_LAST_SESSION_MONTH)
            ?.remove(KEY_LAST_SESSION_WEEK)
            ?.remove(KEY_SESSIONS_THIS_WEEK)
            ?.remove(KEY_SESSION_DURATIONS)
            ?.remove(KEY_LONGEST_SESSION)
            ?.remove(KEY_LAST_SESSION_DURATION)
            ?.apply()
    }
}
