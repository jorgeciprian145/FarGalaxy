package com.example.fargalaxy.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fargalaxy.R

/**
 * LevelStatusCard composable - displays the complete level card with badge and license information.
 * 
 * This composable combines two main elements:
 * 1. A fixed-size SVG badge on the left (level1badge) - displays the level number in a diamond shape
 * 2. The SpaceLicenseCard on the right - displays XP, progress, and level information
 * 
 * Layout behavior:
 * - The badge is fixed-size (124.dp height) and does not scale with screen width
 *   This ensures the badge remains visually consistent regardless of screen size
 * - The SpaceLicenseCard expands horizontally to fill available space using weight(1f)
 *   This makes the card responsive to different screen widths
 * - Both elements have the same height (124.dp) to ensure perfect vertical alignment
 * - The badge overlaps the card by -8.dp (badge extends 8.dp over the card's left edge)
 *   This creates a visual connection between the badge and card, making them appear as a single component
 * 
 * Visual design:
 * - The badge sits partially on top of the card, creating a layered effect
 * - The card has no left border because that edge is visually "covered" by the badge
 * - The overlap creates a seamless integration between the fixed badge and responsive card
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
    // Fixed height for both badge and card to ensure perfect alignment
    // Set to exactly match content + padding with explicit line heights:
    // - Title text (14.sp, lineHeight 16.sp) = 16.dp
    // - XP text (20.sp, lineHeight 24.sp) = 24.dp
    // - Progress bar = 8.dp
    // - Bottom row text (max lineHeight 20.sp) = 20.dp
    // - Spacing between items (3 × 8.dp) = 24.dp
    // - Padding (16.dp top + 16.dp bottom) = 32.dp
    // Total: 16 + 24 + 8 + 20 + 24 + 32 = 124.dp
    val cardHeight = 124.dp
    val badgeHeight = 125.dp // Badge height - can be adjusted independently from card
    // Calculate badge width based on aspect ratio (105dp width / 118dp height)
    val badgeWidth = badgeHeight * (105f / 118f)
    
    // Row containing the badge (left) and SpaceLicenseCard (right)
    // Negative spacing of -8.dp creates overlap: badge extends 8.dp over card's left edge
    Row(
        modifier = modifier
            .fillMaxWidth(), // Height removed - Row sizes to accommodate the taller element (badge)
        horizontalArrangement = Arrangement.spacedBy((-12).dp), // Negative spacing creates overlap
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left section: Fixed-size SVG badge
        // The badge must match the exact height of the card (cardHeight)
        // We use height(cardHeight) to set the exact height, and ContentScale.Fit ensures
        // the badge maintains its intrinsic aspect ratio (105dp x 118dp) without deformation
        // The width will scale proportionally to maintain the aspect ratio
        Image(
            painter = painterResource(id = R.drawable.level1badge),
            contentDescription = "Level $level badge",
            modifier = Modifier
                .width(badgeWidth)
                .height(badgeHeight), // Badge size - height adjustable, width maintains aspect ratio
            contentScale = ContentScale.FillBounds // Fill the exact size specified
        )
        
        // Right section: Responsive SpaceLicenseCard
        // This card expands horizontally to fill available space using weight(1f)
        // The card remains responsive to screen width while the badge stays fixed
        // Both have the same height (cardHeight) to ensure vertical alignment
        SpaceLicenseCard(
            title = title,
            xpCurrent = xpCurrent,
            xpToNext = xpToNext,
            level = level,
            progress = progress,
            modifier = Modifier
                .weight(1f) // Expand to fill remaining horizontal space (responsive)
                .height(cardHeight) // Fixed card height independent from badge height
        )
    }
}

