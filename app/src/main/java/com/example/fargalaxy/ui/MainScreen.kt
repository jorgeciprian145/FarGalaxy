package com.example.fargalaxy.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fargalaxy.R
import kotlinx.coroutines.launch

/**
 * MainScreen composable - manages navigation between CareerScreen and GalaxyScreen.
 * Uses HorizontalPager for smooth swipe transitions.
 * Static layers (background, noise, indicator) are placed outside the pager so they remain fixed.
 * 
 * Pages:
 * - Index 0: CareerScreen
 * - Index 1: GalaxyScreen (center)
 */
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    // Pager state - initial page is 1 (GalaxyScreen)
    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { 2 }
    )
    
    val coroutineScope = rememberCoroutineScope()
    
    // Track if GalaxyScreen is idle (not preparing or traveling)
    // This will be updated by GalaxyScreen
    var isGalaxyIdle by remember { mutableStateOf(true) }
    
    // Track active screen based on current page
    var activeScreen by remember { mutableStateOf(ActiveScreen.CENTER) }
    
    // Update active screen when page changes
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            activeScreen = when (page) {
                0 -> ActiveScreen.LEFT
                1 -> ActiveScreen.CENTER
                else -> ActiveScreen.CENTER
            }
        }
    }
    
    // Navigate to a specific page
    fun navigateToPage(page: Int) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(page)
        }
    }
    
    // Handle career icon click - only navigate if idle
    val onCareerClick: () -> Unit = {
        if (isGalaxyIdle) {
            navigateToPage(0) // Navigate to CareerScreen
        }
    }
    
    // Handle collection icon click (future)
    val onCollectionClick: () -> Unit = {
        // Future implementation
    }
    
    // Disable user scrolling when not idle
    val userScrollEnabled = isGalaxyIdle
    
    Box(modifier = modifier.fillMaxSize()) {
        // Static background layer - doesn't move when swiping (bottom layer)
        Image(
            painter = painterResource(id = R.drawable.bg_galaxy),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Swipeable content - only this moves when swiping
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = userScrollEnabled
        ) { page ->
            when (page) {
                0 -> {
                    // CareerScreen - content only (no background/noise/indicator)
                    CareerScreen()
                }
                1 -> {
                    // GalaxyScreen - content only (no background/noise/indicator)
                    GalaxyScreen(
                        isIdleCallback = { idle -> isGalaxyIdle = idle },
                        activeScreen = activeScreen,
                        onCareerClick = onCareerClick,
                        onCollectionClick = onCollectionClick
                    )
                }
            }
        }
        
        // Static noise overlay - doesn't move when swiping (above content, below indicator)
        Image(
            painter = painterResource(id = R.drawable.noise_8bit),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.085f),
            contentScale = ContentScale.Crop
        )
        
        // Static indicator - positioned above everything (rendered last so it's on top)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 48.dp)
                .fillMaxWidth()
                .height(51.dp),
            contentAlignment = Alignment.Center
        ) {
            Indicator(activeScreen = activeScreen)
        }
    }
}

