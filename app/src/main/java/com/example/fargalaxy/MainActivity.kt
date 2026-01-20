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
        enableEdgeToEdge()
        
        // Set navigation bar to solid black
        // Note: navigationBarColor is deprecated but still functional and needed for solid black background
        window.navigationBarColor = Color.BLACK
        
        // Enable blur effect for navigation bar (Android 12+)
        // enableEdgeToEdge() already handles edge-to-edge mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
            windowInsetsController?.isAppearanceLightNavigationBars = false
            // Note: enableEdgeToEdge() already sets window.setDecorFitsSystemWindows(false)
        }
        
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