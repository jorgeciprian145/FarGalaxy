package com.example.fargalaxy.data

import android.content.Context
import android.os.PowerManager
import android.telephony.TelephonyManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * PenaltyTracker - Tracks penalties when the app goes to background during travel sessions.
 * 
 * Features:
 * - Detects when app goes to background during active travel
 * - Excludes penalties when phone is locked or in sleep mode (screen off)
 * - Excludes penalties when phone calls are active (traditional calls only)
 * - 5 second grace period before counting a penalty
 * - Resets penalties when travel session starts/ends
 * 
 * Usage:
 * - Call startTracking() when travel starts
 * - Call stopTracking() when travel ends
 * - Call getPenaltyCount() to get current penalty count
 * - Call resetPenalties() to reset counter (called automatically on startTracking)
 */
object PenaltyTracker {
    private var isTracking = false
    private var penaltyCount = 0
    private var isAppInBackground = false
    private var backgroundStartTime = 0L
    private var wasScreenOnWhenBackgrounded = false // Track screen state when app went to background
    private var gracePeriodJob: kotlinx.coroutines.Job? = null
    private var cancellationCheckJob: kotlinx.coroutines.Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    // Callback for trip cancellation (when user is away for >20 seconds or has 5+ penalties)
    // The callback receives a reason: "timeout" for >20 seconds away, "penalties" for 5+ penalties
    var onTripCancelled: ((String) -> Unit)? = null
    
    private var context: Context? = null
    private var telephonyManager: TelephonyManager? = null
    private var powerManager: PowerManager? = null
    private val lifecycleObserver = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    // App went to background
                    if (isTracking) {
                        handleAppBackground()
                    }
                }
                Lifecycle.Event.ON_START -> {
                    // App came to foreground
                    if (isTracking) {
                        handleAppForeground()
                    }
                }
                else -> {}
            }
        }
    }
    
    /**
     * Initialize the tracker with context (required for phone call detection).
     * Should be called once from MainActivity.onCreate().
     */
    fun initialize(context: Context) {
        this.context = context.applicationContext
        this.telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        this.powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
    }
    
    /**
     * Start tracking penalties for a new travel session.
     * Resets the penalty counter to 0.
     */
    fun startTracking() {
        isTracking = true
        penaltyCount = 0
        isAppInBackground = false
        backgroundStartTime = 0L
        wasScreenOnWhenBackgrounded = false
        gracePeriodJob?.cancel()
        cancellationCheckJob?.cancel()
        
        // Start checking for trip cancellation (if user is away for >20 seconds)
        cancellationCheckJob = scope.launch {
            while (isTracking) {
                delay(1000) // Check every second
                if (isAppInBackground && backgroundStartTime > 0) {
                    val timeAway = System.currentTimeMillis() - backgroundStartTime
                    if (timeAway > 20000) { // 20 seconds
                        // User has been away for more than 20 seconds, cancel trip
                        onTripCancelled?.invoke("timeout")
                        break
                    }
                }
            }
        }
    }
    
    /**
     * Stop tracking penalties (when travel ends).
     */
    fun stopTracking() {
        isTracking = false
        isAppInBackground = false
        backgroundStartTime = 0L
        wasScreenOnWhenBackgrounded = false
        gracePeriodJob?.cancel()
        cancellationCheckJob?.cancel()
    }
    
    /**
     * Get current penalty count for the active travel session.
     */
    fun getPenaltyCount(): Int = penaltyCount
    
    /**
     * Reset penalty count (called automatically on startTracking).
     */
    fun resetPenalties() {
        penaltyCount = 0
    }
    
    /**
     * Handle app going to background.
     * Starts grace period timer if not already in background.
     * Only counts penalty if screen was on when app went to background (user switched apps),
     * not if phone was locked or sleeping.
     */
    private fun handleAppBackground() {
        if (!isAppInBackground) {
            isAppInBackground = true
            backgroundStartTime = System.currentTimeMillis()
            
            // Capture screen state at the moment app goes to background
            // This is important because the screen might go to sleep during the grace period
            wasScreenOnWhenBackgrounded = isScreenInteractive()
            
            // Start grace period: wait 5 seconds before counting penalty
            gracePeriodJob?.cancel()
            gracePeriodJob = scope.launch {
                delay(5000) // 5 second grace period
                
                // After grace period, check if still in background and no phone call
                // Only count penalty if screen was on when app went to background (user switched apps)
                // Don't count if phone was locked/sleeping when app went to background
                if (isAppInBackground && isTracking) {
                    if (!isPhoneCallActive() && wasScreenOnWhenBackgrounded) {
                        penaltyCount++
                        
                        // Check if penalty count reached 5 - cancel trip
                        if (penaltyCount >= 5) {
                            onTripCancelled?.invoke("penalties")
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Handle app coming to foreground.
     * Cancels grace period if app returns before 5 seconds.
     */
    private fun handleAppForeground() {
        if (isAppInBackground) {
            isAppInBackground = false
            backgroundStartTime = 0L
            gracePeriodJob?.cancel()
        }
    }
    
    /**
     * Check if a phone call is currently active.
     * Returns true if call is ringing or in progress.
     */
    @Suppress("DEPRECATION")
    private fun isPhoneCallActive(): Boolean {
        return try {
            val callState = telephonyManager?.callState
            callState == TelephonyManager.CALL_STATE_RINGING || 
            callState == TelephonyManager.CALL_STATE_OFFHOOK
        } catch (e: SecurityException) {
            // Permission not granted, assume no call (won't exclude penalties)
            false
        } catch (e: Exception) {
            // Other error, assume no call
            false
        }
    }
    
    /**
     * Check if the screen is currently interactive (on).
     * Returns true if the screen is on and interactive.
     */
    private fun isScreenInteractive(): Boolean {
        return try {
            powerManager?.isInteractive ?: true
        } catch (e: Exception) {
            // If we can't determine, assume screen is on (will count penalty if app is backgrounded)
            // This is safer than incorrectly excluding penalties
            true
        }
    }
}
