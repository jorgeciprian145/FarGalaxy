package com.example.fargalaxy.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fargalaxy.R

/**
 * ProgressSection composable - displays three progress items with badges, counts, and titles.
 * 
 * Layout structure:
 * - "Progress" title (18sp, bold) at the top
 * - 16dp spacing below title
 * - Single Row containing: ProgressItem – ProgressConnector – ProgressItem – ProgressConnector – ProgressItem
 *   - ProgressItems: Fixed-width items with SVG badge (78dp × 78dp), count label (20sp bold), title (14sp regular)
 *   - ProgressConnectors: Horizontally responsive connectors with centered horizontal white lines (1dp height)
 * 
 * Responsive layout:
 * - ProgressItems have fixed width (content-based) and do NOT use weight()
 * - ProgressConnectors use weight(1f) to expand horizontally and fillMaxHeight() to match item height
 * - This creates a responsive layout where items stay fixed but connectors adapt to screen width
 * - The Row has 24dp horizontal padding and vertically centers all children
 * 
 * Item structure (each ProgressItem):
 * - SVG badge at top (78dp × 78dp fixed size)
 * - 12dp spacing
 * - Count label (20sp, bold) - dynamic count text on top (e.g., "1/10")
 * - 8dp spacing
 * - Title label (14sp, regular) - static title text below (e.g., "Starships")
 * 
 * Connector structure (each ProgressConnector):
 * - Box with weight(1f) to expand horizontally between fixed-width items
 * - fillMaxHeight() to match ProgressItem height
 * - Horizontal white line (1dp height, full width) vertically centered inside
 * - The line visually connects the centers of adjacent diamond icons
 * 
 * @param starshipsCount Current count of starships (e.g., "1/10")
 * @param locationsCount Current count of locations (e.g., "1/30")
 * @param collectiblesCount Current count of collectibles (e.g., "1/30")
 * @param showTitle Whether to show the "Discoveries" title (default true, set to false if title is handled externally)
 * @param onStarshipsClick Callback when the starships item is clicked
 * @param onLocationsClick Callback when the locations item is clicked
 * @param modifier Modifier for the entire ProgressSection
 */
@Composable
fun ProgressSection(
    starshipsCount: String = "1/10",
    locationsCount: String = "1/30",
    collectiblesCount: String = "1/30",
    showTitle: Boolean = true,
    onStarshipsClick: () -> Unit = {},
    onLocationsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = if (showTitle) {
            Arrangement.spacedBy(16.dp) // 16dp spacing between title and items row
        } else {
            Arrangement.spacedBy(0.dp) // No spacing if title is hidden
        }
    ) {
        // "Discoveries" title: 18sp, bold, Exo2 font with 24dp horizontal padding to match Row padding
        // Only shown if showTitle is true
        if (showTitle) {
            Text(
                text = "Discoveries",
                fontFamily = Exo2,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFFFFFFFF),
                modifier = Modifier.padding(horizontal = 24.dp) // 24dp padding to match Row padding below
            )
        }
        
        // Progress items row: ProgressItem – ProgressConnector – ProgressItem – ProgressConnector – ProgressItem
        // This structure creates responsive horizontal connecting lines between fixed-width progress items
        // The connectors expand horizontally using weight(1f) while the items remain fixed size
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp), // 24dp horizontal padding as specified
            horizontalArrangement = Arrangement.spacedBy(4.dp), // 4dp spacing between ProgressItems and ProgressConnectors
            verticalAlignment = Alignment.Top // Align all children to the top edge
        ) {
            // ProgressItem 1: Starships (fixed width, no weight)
            ProgressItem(
                badgeResId = R.drawable.starshipsbadge,
                title = "Starships",
                count = starshipsCount,
                onClick = onStarshipsClick
            )
            
            // ProgressConnector 1: Horizontally responsive connector with centered horizontal line
            // Uses weight(1f) to expand horizontally and fillMaxHeight() to match ProgressItem height
            ProgressConnector(
                modifier = Modifier.weight(1f) // Apply weight at call site in RowScope
            )
            
            // ProgressItem 2: Locations (fixed width, no weight)
            ProgressItem(
                badgeResId = R.drawable.locationsbadge,
                title = "Locations",
                count = locationsCount,
                onClick = onLocationsClick
            )
            
            // ProgressConnector 2: Horizontally responsive connector with centered horizontal line
            ProgressConnector(
                modifier = Modifier.weight(1f) // Apply weight at call site in RowScope
            )
            
            // ProgressItem 3: Collectibles (fixed width, no weight)
            ProgressItem(
                badgeResId = R.drawable.collectiblesbadge,
                title = "Collectibles",
                count = collectiblesCount
            )
        }
    }
}

/**
 * ProgressItem composable - displays a single progress item with badge, count, and title.
 * 
 * This composable shows:
 * - The diamond SVG icon at the top (78dp × 78dp fixed size)
 * - The dynamic count label (e.g., "1/10") in 20sp, bold font
 * - The static title text (e.g., "Starships") in 14sp, regular font below
 * 
 * All ProgressItems have fixed width (based on their content) and the same height,
 * which is driven by the size of the diamond icon plus the spacing and text below it.
 * ProgressItems do NOT use weight() - they remain fixed size in the Row.
 * 
 * Structure:
 * - SVG badge at top (78dp × 78dp fixed size)
 * - 12dp spacing
 * - Count label (20sp, bold) - dynamic count text on top
 * - 8dp spacing
 * - Title label (14sp, regular) - static title text below
 * 
 * @param badgeResId Resource ID for the badge SVG
 * @param title Static title text (e.g., "Starships")
 * @param count Dynamic count text (e.g., "1/10") - numbers only, no category name
 * @param onClick Callback when the item is clicked (optional)
 * @param modifier Modifier for the item
 */
@Composable
private fun ProgressItem(
    badgeResId: Int,
    title: String,
    count: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp) // Manual spacing control
    ) {
        // SVG badge: 78dp × 78dp fixed size
        Image(
            painter = painterResource(id = badgeResId),
            contentDescription = title,
            modifier = Modifier.size(78.dp),
            contentScale = ContentScale.Fit
        )
        
        // 12dp spacing between SVG and count (now first label)
        Spacer(modifier = Modifier.height(12.dp))
        
        // Count label: 20sp, bold, Exo2 font (now first, on top)
        Text(
            text = count,
            fontFamily = Exo2,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFFFFFFFF)
        )
        
        // 8dp spacing between count and title
        Spacer(modifier = Modifier.height(2.dp))
        
        // Title label: 14sp, regular, Exo2 font (now second, below)
        Text(
            text = title,
            fontFamily = Exo2,
            fontWeight = FontWeight.W400, // Regular
            fontSize = 14.sp,
            color = Color(0xFFFFFFFF)
        )
    }
}

/**
 * ProgressConnector composable - displays a horizontal connecting line between progress items.
 * 
 * This composable creates a responsive connector that:
 * - Expands horizontally to fill available space between fixed-width ProgressItems using weight(1f)
 * - Matches the height of ProgressItems using fillMaxHeight() (fills to the tallest item in the Row)
 * - Contains a horizontal white line (1dp height) that is vertically centered
 * - The line expands to fill the connector width using fillMaxWidth()
 * 
 * How it works:
 * - When used inside a Row with verticalAlignment = CenterVertically, the connector container
 *   expands horizontally via weight(1f) and vertically via fillMaxHeight() to match ProgressItem height
 * - Inside the connector container, a Box with fillMaxWidth().height(1.dp) creates the horizontal line
 * - The line is vertically centered using Alignment.CenterVertically in the parent Box
 * 
 * Visual effect:
 * The horizontal line visually connects the centers of the two adjacent diamond icons,
 * and the connector stretches or shrinks depending on the screen width while maintaining
 * the line centered at the icon height.
 * 
 * Layout structure (item – connector – item – connector – item):
 * - ProgressItems have fixed width (content-based), no weight
 * - ProgressConnectors use weight(1f) to expand horizontally between items
 * - This creates a responsive layout where items stay fixed but connectors adapt to screen width
 */
@Composable
private fun ProgressConnector(modifier: Modifier = Modifier) {
    // Container Box that expands horizontally via weight(1f) and matches ProgressItem height via fillMaxHeight()
    // The weight(1f) is applied at the call site in RowScope and passed via modifier parameter
    Box(
        modifier = modifier
            .height(78.dp), // Fixed height matching the diamond icon size (78dp)
        contentAlignment = Alignment.Center // Center the horizontal line both vertically and horizontally
    ) {
        // Horizontal white line: expands to fill connector width, 1dp height, vertically centered
        Box(
            modifier = Modifier
                .fillMaxWidth() // Expand to fill the connector container width
                .height(1.dp) // 1dp height for the connecting line
                .background(Color.White) // White color matching the design
        )
    }
}


