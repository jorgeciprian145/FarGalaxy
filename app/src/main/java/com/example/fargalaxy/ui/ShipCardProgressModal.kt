package com.example.fargalaxy.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fargalaxy.R
import com.example.fargalaxy.data.ShipCardRepository
import com.example.fargalaxy.model.ShipRarity
import android.graphics.RenderEffect as AndroidRenderEffect
import android.graphics.Shader

/**
 * ShipCardProgressModal composable - displays a 6-part shipcard map and progress text.
 *
 * This follows the same overlay style as the other modals (blur + dark overlay +
 * rounded gradient container with a primary button at the bottom).
 *
 * @param shipId The actual ship ID (e.g., "asn_ag94_centurion", "isc_m450_phoenix", "asn_h99_dragoon")
 * @param rarity The rarity of the ship these cards belong to (UNCOMMON, EPIC, LEGENDARY)
 * @param ownedCards The number of cards currently owned for this ship (0..6)
 * @param onContinueClick Callback when the "CONTINUE" button is clicked
 * @param modifier Modifier for the modal
 */
@Composable
fun ShipCardProgressModal(
    shipId: String,
    rarity: ShipRarity,
    ownedCards: Int,
    onContinueClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Handle back button to close modal
    BackHandler(onBack = onContinueClick)
    val baseColor = getShipCardBaseColor(rarity)
    val strokeColor = baseColor.copy(alpha = 0.32f)
    val fillColor = baseColor.copy(alpha = 0.24f)
    val textColor = baseColor

    val rarityLabel = when (rarity) {
        ShipRarity.UNCOMMON -> "Uncommon"
        ShipRarity.EPIC -> "Epic"
        ShipRarity.LEGENDARY -> "Legendary"
        else -> "Uncommon"
    }

    val clampedOwned = ownedCards.coerceIn(0, 6)

    Box(
        modifier = modifier
            .fillMaxSize()
            // Consume taps so underlying content is not interactive
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { },
        contentAlignment = Alignment.Center
    ) {
        // Blur and dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    renderEffect = AndroidRenderEffect.createBlurEffect(
                        16f,
                        16f,
                        Shader.TileMode.CLAMP
                    ).asComposeRenderEffect()
                }
                .background(Color.Black.copy(alpha = 0.96f))
        )

        // Main content: centered map + text
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Shipcard map: 2 rows x 3 columns of 100x83dp rectangles with numbers 1..6
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    for (index in 1..3) {
                        ShipCardMapCell(
                            shipId = shipId,
                            index = index,
                            textColor = textColor,
                            strokeColor = strokeColor,
                            fillColor = fillColor
                        )
                    }
                }
                Row {
                    for (index in 4..6) {
                        ShipCardMapCell(
                            shipId = shipId,
                            index = index,
                            textColor = textColor,
                            strokeColor = strokeColor,
                            fillColor = fillColor
                        )
                    }
                }
            }

            // 16dp spacing below the map
            Spacer(modifier = Modifier.height(16.dp))

            // Progress label: "X/6 {rarity} shipcards unlocked"
            Text(
                text = "$clampedOwned/6 $rarityLabel shipcards unlocked",
                fontFamily = Exo2,
                fontSize = 28.sp,
                lineHeight = 33.6.sp, // 120% of 28sp
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            // 16dp spacing below progress label
            Spacer(modifier = Modifier.height(16.dp))

            // Helper label: "Obtain all cards to unlock a {rarity} ship"
            Text(
                text = "Obtain all cards to unlock a $rarityLabel ship",
                fontFamily = Exo2,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        // CONTINUE button anchored to bottom of the screen (same pattern as other pages)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 64.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(80.dp))
                .background(Color(0xFFFFFFFF))
                .clickable(onClick = onContinueClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "CONTINUE",
                fontFamily = Exo2,
                fontSize = 16.sp,
                fontWeight = FontWeight.W400,
                color = Color(0xFF010102),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Single cell in the 2x3 shipcard map.
 * Shows the card image if owned, otherwise shows the number.
 */
@Composable
private fun ShipCardMapCell(
    shipId: String,
    index: Int,
    textColor: Color,
    strokeColor: Color,
    fillColor: Color
) {
    val hasCard = ShipCardRepository.hasCard(shipId, index)
    
    // Get the card image resource ID if the card is owned
    val cardImageRes = if (hasCard) {
        when (shipId to index) {
            "asn_ag94_centurion" to 1 -> R.drawable.ship16card1
            "asn_ag94_centurion" to 2 -> R.drawable.ship16card2
            "asn_ag94_centurion" to 3 -> R.drawable.ship16card3
            "asn_ag94_centurion" to 4 -> R.drawable.ship16card4
            "asn_ag94_centurion" to 5 -> R.drawable.ship16card5
            "asn_ag94_centurion" to 6 -> R.drawable.ship16card6

            "isc_m450_phoenix" to 1 -> R.drawable.ship17card1
            "isc_m450_phoenix" to 2 -> R.drawable.ship17card2
            "isc_m450_phoenix" to 3 -> R.drawable.ship17card3
            "isc_m450_phoenix" to 4 -> R.drawable.ship17card4
            "isc_m450_phoenix" to 5 -> R.drawable.ship17card5
            "isc_m450_phoenix" to 6 -> R.drawable.ship17card6

            "asn_h99_dragoon" to 1 -> R.drawable.ship18card1
            "asn_h99_dragoon" to 2 -> R.drawable.ship18card2
            "asn_h99_dragoon" to 3 -> R.drawable.ship18card3
            "asn_h99_dragoon" to 4 -> R.drawable.ship18card4
            "asn_h99_dragoon" to 5 -> R.drawable.ship18card5
            "asn_h99_dragoon" to 6 -> R.drawable.ship18card6

            else -> null
        }
    } else {
        null
    }

    Box(
        modifier = Modifier
            .width(100.dp)
            .height(83.dp)
            .background(
                color = fillColor
            )
            .border(
                width = 1.dp,
                color = strokeColor
            ),
        contentAlignment = Alignment.Center
    ) {
        if (hasCard && cardImageRes != null) {
            // Show the card image if owned - fills the entire rectangle
            Image(
                painter = painterResource(id = cardImageRes),
                contentDescription = "Card $index",
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        } else {
            // Show the number if card is not owned
            Text(
                text = index.toString(),
                fontFamily = Exo2,
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Base color for the shipcard map based on rarity.
 */
private fun getShipCardBaseColor(rarity: ShipRarity): Color {
    return when (rarity) {
        ShipRarity.UNCOMMON -> Color(0xFF45E031)
        ShipRarity.EPIC -> Color(0xFFE06BEA)
        ShipRarity.LEGENDARY -> Color(0xFFE7CC52)
        else -> Color(0xFF45E031)
    }
}

