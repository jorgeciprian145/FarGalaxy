package com.example.fargalaxy.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
 * @param onContinueClick Callback when continue button is clicked
 * @param modifier Modifier for the screen
 */
@Composable
fun RewardsScreen(
    travelMinutes: Int,
    onContinueClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp }
    
    // Calculate earned rewards
    // If travelMinutes is 0, it's test mode (10 seconds), so calculate based on 10 seconds
    val actualMinutes = if (travelMinutes == 0) {
        10f / 60f // 10 seconds = 0.167 minutes
    } else {
        travelMinutes.toFloat()
    }
    val earnedXP = (actualMinutes * 10).toInt() // 1 min = 10 XP
    val earnedCredits = (actualMinutes * 100).toInt() // 1 min = 100 credits
    
    // Get current user data
    val currentXP = UserDataRepository.userXP
    val currentCredits = UserDataRepository.userCredits
    
    // Calculate new values
    val newXP = currentXP + earnedXP
    val newCredits = currentCredits + earnedCredits
    
    // Animation states
    var startXPAnimation by remember { mutableStateOf(false) }
    var startCreditsAnimation by remember { mutableStateOf(false) }
    
    // Animated XP value (starts at current, animates to new)
    var animatedXP by remember { mutableStateOf(currentXP) }
    
    // Animated credits value (starts at current, animates to new)
    var animatedCredits by remember { mutableStateOf(currentCredits) }
    
    // Get context for MediaPlayer
    val context = LocalContext.current
    
    // Record completed session when RewardsScreen appears (session completed successfully)
    LaunchedEffect(Unit) {
        UserDataRepository.recordCompletedSession()
    }
    
    // Sound playback for XP animation
    // XP animation: starts at 3s (2000ms + 1000ms delay), duration 500ms, ends at 3.5s
    // Sound: starts exactly when XP animation starts (3s), ends exactly when XP animation ends (3.5s)
    // Total sound duration: 500ms (XP animation duration)
    LaunchedEffect(Unit) {
        val xpMediaPlayer = MediaPlayer.create(context, R.raw.clicking)
        xpMediaPlayer?.let { player ->
            try {
                // Wait until XP animation starts (at 3s)
                delay(3000)
                
                // Prepare player: set looping in case file is shorter than needed
                player.isLooping = true
                player.setVolume(1f, 1f) // Start at full volume
                player.start()
                
                // Play at full volume during XP animation (500ms)
                delay(500)
                
                // Stop and release exactly when XP animation ends
                player.stop()
                player.release()
            } catch (e: Exception) {
                // Handle any errors silently
                try {
                    if (player.isPlaying) {
                        player.stop()
                    }
                    player.release()
                } catch (e2: Exception) {
                    // Ignore release errors
                }
            }
        }
    }
    
    // Sound playback - only for credits animation
    // Credits animation: starts at 4.5s (3.5s + 1s gap), duration 500ms, ends at 5s
    // Sound: starts at 5.2s (0.7s after credits animation starts), ends when credits animation ends (5s)
    // Note: Sound starts after credits animation has already started
    // Total sound duration: 500ms (credits animation only)
    LaunchedEffect(Unit) {
        val creditsMediaPlayer = MediaPlayer.create(context, R.raw.coins)
        creditsMediaPlayer?.let { player ->
            try {
                // Wait until 5.2s (0.7s after credits animation starts at 4.5s)
                delay(5200)
                
                // Prepare player: set looping in case file is shorter than needed
                player.isLooping = true
                player.setVolume(1f, 1f) // Start at full volume
                player.start()
                
                // Play at full volume during credits animation (500ms)
                delay(500)
                
                // Stop and release exactly when credits animation ends
                player.stop()
                player.release()
            } catch (e: Exception) {
                // Handle any errors silently
                try {
                    if (player.isPlaying) {
                        player.stop()
                    }
                    player.release()
                } catch (e2: Exception) {
                    // Ignore release errors
                }
            }
        }
    }
    
    // Start XP animation after 2 seconds (card appears immediately, animation starts after delay)
    LaunchedEffect(Unit) {
        delay(2000)
        delay(1000) // Start XP animation 1 second after initial delay (3 seconds total)
        startXPAnimation = true
    }
    
    // Animate XP when startXPAnimation becomes true
    LaunchedEffect(startXPAnimation) {
        if (startXPAnimation) {
            // Animate from currentXP to newXP over 500ms
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
            
            // Start credits animation 1 second after XP animation completes
            delay(1000)
            startCreditsAnimation = true
        }
    }
    
    // Animate credits when startCreditsAnimation becomes true
    LaunchedEffect(startCreditsAnimation) {
        if (startCreditsAnimation) {
            // Animate from currentCredits to newCredits over 500ms
            val startValue = currentCredits.toFloat()
            val endValue = newCredits.toFloat()
            val duration = 500L
            val steps = 30
            val stepDelay = duration / steps
            val stepValue = (endValue - startValue) / steps
            
            for (i in 0..steps) {
                animatedCredits = (startValue + stepValue * i).toInt()
                delay(stepDelay)
            }
            animatedCredits = newCredits
            // Update global state
            UserDataRepository.userCredits = newCredits
        }
    }
    
    val scrollState = rememberScrollState()
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Blur and overlay: Same as modal (96% opacity)
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
        
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp) // 16dp side padding
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
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
                // "X mins of focus" - 20sp, bold
                // For test mode (0 minutes), show "0 mins" or handle differently
                val focusTimeText = if (travelMinutes == 0) {
                    "0 mins of focus" // Test mode: 10 seconds
                } else {
                    "$travelMinutes mins of focus"
                }
                Text(
                    text = focusTimeText,
                    fontFamily = Exo2,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                // 8dp spacing
                Spacer(modifier = Modifier.height(8.dp))
                
                // "0 penalties suffered" - 20sp, regular (placeholder)
                Text(
                    text = "0 penalties suffered",
                    fontFamily = Exo2,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
                
                // 16dp spacing
                Spacer(modifier = Modifier.height(16.dp))
                
                // "+X XP earned" - dynamic
                Text(
                    text = "+$earnedXP XP earned",
                    fontFamily = Exo2,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
                
                // 8dp spacing
                Spacer(modifier = Modifier.height(8.dp))
                
                // "+X credits earned" - dynamic
                Text(
                    text = "+$earnedCredits credits earned",
                    fontFamily = Exo2,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
            }
            
            // 24dp spacing
            Spacer(modifier = Modifier.height(24.dp))
            
            // Divider
            HorizontalDivider()
            
            // 24dp spacing
            Spacer(modifier = Modifier.height(24.dp))
            
            // LevelStatusCard - appears immediately, animates after delay
            // Calculate level, progress, and xpToNext based on animated XP
            // We need to calculate these dynamically based on the animated XP value
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
                modifier = Modifier.fillMaxWidth()
            )
            
            // 24dp spacing
            Spacer(modifier = Modifier.height(24.dp))
            
            // Divider
            HorizontalDivider()
            
            // 24dp spacing
            Spacer(modifier = Modifier.height(24.dp))
            
            // CreditsSection (without icon)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy((-8).dp)
            ) {
                // Top row: Credits amount label only (no icon)
                AnimatedNumberCounter(
                    targetValue = if (startCreditsAnimation) animatedCredits else currentCredits,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                // Bottom row: "Interstellar Credits" label + info icon
                Row(
                    modifier = Modifier.offset(x = 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Interstellar Credits",
                        fontFamily = Exo2,
                        fontWeight = FontWeight.W400,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    
                    Image(
                        painter = painterResource(id = R.drawable.infoicon),
                        contentDescription = "Info",
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            
            // Bottom padding: 24dp
            Spacer(modifier = Modifier.height(24.dp))
            
            // Continue button
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
