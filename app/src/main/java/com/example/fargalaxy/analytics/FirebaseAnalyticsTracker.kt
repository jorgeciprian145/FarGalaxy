package com.example.fargalaxy.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object FirebaseAnalyticsTracker {
    private fun analytics(context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context.applicationContext)
    }

    private fun logEvent(
        context: Context,
        eventName: String,
        params: Map<String, Any?> = emptyMap()
    ) {
        val bundle = Bundle()
        params.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Float -> bundle.putFloat(key, value)
                is Double -> bundle.putDouble(key, value)
                is Boolean -> bundle.putString(key, value.toString())
                null -> Unit
                else -> bundle.putString(key, value.toString())
            }
        }
        analytics(context).logEvent(eventName, bundle)
    }

    fun logAppOpen(context: Context) = logEvent(context, "app_open")

    fun logOnboardingStarted(context: Context) = logEvent(context, "onboarding_started")

    fun logOnboardingCompleted(context: Context) = logEvent(context, "onboarding_completed")

    fun logFocusSessionStarted(context: Context, durationMinutes: Int, shipId: String?) {
        logEvent(
            context,
            "focus_session_started",
            mapOf(
                "duration_min" to durationMinutes,
                "ship_id" to shipId
            )
        )
    }

    fun logFocusSessionCompleted(context: Context, durationMinutes: Int, penalties: Int) {
        logEvent(
            context,
            "focus_session_completed",
            mapOf(
                "duration_min" to durationMinutes,
                "penalties" to penalties
            )
        )
    }

    fun logFocusSessionAbandoned(context: Context, reason: String, penalties: Int) {
        logEvent(
            context,
            "focus_session_abandoned",
            mapOf(
                "reason" to reason,
                "penalties" to penalties
            )
        )
    }

    fun logStoreOpened(context: Context) = logEvent(context, "store_opened")

    fun logCrateOpened(
        context: Context,
        crateType: String,
        rewardType: String,
        unlockedShipId: String?
    ) {
        logEvent(
            context,
            "crate_opened",
            mapOf(
                "crate_type" to crateType,
                "reward_type" to rewardType,
                "unlocked_ship_id" to unlockedShipId
            )
        )
    }

    fun logShipUnlocked(context: Context, shipId: String, source: String) {
        logEvent(
            context,
            "ship_unlocked",
            mapOf(
                "ship_id" to shipId,
                "source" to source
            )
        )
    }

    fun logScreenView(context: Context, screenName: String) {
        logEvent(
            context,
            "screen_view",
            mapOf(
                FirebaseAnalytics.Param.SCREEN_NAME to screenName,
                FirebaseAnalytics.Param.SCREEN_CLASS to "MainScreen"
            )
        )
    }
}
