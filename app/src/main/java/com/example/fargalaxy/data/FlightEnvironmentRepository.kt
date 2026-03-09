package com.example.fargalaxy.data

import android.content.Context
import android.content.SharedPreferences
import com.example.fargalaxy.model.ShipProfile
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Represents the different possible daily flight environments.
 *
 * Each environment has a recommended ship profile that receives a bonus when used.
 */
enum class FlightEnvironmentType(
    val displayName: String,
    val recommendedProfile: ShipProfile
) {
    GRAVITATIONAL_TURBULENCE(
        displayName = "Gravitational turbulence",
        recommendedProfile = ShipProfile.STABLE
    ),
    DEBRIS_FIELD(
        displayName = "Debris field",
        recommendedProfile = ShipProfile.ACCELERATOR
    ),
    LONG_RANGE_ROUTE(
        displayName = "Long range route",
        recommendedProfile = ShipProfile.RUNNER
    ),
    STABLE_HYPERLANES(
        displayName = "Stable hyperlanes",
        recommendedProfile = ShipProfile.WELL_ROUNDED
    )
}

/**
 * Repository that manages the daily Flight Environment.
 *
 * Rules:
 * - Only one active environment per day
 * - Randomly selected
 * - Cannot repeat the same environment on consecutive days
 * - Persists across app restarts
 */
object FlightEnvironmentRepository {

    private const val PREFS_NAME = "flight_environment_prefs"
    private const val KEY_CURRENT_ENV = "current_env"
    private const val KEY_LAST_GENERATED_AT = "last_generated_at"
    private const val KEY_LAST_ENV_DATE = "last_env_date" // Central time date string, e.g. 2026-03-04
    private const val KEY_LAST_SCANNER_USED_AT = "last_scanner_used_at"

    private lateinit var prefs: SharedPreferences
    private var isInitialized = false

    fun initialize(context: Context) {
        if (!isInitialized) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            isInitialized = true
        }
    }

    private fun ensureInitialized() {
        check(isInitialized) {
            "FlightEnvironmentRepository is not initialized. Call initialize(context) from MainActivity.onCreate()."
        }
    }

    /**
     * Returns the current daily environment, generating a new one if:
     * - none has been generated yet, or
     * - more than 24 hours have passed since the last generation.
     *
     * When generating a new environment, it will never repeat the previous one.
     */
    fun getCurrentEnvironment(): FlightEnvironmentType {
        ensureInitialized()

        val now = System.currentTimeMillis()
        val storedName = prefs.getString(KEY_CURRENT_ENV, null)
        val storedDate = prefs.getString(KEY_LAST_ENV_DATE, null) // Central date when env was generated

        val storedType = storedName?.let {
            runCatching { FlightEnvironmentType.valueOf(it) }.getOrNull()
        }

        // Current date in Central Time (America/Chicago), formatted as yyyy-MM-dd
        val todayDate = getCurrentCentralDateString()

        // We need a new environment if none exists yet, or if the stored date is not today
        val needsNewEnvironment = storedType == null || storedDate == null || storedDate != todayDate

        return if (!needsNewEnvironment) {
            // Same day and we already have an environment → reuse it
            // storedType is guaranteed to be non-null when !needsNewEnvironment is true
            storedType!!
        } else {
            // When generating a new environment, don't repeat yesterday's environment
            val previousType = if (storedDate != null && storedType != null && storedDate != todayDate) {
                storedType
            } else {
                null
            }

            val newType = generateNewEnvironment(exclude = previousType)
            prefs.edit()
                .putString(KEY_CURRENT_ENV, newType.name)
                .putString(KEY_LAST_ENV_DATE, todayDate)
                .putLong(KEY_LAST_GENERATED_AT, now)
                .apply()
            newType
        }
    }

    /**
     * Returns the display name of the current environment for UI usage.
     */
    fun getCurrentEnvironmentDisplayName(): String {
        return getCurrentEnvironment().displayName
    }

    /**
     * Returns the ShipProfile that is recommended for the current environment.
     */
    fun getCurrentEnvironmentRecommendedProfile(): ShipProfile {
        return getCurrentEnvironment().recommendedProfile
    }

    /**
     * Returns true if the Deep space scanner has already been used for the
     * currently active environment.
     *
     * We treat "once per day" as "once per generated environment". If a new
     * environment is generated (after 24 hours), the scanner becomes usable again.
     */
    fun isScannerUsedForCurrentEnvironment(): Boolean {
        ensureInitialized()

        // Ensure we have a current environment and a valid generation timestamp
        getCurrentEnvironment()
        val lastGeneratedAt = prefs.getLong(KEY_LAST_GENERATED_AT, 0L)
        val lastScannerUsedAt = prefs.getLong(KEY_LAST_SCANNER_USED_AT, 0L)

        return lastGeneratedAt != 0L && lastScannerUsedAt >= lastGeneratedAt
    }

    /**
     * Marks the Deep space scanner as used for the currently active environment.
     */
    fun markScannerUsedForCurrentEnvironment() {
        ensureInitialized()
        // Ensure environment exists and last_generated_at is set
        getCurrentEnvironment()
        prefs.edit()
            .putLong(KEY_LAST_SCANNER_USED_AT, System.currentTimeMillis())
            .apply()
    }

    /**
     * Clears scanner usage state so that the environment is treated as not revealed.
     * Useful when resetting app progress/state.
     */
    fun resetScannerUsage() {
        if (!isInitialized) return
        prefs.edit()
            .remove(KEY_LAST_SCANNER_USED_AT)
            .apply()
    }

    /**
     * Returns the remaining time in milliseconds until the current environment
     * resets and a new one is generated.
     */
    fun getRemainingMillisUntilReset(): Long {
        ensureInitialized()

        // Ensure environment exists so that today's date is stored
        getCurrentEnvironment()

        val now = System.currentTimeMillis()
        val nextMidnight = getNextCentralMidnightMillis()
        val remaining = (nextMidnight - now).coerceAtLeast(0L)
        return remaining
    }

    /**
     * Returns current date in Central Time as yyyy-MM-dd (e.g., 2026-03-04).
     */
    private fun getCurrentCentralDateString(): String {
        val zoneId = ZoneId.of("America/Chicago")
        val now = ZonedDateTime.now(zoneId)
        return String.format("%04d-%02d-%02d", now.year, now.monthValue, now.dayOfMonth)
    }

    /**
     * Returns the timestamp (in millis) of the next midnight in Central Time.
     */
    private fun getNextCentralMidnightMillis(): Long {
        val zoneId = ZoneId.of("America/Chicago")
        val now = ZonedDateTime.now(zoneId)
        // Get next day at midnight
        val nextMidnight = now.toLocalDate()
            .plusDays(1)
            .atStartOfDay(zoneId)
        return nextMidnight.toInstant().toEpochMilli()
    }

    /**
     * Returns the percentage bonus provided by the current environment for the given ship profile.
     *
     * If the shipProfile matches the environment's recommended profile:
     *  - +10% bonus to XP and Credits
     *
     * Otherwise:
     *  - 0% bonus
     */
    fun getEnvironmentBonusPercent(shipProfile: ShipProfile): Int {
        val environment = getCurrentEnvironment()
        return if (environment.recommendedProfile == shipProfile) {
            10
        } else {
            0
        }
    }

    /**
     * Internal helper that generates a new random environment.
     * If [exclude] is provided and there is more than one environment available,
     * the new environment will always be different from [exclude].
     */
    private fun generateNewEnvironment(exclude: FlightEnvironmentType?): FlightEnvironmentType {
        val all = FlightEnvironmentType.values().toList()
        val candidates = if (exclude != null && all.size > 1) {
            all.filter { it != exclude }
        } else {
            all
        }
        return candidates.random()
    }
}

