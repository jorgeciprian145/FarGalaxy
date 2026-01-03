package com.example.fargalaxy.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.fargalaxy.R

/**
 * Helper function to get the faction background resource ID based on faction name.
 * 
 * @param faction The faction name
 * @return The drawable resource ID for the background
 */
private fun getFactionBackgroundResId(faction: String): Int {
    return when (faction) {
        "Alliance of Starfaring Nations", "Alliance of Star Nations" -> R.drawable.factionbackgroundasn
        "Independent Systems Federation" -> R.drawable.factionbackgroundisf
        "Navakeshi Star Armada" -> R.drawable.factionbackgroundnavakeshi
        else -> R.drawable.factionbackgroundasn // Default fallback
    }
}

/**
 * Helper function to get the faction detail logo resource ID based on faction name.
 * 
 * @param faction The faction name
 * @return The drawable resource ID for the detail logo
 */
private fun getFactionDetailLogoResId(faction: String): Int {
    return when (faction) {
        "Alliance of Starfaring Nations", "Alliance of Star Nations" -> R.drawable.factiondetaillogoasn
        "Independent Systems Federation" -> R.drawable.factiondetaillogoisf
        "Navakeshi Star Armada" -> R.drawable.factiondetaillogonavakeshi
        else -> R.drawable.factiondetaillogoasn // Default fallback
    }
}

/**
 * Helper function to get the faction description text based on faction name.
 * 
 * @param faction The faction name
 * @return The description text for the faction
 */
private fun getFactionText(faction: String): String {
    return when (faction) {
        "Alliance of Starfaring Nations", "Alliance of Star Nations" -> {
            "When the mass colonization of star systems began to accelerate, some of the most prosperous worlds recognized the need for an entity capable of providing order and protection to the ever-expanding reach of human life. As new colonies emerged far beyond their points of origin, the absence of a unified framework for security quickly became a growing concern.\n\nThe Alliance was formed in response to this need, with the purpose of overseeing the security and safeguarding of all planets and their inhabitants that fall within its jurisdiction. Rather than acting as a ruling body, it was conceived as a stabilizing presence, ensuring safe passage through space, protecting civilian populations, and responding to threats that exceeded the capacity of individual systems.\n\nThe Alliance holds no political or economic authority over the worlds that comprise its vast reach. Member planets retain full sovereignty over their internal affairs, cultures, and systems of governance. The role of the Alliance is limited to defense, coordination, and the preservation of order, intervening only when the safety of entire systems or the balance between member worlds is at risk.\n\nThrough this mandate, and with over a century having passed since its founding, the Alliance stands as a unifying force among distant worlds, bound not by control or conquest, but by a shared commitment to stability, cooperation, and the continued survival of human civilization across the stars."
        }
        "Independent Systems Federation" -> {
            "The Independent Systems Federation was originally formed by colonized systems that sought to retain full military control over their own forces, rather than delegating their safeguarding to an external authority such as the Alliance. What began as a practical decision rooted in self-defense would eventually evolve into a defining ideological stance. Former frontier colonies grew into core worlds, and what was once a loose conglomerate of isolated systems became an ideal that spread steadily across the galaxy.\n\nThe rise of this ideology was far from peaceful. Numerous strategically important systems were plunged into intense civil conflicts, divided between those who wished to withdraw from the protection of the Alliance and those who sought to remain under its jurisdiction. After decades of unrest, the independentist movements emerged victorious across the vast majority of contested systems. The Alliance's increasing difficulty in intervening effectively and maintaining order during these conflicts exposed the limits of its authority, reinforcing the belief that it was far from a flawless guardian.\n\nSystems that comprise the Independent Systems Federation retain full control over their individual military forces, even though they technically operate as part of a unified fleet. To ensure cohesion and interoperability, ISF member worlds adhere to standardized doctrines of production, shared engineering frameworks, and unified fuselage paint schemes. This structure allows each system to benefit from the technological advancements and industrial strengths of the others, without sacrificing sovereignty or command autonomy.\n\nWhile unified in appearance and coordination, the ISF is fundamentally decentralized in nature. Its strength lies not in centralized command, but in mutual defense agreements and shared strategic interests. To its supporters, the Federation represents self-determination, resilience, and the belief that no distant authority should dictate the security of a system capable of defending itself."
        }
        "Navakeshi Star Armada" -> {
            "A proud, resilient, and militarily oriented people, the Navakeshi are the descendants of early colonizers who set foot centuries ago on the planet Navakesh. Isolated from the comforts of more temperate worlds, they were shaped by necessity rather than choice.\n\nNavakesh is a harsh world, defined by extreme temperatures, crushing atmospheric pressure, and a largely arid surface. Life on the planet is sustained by a vast and complex network of underground rivers, hidden beneath layers of scorched terrain. Over generations, these conditions did more than shape the landscape. They slowly molded the physiology of its inhabitants and forged a culture defined by endurance, discipline, and unwavering resolve.\n\nSurvival on Navakesh demanded cooperation, structure, and strength. As a result, military service became deeply embedded in Navakeshi society, not as an instrument of conquest, but as a fundamental pillar of protection and identity. To the Navakeshi, order is not imposed. It is earned, maintained, and defended through collective effort and proven capability.\n\nBecoming a member of the Navakeshi Star Armada is neither a right nor an expectation, but a distinction earned through rigorous training, unwavering commitment, and years of service. The Armada is not merely a military force. It is the embodiment of Navakeshi values, a symbol of unity forged under pressure, and the final safeguard of a people who learned long ago that survival is secured not by comfort, but by strength, discipline, and resolve."
        }
        else -> "" // Default fallback
    }
}

/**
 * FactionDetailsScreen composable - displays details about a faction.
 * This screen opens when the user taps on a faction badge in the LocationDetailsScreen.
 * 
 * @param faction The faction name
 * @param onBackClick Callback when the back button is clicked
 * @param modifier Modifier for the screen
 */
@Composable
fun FactionDetailsScreen(
    faction: String,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Scroll state to track scrolling
    val scrollState = rememberScrollState()
    
    // Get density to convert dp to pixels
    val density = LocalDensity.current
    
    // Calculate if content is being scrolled (scroll position > 0 means scrolling has started)
    // Header bottom is at statusBarsPadding + 75dp, trim line appears when scrolling starts
    val isContentClipped = derivedStateOf {
        with(density) {
            scrollState.value > 0
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Background image: Changes based on faction, full width, maintains aspect ratio, aligned to top
        Image(
            painter = painterResource(id = getFactionBackgroundResId(faction)),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Top gradient overlay: Covers 35% of screen height, creating a fade effect at the top.
        // Gradient transitions from solid black at the top to transparent at the bottom.
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF000000),
                            Color(0x00000000)
                        )
                    )
                )
        )
        
        // Top controls area: Title and back button
        // Positioned at statusBarsPadding + 24.dp from top, 51.dp height
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 24.dp)
                .fillMaxWidth()
                .height(51.dp)
        ) {
            // Back button
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
            
            // Title: "Factions"
            Text(
                text = "Factions",
                fontFamily = Exo2,
                fontSize = 18.sp,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        // Clip boundary container: Positioned at trim line to clip content that scrolls above it
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 91.dp) // Trim line position (header bottom 75dp + 16dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .clipToBounds() // Clip content that goes above this boundary
        ) {
            // White divider line: Only visible when content is being clipped
            // Positioned behind the Column so it doesn't block touch events
            if (isContentClipped.value) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFFFFFFF)) // White line, full width, 1px
                )
            }
            
            // Scrollable content column: Content can scroll up and get clipped at the boundary
            // JSON should be at 77dp from statusBarsPadding, clip is at 91dp, so offset is -14dp
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .offset(y = (-14).dp) // Offset to position JSON at 77dp (91dp - 14dp)
                    .verticalScroll(scrollState)
                    .navigationBarsPadding()
                    .padding(bottom = 64.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // JSON starts immediately (spacing is already in padding)
                
                // JSON and logo container: 90% width, maintains aspect ratio
                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Faction radar details JSON: 90% of container width, maintains aspect ratio
                    // Plays once and stays (behind the looping factionradar JSON)
                    val factionRadarDetailsComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.factionradardetails))
                    LottieAnimation(
                        composition = factionRadarDetailsComposition,
                        iterations = 1, // Play once and stay
                        speed = 0.65f, // Same speed as the looping JSON
                        modifier = Modifier
                            .width(maxWidth * 0.90f) // 90% of container width
                            .align(Alignment.Center),
                        contentScale = ContentScale.Fit // Maintain aspect ratio
                    )
                    
                    // Faction radar JSON: 90% of container width, maintains aspect ratio
                    // Loops forever (on top of factionradardetails JSON)
                    val factionRadarComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.factionradar))
                    LottieAnimation(
                        composition = factionRadarComposition,
                        iterations = LottieConstants.IterateForever,
                        speed = 0.65f, // Play at 0.65x speed (same as LocationDetailsScreen)
                        modifier = Modifier
                            .width(maxWidth * 0.90f) // 90% of container width
                            .align(Alignment.Center),
                        contentScale = ContentScale.Fit // Maintain aspect ratio
                    )
                    
                    // Faction logo on top: 90% of container width, centered relative to JSON
                    // Maintains aspect ratio, vertically and horizontally centered
                    Image(
                        painter = painterResource(id = getFactionDetailLogoResId(faction)),
                        contentDescription = "Faction logo",
                        modifier = Modifier
                            .width(maxWidth * 0.90f) // 90% of container width (same as JSON)
                            .align(Alignment.Center), // Center both vertically and horizontally
                        contentScale = ContentScale.Fit // Maintain aspect ratio
                    )
                }
                
                // Spacing from JSON/logo composition to faction name: 24dp
                Spacer(modifier = Modifier.height(24.dp))
                
                // Faction name
                Text(
                    text = faction,
                    fontFamily = Exo2,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                
                // Spacing from faction name to divider: 24dp
                Spacer(modifier = Modifier.height(24.dp))
                
                // Horizontal divider line: Static divider between name and content
                // White color, 32% opacity, 1dp height, 16dp horizontal padding
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(horizontal = 16.dp)
                        .background(Color(0xFFFFFFFF).copy(alpha = 0.32f))
                )
                
                // Spacing from divider to content: 24dp
                Spacer(modifier = Modifier.height(24.dp))
                
                // Faction description text
                Text(
                    text = getFactionText(faction),
                    fontFamily = Exo2,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    color = Color(0xFFFFFFFF),
                    lineHeight = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }
        
        // Bottom gradient container: 10% screen height, right above Android bottom bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.10f) // 10% of screen height
                .navigationBarsPadding() // Right above Android bottom bar
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x00000000), // 0% opacity at top
                            Color(0xFF000000)  // 100% opacity at bottom
                        )
                    )
                )
        )
    }
}

