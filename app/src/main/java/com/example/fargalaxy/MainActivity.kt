package com.example.fargalaxy

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.example.fargalaxy.ui.theme.FarGalaxyTheme
import com.example.fargalaxy.ui.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set window background to black first
        window.setBackgroundDrawableResource(android.R.color.black)
        
        enableEdgeToEdge()
        
        // Set navigation bar to solid opaque black
        // This must be done after enableEdgeToEdge() to ensure it takes effect
        @Suppress("DEPRECATION")
        window.navigationBarColor = Color.BLACK
        
        // Ensure navigation bar is solid black in edge-to-edge mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController?.isAppearanceLightNavigationBars = false
        
        // For Android 8.0+ (API 26+), ensure navigation bar color is set and opaque
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            window.navigationBarColor = Color.BLACK
            // Ensure the navigation bar is opaque (not translucent)
            window.statusBarColor = Color.TRANSPARENT // Keep status bar transparent
        }
        
        // For Android 5.0+ (API 21+), ensure navigation bar is opaque
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            @Suppress("DEPRECATION")
            window.navigationBarColor = Color.BLACK
        }
        
        // Initialize repositories with persistence
        com.example.fargalaxy.data.UserDataRepository.initialize(this)
        com.example.fargalaxy.data.ShipRepository.initialize(this)
        com.example.fargalaxy.data.GameStateRepository.initialize(this)
        com.example.fargalaxy.data.PenaltyTracker.initialize(this)
        com.example.fargalaxy.data.InventoryRepository.initialize(this)
        com.example.fargalaxy.data.FlightEnvironmentRepository.initialize(this)
        com.example.fargalaxy.data.EquipmentRepository.initialize(this)
        com.example.fargalaxy.data.EquipmentUsageRepository.initialize(this)
        
        // TODO: REMOVE TESTING CODE - Reset progress to zero for testing ship unlock
        // NOTE: Commented out to preserve progress across app sessions
        // Uncomment only when testing reset functionality
        // com.example.fargalaxy.data.GameStateRepository.resetProgress()
        
        // Set credits to 50000 in test mode (for testing purposes)
        // Must be called AFTER resetProgress() to avoid credits being reset to 0
        com.example.fargalaxy.data.UserDataRepository.setTestModeCreditsIfEnabled()
        
        setContent {
            // Override font scale to always be 1.0 (no scaling) to prevent system font size
            // from breaking the UI design. This applies to all screens in the app.
            val resources = resources
            val displayMetrics = resources.displayMetrics
            val density = displayMetrics.density
            
            CompositionLocalProvider(
                LocalDensity provides Density(
                    density = density,
                    fontScale = 1.0f // Lock font scale to 1.0
                )
            ) {
                FarGalaxyTheme {
                    MainScreen()
                }
            }
        }
    }
}