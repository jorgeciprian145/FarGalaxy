package com.example.fargalaxy.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.RenderEffect as AndroidRenderEffect
import android.graphics.Shader
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.fargalaxy.R
import com.example.fargalaxy.data.UserDataRepository
import com.example.fargalaxy.data.FlightEnvironmentRepository
import com.example.fargalaxy.data.ShipPerformanceRepository
import com.example.fargalaxy.data.EquipmentRepository
import com.example.fargalaxy.data.EquipmentUsageRepository
import com.example.fargalaxy.model.Ship
import kotlinx.coroutines.delay
import android.media.MediaPlayer
import androidx.compose.ui.platform.LocalContext

/**
 * AnimatedNumberCounter composable - displays a number that animates from old value to new value.
 * 
 * @param targetValue The target value to animate to
 * @param modifier Modifier for the text
 * @param fontSize Font size for the number
 * @param fontWeight Font weight for the number
 * @param color Color for the number
 */
@Composable
fun AnimatedNumberCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = 20.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = Color.White
) {
    var previousValue by remember { mutableStateOf(targetValue) }
    
    // Animate from previousValue to targetValue
    val animatedValue by animateFloatAsState(
        targetValue = targetValue.toFloat(),
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "number_counter"
    )
    
    // Update previous value when target changes
    LaunchedEffect(targetValue) {
        previousValue = targetValue
    }
    
    Text(
        text = animatedValue.toInt().toString(),
        fontFamily = Exo2,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = color,
        modifier = modifier
    )
}

/**
 * Helper function to calculate level from XP (for animation purposes).
 * This is a copy of the logic from UserDataRepository but works with any XP value.
 */
private fun calculateLevelFromXP(xp: Int): Int {
    var level = 1
    var totalRequired = 0
    
    while (totalRequired <= xp) {
        level++
        val xpForNextLevel = com.example.fargalaxy.data.UserDataRepository.getXPRequiredForLevel(level - 1)
        totalRequired += xpForNextLevel
        
        // Safety check to prevent infinite loop
        if (level > 100) break
    }
    
    return level - 1
}

/**
 * RewardsScreen composable - displays rewards after completing a travel session.
 * 
 * @param travelMinutes The number of minutes the travel lasted (0 for test mode)
 * @param penaltyCount The number of penalties suffered during travel
 * @param onContinueClick Callback when continue button is clicked
 * @param modifier Modifier for the screen
 */
@Composable
fun RewardsScreen(
    travelMinutes: Int,
    penaltyCount: Int = 0,
    equippedItemAtTravelTime: String? = null,
    hasUnstableCargoPenalty: Boolean = false,
    onContinueClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    currentShip: Ship
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp }
    
    // Use equipment info captured at travel time (before consumption)
    val equippedItem = equippedItemAtTravelTime
    val isEmergencyModulatorUsed = com.example.fargalaxy.data.EquipmentUsageRepository.isEmergencyModulatorUsed()
    
    // Adjust penalty count: if emergency modulator was used, the first penalty should be ignored
    // So if emergency modulator was used and penaltyCount is 0, it means no actual penalties occurred
    val adjustedPenaltyCount = if (equippedItem == "emergency_modulator" && isEmergencyModulatorUsed && penaltyCount == 0) {
        0 // Emergency modulator was used, no actual penalties
    } else {
        penaltyCount
    }
    
    // Calculate earned rewards
    // If travelMinutes is 0, it's test mode (10 seconds), so calculate based on 10 seconds
    val actualMinutes = if (travelMinutes == 0) {
        10f / 60f // 10 seconds = 0.167 minutes
    } else {
        travelMinutes.toFloat()
    }
    val baseEarnedXP = (actualMinutes * 10).toInt() // 1 min = 10 XP
    val baseEarnedCredits = (actualMinutes * 100).toInt() // 1 min = 100 credits
    
    // Calculate penalty percentage (5% per penalty)
    val penaltyPercentage = adjustedPenaltyCount * 5
    val isFlawlessTravel = adjustedPenaltyCount == 0
    
    // Flight Environment bonus: +10% if current ship's profile matches today's environment
    val spaceConditionsPercentage =
        FlightEnvironmentRepository.getEnvironmentBonusPercent(currentShip.shipProfile)
    
    // Ship performance bonus: 0–20% based on average of acceleration, speed, and stability
    val shipPerformancePercentage =
        ShipPerformanceRepository.getPerformanceBonusPercent(currentShip.id)
    
    // Equipment effects
    var equipmentXPModifier = 0f
    var equipmentCreditsModifier = 0f
    
    when (equippedItem) {
        "unstable_cargo" -> {
            if (hasUnstableCargoPenalty) {
                // Lose all XP and credits if penalty occurred
                equipmentXPModifier = -1f // -100%
                equipmentCreditsModifier = -1f // -100%
            } else if (isFlawlessTravel) {
                // +20% bonus if flawless
                equipmentXPModifier = 0.2f
                equipmentCreditsModifier = 0.2f
            }
        }
        "experimental_fuel" -> {
            // -10% reduction to XP and credits
            equipmentXPModifier = -0.1f
            equipmentCreditsModifier = -0.1f
        }
    }
    
    // Calculate total percentage modifier
    // 1) Flawless travel: +5% if obtained, or penalties: -5% per penalty
    // 2) Flight Environment bonus: +10% if profile matches
    // 3) Ship performance bonus: 0–20% based on stats
    val penaltyOrFlawlessPercentage = if (isFlawlessTravel) {
        5 // +5% bonus for flawless travel
    } else {
        -penaltyPercentage // Penalties reduce earnings
    }
    val totalPercentage =
        penaltyOrFlawlessPercentage + spaceConditionsPercentage + shipPerformancePercentage
    
    // Calculate base rewards after penalties/bonuses and space conditions
    val baseRewardsXP = if (totalPercentage != 0) {
        (baseEarnedXP * (1f + totalPercentage / 100f)).toInt()
    } else {
        baseEarnedXP
    }
    val baseRewardsCredits = if (totalPercentage != 0) {
        (baseEarnedCredits * (1f + totalPercentage / 100f)).toInt()
    } else {
        baseEarnedCredits
    }
    
    // Apply equipment effects
    val earnedXP = if (equipmentXPModifier == -1f) {
        // Unstable cargo penalty: lose all
        0
    } else {
        (baseRewardsXP * (1f + equipmentXPModifier)).toInt()
    }
    val earnedCredits = if (equipmentCreditsModifier == -1f) {
        // Unstable cargo penalty: lose all
        0
    } else {
        (baseRewardsCredits * (1f + equipmentCreditsModifier)).toInt()
    }
    
    // Get current user data
    val currentXP = UserDataRepository.userXP
    val currentCredits = UserDataRepository.userCredits
    
    // Calculate new values (base values before penalties/space conditions are applied)
    val newXP = currentXP + earnedXP
    val newCredits = currentCredits + earnedCredits
    
    // Animation states
    var startXPAnimation by remember { mutableStateOf(false) }
    var startCreditsAnimation by remember { mutableStateOf(false) }
    
    // Animated XP value (starts at current, animates to new)
    var animatedXP by remember { mutableStateOf(currentXP) }
    
    // Animated credits value (starts at current, animates to new)
    var animatedCredits by remember { mutableStateOf(currentCredits) }
    
    // Visibility states for progressive content appearance
    var showPenaltySection by remember { mutableStateOf(false) } // Penalty/flawless + small label
    var showSpaceConditionsSection by remember { mutableStateOf(false) } // Space conditions + small label
    var showXPEarned by remember { mutableStateOf(false) } // XP earned (no small label)
    var showCreditsEarned by remember { mutableStateOf(false) } // Credits earned (no small label)
    var showLevelStatusSection by remember { mutableStateOf(false) } // Content up to level status card
    var showRemainingContent by remember { mutableStateOf(false) } // Credits section at bottom
    
    // "X mins of focus" animation states (slide from top + fade in)
    var focusTimeOffset by remember { mutableStateOf((-50).dp) } // Start off-screen top
    var focusTimeAlpha by remember { mutableStateOf(0f) } // Start invisible
    
    // Slide-in from left + fade-in animation states (for labels under "X mins of focus")
    var penaltySectionOffset by remember { mutableStateOf((-100).dp) } // Start off-screen left
    var penaltySectionAlpha by remember { mutableStateOf(0f) } // Start invisible
    var spaceConditionsSectionOffset by remember { mutableStateOf((-100).dp) }
    var spaceConditionsSectionAlpha by remember { mutableStateOf(0f) }
    var xpEarnedOffset by remember { mutableStateOf((-100).dp) }
    var xpEarnedAlpha by remember { mutableStateOf(0f) }
    var creditsEarnedOffset by remember { mutableStateOf((-100).dp) }
    var creditsEarnedAlpha by remember { mutableStateOf(0f) }
    
    // Counter animation states for XP and Credits earned
    var xpEarnedCounter by remember { mutableStateOf(0) } // Counter for XP earned (starts at 0)
    var creditsEarnedCounter by remember { mutableStateOf(0) } // Counter for Credits earned (starts at 0)
    var startXPEarnedCounter by remember { mutableStateOf(false) } // Trigger for XP counter
    var startCreditsEarnedCounter by remember { mutableStateOf(false) } // Trigger for Credits counter
    
    // LevelStatusCard slide out animation states
    var levelStatusCardOffset by remember { mutableStateOf(0.dp) } // Start at center, slide out to left
    var showLevelStatusCard by remember { mutableStateOf(false) } // Visibility control
    var levelStatusSectionAlpha by remember { mutableStateOf(0f) } // Fade in for level status section
    
    // Credits section slide in animation states
    var creditsSectionOffset by remember { mutableStateOf(1000.dp) } // Start off-screen right, slide to center (0.dp)
    var showCreditsSection by remember { mutableStateOf(false) } // Visibility control
    
    // Penalty/Flawless travel animation states
    var showFlawlessTravel by remember { mutableStateOf(false) }
    var penaltyLabelAlpha by remember { mutableStateOf(1f) } // Fade out/in instead of slide
    var flawlessTravelAlpha by remember { mutableStateOf(0f) }
    
    // Main label offset animation states removed - using natural Row centering instead
    
    // Fade-in animation states for 14sp labels (now appearing with main labels)
    var penaltyPercentageAlpha by remember { mutableStateOf(0f) } // Fade in with main label
    var spaceConditionsAlpha by remember { mutableStateOf(0f) } // Fade in with main label
    
    // Counter animation removed - XP and Credits show final values immediately
    
    // Get context for MediaPlayer
    val context = LocalContext.current
    
    // Record completed session when RewardsScreen appears (session completed successfully)
    // Note: Focus time is already added in GalaxyScreen when travel ends, so we don't add it again here
    LaunchedEffect(Unit) {
        // Record the completed session (updates streak, sessions count, etc.)
        UserDataRepository.recordCompletedSession()
    }
    
    // Sound playback for XP animation
    // Sound effects are now integrated into the animation sequence above
    
    // New animation sequence:
    // 0s: Screen appears - "X mins of focus" slides in from top + fades in
    // 1s: Penalty/flawless section appears (slide in from left + fade in) - main label + small label together
    //     If flawless: play ping sound
    // 0.5s after penalty section: Space conditions section appears (slide in from left + fade in) - main label + small label together
    // 1s after space conditions: XP earned appears (slide in from left + fade in, final value, no small label)
    // 1s after XP earned: Credits earned appears (slide in from left + fade in, final value, no small label)
    // 1s after credits earned: Level status section appears (fade in)
    // 1s after level status: XP count animation with sound
    // 1s after XP animation ends: Remaining content appears (fade in)
    // 1s after remaining content: Credits count animation with sound
    
    // "X mins of focus" slide in from top + fade in (immediately when screen appears)
    LaunchedEffect(Unit) {
        focusTimeOffset = 0.dp // Slide to final position
        focusTimeAlpha = 1f // Fade in
    }
    
    // Penalty/flawless section slide in from left + fade in (1s after screen appears)
    LaunchedEffect(Unit) {
        delay(1000)
        
        if (isFlawlessTravel) {
            // Play ping sound for flawless travel
            try {
                val pingPlayer = MediaPlayer.create(context, R.raw.ping)
                pingPlayer?.let { player ->
                    player.setVolume(1f, 1f)
                    player.start()
                    // Don't wait for sound to finish, let it play in background
                }
            } catch (e: Exception) {
                // Ignore sound errors
            }
            
            // Show flawless travel label
            showFlawlessTravel = true
            flawlessTravelAlpha = 1f
        }
        
        // Show penalty section and slide in from left + fade in (main label + small label together)
        showPenaltySection = true
        penaltySectionOffset = 0.dp // Slide to final position
        penaltySectionAlpha = 1f // Fade in
        penaltyPercentageAlpha = 1f // Small label appears with main label
    }
    
    // Space conditions section slide in from left + fade in (0.5s after penalty section animation ends)
    LaunchedEffect(penaltySectionOffset) {
        if (penaltySectionOffset == 0.dp) {
            delay(600 + 500) // Wait for slide in animation (600ms) + 0.5s delay
            showSpaceConditionsSection = true
            spaceConditionsSectionOffset = 0.dp // Slide to final position
            spaceConditionsSectionAlpha = 1f // Fade in
            spaceConditionsAlpha = 1f // Small label appears with main label
        }
    }
    
    // XP earned appears with slide in from left + fade in (1s after space conditions section)
    // Counter animation starts simultaneously with slide/fade animation
    LaunchedEffect(spaceConditionsSectionOffset) {
        if (spaceConditionsSectionOffset == 0.dp) {
            delay(600 + 1000) // Wait for slide in animation (600ms) + 1s delay
            showXPEarned = true
            xpEarnedOffset = 0.dp // Slide to final position
            xpEarnedAlpha = 1f // Fade in
            startXPEarnedCounter = true // Start counter animation
        }
    }
    
    // XP earned counter animation (1 second from 0 to earnedXP)
    LaunchedEffect(startXPEarnedCounter) {
        if (startXPEarnedCounter) {
            val startValue = 0
            val endValue = earnedXP
            val duration = 1000L // 1 second
            val steps = 30
            val stepDelay = duration / steps
            val stepValue = (endValue - startValue) / steps
            
            for (i in 0..steps) {
                xpEarnedCounter = (startValue + stepValue * i).toInt()
                delay(stepDelay)
            }
            xpEarnedCounter = earnedXP // Ensure final value
        }
    }
    
    // Credits earned appears with slide in from left + fade in (1s after XP earned)
    // Counter animation starts simultaneously with slide/fade animation
    LaunchedEffect(xpEarnedOffset) {
        if (xpEarnedOffset == 0.dp) {
            delay(600 + 1000) // Wait for slide in animation (600ms) + 1s delay
            showCreditsEarned = true
            creditsEarnedOffset = 0.dp // Slide to final position
            creditsEarnedAlpha = 1f // Fade in
            startCreditsEarnedCounter = true // Start counter animation
        }
    }
    
    // Credits earned counter animation (1 second from 0 to earnedCredits)
    LaunchedEffect(startCreditsEarnedCounter) {
        if (startCreditsEarnedCounter) {
            val startValue = 0
            val endValue = earnedCredits
            val duration = 1000L // 1 second
            val steps = 30
            val stepDelay = duration / steps
            val stepValue = (endValue - startValue) / steps
            
            for (i in 0..steps) {
                creditsEarnedCounter = (startValue + stepValue * i).toInt()
                delay(stepDelay)
            }
            creditsEarnedCounter = earnedCredits // Ensure final value
        }
    }
    
    // Level status section fade in (1s after credits earned)
    LaunchedEffect(creditsEarnedOffset) {
        if (creditsEarnedOffset == 0.dp) {
            delay(600 + 1000) // Wait for slide in animation (600ms) + 1s delay
            showLevelStatusCard = true
            levelStatusSectionAlpha = 1f
        }
    }
    
    // XP count animation (1s after level status section appears)
    LaunchedEffect(levelStatusSectionAlpha) {
        if (levelStatusSectionAlpha == 1f) {
            delay(1000 + 1000) // Wait for fade in (1s) + 1s delay
            startXPAnimation = true
            
            // Play XP sound effect (start with animation)
            val xpSoundPlayer = try {
                MediaPlayer.create(context, R.raw.charge)?.apply {
                    isLooping = true
                    setVolume(1f, 1f)
                    start()
                }
            } catch (e: Exception) {
                null
            }
            
            // Animate XP from currentXP to newXP over 500ms
            val startValue = currentXP.toFloat()
            val endValue = newXP.toFloat()
            val duration = 500L
            val steps = 30
            val stepDelay = duration / steps
            val stepValue = (endValue - startValue) / steps
            
            for (i in 0..steps) {
                animatedXP = (startValue + stepValue * i).toInt()
                delay(stepDelay)
            }
            animatedXP = newXP
            // Update global state
            UserDataRepository.userXP = newXP
            
            // Stop XP sound effect
            try {
                xpSoundPlayer?.let { player ->
                    if (player.isPlaying) {
                        player.stop()
                    }
                    player.release()
                }
            } catch (e: Exception) {
                // Ignore sound errors
            }
            
            // After XP animation ends: Wait 1.5s, then LevelStatusCard slides out left, Credits section slides in from right
            delay(500 + 1500) // Wait for XP animation to complete (500ms) + 1.5s delay
            
            // Slide out LevelStatusCard to the left
            levelStatusCardOffset = (-1000).dp
            
            // Slide in Credits section from the right (simultaneously)
            showCreditsSection = true
            creditsSectionOffset = 0.dp
            
            // Wait for slide animations to complete (600ms), then wait 1s before starting credits count
            delay(600 + 1000) // Slide animation (600ms) + 1s delay
            startCreditsAnimation = true
        }
    }
    
    // Credits count animation (triggered when startCreditsAnimation becomes true)
    LaunchedEffect(startCreditsAnimation) {
        if (startCreditsAnimation) {
            // Play credits sound effect (start with animation)
            val creditsSoundPlayer = try {
                MediaPlayer.create(context, R.raw.coins)?.apply {
                    isLooping = true
                    setVolume(1f, 1f)
                    start()
                }
            } catch (e: Exception) {
                null
            }
            
            // Animate credits from currentCredits to newCredits over 500ms
            val creditsStartValue = currentCredits.toFloat()
            val creditsEndValue = newCredits.toFloat()
            val creditsDuration = 500L
            val creditsSteps = 30
            val creditsStepDelay = creditsDuration / creditsSteps
            val creditsStepValue = (creditsEndValue - creditsStartValue) / creditsSteps
            
            for (i in 0..creditsSteps) {
                animatedCredits = (creditsStartValue + creditsStepValue * i).toInt()
                delay(creditsStepDelay)
            }
            animatedCredits = newCredits
            // Update global state
            UserDataRepository.userCredits = newCredits
            
            // Stop credits sound effect
            try {
                creditsSoundPlayer?.let { player ->
                    if (player.isPlaying) {
                        player.stop()
                    }
                    player.release()
                }
            } catch (e: Exception) {
                // Ignore sound errors
            }
        }
    }
    
    val scrollState = rememberScrollState()
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Blur and overlay: Same as modal (96% opacity)
        // Clickable to block all interactions behind the screen
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
                .clickable(enabled = false) { } // Block all clicks
        )
        
        // Scrollable content (with bottom padding for continue button and gradient)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp) // 16dp side padding
                .padding(bottom = 60.dp), // Extra padding for scrolling and fixed button area
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top padding: 48dp (JSON positioned 48dp from top)
            Spacer(modifier = Modifier.height(48.dp))
            
            // JSON Animation: 50% of screen width
            val jsonComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.modalcheck))
            Box(
                modifier = Modifier
                    .width(screenWidth * 0.5f)
            ) {
                LottieAnimation(
                    composition = jsonComposition,
                    iterations = 1, // Play only once
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentScale = ContentScale.Fit
                )
            }
            
            // 24dp spacing below JSON
            Spacer(modifier = Modifier.height(24.dp))
            
            // Title: "Rewards" - bold, 28sp
            Text(
                text = "Rewards",
                fontFamily = Exo2,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            // 4dp spacing between title and label
            Spacer(modifier = Modifier.height(4.dp))
            
            // Label: "Your focus has paid off" - regular, 16sp
            Text(
                text = "Your focus has paid off",
                fontFamily = Exo2,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            // 24dp spacing below label
            Spacer(modifier = Modifier.height(24.dp))
            
            // Divider
            HorizontalDivider()
            
            // 24dp spacing below divider
            Spacer(modifier = Modifier.height(24.dp))
            
            // Session summary section - center aligned
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // "X mins of focus" - 20sp, bold - appears with slide in from top + fade in
                // For test mode (0 minutes), show "0 mins" or handle differently
                val focusTimeText = if (travelMinutes == 0) {
                    "0 mins of focus" // Test mode: 10 seconds
                } else {
                    "$travelMinutes mins of focus"
                }
                
                val focusTimeOffsetAnimated by animateDpAsState(
                    targetValue = focusTimeOffset,
                    animationSpec = tween(durationMillis = 600),
                    label = "focus_time_slide"
                )
                val focusTimeAlphaAnimated by animateFloatAsState(
                    targetValue = focusTimeAlpha,
                    animationSpec = tween(durationMillis = 600),
                    label = "focus_time_fade"
                )
                
                // Always show the label (it animates in)
                Text(
                    text = focusTimeText,
                    fontFamily = Exo2,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .offset(y = focusTimeOffsetAnimated)
                        .alpha(focusTimeAlphaAnimated)
                )
                
                // 8dp spacing
                Spacer(modifier = Modifier.height(8.dp))
                
                // Penalty counter / Flawless travel row - appears with slide in from left + fade in
                val penaltySectionOffsetAnimated by animateDpAsState(
                    targetValue = penaltySectionOffset,
                    animationSpec = tween(durationMillis = 600),
                    label = "penalty_section_slide"
                )
                val penaltySectionAlphaAnimated by animateFloatAsState(
                    targetValue = penaltySectionAlpha,
                    animationSpec = tween(durationMillis = 600),
                    label = "penalty_section_fade"
                )
                
                if (showPenaltySection) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(x = penaltySectionOffsetAnimated)
                            .alpha(penaltySectionAlphaAnimated),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Penalty counter or Flawless travel label
                        if (!showFlawlessTravel) {
                            Text(
                                text = "$adjustedPenaltyCount penalties",
                                fontFamily = Exo2,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Flawless travel",
                                fontFamily = Exo2,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White
                            )
                        }
                        
                        // Penalty percentage label (4dp to the right) - fades in with main label
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        val penaltyPercentageAlphaAnimated by animateFloatAsState(
                            targetValue = penaltyPercentageAlpha,
                            animationSpec = tween(durationMillis = 1000),
                            label = "penalty_percentage_fade"
                        )
                        
                        val percentageText = if (isFlawlessTravel) {
                            "(+5%)"
                        } else {
                            "(-$penaltyPercentage%)"
                        }
                        Text(
                            text = percentageText,
                            fontFamily = Exo2,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White,
                            modifier = Modifier.alpha(penaltyPercentageAlphaAnimated)
                        )
                    }
                }
                
                // 0dp spacing (Space conditions directly below)
                Spacer(modifier = Modifier.height(0.dp))
                
                // Space conditions row - appears with slide in from left + fade in
                val spaceConditionsSectionOffsetAnimated by animateDpAsState(
                    targetValue = spaceConditionsSectionOffset,
                    animationSpec = tween(durationMillis = 600),
                    label = "space_conditions_section_slide"
                )
                val spaceConditionsSectionAlphaAnimated by animateFloatAsState(
                    targetValue = spaceConditionsSectionAlpha,
                    animationSpec = tween(durationMillis = 600),
                    label = "space_conditions_section_fade"
                )
                
                if (showSpaceConditionsSection) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(x = spaceConditionsSectionOffsetAnimated)
                            .alpha(spaceConditionsSectionAlphaAnimated),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Flight Environment",
                            fontFamily = Exo2,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White
                        )
                        
                        // Space conditions percentage label (4dp to the right) - fades in with main label
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        val spaceConditionsAlphaAnimated by animateFloatAsState(
                            targetValue = spaceConditionsAlpha,
                            animationSpec = tween(durationMillis = 1000),
                            label = "space_conditions_fade"
                        )
                        
                        Text(
                            text = "(${if (spaceConditionsPercentage >= 0) "+" else ""}$spaceConditionsPercentage%)",
                            fontFamily = Exo2,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White,
                            modifier = Modifier.alpha(spaceConditionsAlphaAnimated)
                        )
                    }
                    
                    // Ship performance row: same style, appears directly under Flight Environment
                    Spacer(modifier = Modifier.height(0.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(x = spaceConditionsSectionOffsetAnimated)
                            .alpha(spaceConditionsSectionAlphaAnimated),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ship performance",
                            fontFamily = Exo2,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        val shipPerformanceAlphaAnimated by animateFloatAsState(
                            targetValue = spaceConditionsAlpha,
                            animationSpec = tween(durationMillis = 1000),
                            label = "ship_performance_fade"
                        )
                        
                        Text(
                            text = "(${if (shipPerformancePercentage >= 0) "+" else ""}$shipPerformancePercentage%)",
                            fontFamily = Exo2,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White,
                            modifier = Modifier.alpha(shipPerformanceAlphaAnimated)
                        )
                    }
                    
                    // Equipment labels: appear directly under Ship performance (0dp spacing)
                    // Unstable cargo: show if equipped and travel was flawless
                    if (equippedItem == "unstable_cargo" && isFlawlessTravel && !hasUnstableCargoPenalty) {
                        Spacer(modifier = Modifier.height(0.dp))
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(x = spaceConditionsSectionOffsetAnimated)
                                .alpha(spaceConditionsSectionAlphaAnimated),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Unstable cargo",
                                fontFamily = Exo2,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White
                            )
                            
                            Spacer(modifier = Modifier.width(4.dp))
                            
                            val equipmentAlphaAnimated by animateFloatAsState(
                                targetValue = spaceConditionsAlpha,
                                animationSpec = tween(durationMillis = 1000),
                                label = "equipment_fade"
                            )
                            
                            Text(
                                text = "(+20%)",
                                fontFamily = Exo2,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White,
                                modifier = Modifier.alpha(equipmentAlphaAnimated)
                            )
                        }
                    }
                    
                    // Experimental fuel: show if equipped
                    if (equippedItem == "experimental_fuel") {
                        Spacer(modifier = Modifier.height(0.dp))
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(x = spaceConditionsSectionOffsetAnimated)
                                .alpha(spaceConditionsSectionAlphaAnimated),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Experimental fuel",
                                fontFamily = Exo2,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White
                            )
                            
                            Spacer(modifier = Modifier.width(4.dp))
                            
                            val equipmentAlphaAnimated by animateFloatAsState(
                                targetValue = spaceConditionsAlpha,
                                animationSpec = tween(durationMillis = 1000),
                                label = "equipment_fade"
                            )
                            
                            Text(
                                text = "(-10%)",
                                fontFamily = Exo2,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White,
                                modifier = Modifier.alpha(equipmentAlphaAnimated)
                            )
                        }
                    }
                }
                
                // 8dp spacing (between performance section and XP earned)
                Spacer(modifier = Modifier.height(8.dp))
                
                // XP earned row - appears with slide in from left + fade in (with counter animation, no small label)
                val xpEarnedOffsetAnimated by animateDpAsState(
                    targetValue = xpEarnedOffset,
                    animationSpec = tween(durationMillis = 600),
                    label = "xp_earned_slide"
                )
                val xpEarnedAlphaAnimated by animateFloatAsState(
                    targetValue = xpEarnedAlpha,
                    animationSpec = tween(durationMillis = 600),
                    label = "xp_earned_fade"
                )
                
                if (showXPEarned) {
                    Text(
                        text = "+$xpEarnedCounter XP earned",
                        fontFamily = Exo2,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(x = xpEarnedOffsetAnimated)
                            .alpha(xpEarnedAlphaAnimated),
                        textAlign = TextAlign.Center
                    )
                }
                
                // 0dp spacing (between XP and Credits earned)
                Spacer(modifier = Modifier.height(0.dp))
                
                // Credits earned row - appears with slide in from left + fade in (with counter animation, no small label)
                val creditsEarnedOffsetAnimated by animateDpAsState(
                    targetValue = creditsEarnedOffset,
                    animationSpec = tween(durationMillis = 600),
                    label = "credits_earned_slide"
                )
                val creditsEarnedAlphaAnimated by animateFloatAsState(
                    targetValue = creditsEarnedAlpha,
                    animationSpec = tween(durationMillis = 600),
                    label = "credits_earned_fade"
                )
                
                if (showCreditsEarned) {
                    Text(
                        text = "+$creditsEarnedCounter credits earned",
                        fontFamily = Exo2,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(x = creditsEarnedOffsetAnimated)
                            .alpha(creditsEarnedAlphaAnimated),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Divider and spacing before level status/credits section
            // 24dp spacing
            Spacer(modifier = Modifier.height(24.dp))
            
            // Divider
            HorizontalDivider()
            
            // 24dp spacing
            Spacer(modifier = Modifier.height(24.dp))
            
            // Container for LevelStatusCard and Credits section (same position, overlapping)
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // LevelStatusCard - appears with fade in, then slides out to left
                val levelStatusSectionAlphaAnimated by animateFloatAsState(
                    targetValue = levelStatusSectionAlpha,
                    animationSpec = tween(durationMillis = 600),
                    label = "level_status_section_fade"
                )
                val levelStatusCardOffsetAnimated by animateDpAsState(
                    targetValue = levelStatusCardOffset,
                    animationSpec = tween(durationMillis = 600),
                    label = "level_status_card_slide_out"
                )
                
                if (showLevelStatusCard) {
                    // Calculate level, progress, and xpToNext based on animated XP
                    val currentLevel = calculateLevelFromXP(animatedXP)
                    val xpForCurrentLevel = UserDataRepository.getTotalXPForLevel(currentLevel)
                    val xpInCurrentLevel = animatedXP - xpForCurrentLevel
                    val xpRequiredForNext = UserDataRepository.getXPRequiredForLevel(currentLevel)
                    val progress = (xpInCurrentLevel.toFloat() / xpRequiredForNext).coerceIn(0f, 1f)
                    val xpToNext = (xpRequiredForNext - xpInCurrentLevel).coerceAtLeast(0)
                    
                    LevelStatusCard(
                        title = "Standard space license",
                        xpCurrent = animatedXP,
                        xpToNext = xpToNext,
                        level = currentLevel,
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(levelStatusSectionAlphaAnimated)
                            .offset(x = levelStatusCardOffsetAnimated)
                    )
                }
                
                // Credits section - slides in from right, replaces LevelStatusCard
                val creditsSectionOffsetAnimated by animateDpAsState(
                    targetValue = creditsSectionOffset,
                    animationSpec = tween(durationMillis = 600),
                    label = "credits_section_slide_in"
                )
                
                if (showCreditsSection) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(x = creditsSectionOffsetAnimated),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        // Top row: Credits icon + credits amount label
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp), // 8dp spacing between icon and label
                            verticalAlignment = Alignment.CenterVertically // Vertically align icon and label
                        ) {
                            // Credits icon: 32dp width, maintaining aspect ratio
                            Image(
                                painter = painterResource(id = R.drawable.creditsicon),
                                contentDescription = "Credits",
                                modifier = Modifier.width(32.dp), // 32dp width, maintaining aspect ratio
                                contentScale = ContentScale.Fit
                            )
                            
                            // Credits amount label: Bold, 32sp
                            AnimatedNumberCounter(
                                targetValue = if (startCreditsAnimation) animatedCredits else currentCredits,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        
                        // Bottom row: "Interstellar Credits" label only
                        Text(
                            text = "Interstellar Credits",
                            fontFamily = Exo2,
                            fontWeight = FontWeight.W400,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
        
        // Bottom fixed container: Contains gradient and button container
        // Both stay fixed at bottom when scrolling
        // navigationBarsPadding() ensures button container sits right above navigation bar with 0 spacing
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding() // Positions content above navigation bar (0 spacing between them)
        ) {
            // Gradient overlay container: 48dp height, full width
            // Gradient from 0% opacity black at top to 100% opacity black at bottom
            // Positioned right above the button container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x00000000), // 0% opacity at top
                                Color(0xFF000000)  // 100% opacity at bottom
                            )
                        )
                    )
            )
            
            // Button container: Full width, black background
            // Contains single full-width button with 16dp padding on all sides
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF000000)) // Black background at 100% opacity
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(80.dp))
                        .background(Color(0xFFFFFFFF))
                        .clickable(onClick = onContinueClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CONTINUE",
                        fontFamily = Exo2,
                        fontSize = 24.sp,
                        lineHeight = 24.sp,
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
}

/**
 * HorizontalDivider composable - displays a horizontal white line with side padding.
 * Same as the one in VaultScreen.
 */
@Composable
private fun HorizontalDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 16.dp)
            .background(Color(0x66FFFFFF)) // White with 40% opacity
    )
}
