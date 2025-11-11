package com.example.fargalaxy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.fargalaxy.ui.theme.FarGalaxyTheme
import com.example.fargalaxy.ui.GalaxyScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FarGalaxyTheme {
                GalaxyScreen()
            }
        }
    }
}