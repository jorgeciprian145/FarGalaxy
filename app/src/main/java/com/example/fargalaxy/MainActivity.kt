package com.example.fargalaxy

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.fargalaxy.ui.theme.FarGalaxyTheme
import com.example.fargalaxy.ui.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Enable blur effect for navigation bar (Android 12+)
        // enableEdgeToEdge() already handles edge-to-edge mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
            windowInsetsController?.isAppearanceLightNavigationBars = false
            // Note: enableEdgeToEdge() already sets window.setDecorFitsSystemWindows(false)
        }
        
        setContent {
            FarGalaxyTheme {
                MainScreen()
            }
        }
    }
}