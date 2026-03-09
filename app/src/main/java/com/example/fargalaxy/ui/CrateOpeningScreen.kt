package com.example.fargalaxy.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.fargalaxy.R
import com.example.fargalaxy.data.CrateOpenResult
import com.example.fargalaxy.data.CrateReward
import com.example.fargalaxy.data.CrateType
import com.example.fargalaxy.data.ShipRepository
import com.example.fargalaxy.model.ShipRarity
import android.graphics.RenderEffect as AndroidRenderEffect
import android.graphics.Shader

/**
 * Overlay composable that runs the crate opening sequence and shows the obtained reward.
 *
 * Sequence:
 * - Fade in crate JSON (80% of screen width), wait 2 seconds, then play once.
 * - When animation finishes, fade/scale out the crate.
 * - Fade/scale in reward reveal page, similar to ship unlock flow, showing:
 *   - Reward image (size depends on reward type).
 *   - Simple label for the obtained item.
 * - Tapping anywhere (or the CONTINUE button) calls [onContinue].
 */
@Composable
fun CrateOpeningScreen(
    crateType: CrateType,
    result: CrateOpenResult,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp }

    // Phase control
    var showCrateAnim by remember { mutableStateOf(true) }
    var crateAlpha by remember { mutableStateOf(0f) }
    var crateScale by remember { mutableStateOf(0.9f) }

    var showReward by remember { mutableStateOf(false) }
    var rewardAlpha by remember { mutableStateOf(0f) }
    var rewardScale by remember { mutableStateOf(0.9f) }

    // Crate animation JSON selection
    val crateAnimRes = when (crateType) {
        CrateType.STANDARD -> R.raw.standardcratesequence
        CrateType.ADVANCED -> R.raw.advancedcratesequence
        CrateType.ELITE -> R.raw.elitecratesequence
    }
    val crateComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(crateAnimRes))

    // Animated values
    val crateAlphaAnimated by animateFloatAsState(
        targetValue = crateAlpha,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "crate_alpha"
    )
    val crateScaleAnimated by animateFloatAsState(
        targetValue = crateScale,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "crate_scale"
    )

    val rewardAlphaAnimated by animateFloatAsState(
        targetValue = rewardAlpha,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "reward_alpha"
    )
    val rewardScaleAnimated by animateFloatAsState(
        targetValue = rewardScale,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "reward_scale"
    )

    // Drive the sequence.
    LaunchedEffect(crateComposition) {
        // Fade in crate
        crateAlpha = 1f
        crateScale = 1f

        // Wait 2 seconds before playing
        kotlinx.coroutines.delay(2000)

        // Approximate crate animation duration; play once visually
        // (LottieAnimation itself plays continuously; we control visibility here).
        val approxDuration = 2500L
        kotlinx.coroutines.delay(approxDuration)

        // Fade/scale out crate
        crateAlpha = 0f
        crateScale = 0.8f
        kotlinx.coroutines.delay(250)

        // Switch to reward phase
        showCrateAnim = false
        showReward = true
        rewardAlpha = 1f
        rewardScale = 1f
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Blurred dark background layer
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

        // Foreground content (kept sharp)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = showReward) {
                    // Allow dismiss only once reward is visible
                    onContinue()
                }
        ) {
            // Crate animation phase
            if (showCrateAnim) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(screenWidth * 1.25f) // 125% of screen width
                        .wrapContentHeight()
                        .scale(crateScaleAnimated)
                        .alpha(crateAlphaAnimated)
                ) {
                    LottieAnimation(
                        composition = crateComposition,
                        iterations = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Reward reveal phase
            if (showReward) {
                RewardRevealContent(
                    result = result,
                    rewardAlpha = rewardAlphaAnimated,
                    rewardScale = rewardScaleAnimated,
                    screenWidth = screenWidth,
                    onContinue = onContinue,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun RewardRevealContent(
    result: CrateOpenResult,
    rewardAlpha: Float,
    rewardScale: Float,
    screenWidth: androidx.compose.ui.unit.Dp,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reward = result.reward

    val (title, subtitle, imageRes) = when (reward) {
        is CrateReward.Credits -> {
            Triple(
                "Reward obtained",
                "You received 50 000 credits",
                R.drawable.creditsrender
            )
        }

        is CrateReward.Equipment -> {
            val (label, resId) = getEquipmentLabelAndImage(reward.itemId)
            Triple(
                "Reward obtained",
                label,
                resId
            )
        }

        is CrateReward.ShipCard -> {
            val (cardLabel, resId) = getShipCardLabelAndImage(
                shipId = reward.shipId,
                cardIndex = reward.cardIndex,
                rarity = reward.rarity
            )
            Triple(
                "Shipcard obtained",
                cardLabel,
                resId
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .scale(rewardScale)
            .alpha(rewardAlpha),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Reward image - Make images large and prominent
        // Using BoxWithConstraints to get actual screen width and calculate a very large size
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Calculate a very large width - use the larger of: 90% of screen width or 500dp
            // This ensures images are always large regardless of screen size
            val screenBasedWidth = maxWidth * 0.9f
            val minLargeWidth = 500.dp
            val imageWidth = maxOf(screenBasedWidth, minLargeWidth)
            
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = subtitle,
                modifier = Modifier
                    .width(imageWidth)
                    .wrapContentHeight(),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            fontFamily = Exo2,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = subtitle,
            fontFamily = Exo2,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // CONTINUE button at bottom: Same format as other overlay screens
        // 16dp horizontal padding on sides
        Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(80.dp))
                        .background(Color(0xFFFFFFFF))
                        .clickable(onClick = onContinue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CONTINUE",
                        fontFamily = Exo2,
                        fontSize = 24.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF010102),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = (-2).dp)
                    )
                }
            }
        }
    }

/**
 * Helper to map equipment item IDs to display labels and selection images.
 */
private fun getEquipmentLabelAndImage(itemId: String): Pair<String, Int> {
    return when (itemId) {
        "emergency_modulator" -> "Emergency modulator" to R.drawable.modulatorrender
        "experimental_fuel" -> "Experimental fuel" to R.drawable.fuelrender
        "unstable_cargo" -> "Unstable cargo" to R.drawable.cargorender
        "deep_space_scanner" -> "Deep space scanner" to R.drawable.scannerrender
        else -> "Equipment item" to R.drawable.scannerrender
    }
}

/**
 * Helper to map shipId + cardIndex to a shipcard image.
 * For ships unlocked via cards we follow the convention:
 * - ship16card1..6 (Centurion)
 * - ship17card1..6 (Phoenix)
 * - ship18card1..6 (Dragoon)
 */
private fun getShipCardLabelAndImage(
    shipId: String,
    cardIndex: Int,
    rarity: ShipRarity
): Pair<String, Int> {
    val rarityLabel = when (rarity) {
        ShipRarity.UNCOMMON -> "Uncommon"
        ShipRarity.EPIC -> "Epic"
        ShipRarity.LEGENDARY -> "Legendary"
        else -> "Uncommon"
    }

    val baseLabel = "Obtain all 6 cards to get a $rarityLabel ship"

    val drawableRes = when (shipId to cardIndex) {
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

        else -> R.drawable.ship16card1
    }

    return baseLabel to drawableRes
}

/**
 * Small helper data holder since Kotlin Pair/Triple aren't descriptive enough for local destructuring.
 */
private data class Quad<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

