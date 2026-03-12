package com.example.fargalaxy.data

import android.content.Context
import android.content.SharedPreferences
import java.util.Calendar
import java.util.Locale

/**
 * AdsRepository - manages interstitial ad limits and visit tracking.
 *
 * Rules:
 * - Only start showing ads from the second lifetime visit to either Career or Vault.
 * - Maximum of 2 interstitials per day (across the whole app).
 * - Require at least 4 hours between interstitials.
 */
object AdsRepository {
    private const val PREFS_NAME = "ads_prefs"

    private const val KEY_VISITS_CAREER_OR_VAULT = "visits_career_or_vault"
    private const val KEY_ADS_SHOWN_DATE = "ads_shown_date" // Format: "yyyy-MM-dd"
    private const val KEY_ADS_SHOWN_TODAY = "ads_shown_today"
    private const val KEY_LAST_AD_SHOWN_TIME = "last_ad_shown_time" // epoch millis

    private var prefs: SharedPreferences? = null

    /**
     * Initialize repository. Call once from MainActivity.onCreate().
     */
    fun initialize(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    private fun getTodayString(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-based
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return String.format(Locale.US, "%04d-%02d-%02d", year, month, day)
    }

    /**
     * Register a visit to either Career or Vault screen.
     *
     * @return true once user has visited these screens at least twice (lifetime).
     */
    fun registerCareerOrVaultVisit(): Boolean {
        val p = prefs ?: return false
        val current = p.getInt(KEY_VISITS_CAREER_OR_VAULT, 0) + 1
        p.edit().putInt(KEY_VISITS_CAREER_OR_VAULT, current).apply()
        return current >= 2
    }

    /**
     * Check if we are allowed to show an interstitial right now, based on:
     * - max 2 ads per day
     * - at least 4 hours between ads
     */
    fun canShowAdNow(): Boolean {
        val p = prefs ?: return false
        val today = getTodayString()
        val storedDate = p.getString(KEY_ADS_SHOWN_DATE, null)
        var adsShownToday = p.getInt(KEY_ADS_SHOWN_TODAY, 0)

        // New day -> reset per-day counter
        if (storedDate != today) {
            adsShownToday = 0
        }

        // Daily cap reached
        if (adsShownToday >= 2) {
            return false
        }

        // Enforce 4-hour spacing between ads
        val lastShownMillis = p.getLong(KEY_LAST_AD_SHOWN_TIME, 0L)
        if (lastShownMillis > 0L) {
            val now = System.currentTimeMillis()
            val fourHoursMillis = 4L * 60L * 60L * 1000L
            if (now - lastShownMillis < fourHoursMillis) {
                return false
            }
        }

        return true
    }

    /**
     * Record that an interstitial was actually shown.
     * Updates per-day count and last-shown timestamp.
     */
    fun recordAdShown() {
        val p = prefs ?: return
        val today = getTodayString()
        val storedDate = p.getString(KEY_ADS_SHOWN_DATE, null)
        val currentCount = p.getInt(KEY_ADS_SHOWN_TODAY, 0)

        val newCount = if (storedDate == today) {
            currentCount + 1
        } else {
            1
        }

        p.edit()
            .putString(KEY_ADS_SHOWN_DATE, today)
            .putInt(KEY_ADS_SHOWN_TODAY, newCount)
            .putLong(KEY_LAST_AD_SHOWN_TIME, System.currentTimeMillis())
            .apply()
    }
}

