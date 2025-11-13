package com.example.fargalaxy.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.fargalaxy.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

// Exo 2 font family definition - variable font set to Regular weight (W400)
val Exo2 = FontFamily(Font(R.font.exo2, weight = FontWeight.W400))

/**
 * Indicator composable - displays three pagination dots in a horizontal row.
 * The center dot is filled white when active, while the outer dots show as white outlines.
 * Used to indicate the current page/screen in the navigation.
 */
@Composable
fun Indicator(isCenterActive: Boolean = true) {
    // Horizontal row containing three dots with 8.dp spacing between them
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left dot - inactive state: transparent fill with 1.dp white border
        Box(
            modifier = Modifier
                .size(8.dp)
                .border(
                    width = 1.dp,
                    color = Color.White,
                    shape = CircleShape
                )
        )
        
        // Center dot - active state: filled white when isCenterActive is true,
        // otherwise shows as outline like the other inactive dots
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = if (isCenterActive) Color.White else Color.Transparent,
                    shape = CircleShape
                )
                .then(
                    if (!isCenterActive) {
                        Modifier.border(
                            width = 1.dp,
                            color = Color.White,
                            shape = CircleShape
                        )
                    } else Modifier
                )
        )
        
        // Right dot - inactive state: transparent fill with 1.dp white border
        Box(
            modifier = Modifier
                .size(8.dp)
                .border(
                    width = 1.dp,
                    color = Color.White,
                    shape = CircleShape
                )
        )
    }
}

/**
 * CareerButton composable - displays the career icon button in the top controls bar.
 * Uses a vector drawable that maintains its aspect ratio when sized to 51.dp height.
 */
@Composable
fun CareerButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.career),
        contentDescription = "Career",
        modifier = modifier
            .height(51.dp)
            .clickable(onClick = onClick),
        contentScale = ContentScale.Fit
    )
}

/**
 * CollectionButton composable - displays the collection icon button in the top controls bar.
 * Uses a vector drawable that maintains its aspect ratio when sized to 51.dp height.
 */
@Composable
fun CollectionButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.collection),
        contentDescription = "Collection",
        modifier = modifier
            .height(51.dp)
            .clickable(onClick = onClick),
        contentScale = ContentScale.Fit
    )
}

/**
 * TopControlsBar composable - horizontal row containing the top navigation controls.
 * Displays CareerButton on the left, Indicator in the center, and CollectionButton on the right.
 * Uses SpaceBetween arrangement to evenly distribute elements across the full width.
 */
@Composable
fun TopControlsBar(
    modifier: Modifier = Modifier,
    onCareerClick: () -> Unit = {},
    onCollectionClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(51.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CareerButton(onClick = onCareerClick)
        
        Indicator(isCenterActive = true)
        
        CollectionButton(onClick = onCollectionClick)
    }
}

/**
 * MinusButton composable - displays the minus/decrement icon for time controls.
 * Uses a vector drawable that maintains its aspect ratio when sized to 51.dp height.
 * Button is disabled (non-clickable) when enabled is false.
 * Shows tap state (minustap) when pressed, default (minusdefault) otherwise.
 */
@Composable
fun MinusButton(
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    // Interaction source to track press state
    val interactionSource = remember { MutableInteractionSource() }
    
    // Collect pressed state to detect when button is being tapped
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Select drawable based on press state
    val drawableId = if (isPressed) {
        R.drawable.minustap
    } else {
        R.drawable.minusdefault
    }
    
    Image(
        painter = painterResource(id = drawableId),
        contentDescription = "Decrease time",
        modifier = modifier
            .size(51.dp) // Fixed size to prevent layout shifts when switching drawables
            .alpha(if (enabled) 1f else 0.5f) // Reduce opacity when disabled
            .clickable(
                enabled = enabled,
                onClick = onClick,
                indication = null,
                interactionSource = interactionSource
            ),
        contentScale = ContentScale.Fit
    )
}

/**
 * PlusButton composable - displays the plus/increment icon for time controls.
 * Uses a vector drawable that maintains its aspect ratio when sized to 51.dp height.
 * Button is disabled (non-clickable) when enabled is false.
 * Shows tap state (plustap) when pressed, default (plusdefault) otherwise.
 */
@Composable
fun PlusButton(
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    // Interaction source to track press state
    val interactionSource = remember { MutableInteractionSource() }
    
    // Collect pressed state to detect when button is being tapped
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Select drawable based on press state
    val drawableId = if (isPressed) {
        R.drawable.plustap
    } else {
        R.drawable.plusdefault
    }
    
    Image(
        painter = painterResource(id = drawableId),
        contentDescription = "Increase time",
        modifier = modifier
            .size(51.dp) // Fixed size to prevent layout shifts when switching drawables
            .alpha(if (enabled) 1f else 0.5f) // Reduce opacity when disabled
            .clickable(
                enabled = enabled,
                onClick = onClick,
                indication = null,
                interactionSource = interactionSource
            ),
        contentScale = ContentScale.Fit
    )
}

/**
 * TimeLabel composable - displays the time duration in minutes or launch countdown.
 * Shows different text based on state:
 * - When preparing: "Launching in X" (where X is 3, 2, or 1)
 * - When idle: "$selectedMinutes mins"
 * - When traveling: "$remainingMinutes mins remaining" (converts seconds to minutes)
 * Uses Exo 2 Regular font at 24.sp with white color for visibility on dark background.
 */
@Composable
fun TimeLabel(
    selectedMinutes: Int = 25,
    remainingSeconds: Int = 25 * 60,
    isTraveling: Boolean = false,
    isPreparingLaunch: Boolean = false,
    launchCountdown: Int = 3,
    modifier: Modifier = Modifier
) {
    // Determine the text to display based on state
    val displayText = when {
        isPreparingLaunch -> "Launching in $launchCountdown"
        isTraveling -> {
            val remainingMinutes = (remainingSeconds + 59) / 60 // Round up to nearest minute
            "$remainingMinutes mins remaining"
        }
        else -> "$selectedMinutes mins"
    }
    
    Text(
        text = displayText,
        fontFamily = Exo2,
        fontSize = 24.sp,
        lineHeight = 24.sp,
        color = Color(0xFFFFFFFF),
        modifier = modifier
    )
}

/**
 * TimeControlsBar composable - horizontal row containing time adjustment controls.
 * When not traveling/preparing: Displays MinusButton on the left, TimeLabel in the center, and PlusButton on the right.
 * When traveling/preparing: Only displays TimeLabel in the center (buttons are hidden).
 * Uses SpaceBetween arrangement to evenly distribute elements across the full width.
 * Buttons are hidden when isTraveling or isPreparingLaunch is true.
 */
@Composable
fun TimeControlsBar(
    selectedMinutes: Int = 25,
    remainingSeconds: Int = 25 * 60,
    isTraveling: Boolean = false,
    isPreparingLaunch: Boolean = false,
    launchCountdown: Int = 3,
    modifier: Modifier = Modifier,
    onMinusClick: () -> Unit = {},
    onPlusClick: () -> Unit = {}
) {
    // Center the label when traveling or preparing, otherwise space buttons
    val shouldCenter = isTraveling || isPreparingLaunch
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(51.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = if (shouldCenter) {
            Arrangement.Center // Center the label when traveling or preparing
        } else {
            Arrangement.SpaceBetween // Space buttons when not traveling/preparing
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Only show buttons when not traveling or preparing
        if (!shouldCenter) {
            MinusButton(
                onClick = onMinusClick,
                enabled = true
            )
        }
        
        TimeLabel(
            selectedMinutes = selectedMinutes,
            remainingSeconds = remainingSeconds,
            isTraveling = isTraveling,
            isPreparingLaunch = isPreparingLaunch,
            launchCountdown = launchCountdown
        )
        
        // Only show buttons when not traveling or preparing
        if (!shouldCenter) {
            PlusButton(
                onClick = onPlusClick,
                enabled = true
            )
        }
    }
}

/**
 * LaunchButton composable - displays the main action button at the bottom of the screen.
 * Primary style (idle): White background with rounded corners (80.dp radius) and dark text.
 * Secondary style (traveling/preparing): Transparent background with 1px white border and white text.
 * Button text toggles between "LAUNCH" (when idle), "CANCEL" (when preparing), and "STOP TRAVEL" (when traveling).
 * Uses Exo 2 Regular font at 24.sp for the button label.
 */
@Composable
fun LaunchButton(
    onClick: () -> Unit = {},
    isTraveling: Boolean = false,
    isPreparingLaunch: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Determine button text based on state
    val buttonText = when {
        isPreparingLaunch -> "CANCEL"
        isTraveling -> "STOP TRAVEL"
        else -> "LAUNCH"
    }

    // Determine button styling based on state
    // Preparing and traveling use secondary style (transparent with border)
    val useSecondaryStyle = isTraveling || isPreparingLaunch
    val backgroundColor = if (useSecondaryStyle) {
        Color.Transparent // Secondary style: no fill
    } else {
        Color(0xFFFFFFFF) // Primary style: white fill
    }

    val textColor = if (useSecondaryStyle) {
        Color(0xFFFFFFFF) // Secondary style: white text
    } else {
        Color(0xFF010102) // Primary style: dark text
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(80.dp))
            .then(
                // Add border only when in secondary style (traveling or preparing)
                if (useSecondaryStyle) {
                    Modifier.border(
                        width = 1.dp,
                        color = Color(0xFFFFFFFF), // White border
                        shape = RoundedCornerShape(80.dp)
                    )
                } else {
                    Modifier
                }
            )
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = buttonText,
            fontFamily = Exo2,
            fontSize = 24.sp,
            lineHeight = 24.sp,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-2).dp)
        )
    }
}

/**
 * ImpulseLayer composable - displays the ship's engine thrust/impulse effect.
 * The image fades in from 0% to 100% opacity over 4 seconds when travel starts.
 * 
 * @param isTraveling Boolean flag indicating if travel is active
 * @param modifier Modifier for positioning and sizing
 * 
 * The impulse effect:
 * - Uses impulse1.png from drawable-nodpi
 * - Fixed width of 600.dp and height of 100.dp
 * - Starts at 0% opacity and fades to 100% over 4 seconds when travel begins
 * - Vertically centered
 * - Offset 120.dp to the left from center
 * - Only visible when isTraveling is true
 */
@Composable
fun ImpulseLayer(
    isTraveling: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Return early if not traveling
    if (!isTraveling) {
        return
    }

    // State to control opacity target - starts at 0f, animates to 1f when travel begins
    var targetOpacity by remember(isTraveling) { mutableStateOf(0f) }
    
    // Trigger fade-in animation when isTraveling becomes true
    LaunchedEffect(isTraveling) {
        if (isTraveling) {
            targetOpacity = 0f // Start from 0
            delay(10) // Small delay to ensure state is set
            targetOpacity = 1f // Animate to 1f
        } else {
            targetOpacity = 0f // Reset when travel stops
        }
    }

    // Animate opacity from 0f to 1f over 4 seconds
    val opacity by animateFloatAsState(
        targetValue = targetOpacity,
        animationSpec = tween(durationMillis = 4000),
        label = "impulse_fade_in"
    )

    Box(
        modifier = modifier
            .offset(x = (-120).dp), // Debug background
        contentAlignment = Alignment.CenterEnd
    ) {
        Image(
            painter = painterResource(id = R.drawable.impulse1),
            contentDescription = "Engine thrust effect",
            modifier = Modifier
                .width(600.dp)
                .height(100.dp)
                .alpha(opacity), // Apply fade-in animation
            contentScale = ContentScale.Fit
        )
    }
}

/**
 * SpeedEffectLayer composable - displays a Lottie animation speed effect.
 * The animation appears at full opacity and speeds up from 0.5x to 1.0x over 8 seconds.
 * 
 * @param isVisible Boolean flag indicating if the speed effect should be visible
 * @param modifier Modifier for positioning and sizing
 * 
 * The speed effect:
 * - Uses speedeffect.json from raw resources
 * - Scales to full width of the screen using fillMaxWidth()
 * - Maintains aspect ratio using ContentScale.Fit
 * - Loops continuously when visible
 * - Appears at full opacity (100%) when visible
 * - Animates speed from 0.5x to 1.0x over 8 seconds when visible
 */
@Composable
fun SpeedEffectLayer(
    isVisible: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Return early if not visible
    if (!isVisible) {
        return
    }

    // State to control speed target - starts at 0.5f, animates to 1.0f when visible
    var targetSpeed by remember(isVisible) { mutableStateOf(0.5f) }
    
    // Trigger speed animation when isVisible becomes true
    LaunchedEffect(isVisible) {
        if (isVisible) {
            // Start from initial speed
            targetSpeed = 0.5f // Start from 0.5
            
            // Start speed animation immediately
            targetSpeed = 1.0f // Animate speed from 0.5f to 1.0f over 8 seconds
        } else {
            // Reset to initial speed when not visible
            targetSpeed = 0.5f
        }
    }

    // Animate speed from 0.5f to 1.0f over 8 seconds
    val speed by animateFloatAsState(
        targetValue = targetSpeed,
        animationSpec = tween(durationMillis = 8000),
        label = "speed_effect_speed"
    )

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.speedeffect))

    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = speed, // Apply animated speed
        modifier = modifier
            .fillMaxWidth(),
        contentScale = ContentScale.Fit
    )
}

/**
 * CountdownRing composable - displays a circular progress ring that visually represents remaining time.
 * The ring is drawn using Canvas and shows a shrinking arc as time progresses.
 * When first appearing, the ring animates from 0° to full circle over 5 seconds.
 * 
 * @param selectedMinutes The total selected time duration in minutes (5-60 range)
 * @param remainingSeconds The current remaining time in seconds (decreases every second during countdown)
 * @param isInitialAppearance Boolean flag indicating if this is the first time the ring appears
 * @param modifier Modifier for positioning and sizing the ring
 * 
 * The ring:
 * - Has a diameter of 280.dp with 3.dp stroke width
 * - Uses white color (#FFFFFF) with transparent fill
 * - Starts at the top (-90° offset) and shrinks clockwise
 * - Disappears completely when remainingSeconds reaches 0
 * - Animates its initial appearance over 5 seconds when first shown
 * - Uses smooth animation via animateFloatAsState for visual transitions
 * - Decreases smoothly every second for continuous visual feedback
 */
@Composable
fun CountdownRing(
    selectedMinutes: Int,
    remainingSeconds: Int,
    isInitialAppearance: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Calculate the target sweep angle based on remaining time proportion
    // Formula: sweepAngle = 360f * (remainingSeconds / (selectedMinutes * 60))
    // When remainingSeconds = selectedMinutes * 60, sweepAngle = 360° (full circle)
    // When remainingSeconds = 0, sweepAngle = 0° (no circle)
    val totalSeconds = selectedMinutes * 60
    val targetSweepAngle = if (selectedMinutes > 0 && remainingSeconds > 0) {
        360f * (remainingSeconds.toFloat() / totalSeconds.toFloat())
    } else {
        0f
    }
    
    // State to track the target for initial animation
    // When isInitialAppearance becomes true, we want to animate from 0 to 1
    // Use a key to reset the animation state when isInitialAppearance changes
    val animationTrigger = remember(isInitialAppearance) { 
        if (isInitialAppearance) System.currentTimeMillis() else 0L 
    }
    
    // State to hold the current animation target (0f when starting, 1f when complete)
    var animationTarget by remember(animationTrigger) { 
        mutableStateOf(if (animationTrigger > 0) 0f else 1f) 
    }
    
    // Trigger animation to 1 when isInitialAppearance becomes true
    LaunchedEffect(animationTrigger) {
        if (animationTrigger > 0) {
            animationTarget = 0f // Start at 0
            delay(50) // Small delay to ensure state is set
            animationTarget = 1f // Animate to 1
        } else {
            animationTarget = 1f // Reset when not in initial appearance
        }
    }
    
    // Animate the initial drawing progress smoothly from 0 to 1 over 5 seconds
    // This creates the effect of the stroke drawing itself when the ring first appears
    val initialAnimationProgress by animateFloatAsState(
        targetValue = animationTarget,
        animationSpec = if (animationTrigger > 0 && animationTarget == 1f) {
            tween(durationMillis = 1000) // 5 seconds for initial draw
        } else {
            tween(durationMillis = 0) // Instant when resetting
        },
        label = "initial_ring_draw"
    )
    
    // Calculate the actual sweep angle to display
    // During initial appearance, animate from 0 to targetSweepAngle
    // After initial animation completes, use targetSweepAngle directly
    val displaySweepAngle = if (isInitialAppearance && animationTrigger > 0 && initialAnimationProgress < 1f) {
        // During initial draw: animate from 0 to targetSweepAngle proportionally (counter-clockwise)
        // This makes the ring draw itself from 0° to the full remaining time circle
        -(targetSweepAngle * initialAnimationProgress) // Negative for counter-clockwise
    } else {
        // After initial draw: use normal countdown behavior (counter-clockwise)
        -targetSweepAngle // Negative for counter-clockwise
    }

    // Animate the sweep angle smoothly when remainingMinutes changes (after initial draw)
    // This creates a smooth visual transition as the countdown progresses
    val animatedSweepAngle by animateFloatAsState(
        targetValue = displaySweepAngle,
        animationSpec = tween(durationMillis = 500),
        label = "countdown_ring_animation"
    )

    // Don't render the ring if countdown has reached 0 or if selectedMinutes is invalid
    if (remainingSeconds <= 0 || selectedMinutes <= 0) {
        return
    }

    // Canvas composable to draw the circular arc
    Canvas(
        modifier = modifier.size(274.dp)
    ) {
        // Calculate the center point and radius of the circle
        // Account for stroke width to ensure the full circle fits within the canvas
        val center = Offset(size.width / 2, size.height / 2)
        val radius = (size.minDimension - 3.dp.toPx()) / 2

        // Convert stroke width from dp to pixels
        val strokeWidth = 3.dp.toPx()

        // Draw the arc representing remaining time
        // startAngle: -90° (top of circle, Compose uses 0° at 3 o'clock, so -90° is at 12 o'clock)
        // sweepAngle: animated angle that shrinks as time decreases (clockwise)
        // useCenter: false (draw arc only, not filled pie slice)
        // The arc is drawn within a bounding rectangle centered on the canvas
        drawArc(
            color = Color(0xFFFFFFFF), // White color #FFFFFF
            startAngle = -90f, // Start at top (-90° offset)
            sweepAngle = animatedSweepAngle, // Shrinking arc based on remaining time (negative = counter-clockwise)
            useCenter = false, // Draw arc only, not filled
            topLeft = Offset(
                center.x - radius,
                center.y - radius
            ),
            size = Size(radius * 2, radius * 2),
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round // Rounded ends for smoother appearance
            )
        )
    }
}

/**
 * GalaxyScreen composable - the main screen displaying a layered space-themed UI.
 * Layers are stacked from back to front: background, radar animation, countdown ring (when traveling), spaceship, gradients, UI controls, and noise overlay.
 * The layout creates a futuristic space exploration interface with navigation and time controls.
 * 
 * State Management:
 * - selectedMinutes: User-selected time duration (5-60 minutes range, increments of 5)
 * - isTraveling: Boolean flag indicating if the countdown is active
 * - remainingSeconds: Current remaining time in seconds that decreases every second during countdown
 * 
 * All state is preserved across configuration changes using rememberSaveable.
 */
@Composable
fun GalaxyScreen(modifier: Modifier = Modifier) {
    // State management: All state is saved across configuration changes (screen rotation, etc.)
    // selectedMinutes: The time duration selected by the user (range: 5-60, step: 5)
    var selectedMinutes by rememberSaveable { mutableStateOf(25) }
    
    // isTraveling: Boolean flag that indicates if the countdown/travel is currently active
    var isTraveling by rememberSaveable { mutableStateOf(false) }
    
    // remainingSeconds: The current remaining time in seconds that decreases as the countdown progresses
    // When not traveling, this represents minutes (multiplied by 60 for conversion)
    // When traveling, this decreases every second for smooth ring animation
    var remainingSeconds by rememberSaveable { mutableStateOf(25 * 60) } // Start with 25 minutes in seconds
    
    // isInitialRingAppearance: Tracks if the countdown ring is appearing for the first time
    // Used to trigger the initial drawing animation (0° to 360° over 5 seconds)
    var isInitialRingAppearance by remember { mutableStateOf(false) }
    
    // showSpeedEffect: Controls visibility of the speed effect Lottie animation
    // Appears 3 seconds after travel starts and disappears immediately when travel stops
    var showSpeedEffect by remember { mutableStateOf(false) }
    
    // isPreparingLaunch: Boolean flag that indicates if the launch preparation countdown is active
    // When true, shows 3-second countdown before actual travel begins
    var isPreparingLaunch by remember { mutableStateOf(false) }
    
    // launchCountdown: The countdown number displayed during launch preparation (3, 2, 1)
    var launchCountdown by remember { mutableStateOf(3) }
    
    // Handler: Increment selectedMinutes by 5, clamped to maximum of 60
    // Only works when not traveling (buttons are disabled during travel)
    fun onIncrement() {
        if (!isTraveling) {
            selectedMinutes = (selectedMinutes + 5).coerceAtMost(60)
        }
    }

    // Handler: Decrement selectedMinutes by 5, clamped to minimum of 5
    // Only works when not traveling (buttons are disabled during travel)
    fun onDecrement() {
        if (!isTraveling) {
            selectedMinutes = (selectedMinutes - 5).coerceAtLeast(5)
        }
    }
    
    // Handler: Toggle travel state (launch/stop travel)
    // When launching: Start preparation countdown instead of immediately starting travel
    // When preparing: Cancel the launch preparation
    // When stopping: Set isTraveling to false (countdown will stop automatically)
    fun onLaunchToggle() {
        if (isPreparingLaunch) {
            // Cancel launch during preparation
            isPreparingLaunch = false
            launchCountdown = 3 // Reset countdown
        } else if (!isTraveling) {
            // Start preparation instead of immediate launch
            isPreparingLaunch = true
            launchCountdown = 3 // Initialize countdown
        } else {
            // Stop travel: Cancel countdown
            isTraveling = false
            isInitialRingAppearance = false // Reset for next launch
        }
    }
    
    // Countdown timer: Second-based countdown that runs when isTraveling is true
    // Uses LaunchedEffect to run a coroutine that decrements remainingSeconds every second
    // Automatically stops when remainingSeconds reaches 0 or isTraveling becomes false
    LaunchedEffect(isTraveling) {
        if (isTraveling) {
            // Continue countdown while traveling and time remains
            while (isTraveling && remainingSeconds > 0) {
                delay(1_000) // Wait 1 second
                remainingSeconds -= 1 // Decrement remaining time by 1 second
            }
            // Auto-stop when countdown completes
            isTraveling = false
        }
    }
    
    // Reset initial appearance flag after the 5-second animation completes
    // This ensures the flag is reset for the next launch
    LaunchedEffect(isInitialRingAppearance) {
        if (isInitialRingAppearance) {
            delay(5000) // Wait for 5 second animation to complete
            isInitialRingAppearance = false
        }
    }
    
    // Launch preparation countdown: 3-second countdown before actual travel begins
    // Decrements launchCountdown every second (3, 2, 1)
    // After countdown completes, automatically starts travel if not cancelled
    LaunchedEffect(isPreparingLaunch) {
        if (isPreparingLaunch) {
            launchCountdown = 3 // Reset to 3
            while (isPreparingLaunch && launchCountdown > 0) {
                delay(1000) // Wait 1 second
                if (isPreparingLaunch) { // Check if still preparing (not cancelled)
                    launchCountdown--
                }
            }
            // After countdown completes, start travel if still preparing
            if (isPreparingLaunch) {
                isPreparingLaunch = false
                isTraveling = true
                remainingSeconds = selectedMinutes * 60
                isInitialRingAppearance = true // Trigger initial drawing animation
                launchCountdown = 3 // Reset for next time
            }
        } else {
            launchCountdown = 3 // Reset when cancelled
        }
    }

    // Speed effect visibility management: Controls when the speed effect appears
    // Uses a remembered coroutine scope to avoid LaunchedEffect conflicts
    // When isTraveling becomes true: show after 3 seconds
    // When isTraveling becomes false: immediately hide

    // Remember a coroutine scope for speed effect timing
    val speedEffectScope = rememberCoroutineScope()
    var speedEffectJob by remember { mutableStateOf<Job?>(null) }

    // Track a unique identifier for each travel session to prevent stale checks
    var travelSessionId by remember { mutableStateOf(0L) }

    // Handle speed effect timing when travel state changes
    LaunchedEffect(isTraveling) {
        // Cancel any existing job
        speedEffectJob?.cancel()
        speedEffectJob = null

        if (isTraveling) {
            // Increment session ID for this travel session
            val currentSessionId = System.currentTimeMillis()
            travelSessionId = currentSessionId

            // Launch coroutine to show speed effect after 3 seconds
            val job = speedEffectScope.launch {
                // delay(3000) // Wait 3 seconds
                // Only show if this is still the current travel session
                // (check by comparing session IDs, not isTraveling which might be stale)
                if (travelSessionId == currentSessionId) {
                    showSpeedEffect = true
                }
            }
            speedEffectJob = job
        } else {
            // Immediately hide when travel stops and invalidate session
            showSpeedEffect = false
            travelSessionId = 0L // Invalidate session
        }
    }
    
    // Main container Box that fills the entire screen
    Box(modifier = modifier.fillMaxSize()) {
        // Background layer: Full-screen galaxy background image, cropped to fill the screen
        Image(
            painter = painterResource(id = R.drawable.bg_galaxy),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Radar animation layer: Lottie animation displayed as a centered 640.dp square.
        // Positioned at the vertical center. Radar speed changes based on travel state:
        // - When traveling: 1.75f (faster animation)
        // - When idle: 0.8f (normal speed)
        RadarLayer(
            isTraveling = isTraveling,
            modifier = Modifier
                .align(Alignment.Center)
                .size(640.dp)
                .clipToBounds()
        )

        // Speed effect layer: Lottie animation that appears 2 seconds after travel starts.
        // Positioned between the radar layer and countdown ring.
        // Scales to full width and maintains aspect ratio.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        ) {
            SpeedEffectLayer(
                isVisible = showSpeedEffect,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }

        // Countdown ring layer: Circular progress ring centered on the radar and ship.
        // Only visible when isTraveling is true.
        // Positioned above the radar but below the ship image and impulse.
        // The ring visually represents remaining time, shrinking as the countdown progresses.
        // Decreases smoothly every second for continuous visual feedback.
        // On first appearance, animates from 0° to full circle over 5 seconds.
        // Disappears completely when countdown reaches 0.
        if (isTraveling) {
            CountdownRing(
                selectedMinutes = selectedMinutes,
                remainingSeconds = remainingSeconds,
                isInitialAppearance = isInitialRingAppearance,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-1).dp)  // Negative value moves upward
                    .size(278.dp)
            )
        }

        // Impulse/thrust effect layer: Engine thrust effect that appears when traveling.
        // Positioned above the countdown ring but below the ship image.
        // Animates from 0% to 100% width over 3 seconds when travel starts.
        // Vertically centered and offset 40.dp to the left from center.
        if (isTraveling) {
            ImpulseLayer(
                isTraveling = isTraveling,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }

        // Spaceship layer: Centered spaceship image with fixed height of 170.dp.
        // Positioned at the vertical center to align with the radar animation and countdown ring.
        // Appears above the countdown ring and impulse layer.
        Image(
            painter = painterResource(id = R.drawable.ship1),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(170.dp),
            contentScale = ContentScale.Fit
        )

        // Top gradient overlay: Covers 20% of screen height, creating a fade effect at the top.
        // Gradient transitions from solid black at the top to transparent at the bottom.
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.20f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF000000),
                            Color(0x00000000)
                        )
                    )
                )
        )

        // Bottom gradient overlay: Covers 25% of screen height, creating a fade effect at the bottom.
        // Gradient transitions from transparent at the top to solid black at the bottom.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.25f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x00000000),
                            Color(0xFF000000)
                        )
                    )
                )
        )

        // Top controls area: Shows different content based on state
        // When preparing: Shows "Preparing for launch" label
        // When traveling: Shows "In travel" label
        // When idle: Shows TopControlsBar with navigation controls
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 48.dp)
                .fillMaxWidth()
                .height(51.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                isPreparingLaunch -> {
                    // Show "Preparing for launch" label when preparing
                    // Uses same font style as TimeLabel (Exo2 Regular, 24.sp, white color)
                    Text(
                        text = "Preparing for launch",
                        fontFamily = Exo2,
                        fontSize = 24.sp,
                        lineHeight = 24.sp,
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center
                    )
                }
                isTraveling -> {
                    // Show "In travel" label when traveling
                    // Uses same font style as TimeLabel (Exo2 Regular, 24.sp, white color)
                    Text(
                        text = "In travel",
                        fontFamily = Exo2,
                        fontSize = 24.sp,
                        lineHeight = 24.sp,
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    // Show navigation controls when idle
                    TopControlsBar()
                }
            }
        }

        // Bottom controls container: Vertical column containing time controls and launch button.
        // Positioned 24.dp above the navigation bar with 16.dp spacing between elements.
        // All controls are wired to state and handlers for proper functionality.
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Time controls bar: Displays minus button, time label, and plus button.
            // When traveling/preparing: Only shows time label (buttons are hidden).
            // When not traveling/preparing: Shows all controls with buttons enabled.
            TimeControlsBar(
                selectedMinutes = selectedMinutes,
                remainingSeconds = remainingSeconds,
                isTraveling = isTraveling,
                isPreparingLaunch = isPreparingLaunch,
                launchCountdown = launchCountdown,
                onMinusClick = ::onDecrement,
                onPlusClick = ::onIncrement
            )
            
            // Launch button: Toggles between "LAUNCH", "CANCEL" (when preparing), and "STOP TRAVEL" (when traveling).
            // Calls onLaunchToggle handler to start preparation, cancel, or stop the countdown.
            LaunchButton(
                onClick = ::onLaunchToggle,
                isTraveling = isTraveling,
                isPreparingLaunch = isPreparingLaunch
            )
        }

        // Noise overlay: Fine grain texture applied on top of all other layers.
        // Uses low opacity (0.082f) to add subtle texture without obscuring content.
        Image(
            painter = painterResource(id = R.drawable.noise_8bit),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .alpha(0.082f),
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * Preview composable for GalaxyScreen - used for design preview in Android Studio.
 * Displays the screen with default state values (25 minutes selected, not traveling).
 */
@Preview(showBackground = true)
@Composable
private fun GalaxyScreenPreview() {
    GalaxyScreen()
}

