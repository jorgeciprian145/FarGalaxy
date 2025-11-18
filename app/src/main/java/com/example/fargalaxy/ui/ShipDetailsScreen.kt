package com.example.fargalaxy.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fargalaxy.R
import com.example.fargalaxy.model.Ship

/**
 * ShipDetailsScreen composable - displays details about the user's current ship.
 * This screen opens when the user taps "view" next to the ship name on the career screen.
 * 
 * @param ship The ship to display details for
 * @param onBackClick Callback when the back button is clicked
 * @param modifier Modifier for the screen
 */
@Composable
fun ShipDetailsScreen(
    ship: Ship,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.shipscreenbackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Top controls area: Title and back button
        // Positioned at the same location as the top controls in GalaxyScreen
        // (statusBarsPadding + 48.dp from top, 51.dp height)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 48.dp)
                .fillMaxWidth()
                .height(51.dp)
        ) {
            // Back button: Same size and style as CareerButton
            // Uses the backdefault SVG icon
            // Positioned on the left side
            Image(
                painter = painterResource(id = R.drawable.backdefault),
                contentDescription = "Back",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 24.dp)
                    .height(51.dp)
                    .clickable(onClick = onBackClick),
                contentScale = ContentScale.Fit
            )
            
            // Title: "Your current ship"
            // Same font style as "Your career" in CareerScreen
            // Horizontally centered on the screen
            Text(
                text = "Your current ship",
                fontFamily = Exo2,
                fontSize = 18.sp,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        // Track ship image size to align other elements
        val density = LocalDensity.current
        var shipImageSize by remember { mutableStateOf(IntSize.Zero) }
        
        // Ship image container: Multi-layered component
        // Button bottom edge is at: statusBarsPadding + 48.dp + 51.dp = statusBarsPadding + 99.dp
        // Ship image starts 8px below button: statusBarsPadding + 99.dp + 8.dp = statusBarsPadding + 107.dp
        // Outer container with 16dp horizontal padding (the red outline container)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 107.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp) // This creates the 16dp padding container
        ) {
            // Inner container to center all layers
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            ) {
                // Get screen width to calculate logo size
                val configuration = LocalConfiguration.current
                val screenWidthDp = configuration.screenWidthDp.dp
                val logoWidth = screenWidthDp * 0.48f // 48% of screen width
                
                // Logo layer (backmost) - behind backglare
                // 48% of screen width, vertically and horizontally aligned with ship
                // For now, using valkethlogo for Valketh Industries ships
                // TODO: Make logo selection dynamic based on ship.manufacturer
                Image(
                    painter = painterResource(id = R.drawable.valkethlogo),
                    contentDescription = "Manufacturer logo",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(logoWidth),
                    contentScale = ContentScale.Fit
                )
                
                // Ship image (frontmost) - on top of everything, even noise
                // Fills available width (already constrained by 16dp padding), maintains aspect ratio
                // Height is calculated automatically based on aspect ratio
                // Position is fixed at 8px below button (statusBarsPadding + 107.dp)
                Image(
                    painter = painterResource(id = ship.renderImageResId),
                    contentDescription = ship.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .onSizeChanged { size ->
                            shipImageSize = size
                        },
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

