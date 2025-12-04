package com.example.fargalaxy.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.fargalaxy.R

/**
 * LevelStatusCard composable - displays the complete level card with badge and license information.
 * 
 * This composable combines two main elements:
 * 1. A responsive SVG badge on the left (level1badge) - displays the level number in a diamond shape
 * 2. The SpaceLicenseCard on the right - displays XP, progress, and level information
 * 
 * Layout behavior:
 * - The SpaceLicenseCard sizes itself vertically based on its content (padding + text + spacing)
 * - The badge height automatically matches the exact height of the SpaceLicenseCard
 * - The badge width maintains its aspect ratio based on the height
 * - Both elements have the same height to ensure perfect vertical alignment
 * - The badge overlaps the card by -12.dp (badge extends 12.dp over the card's left edge)
 *   This creates a visual connection between the badge and card, making them appear as a single component
 * 
 * Visual design:
 * - The badge sits partially on top of the card, creating a layered effect
 * - The card has no left border because that edge is visually "covered" by the badge
 * - The overlap creates a seamless integration between the responsive badge and responsive card
 * 
 * @param title The title text for the license (e.g., "Space license")
 * @param xpCurrent Current experience points
 * @param xpToNext Experience points needed to reach next level
 * @param level Current level number
 * @param progress Progress value between 0f and 1f (0 = 0%, 1 = 100%)
 * @param modifier Modifier for the entire LevelStatusCard
 */
@Composable
fun LevelStatusCard(
    title: String,
    xpCurrent: Int,
    xpToNext: Int,
    level: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    // State to track the actual height of the SpaceLicenseCard
    var cardHeight by remember { mutableStateOf<Dp?>(null) }
    
    // Badge aspect ratio: 105dp width / 118dp height
    val badgeAspectRatio = 105f / 118f
    
    // Row containing the badge (left) and SpaceLicenseCard (right)
    // Negative spacing of -12.dp creates overlap: badge extends 12.dp over card's left edge
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy((-12).dp), // Negative spacing creates overlap
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left section: Responsive SVG badge
        // The badge height is 0.7% taller than the SpaceLicenseCard
        // Width maintains aspect ratio based on the height
        if (cardHeight != null) {
            val badgeHeight = cardHeight!! * 1.007f // 0.7% taller than card
            Image(
                painter = painterResource(id = R.drawable.level1badge),
                contentDescription = "Level $level badge",
                modifier = Modifier
                    .width(badgeHeight * badgeAspectRatio)
                    .height(badgeHeight), // 0.7% taller than the card
                contentScale = ContentScale.FillBounds // Fill the exact size specified
            )
        }
        
        // Right section: Responsive SpaceLicenseCard
        // This card expands horizontally to fill available space using weight(1f)
        // The card sizes vertically based on its content
        // We measure its height and apply it to the badge
        SpaceLicenseCard(
            title = title,
            xpCurrent = xpCurrent,
            xpToNext = xpToNext,
            level = level,
            progress = progress,
            modifier = Modifier
                .weight(1f) // Expand to fill remaining horizontal space (responsive)
                .onSizeChanged { size ->
                    // Measure the actual height and convert to dp
                    cardHeight = with(density) { size.height.toDp() }
                }
        )
    }
}

