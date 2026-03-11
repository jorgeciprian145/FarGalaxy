package com.example.fargalaxy.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import android.graphics.RenderEffect as AndroidRenderEffect
import android.graphics.Shader
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.media.MediaPlayer
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.Context
import kotlin.math.abs
import kotlin.math.sqrt
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.fargalaxy.R
import com.example.fargalaxy.model.Ship
import com.example.fargalaxy.utils.playMouseClickSound
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

// Exo 2 font family definition - variable font set to Regular weight (W400)
val Exo2 = FontFamily(Font(R.font.exo2, weight = FontWeight.W400))

/**
 * Enum to represent which screen is active in the indicator
 */
enum class ActiveScreen {
    LEFT,   // Career screen
    CENTER, // Galaxy screen
    RIGHT   // Collection screen (future)
}

/**
 * Indicator composable - displays three pagination dots in a horizontal row.
 * The active dot is filled white, while inactive dots show as white outlines.
 * Used to indicate the current page/screen in the navigation.
 */
@Composable
fun Indicator(activeScreen: ActiveScreen = ActiveScreen.CENTER) {
    // Horizontal row containing three dots with 8.dp spacing between them
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left dot - active when activeScreen is LEFT
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = if (activeScreen == ActiveScreen.LEFT) Color.White else Color.Transparent,
                    shape = CircleShape
                )
                .then(
                    if (activeScreen != ActiveScreen.LEFT) {
                        Modifier.border(
                            width = 1.dp,
                            color = Color.White,
                            shape = CircleShape
                        )
                    } else Modifier
                )
        )
        
        // Center dot - active when activeScreen is CENTER
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = if (activeScreen == ActiveScreen.CENTER) Color.White else Color.Transparent,
                    shape = CircleShape
                )
                .then(
                    if (activeScreen != ActiveScreen.CENTER) {
                        Modifier.border(
                            width = 1.dp,
                            color = Color.White,
                            shape = CircleShape
                        )
                    } else Modifier
                )
        )
        
        // Right dot - active when activeScreen is RIGHT
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = if (activeScreen == ActiveScreen.RIGHT) Color.White else Color.Transparent,
                    shape = CircleShape
                )
                .then(
                    if (activeScreen != ActiveScreen.RIGHT) {
                        Modifier.border(
                            width = 1.dp,
                            color = Color.White,
                            shape = CircleShape
                        )
                    } else Modifier
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
    activeScreen: ActiveScreen = ActiveScreen.CENTER,
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
        
        Indicator(activeScreen = activeScreen)
        
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
    isTestMode: Boolean = false, // TODO: REMOVE TESTING CODE
    modifier: Modifier = Modifier
) {
    // Determine the text to display based on state
    // TODO: REMOVE TESTING CODE - Show "TEST" when in test mode
    val displayText = when {
        isPreparingLaunch -> "Launching in $launchCountdown"
        isTraveling -> {
            val remainingMinutes = (remainingSeconds + 59) / 60 // Round up to nearest minute
            "$remainingMinutes mins remaining"
        }
        else -> {
            // TODO: REMOVE TESTING CODE - Show "TEST" when in test mode
            if (selectedMinutes == 5 && isTestMode) {
                "TEST"
            } else {
                "$selectedMinutes mins"
            }
        }
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
 * PenaltyCounter composable - displays the penalty counter with icon and label.
 * Shows different labels based on equipped equipment:
 * - Emergency modulator: "Emergency modulator use 0/1" or "1/1" (for 5 seconds) then "0/5 penalties"
 * - Unstable cargo: "No penalties allowed"
 * - Default: "#/5 penalties"
 * 
 * @param penaltyCount The current penalty count
 * @param modifier Modifier for the component
 */
@Composable
fun PenaltyCounter(
    penaltyCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val equippedItem = remember {
        com.example.fargalaxy.data.EquipmentRepository.getEquippedItem()
    }
    
    // For emergency modulator: track if it's been used and show "1/1" for 5 seconds
    var showEmergencyModulatorUsed by remember { mutableStateOf(false) }
    val isEmergencyModulatorUsed = com.example.fargalaxy.data.EquipmentUsageRepository.isEmergencyModulatorUsed()
    
    LaunchedEffect(isEmergencyModulatorUsed) {
        if (isEmergencyModulatorUsed && equippedItem == "emergency_modulator") {
            showEmergencyModulatorUsed = true
            delay(5000) // Show "1/1" for 5 seconds
            showEmergencyModulatorUsed = false
        }
    }
    
    // Get the actual penalty count - if emergency modulator was used, it should be 0
    // Read directly from PenaltyTracker to ensure we have the latest value
    val actualPenaltyCount = remember(penaltyCount, isEmergencyModulatorUsed, equippedItem) {
        // If emergency modulator is equipped and was used, the penalty count must be 0
        if (equippedItem == "emergency_modulator" && isEmergencyModulatorUsed) {
            // Read directly from PenaltyTracker to get the latest value
            val trackerCount = com.example.fargalaxy.data.PenaltyTracker.getPenaltyCount()
            // Emergency modulator was used, so count should be 0 (force to 0 if somehow it's not)
            trackerCount.coerceAtMost(0) // Ensure it's never > 0 when modulator was used
        } else {
            penaltyCount
        }
    }
    
    // Determine label text based on equipped equipment
    val labelText = when (equippedItem) {
        "emergency_modulator" -> {
            if (showEmergencyModulatorUsed) {
                "Emergency modulator use 1/1"
            } else if (!isEmergencyModulatorUsed) {
                "Emergency modulator use 0/1"
            } else {
                // Emergency modulator was used, show actual penalty count (should be 0)
                "$actualPenaltyCount/5 penalties"
            }
        }
        "unstable_cargo" -> {
            "No penalties allowed"
        }
        else -> {
            "$penaltyCount/5 penalties"
        }
    }
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Penalty icon: 32dp size
        Image(
            painter = painterResource(id = R.drawable.penaltyicon),
            contentDescription = "Penalties",
            modifier = Modifier.size(32.dp),
            contentScale = ContentScale.Fit
        )
        
        // 4dp spacing between icon and label
        Spacer(modifier = Modifier.width(4.dp))
        
        // Label: Dynamic based on equipment - 18sp, regular weight
        Text(
            text = labelText,
            fontFamily = Exo2,
            fontSize = 18.sp,
            fontWeight = FontWeight.W400, // Regular
            color = Color(0xFFFFFFFF),
            textAlign = TextAlign.Center
        )
    }
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
    isTestMode: Boolean = false, // TODO: REMOVE TESTING CODE
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
            launchCountdown = launchCountdown,
            isTestMode = isTestMode // TODO: REMOVE TESTING CODE
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
 * Disabled style (no travels available): 40% fill opacity, "NOT AVAILABLE" text, #D5D5D5 color, lock icon.
 * Button text toggles between "LAUNCH" (when idle), "CANCEL" (when preparing), "STOP TRAVEL" (when traveling), and "NOT AVAILABLE" (when disabled).
 * Uses Exo 2 Regular font at 24.sp for the button label.
 */
@Composable
fun LaunchButton(
    onClick: () -> Unit = {},
    isTraveling: Boolean = false,
    isPreparingLaunch: Boolean = false,
    isDisabled: Boolean = false, // No travels available
    modifier: Modifier = Modifier
) {
    // Determine button text based on state
    val buttonText = when {
        isPreparingLaunch -> "CANCEL"
        isTraveling -> "STOP TRAVEL"
        isDisabled -> "NOT AVAILABLE"
        else -> "LAUNCH"
    }

    // Determine button styling based on state
    // Preparing and traveling use secondary style (transparent with border)
    val useSecondaryStyle = isTraveling || isPreparingLaunch
    val backgroundColor = when {
        useSecondaryStyle -> Color.Transparent // Secondary style: no fill
        isDisabled -> Color(0xFFFFFFFF).copy(alpha = 0.4f) // Disabled: 40% opacity white fill
        else -> Color(0xFFFFFFFF) // Primary style: white fill
    }

    val textColor = when {
        useSecondaryStyle -> Color(0xFFFFFFFF) // Secondary style: white text
        isDisabled -> Color(0xFFD5D5D5) // Disabled: #D5D5D5 color
        else -> Color(0xFF010102) // Primary style: dark text
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
            .then(
                if (isDisabled) {
                    Modifier // Disabled: not clickable
                } else {
                    Modifier.clickable(onClick = onClick)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-2).dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lock icon: Only shown when disabled, 16dp size, 4dp spacing to the left of text
            if (isDisabled) {
                Image(
                    painter = painterResource(id = R.drawable.lock),
                    contentDescription = "Locked",
                    modifier = Modifier.size(16.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(textColor) // Use same color as text
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            
            Text(
                text = buttonText,
                fontFamily = Exo2,
                fontSize = 24.sp,
                lineHeight = 24.sp,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * BoostSelectionBottomSheet composable - displays a bottom sheet for selecting boosts to equip.
 * Shows an overlay with blur, a bottom sheet container with gradient background and title.
 * Slides up from the bottom with rounded corners on top.
 */
@Composable
fun BoostSelectionBottomSheet(
    onDismiss: () -> Unit = {},
    currentShip: Ship,
    onShowToast: (String) -> Unit = {},
    onShowScannerProgress: () -> Unit = {},
    onShowExperimentalFuelRemoveModal: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Animation state for slide-in from bottom
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    // Animate the bottom sheet sliding up from bottom
    val offsetY by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 1000.dp, // Slide from off-screen bottom
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "bottom_sheet_slide"
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // Block all pointer input (scrolling, dragging, etc.) to prevent pager scrolling
                detectDragGestures { change, dragAmount ->
                    // Consume all drag gestures to prevent pager scrolling
                }
            }
    ) {
        // Blur and overlay: Blurs the content behind and applies dark overlay with 96% opacity
        // Clickable to dismiss the bottom sheet when tapping outside
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
                .clickable(onClick = onDismiss) // Dismiss when tapping outside
        )
        
        // Bottom Sheet Container: Slides up from bottom
        // Rounded corners only on top (top-left and top-right, 32dp)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .wrapContentHeight() // Wrap content vertically
                .offset(y = offsetY) // Animate slide-in from bottom
                .navigationBarsPadding() // Account for navigation bar to prevent clipping
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF373A3E), // Top color
                            Color(0xFF2B2E32)  // Bottom color
                        )
                    ),
                    shape = RoundedCornerShape(
                        topStart = 32.dp,
                        topEnd = 32.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFF6B6C6F),
                    shape = RoundedCornerShape(
                        topStart = 32.dp,
                        topEnd = 32.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .padding(horizontal = 16.dp), // 16dp side padding for all elements
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // Ensure content starts at top
        ) {
                // Title: "Select the boost you want to equip" - bold, 16sp, 24dp from top
            Text(
                text = "Select the boost you want to equip",
                fontFamily = Exo2,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 24.dp)
            )
            
            // Spacing: 24dp below title
            Spacer(modifier = Modifier.height(24.dp))
            
            // Boost items section: 3 rows
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                    // Row 1: Emergency modulators
                    val emergencyModulatorQuantity = com.example.fargalaxy.data.InventoryRepository.getItemQuantity("emergency_modulator")
                    val isEmergencyModulatorEquipped = com.example.fargalaxy.data.EquipmentRepository.isItemEquipped("emergency_modulator")
                    BoostItemRow(
                        imageResId = R.drawable.modulatorselection,
                        itemName = "Emergency modulators",
                        quantity = "x$emergencyModulatorQuantity",
                        showBottomDivider = false,
                        isEquipped = isEmergencyModulatorEquipped,
                        onClick = {
                            if (isEmergencyModulatorEquipped) {
                                // Unequip if already equipped
                                com.example.fargalaxy.data.EquipmentRepository.unequipItem()
                                com.example.fargalaxy.data.EquipmentUsageRepository.resetUsage()
                                onDismiss() // Close the bottom sheet
                            } else {
                                // Check if user has the item in inventory
                                if (emergencyModulatorQuantity > 0) {
                                    // Equip this item (unequips any other item first)
                                    com.example.fargalaxy.data.EquipmentRepository.equipItem("emergency_modulator")
                                    com.example.fargalaxy.data.EquipmentUsageRepository.initializeUsage("emergency_modulator")
                                    onDismiss() // Close the bottom sheet
                                } else {
                                    // Show toast message if no items available
                                    onShowToast("You don't have any Emergency modulators remaining")
                                }
                            }
                        }
                    )
                    
                    // Row 2: Unstable cargo
                    val unstableCargoQuantity = com.example.fargalaxy.data.InventoryRepository.getItemQuantity("unstable_cargo")
                    val isUnstableCargoEquipped = com.example.fargalaxy.data.EquipmentRepository.isItemEquipped("unstable_cargo")
                    BoostItemRow(
                        imageResId = R.drawable.cargoselection,
                        itemName = "Unstable cargo",
                        quantity = "x$unstableCargoQuantity",
                        showBottomDivider = false,
                        isEquipped = isUnstableCargoEquipped,
                        onClick = {
                            if (isUnstableCargoEquipped) {
                                // Unequip if already equipped
                                com.example.fargalaxy.data.EquipmentRepository.unequipItem()
                                com.example.fargalaxy.data.EquipmentUsageRepository.resetUsage()
                                onDismiss() // Close the bottom sheet
                            } else {
                                // Check if user has the item in inventory
                                if (unstableCargoQuantity > 0) {
                                    // Equip this item (unequips any other item first)
                                    com.example.fargalaxy.data.EquipmentRepository.equipItem("unstable_cargo")
                                    com.example.fargalaxy.data.EquipmentUsageRepository.initializeUsage("unstable_cargo")
                                    onDismiss() // Close the bottom sheet
                                } else {
                                    // Show toast message if no items available
                                    onShowToast("You don't have any Unstable cargo remaining")
                                }
                            }
                        }
                    )
                    
                    // Row 3: Experimental fuel (last row - has bottom divider)
                    val experimentalFuelQuantity = com.example.fargalaxy.data.InventoryRepository.getItemQuantity("experimental_fuel")
                    val isExperimentalFuelEquipped = com.example.fargalaxy.data.EquipmentRepository.isItemEquipped("experimental_fuel")
                    BoostItemRow(
                        imageResId = R.drawable.fuelselection,
                        itemName = "Experimental fuel",
                        quantity = "x$experimentalFuelQuantity",
                        showBottomDivider = true,
                        isEquipped = isExperimentalFuelEquipped,
                        onClick = {
                            if (isExperimentalFuelEquipped) {
                                // Check if there are remaining travels - show confirmation modal
                                val remainingTravels = com.example.fargalaxy.data.EquipmentUsageRepository.getExperimentalFuelRemaining()
                                if (remainingTravels > 0) {
                                    // Show removal confirmation modal
                                    onShowExperimentalFuelRemoveModal()
                                } else {
                                    // No remaining travels, safe to unequip
                                    com.example.fargalaxy.data.EquipmentRepository.unequipItem()
                                    com.example.fargalaxy.data.EquipmentUsageRepository.resetUsage()
                                }
                                onDismiss() // Close the bottom sheet
                            } else {
                                // Check if user has the item in inventory
                                if (experimentalFuelQuantity > 0) {
                                    // Equip this item (unequips any other item first)
                                    // Remove item from inventory immediately when equipped
                                    com.example.fargalaxy.data.InventoryRepository.removeItem("experimental_fuel", 1)
                                    com.example.fargalaxy.data.EquipmentRepository.equipItem("experimental_fuel")
                                    com.example.fargalaxy.data.EquipmentUsageRepository.initializeUsage("experimental_fuel")
                                    onDismiss() // Close the bottom sheet
                                } else {
                                    // Show toast message if no items available
                                    onShowToast("You don't have any Experimental fuel remaining")
                                }
                            }
                        }
                    )
            }
            
            // Spacing: 24dp below last row
            Spacer(modifier = Modifier.height(24.dp))
            
            // Deep Space Scanners container
            DeepSpaceScannersContainer(
                currentShip = currentShip,
                onShowToast = onShowToast,
                onShowScannerProgress = onShowScannerProgress
            )
            
            // Spacing: 24dp below container
            Spacer(modifier = Modifier.height(24.dp))
            
            // CANCEL button: Similar to cancel button when preparing launch
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(80.dp))
                    .border(
                        width = 1.dp,
                        color = Color(0xFFFFFFFF), // White border
                        shape = RoundedCornerShape(80.dp)
                    )
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CANCEL",
                    fontFamily = Exo2,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    color = Color(0xFFFFFFFF), // White text
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-2).dp)
                )
            }
            
            // Bottom padding: 24dp
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * BoostItemRow composable - displays a single boost item row with image, name, and quantity.
 * 
 * @param imageResId The drawable resource ID for the item image
 * @param itemName The name of the boost item
 * @param quantity The quantity remaining (e.g., "x3")
 * @param showBottomDivider Whether to show a divider at the bottom of the row
 * @param isEquipped Whether this item is currently equipped
 * @param onClick Callback when the row is clicked
 * @param modifier Modifier for the row
 */
@Composable
private fun BoostItemRow(
    imageResId: Int,
    itemName: String,
    quantity: String,
    showBottomDivider: Boolean = false,
    isEquipped: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick) // Make entire row clickable
    ) {
        // Divider on top: 1dp height, white 32% opacity
        // Note: Parent Column already has 16dp horizontal padding, so divider respects that
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFFFFFFF).copy(alpha = 0.32f))
        )
        
        // Row content: Image, name, and quantity
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image container: 64dp height, maintains aspect ratio, fills full height
            Box(
                modifier = Modifier
                    .height(64.dp)
                    .width(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = itemName,
                    modifier = Modifier
                        .height(64.dp) // Fill full height of container (64dp)
                        .wrapContentWidth(), // Maintain aspect ratio, width adjusts automatically
                    contentScale = ContentScale.Fit
                )
            }
            
            // Spacing: 16dp between image and label
            Spacer(modifier = Modifier.width(16.dp))
            
            // Item name label: Bold, 16sp (same format as title)
            Text(
                text = itemName,
                fontFamily = Exo2,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f) // Takes remaining space
            )
            
            // Quantity label: Regular, 16sp, aligned to the right
            // Show "EQUIPPED" if equipped, otherwise show quantity
            Text(
                text = if (isEquipped) "EQUIPPED" else quantity,
                fontFamily = Exo2,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
        }
        
        // Bottom divider: Only shown on last row
        if (showBottomDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFFFFFFF).copy(alpha = 0.32f))
            )
        }
    }
}

/**
 * ScannerProgressScreen composable - displays the scanning progress screen.
 * Shows radar animation and "Deep space scanning in progress" text.
 * Plays for 3 seconds then fades out.
 */
@Composable
private fun ScannerProgressScreen(
    onComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp }
    
    // Animation states
    var alpha by remember { mutableStateOf(1f) }
    
    // Load radar JSON composition
    val radarComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.radar))
    
    // Auto-advance after 3 seconds with fade out
    LaunchedEffect(Unit) {
        delay(3000) // Play for 3 seconds
        // Fade out animation
        alpha = 0f
        delay(600) // Wait for fade animation to complete
        onComplete()
    }
    
    // Animate alpha
    val animatedAlpha by animateFloatAsState(
        targetValue = alpha,
        animationSpec = tween(durationMillis = 600),
        label = "fade_out"
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    // Block drag gestures
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Blur and overlay: Same as modals (96% opacity black overlay)
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
        
        // Content container: Centered vertically and horizontally
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(animatedAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Radar animation: 85% of screen width, maintaining aspect ratio
            Box(
                modifier = Modifier
                    .width(screenWidth * 0.85f)
                    .wrapContentHeight()
            ) {
                LottieAnimation(
                    composition = radarComposition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
            }
            
            // 32dp spacing below animation
            Spacer(modifier = Modifier.height(32.dp))
            
            // Label: "Deep space scanning in progress" - bold, 28sp
            Text(
                text = "Deep space scanning in progress",
                fontFamily = Exo2,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp) // 32dp side padding
            )
        }
    }
}

/**
 * ScannerResultsScreen composable - displays the scanner results.
 * Shows environment, recommended profile, and reset time.
 */
@Composable
private fun ScannerResultsScreen(
    onContinueClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Get environment data
    val currentEnvironment = com.example.fargalaxy.data.FlightEnvironmentRepository.getCurrentEnvironment()
    val environmentName = currentEnvironment.displayName
    val recommendedProfile = currentEnvironment.recommendedProfile
    val recommendedProfileText = when (recommendedProfile) {
        com.example.fargalaxy.model.ShipProfile.STABLE -> "Stable"
        com.example.fargalaxy.model.ShipProfile.ACCELERATOR -> "Accelerator"
        com.example.fargalaxy.model.ShipProfile.RUNNER -> "Runner"
        com.example.fargalaxy.model.ShipProfile.WELL_ROUNDED -> "Well rounded"
    }
    
    // Track remaining time until reset
    var remainingMillis by remember {
        mutableStateOf(com.example.fargalaxy.data.FlightEnvironmentRepository.getRemainingMillisUntilReset())
    }
    
    // Update remaining time every second
    LaunchedEffect(Unit) {
        while (true) {
            remainingMillis = com.example.fargalaxy.data.FlightEnvironmentRepository.getRemainingMillisUntilReset()
            kotlinx.coroutines.delay(1000L)
        }
    }
    
    // Helper to format remaining time as "HH:MM hs"
    fun formatRemaining(millis: Long): String {
        val totalSeconds = (millis / 1000L).coerceAtLeast(0L)
        val hours = totalSeconds / 3600L
        val minutes = (totalSeconds % 3600L) / 60L
        return String.format("%02d:%02d hs", hours, minutes)
    }
    
    val remainingText = formatRemaining(remainingMillis)
    
    // Fade in animation
    var alpha by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        alpha = 1f
    }
    
    val animatedAlpha by animateFloatAsState(
        targetValue = alpha,
        animationSpec = tween(durationMillis = 600),
        label = "fade_in"
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    // Block drag gestures
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Blur and overlay: Same as modals (96% opacity black overlay)
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
        
        // Content container: 16dp side padding, vertically and horizontally centered
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .alpha(animatedAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title: "Scanner results" - bold, 28sp
            Text(
                text = "Scanner results",
                fontFamily = Exo2,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            // 4dp spacing below title
            Spacer(modifier = Modifier.height(4.dp))
            
            // Label: "This is how the flight environment is today" - regular, 16sp
            Text(
                text = "This is how the flight environment is today",
                fontFamily = Exo2,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            // 16dp spacing below label
            Spacer(modifier = Modifier.height(16.dp))
            
            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFFFFFFF).copy(alpha = 0.32f))
            )
            
            // 16dp spacing below divider
            Spacer(modifier = Modifier.height(16.dp))
            
            // First pair: Environment today
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Environment today",
                    fontFamily = Exo2,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = environmentName,
                    fontFamily = Exo2,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
            
            // 16dp spacing below first pair
            Spacer(modifier = Modifier.height(16.dp))
            
            // Second pair: Boosted ship profile
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Boosted ship profile",
                    fontFamily = Exo2,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = recommendedProfileText,
                    fontFamily = Exo2,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
            
            // 16dp spacing below second pair
            Spacer(modifier = Modifier.height(16.dp))
            
            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFFFFFFF).copy(alpha = 0.32f))
            )
            
            // 16dp spacing below divider
            Spacer(modifier = Modifier.height(16.dp))
            
            // Third pair: Flight environment will reset in
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Flight environment will reset in:",
                    fontFamily = Exo2,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = remainingText,
                    fontFamily = Exo2,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
            
            // 24dp spacing before button
            Spacer(modifier = Modifier.height(24.dp))
            
            // CONTINUE button: Full width, white background, black text
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
 * DeepSpaceScannersContainer composable - displays the deep space scanners section.
 * When the scanner has not been used for the current environment, it shows the
 * "Deep space scanners" description and a button to reveal conditions.
 * After use (once per environment/day), it shows the conditions for the day.
 */
@Composable
private fun DeepSpaceScannersContainer(
    currentShip: Ship,
    onShowToast: (String) -> Unit = {},
    onShowScannerProgress: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Track current scanner quantity from inventory
    var scannerCount by remember {
        mutableStateOf(com.example.fargalaxy.data.InventoryRepository.getItemQuantity("deep_space_scanner"))
    }
    // Track whether scanner has been used for the current environment
    var hasRevealedToday by remember {
        mutableStateOf(com.example.fargalaxy.data.FlightEnvironmentRepository.isScannerUsedForCurrentEnvironment())
    }
    // Track remaining time until reset (ms)
    var remainingMillis by remember {
        mutableStateOf(com.example.fargalaxy.data.FlightEnvironmentRepository.getRemainingMillisUntilReset())
    }

    // Update remaining time every second while this container is on screen
    LaunchedEffect(Unit) {
        while (true) {
            remainingMillis = com.example.fargalaxy.data.FlightEnvironmentRepository.getRemainingMillisUntilReset()
            kotlinx.coroutines.delay(1000L)
        }
    }

    // Helper to format remaining time as "HH:MM hs"
    fun formatRemaining(millis: Long): String {
        val totalSeconds = (millis / 1000L).coerceAtLeast(0L)
        val hours = totalSeconds / 3600L
        val minutes = (totalSeconds % 3600L) / 60L
        return String.format("%02d:%02d hs", hours, minutes)
    }

    val currentEnvironment = com.example.fargalaxy.data.FlightEnvironmentRepository.getCurrentEnvironment()
    val environmentName = currentEnvironment.displayName
    val recommendedProfile = currentEnvironment.recommendedProfile
    val recommendedProfileText = when (recommendedProfile) {
        com.example.fargalaxy.model.ShipProfile.STABLE -> "Stable"
        com.example.fargalaxy.model.ShipProfile.ACCELERATOR -> "Accelerator"
        com.example.fargalaxy.model.ShipProfile.RUNNER -> "Runner"
        com.example.fargalaxy.model.ShipProfile.WELL_ROUNDED -> "Well rounded"
    }
    val remainingText = formatRemaining(remainingMillis)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight() // Vertically hug content
            .border(
                width = 1.dp,
                color = Color(0xFF6B6C6F), // Stroke color
                shape = RoundedCornerShape(24.dp) // 24dp corner radius
            )
            .background(
                color = Color(0x29FFFFFF), // White at 16% opacity (0x29 = ~16%)
                shape = RoundedCornerShape(24.dp) // 24dp corner radius
            )
            .padding(
                start = 8.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Image: 100x100, maintains aspect ratio, fills full height
            if (!hasRevealedToday) {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .width(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.scannerselection),
                        contentDescription = "Deep space scanners",
                        modifier = Modifier
                            .height(100.dp) // Fill full height of container (100dp)
                            .wrapContentWidth(), // Maintain aspect ratio, width adjusts automatically
                        contentScale = ContentScale.Fit
                    )
                }
            }
            
            // Container to the right: Stretches full remaining width, no spacing from image
            Column(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight() // Hug content vertically
            ) {
                if (!hasRevealedToday) {
                    // Top container: Badge, title, and description
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Badge: "AVAILABLE" - similar to common ship badge
                        Box(
                            modifier = Modifier
                                .wrapContentHeight()
                                .wrapContentWidth()
                                .background(
                                    color = Color(0x29FFFFFF) // White at 16% opacity
                                )
                                .padding(start = 4.dp, top = 2.dp, end = 4.dp, bottom = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AVAILABLE",
                                fontFamily = Exo2,
                                fontSize = 10.sp,
                                lineHeight = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFFFFFFF), // White text
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        // Spacing: 4dp below badge
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Title: "Deep space scanners" - bold, 16sp
                        Text(
                            text = "Deep space scanners",
                            fontFamily = Exo2,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        // Spacing: 4dp below title
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Paragraph: Regular, 14sp with dynamic remaining count
                        Text(
                            text = "Reveal the flight environment of the day. x$scannerCount Remaining",
                            fontFamily = Exo2,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White
                        )
                    }
                    
                    // Button at bottom: "REVEAL ENVIRONMENT" - same format as LAUNCH button, 24dp height, 14sp
                    // Spacing: Add some space between content and button
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val buttonEnabled = scannerCount > 0
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .clip(RoundedCornerShape(80.dp))
                            .background(Color(0xFFFFFFFF).copy(alpha = if (buttonEnabled) 1f else 0.4f))
                            .clickable {
                                if (buttonEnabled) {
                                    // Show first intermediate screen instead of immediately revealing
                                    onShowScannerProgress()
                                } else {
                                    // Show toast message if no scanners available
                                    onShowToast("You don't have any Deep space scanners remaining")
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "REVEAL ENVIRONMENT",
                            fontFamily = Exo2,
                            fontSize = 14.sp,
                            lineHeight = 14.sp,
                            color = Color(0xFF010102), // Dark text (primary style)
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(y = (-1).dp) // Smaller offset for 14sp text
                        )
                    }
                } else {
                    // Scanner already used for this environment: show conditions for the day
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            // Parent Box has 8dp start padding; add 8dp more so effective left padding is 16dp
                            .padding(start = 8.dp, end = 0.dp)
                    ) {
                        // Title: "Conditions for the day"
                        Text(
                            text = "Conditions for the day",
                            fontFamily = Exo2,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        // 24dp below title: divider
                        Spacer(modifier = Modifier.height(24.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFFFFFFF).copy(alpha = 0.32f))
                        )
                        
                        // 24dp below divider: environment/profile container
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Environment today",
                                    fontFamily = Exo2,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = environmentName,
                                    fontFamily = Exo2,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Ship profile",
                                    fontFamily = Exo2,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = recommendedProfileText,
                                    fontFamily = Exo2,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        
                        // 24dp under the container: another divider
                        Spacer(modifier = Modifier.height(24.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFFFFFFF).copy(alpha = 0.32f))
                        )
                        
                        // 24dp under the last divider: "Resets in" label and badge
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Resets in:",
                                fontFamily = Exo2,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Box(
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .wrapContentHeight()
                                    .background(
                                        color = Color(0x29FFFFFF), // 16% white
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = remainingText,
                                    fontFamily = Exo2,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * RepairNeededModal composable - displays a modal when ship runs out of travels.
 * Shows an overlay with blur, a container with gradient background, title, label, two buttons, and repairtool icon.
 */
@Composable
fun RepairNeededModal(
    repairCost: Int,
    maintenanceMinutes: Int,
    onRepairClick: () -> Unit = {},
    onWaitClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Track JSON height for offset calculation
    var jsonHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    
    // Load JSON composition
    val jsonComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.modalcheck))
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // Block all pointer input (scrolling, dragging, etc.) to prevent pager scrolling
                detectDragGestures { change, dragAmount ->
                    // Consume all drag gestures to prevent pager scrolling
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Blur and overlay: Blurs the content behind and applies dark overlay with 96% opacity
        // Clickable to block all interactions behind the modal
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
        
        // Modal Container with repairtool icon on top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Modal Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF373A3E), // Top color
                                Color(0xFF2B2E32)  // Bottom color
                            )
                        ),
                        shape = RoundedCornerShape(32.dp) // 32dp corner radius
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFF6B6C6F),
                        shape = RoundedCornerShape(32.dp) // 32dp corner radius
                    )
                    .padding(
                        top = 72.dp,
                        bottom = 24.dp,
                        start = 24.dp,
                        end = 24.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title: "Repairs needed" - bold, 28sp
                Text(
                    text = "Repairs needed",
                    fontFamily = Exo2,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                // Spacing: 4dp between title and label
                Spacer(modifier = Modifier.height(4.dp))
                
                // Label: Description message - regular, 16sp
                Text(
                    text = "Your ship used all the available travels and maintenance is needed. You can repair your ship for $repairCost credits or wait $maintenanceMinutes minutes before using it again.",
                    fontFamily = Exo2,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                // Spacing: 16dp between label and buttons
                Spacer(modifier = Modifier.height(16.dp))
                
                // Primary Button: "REPAIR (X CREDITS)" - primary style (white background, dark text), 16sp
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(80.dp))
                        .background(Color(0xFFFFFFFF)) // White background (primary style)
                        .clickable(onClick = onRepairClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "REPAIR ($repairCost CREDITS)",
                        fontFamily = Exo2,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        color = Color(0xFF010102), // Dark text (primary style)
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = (-2).dp)
                    )
                }
                
                // Spacing: 12dp between buttons
                Spacer(modifier = Modifier.height(12.dp))
                
                // Secondary Button: "WAIT FOR MAINTENANCE" - secondary style (transparent with white border, white text), 16sp
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(80.dp))
                        .border(
                            width = 1.dp,
                            color = Color(0xFFFFFFFF), // White border
                            shape = RoundedCornerShape(80.dp)
                        )
                        .clickable(onClick = onWaitClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "WAIT FOR MAINTENANCE",
                        fontFamily = Exo2,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        color = Color(0xFFFFFFFF), // White text (secondary style)
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = (-2).dp)
                    )
                }
            }
            
            // JSON Animation: Positioned right above the container (bottom edge at container top), then offset down by 50% of its height
            // Width is 70% of screen width
            val configuration = LocalConfiguration.current
            val screenWidth = with(density) { configuration.screenWidthDp.dp }
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .width(screenWidth * 0.7f) // 70% of screen width
                    .onSizeChanged { size ->
                        jsonHeight = with(density) { size.height.toDp() }
                    }
                    .offset(y = -jsonHeight + jsonHeight / 2) // Move up by full height (so bottom aligns with container top), then offset down by 50%
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
        }
    }
}

/**
 * TravelSuccessModal composable - displays a success modal when travel completes.
 * Shows an overlay with blur, a container with gradient background, title, label, button, and JSON animation.
 */
@Composable
fun TravelSuccessModal(
    onContinueClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Track JSON height for offset calculation
    var jsonHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    
    // Load JSON composition
    val jsonComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.modalcheck))
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Blur and overlay: Blurs the content behind and applies dark overlay with 96% opacity
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
        
        // Modal Container with JSON on top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Modal Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF373A3E), // Top color
                                Color(0xFF2B2E32)  // Bottom color
                            )
                        ),
                        shape = RoundedCornerShape(32.dp) // 32dp corner radius
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFF6B6C6F),
                        shape = RoundedCornerShape(32.dp) // 32dp corner radius
                    )
                    .padding(
                        top = 72.dp,
                        bottom = 24.dp,
                        start = 24.dp,
                        end = 24.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title: "Success!" - bold, 28dp
                Text(
                    text = "Success!",
                    fontFamily = Exo2,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                // Spacing: 4dp between title and label
                Spacer(modifier = Modifier.height(4.dp))
                
                // Label: "You completed this session" - regular, 16dp
                Text(
                    text = "You completed this session",
                    fontFamily = Exo2,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                // Spacing: 16dp between label and button
                Spacer(modifier = Modifier.height(16.dp))
                
                // Button: "CONTINUE" - same format as LAUNCH button (primary style)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(80.dp))
                        .background(Color(0xFFFFFFFF)) // White background (primary style)
                        .clickable(onClick = onContinueClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CONTINUE",
                        fontFamily = Exo2,
                        fontSize = 24.sp,
                        lineHeight = 24.sp,
                        color = Color(0xFF010102), // Dark text (primary style)
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = (-2).dp)
                    )
                }
            }
            
            // JSON Animation: Positioned right above the container (bottom edge at container top), then offset down by 50% of its height
            // Width is 70% of screen width
            val configuration = LocalConfiguration.current
            val screenWidth = with(density) { configuration.screenWidthDp.dp }
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .width(screenWidth * 0.7f) // 70% of screen width
                    .onSizeChanged { size ->
                        jsonHeight = with(density) { size.height.toDp() }
                    }
                    .offset(y = -jsonHeight + jsonHeight / 2) // Move up by full height (so bottom aligns with container top), then offset down by 50%
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
        }
    }
}

/**
 * Helper function to get the ship image resource ID for GalaxyScreen based on ship ID.
 * Maps ship IDs to their corresponding ship images (ship1, ship2, etc.).
 */
private fun getGalaxyShipImageResId(shipId: String): Int {
    return when (shipId) {
        "b14_phantom" -> R.drawable.ship1
        "type45c_shooting_star" -> R.drawable.ship2
        "navakeshi_star_pouncer" -> R.drawable.ship3
        "a300_albatross" -> R.drawable.ship4
        "b7f_starforce" -> R.drawable.ship5
        "navakeshi_star_crusher" -> R.drawable.ship6
        "b15_specter" -> R.drawable.ship7
        "n6_98_melina" -> R.drawable.ship8
        "model3_tortoise_ccp" -> R.drawable.ship9
        "navakeshi_star_ravager" -> R.drawable.ship11
        "h98_valkyrie" -> R.drawable.ship10
        "asn_h99_dragoon" -> R.drawable.ship18
        "silver_lightning" -> R.drawable.ship12
        "vulcani_legenda_f1" -> R.drawable.ship13
        "force_of_nature" -> R.drawable.ship14
        "dying_star" -> R.drawable.ship15
        "asn_ag94_centurion" -> R.drawable.ship16
        "isc_m450_phoenix" -> R.drawable.ship17
        "p7h_skyblazer" -> R.drawable.ship19
        "a450_sparrow" -> R.drawable.ship20
        "t47_dolphin" -> R.drawable.ship21
        "ship22" -> R.drawable.ship22
        "ship23" -> R.drawable.ship23
        "legendary_ship" -> R.drawable.ship1 // Fallback
        else -> R.drawable.ship1 // Default fallback
    }
}

/**
 * Helper function to get the ship image height for GalaxyScreen based on ship ID.
 * Returns ship-specific height, or default 113.dp if not specified.
 */
private fun getGalaxyShipHeight(shipId: String): androidx.compose.ui.unit.Dp {
    return when (shipId) {
        "type45c_shooting_star" -> 174.dp // Ship2: 10% bigger than 158.dp (158 * 1.1 = 173.8)
        "a300_albatross" -> 249.dp // Ship4: 120% bigger than 113.dp (113 * 2.2 = 248.6)
        "b7f_starforce" -> 249.dp // Ship5: Same height as ship4 (249.dp)
        "navakeshi_star_crusher" -> 196.dp // Ship6: 73% bigger than default 113.dp (163 * 1.2 = 195.6, or 113 * 1.728 = 195.26)
        "b15_specter" -> 236.dp // Ship7: 5% bigger than 225.dp (225 * 1.05 = 236.25)
        "n6_98_melina" -> 249.dp // Ship8: Similar to ship4/ship5, multi-purpose ship
        "model3_tortoise_ccp" -> 234.dp // Ship9: 10% smaller (260.dp * 0.9 = 234.dp)
        "navakeshi_star_ravager" -> 210.dp // Ship11: EPIC assault ship, larger than ship6
        "h98_valkyrie" -> 237.dp // Ship10: 110% bigger than 113.dp (113 * 2.1 = 237.3)
        "asn_h99_dragoon" -> 190.785.dp // Ship18: 15% larger from current size - 165.9.dp * 1.15 = 190.785.dp
        "silver_lightning" -> 191.dp // Ship12: 10% bigger than 174.dp (174 * 1.1 = 191.4)
        "vulcani_legenda_f1" -> 136.dp // Ship13: 20% bigger than 113.dp (113 * 1.2 = 135.6)
        "force_of_nature" -> 130.dp // Ship14: 15% bigger than 113.dp (113 * 1.15 = 129.95)
        "dying_star" -> 208.dp // Ship15: 60% larger than 130.dp (130 * 1.6 = 208)
        "asn_ag94_centurion" -> 162.dp // Ship16: 10% smaller than 180.dp (180 * 0.9 = 162)
        "isc_m450_phoenix" -> 234.dp // Ship17: 30% larger than 180.dp (180 * 1.3 = 234)
        "p7h_skyblazer" -> 236.5.dp // Ship19: 5% smaller than 249.dp (249 * 0.95 = 236.55)
        "a450_sparrow" -> 163.8.dp // Ship20: 30% smaller than ship17 (Phoenix) - 234.dp * 0.7 = 163.8.dp
        "t47_dolphin" -> 269.1.dp // Ship21: 15% larger than ship9 (Tortoise) - 234.dp * 1.15 = 269.1.dp
        "ship22" -> 208.dp // Ship22: Same height as ship15 (208.dp)
        "ship23" -> 231.dp // Ship23: 15% bigger than 200.dp (200 * 1.15 = 230, then 220 * 1.05 = 231)
        else -> 113.dp // Default height
    }
}

/**
 * Helper function to get the impulse image resource ID based on ship ID.
 * Maps ship IDs to their corresponding impulse images (impulse1, impulse2, etc.).
 */
private fun getImpulseImageResId(shipId: String): Int {
    return when (shipId) {
        "b14_phantom" -> R.drawable.impulse1
        "type45c_shooting_star" -> R.drawable.impulse2
        "navakeshi_star_pouncer" -> R.drawable.impulse3
        "a300_albatross" -> R.drawable.impulse4
        "b7f_starforce" -> R.drawable.impulse5
        "navakeshi_star_crusher" -> R.drawable.impulse6
        "b15_specter" -> R.drawable.impulse7
        "n6_98_melina" -> R.drawable.impulse8
        "model3_tortoise_ccp" -> R.drawable.impulse9
        "navakeshi_star_ravager" -> R.drawable.impulse11
        "h98_valkyrie" -> R.drawable.impulse10
        "asn_h99_dragoon" -> R.drawable.impulse18
        "silver_lightning" -> R.drawable.impulse12
        "vulcani_legenda_f1" -> R.drawable.impulse13
        "force_of_nature" -> R.drawable.impulse14
        "dying_star" -> R.drawable.impulse15
        "asn_ag94_centurion" -> R.drawable.impulse16
        "isc_m450_phoenix" -> R.drawable.impulse17
        "p7h_skyblazer" -> R.drawable.impulse19
        "a450_sparrow" -> R.drawable.impulse20
        "t47_dolphin" -> R.drawable.impulse21
        "ship22" -> R.drawable.impulse22
        "ship23" -> R.drawable.impulse23
        "legendary_ship" -> R.drawable.impulse1 // Fallback
        else -> R.drawable.impulse1 // Default fallback
    }
}

/**
 * Helper function to get the impulse image width based on ship ID.
 * Returns ship-specific width, or default 600.dp if not specified.
 */
private fun getImpulseWidth(shipId: String): androidx.compose.ui.unit.Dp {
    return when (shipId) {
        "type45c_shooting_star" -> 924.dp // Impulse2: 10% bigger than 840.dp (840 * 1.1 = 924)
        "navakeshi_star_pouncer" -> 660.dp // Impulse3: 10% bigger than 600.dp (600 * 1.1 = 660)
        "a300_albatross" -> 1452.dp // Impulse4: 10% bigger than 1320.dp (1320 * 1.1 = 1452)
        "b7f_starforce" -> 1320.dp // Impulse5: 120% bigger than 600.dp (600 * 2.2 = 1320), same % as ship5
        "navakeshi_star_crusher" -> 1083.dp // Impulse6: 64% bigger than Impulse3 660.dp (1140 * 0.95 = 1083)
        "b15_specter" -> 1307.dp // Impulse7: 5% bigger than 1245.dp (1245 * 1.05 = 1307.25)
        "n6_98_melina" -> 1452.dp // Impulse8: Similar to ship4, multi-purpose ship
        "model3_tortoise_ccp" -> 1307.dp // Impulse9: 10% smaller than similar cargo ships (1452.dp * 0.9 = 1306.8)
        "navakeshi_star_ravager" -> 1200.dp // Impulse11: Larger than ship6, similar to mid-tier EPIC ships
        "h98_valkyrie" -> 1386.dp // Impulse10: 10% bigger than 1260.dp (1260 * 1.1 = 1386)
        "asn_h99_dragoon" -> 1115.73.dp // Impulse18: 15% larger from current size - 970.2.dp * 1.15 = 1115.73.dp
        "silver_lightning" -> 1118.dp // Impulse13: 10% bigger than 1016.dp (1016 * 1.1 = 1117.6)
        "vulcani_legenda_f1" -> 726.dp // Impulse13: 20% bigger than 600.dp (600 * 1.2 = 720, or 660 * 1.1 = 726)
        "force_of_nature" -> 690.dp // Impulse14: 15% bigger than 600.dp (600 * 1.15 = 690)
        "dying_star" -> 1104.dp // Impulse15: 60% larger than 690.dp (690 * 1.6 = 1104)
        "asn_ag94_centurion" -> 945.dp // Impulse16: 10% smaller than 1050.dp (1050 * 0.9 = 945)
        "isc_m450_phoenix" -> 1300.dp // Impulse17: 30% larger than 1000.dp (1000 * 1.3 = 1300)
        "p7h_skyblazer" -> 1379.dp // Impulse19: 5% smaller than 1452.dp (1452 * 0.95 = 1379.4)
        "a450_sparrow" -> 910.dp // Impulse20: 30% smaller than ship17 (Phoenix) - 1300.dp * 0.7 = 910.dp
        "t47_dolphin" -> 1503.05.dp // Impulse21: 15% larger than ship9 (Tortoise) - 1307.dp * 1.15 = 1503.05.dp
        "ship22" -> 1104.dp // Impulse22: Same width as impulse15 (1104.dp)
        "ship23" -> 1270.5.dp // Impulse23: 15% bigger than 1100.dp (1100 * 1.15 = 1265, then 1210 * 1.05 = 1270.5)
        else -> 600.dp // Default width
    }
}

/**
 * Helper function to get the impulse image height based on ship ID.
 * Returns ship-specific height, or default 100.dp if not specified.
 */
private fun getImpulseHeight(shipId: String): androidx.compose.ui.unit.Dp {
    return when (shipId) {
        "type45c_shooting_star" -> 154.dp // Impulse2: 10% bigger than 140.dp (140 * 1.1 = 154)
        "navakeshi_star_pouncer" -> 110.dp // Impulse3: 10% bigger than 100.dp (100 * 1.1 = 110)
        "a300_albatross" -> 242.dp // Impulse4: 10% bigger than 220.dp (220 * 1.1 = 242)
        "b7f_starforce" -> 220.dp // Impulse5: 120% bigger than 100.dp (100 * 2.2 = 220), same % as ship5
        "navakeshi_star_crusher" -> 181.dp // Impulse6: 64% bigger than Impulse3 110.dp (190 * 0.95 = 180.5)
        "b15_specter" -> 218.dp // Impulse7: 5% bigger than 208.dp (208 * 1.05 = 218.4)
        "n6_98_melina" -> 242.dp // Impulse8: Similar to ship4, multi-purpose ship
        "model3_tortoise_ccp" -> 218.dp // Impulse9: 10% smaller than similar cargo ships (242.dp * 0.9 = 217.8)
        "navakeshi_star_ravager" -> 200.dp // Impulse11: Larger than ship6, similar to mid-tier EPIC ships
        "h98_valkyrie" -> 231.dp // Impulse10: 10% bigger than 210.dp (210 * 1.1 = 231)
        "asn_h99_dragoon" -> 185.955.dp // Impulse18: 15% larger from current size - 161.7.dp * 1.15 = 185.955.dp
        "silver_lightning" -> 186.dp // Impulse13: 10% bigger than 169.dp (169 * 1.1 = 185.9)
        "vulcani_legenda_f1" -> 121.dp // Impulse13: 20% bigger than 100.dp (100 * 1.2 = 120, or 110 * 1.1 = 121)
        "force_of_nature" -> 115.dp // Impulse14: 15% bigger than 100.dp (100 * 1.15 = 115)
        "dying_star" -> 184.dp // Impulse15: 60% larger than 115.dp (115 * 1.6 = 184)
        "asn_ag94_centurion" -> 157.5.dp // Impulse16: 10% smaller than 175.dp (175 * 0.9 = 157.5)
        "isc_m450_phoenix" -> 214.5.dp // Impulse17: 30% larger than 165.dp (165 * 1.3 = 214.5)
        "p7h_skyblazer" -> 230.dp // Impulse19: 5% smaller than 242.dp (242 * 0.95 = 229.9)
        "a450_sparrow" -> 150.15.dp // Impulse20: 30% smaller than ship17 (Phoenix) - 214.5.dp * 0.7 = 150.15.dp
        "t47_dolphin" -> 250.7.dp // Impulse21: 15% larger than ship9 (Tortoise) - 218.dp * 1.15 = 250.7.dp
        "ship22" -> 184.dp // Impulse22: Same height as impulse15 (184.dp)
        "ship23" -> 213.675.dp // Impulse23: 15% bigger than 185.dp (185 * 1.15 = 212.75, then 203.5 * 1.05 = 213.675)
        else -> 100.dp // Default height
    }
}

/**
 * Helper function to get the durability value for a ship.
 * Returns the number of travel sessions available per day.
 * 
 * @param shipId The ship's ID
 * @return The durability value (number of travels)
 */
private fun getDurabilityValue(shipId: String): Int {
    return when (shipId) {
        "b14_phantom" -> 4 // Ship1
        "type45c_shooting_star" -> 3 // Ship2
        "navakeshi_star_pouncer" -> 3 // Ship3
        "a300_albatross" -> 5 // Ship4
        "b7f_starforce" -> 3 // Ship5
        "navakeshi_star_crusher" -> 5 // Ship6
        "b15_specter" -> 5 // Ship7
        "n6_98_melina" -> 6 // Ship8
        "model3_tortoise_ccp" -> 7 // Ship9
        "h98_valkyrie" -> 5 // Ship10
        "navakeshi_star_ravager" -> 6 // Ship11
        "silver_lightning" -> 4 // Ship12
        "vulcani_legenda_f1" -> 3 // Ship13
        "force_of_nature" -> 4 // Ship14
        "dying_star" -> 4 // Ship15
        "asn_ag94_centurion" -> 3 // Ship16
        "isc_m450_phoenix" -> 4 // Ship17
        "asn_h99_dragoon" -> 6 // Ship18
        "p7h_skyblazer" -> 6 // Ship19
        "a450_sparrow" -> 3 // Ship20
        "t47_dolphin" -> 7 // Ship21
        "ship22" -> 4 // Ship22
        "ship23" -> 4 // Ship23
        else -> 0
    }
}

/**
 * Helper function to get maintenance time requirement for a ship.
 * 
 * @param shipId The ship's ID
 * @return The maintenance time in minutes
 */
private fun getMaintenanceTime(shipId: String): Int {
    return com.example.fargalaxy.data.GameStateRepository.getShipMaintenanceTime(shipId)
}

/**
 * Helper function to get the impulse horizontal offset based on ship ID.
 * Returns ship-specific horizontal offset, or default -120.dp if not specified.
 * Negative values move left, positive values move right.
 */
private fun getImpulseHorizontalOffset(shipId: String): androidx.compose.ui.unit.Dp {
    return when (shipId) {
        "b14_phantom" -> (-120).dp // Ship1: B14 Phantom
        "type45c_shooting_star" -> (-120).dp // Ship2: Type 45C Shooting Star
        "a300_albatross" -> (-80).dp // Ship4: A-300 Albatross
        "b7f_starforce" -> (-120).dp // Ship5: B7F Starforce
        "navakeshi_star_crusher" -> (-102).dp // Ship6: Navakeshi Star Crusher - moved 15% to the right from default (-120 + 18 = -102)
        "b15_specter" -> (-102).dp // Ship7: B15 Specter - moved 15% to the right from default (-120 + 18 = -102)
        "n6_98_melina" -> (-92).dp // Ship8: N6-98 Melina - moved 15% to the left from -80.dp (-80 - 12 = -92)
        "model3_tortoise_ccp" -> (-72).dp // Ship9: Model 3 "Tortoise" CCP - moved 40% to the right from default (-120 + 48 = -72)
        "navakeshi_star_ravager" -> (-102).dp // Ship11: Navakeshi Star Ravager - moved 15% to the right from default (-120 + 18 = -102)
        "h98_valkyrie" -> (-120).dp // Ship10: H-98 Valkyrie
        "asn_h99_dragoon" -> (-100).dp // Ship18: 20dp to the right from Valkyrie (-120.dp + 20.dp = -100.dp)
        "force_of_nature" -> (-120).dp // Ship14: Force of nature
        "dying_star" -> (-120).dp // Ship15: Dying Star
        "asn_ag94_centurion" -> (-112).dp // Ship16: ASN AG94 Centurion - 8dp to the right from default (-120 + 8 = -112)
        "isc_m450_phoenix" -> (-110).dp // Ship17: ISC M450 Phoenix - 10dp to the right from default (-120 + 10 = -110)
        "p7h_skyblazer" -> (-88).dp // Ship19: 10% to the left from -80.dp (-80 * 1.1 = -88)
        "a450_sparrow" -> (-90).dp // Ship20: 20dp to the right from Phoenix (-110.dp + 20.dp = -90.dp)
        "t47_dolphin" -> (-112).dp // Ship21: 40dp to the left from Tortoise (-72.dp - 40.dp = -112.dp)
        "ship22" -> (-120).dp // Ship22: Same offset as ship15
        "ship23" -> (-132.25).dp // Ship23: 30% to the left from -100.dp (-115 * 1.15 = -132.25)
        "legendary_ship" -> (-120).dp // Legendary ship fallback
        else -> (-120).dp // Default offset
    }
}

/**
 * ImpulseLayer composable - displays the ship's engine thrust/impulse effect.
 * The image fades in from 0% to 100% opacity over 4 seconds when travel starts.
 * 
 * @param isTraveling Boolean flag indicating if travel is active
 * @param impulseResId The drawable resource ID for the impulse image
 * @param impulseWidth The width of the impulse image
 * @param impulseHeight The height of the impulse image
 * @param horizontalOffset The horizontal offset from center (negative = left, positive = right)
 * @param modifier Modifier for positioning and sizing
 * 
 * The impulse effect:
 * - Uses the provided impulse image from drawable-nodpi
 * - Ship-specific width and height
 * - Starts at 0% opacity and fades to 100% over 4 seconds when travel begins
 * - Vertically centered
 * - Horizontally offset from center based on ship
 * - Only visible when isTraveling is true
 */
@Composable
fun ImpulseLayer(
    isTraveling: Boolean = false,
    impulseResId: Int = R.drawable.impulse1,
    impulseWidth: androidx.compose.ui.unit.Dp = 600.dp,
    impulseHeight: androidx.compose.ui.unit.Dp = 100.dp,
    horizontalOffset: androidx.compose.ui.unit.Dp = (-120).dp,
    animatedOpacity: Float = 1f, // Animated opacity from parent
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

    // Animate opacity from 0f to 1f over 4 seconds, then apply animatedOpacity from parent
    val baseOpacity by animateFloatAsState(
        targetValue = targetOpacity,
        animationSpec = tween(durationMillis = 4000),
        label = "impulse_fade_in"
    )
    
    // Combine base fade-in with animated opacity from parent
    val finalOpacity = baseOpacity * animatedOpacity

    Box(
        modifier = modifier
            .offset(x = horizontalOffset), // Horizontal offset (negative = left, positive = right)
        contentAlignment = Alignment.CenterEnd
    ) {
        Image(
            painter = painterResource(id = impulseResId),
            contentDescription = "Engine thrust effect",
            modifier = Modifier
                .width(impulseWidth)
                .height(impulseHeight)
                .alpha(finalOpacity), // Apply combined opacity
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
 * MaintenanceArcIndicator composable - displays a single continuous arc representing maintenance time remaining.
 * 
 * The indicator:
 * - Has a diameter of 300dp (radius 150dp)
 * - Spans 60 degrees total
 * - Is positioned aligned with the radar circle (640dp square, centered)
 * - The middle of the arc is aligned with the vertical center (pointing left, opposite of durability arc)
 * - Color: #F87F7F (red)
 * - Arc shrinks from full (60°) to 0° as maintenance time decreases
 * 
 * @param remainingSeconds The remaining maintenance time in seconds
 * @param totalMinutes The total maintenance duration in minutes
 * @param modifier Modifier for positioning and sizing
 */
@Composable
fun MaintenanceArcIndicator(
    remainingSeconds: Int,
    totalMinutes: Int,
    modifier: Modifier = Modifier
) {
    if (remainingSeconds <= 0 || totalMinutes <= 0) {
        return
    }
    
    // Arc specifications
    val arcDiameter = 300.dp
    val arcRadius = 150.dp
    val totalSpanDegrees = 60f // Total span: 60 degrees
    val strokeWidth = 8.dp // Stroke width for arc
    
    // Calculate remaining arc angle based on remaining time
    // remainingSeconds / (totalMinutes * 60) gives progress (0 to 1)
    // Multiply by totalSpanDegrees to get remaining arc angle
    val totalSeconds = totalMinutes * 60
    val remainingArcAngle = if (totalSeconds > 0) {
        totalSpanDegrees * (remainingSeconds.toFloat() / totalSeconds.toFloat())
    } else {
        0f
    }
    
    // Start angle: Middle of arc should be at vertical center (180° = 9 o'clock, pointing left)
    // Arc spans 60 degrees total, so it should go from 150° to 210°
    // Start at 150° (top-left) and sweep remainingArcAngle clockwise
    // As time passes, remainingArcAngle decreases from 60° to 0°
    val startAngle = 150f // Start 30 degrees clockwise from left (top-left)
    
    Canvas(
        modifier = modifier
            .size(arcDiameter)
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = arcRadius.toPx()
        val strokeWidthPx = strokeWidth.toPx()
        
        // Draw the maintenance arc (shrinks as time passes)
        drawArc(
            color = Color(0xFFF87F7F), // Red color #F87F7F
            startAngle = startAngle,
            sweepAngle = remainingArcAngle, // Positive for clockwise, shrinks as time passes
            useCenter = false,
            topLeft = Offset(
                center.x - radius,
                center.y - radius
            ),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            style = Stroke(
                width = strokeWidthPx,
                cap = StrokeCap.Butt // No rounded corners
            )
        )
    }
}

/**
 * DurabilityArcIndicator composable - displays curved segments along an arc representing remaining travels.
 * 
 * The indicator:
 * - Has a diameter of 300dp (radius 150dp)
 * - Spans 60 degrees total (for all segments + gaps)
 * - Is positioned aligned with the radar circle (640dp square, centered)
 * - The middle of the arc is aligned with the vertical center (pointing right)
 * - Only visible when idle
 * - Consumed segments are shown at 20% opacity (starting from the top segment)
 * 
 * @param durability The total durability value (number of travels available)
 * @param consumedTravels The number of consumed travels (segments to show at 20% opacity)
 * @param modifier Modifier for positioning and sizing
 */
@Composable
fun DurabilityArcIndicator(
    durability: Int,
    consumedTravels: Int = 0,
    modifier: Modifier = Modifier
) {
    if (durability <= 0) {
        return
    }
    
    // Arc specifications
    val arcDiameter = 300.dp
    val arcRadius = 150.dp
    val totalSpanDegrees = 60f // Total span for all segments + gaps
    val gapAngleDegrees = 1.5f // Gap angle in degrees (approximately 4dp on the arc)
    val strokeWidth = 8.dp // Stroke width for segments
    
    // Calculate segment angle: (total span - gaps) / number of segments
    val numGaps = durability - 1
    val totalGapAngle = numGaps * gapAngleDegrees
    val segmentAngle = (totalSpanDegrees - totalGapAngle) / durability
    
    // Start angle: Middle of arc should be at vertical center (0° = 3 o'clock, pointing right)
    // Arc spans 60 degrees total, so it should go from -30° to +30°
    // Start at -30° and sweep +60° (clockwise) to reach +30°
    // The first segment (top) is at -30°, and consumption moves clockwise (downward)
    val startAngle = -30f // Start 30 degrees counter-clockwise from right (top-right)
    
    Canvas(
        modifier = modifier
            .size(arcDiameter)
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = arcRadius.toPx()
        val strokeWidthPx = strokeWidth.toPx()
        
        // Draw each segment
        var currentAngle = startAngle
        repeat(durability) { index ->
            // Determine if this segment is consumed (starting from top, moving clockwise)
            val isConsumed = index < consumedTravels
            val opacity = if (isConsumed) 0.2f else 1.0f // 20% opacity for consumed, 100% for remaining
            
            // Draw the segment arc
            drawArc(
                color = Color(0xFFFFFFFF).copy(alpha = opacity), // White with opacity
                startAngle = currentAngle,
                sweepAngle = segmentAngle, // Positive for clockwise
                useCenter = false,
                topLeft = Offset(
                    center.x - radius,
                    center.y - radius
                ),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(
                    width = strokeWidthPx,
                    cap = StrokeCap.Butt // No rounded corners
                )
            )
            
            // Move to next segment position (segment + gap)
            currentAngle += (segmentAngle + gapAngleDegrees)
        }
    }
}

/**
 * CountdownRing composable - displays a circular progress ring that visually represents remaining time.
 * The ring is drawn using Canvas and shows a shrinking arc as time progresses.
 * When first appearing, the ring animates from 0° to full circle over 5 seconds.
 * 
 * @param selectedMinutes The total selected time duration in minutes (5-60 range)
 * @param remainingSeconds The current remaining time in seconds (decreases every second during countdown)
 * @param isInitialAppearance Boolean flag indicating if this is the first time the ring appears
 * @param isVisible Boolean flag controlling ring visibility (can be toggled during travel)
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
 * - Can fade in/out when isVisible changes (1.5 second animation)
 */
@Composable
fun CountdownRing(
    selectedMinutes: Int,
    remainingSeconds: Int,
    isInitialAppearance: Boolean = false,
    isVisible: Boolean = true,
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
    
    // Animate visibility (fade in/out) when isVisible changes
    val ringAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1500), // 1.5 seconds fade
        label = "ring_visibility_fade"
    )

    // Don't render the ring if countdown has reached 0 or if selectedMinutes is invalid
    if (remainingSeconds <= 0 || selectedMinutes <= 0) {
        return
    }

    // Canvas composable to draw the circular arc
    Canvas(
        modifier = modifier
            .size(274.dp)
            .alpha(ringAlpha) // Apply fade animation
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
 * 
 * @param currentShip The currently selected ship (used to display correct ship and impulse images)
 * @param modifier Modifier for the screen
 * @param isIdleCallback Callback to notify parent when idle state changes (true when idle, false when preparing/traveling)
 * @param activeScreen The currently active screen for indicator display
 * @param onCareerClick Callback for career icon click
 * @param onCollectionClick Callback for collection icon click
 * @param onRewardsScreenVisibilityChange Callback to notify parent when rewards screen visibility changes
 */
@Composable
fun GalaxyScreen(
    currentShip: Ship,
    modifier: Modifier = Modifier,
    isIdleCallback: (Boolean) -> Unit = {},
    activeScreen: ActiveScreen = ActiveScreen.CENTER,
    onCareerClick: () -> Unit = {},
    onCollectionClick: () -> Unit = {},
    onRewardsScreenVisibilityChange: (Boolean) -> Unit = {},
    onShipUnlockedScreenVisibilityChange: (Boolean) -> Unit = {},
    onLocationDiscoveredScreenVisibilityChange: (Boolean) -> Unit = {},
    onBoostSelectionBottomSheetVisibilityChange: (Boolean) -> Unit = {},
    onShowToast: (String) -> Unit = {}
) {
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
    
    // isRingVisible: Controls visibility of the countdown ring
    // Can be toggled by tapping the ship during travel
    var isRingVisible by remember { mutableStateOf(true) }
    
    // isPreparingLaunch: Boolean flag that indicates if the launch preparation countdown is active
    // When true, shows 3-second countdown before actual travel begins
    var isPreparingLaunch by remember { mutableStateOf(false) }
    
    // penaltyCount: Current penalty count for the active travel session
    // Resets to 0 when travel starts, increments when app goes to background (with grace period)
    var penaltyCount by remember { mutableStateOf(0) }
    
    // launchCountdown: The countdown number displayed during launch preparation (3, 2, 1)
    var launchCountdown by remember { mutableStateOf(3) }
    
    // showTravelSuccessModal: Controls visibility of the success modal when travel completes
    var showTravelSuccessModal by remember { mutableStateOf(false) }
    
    // showTravelCanceledModal: Controls visibility of the cancellation modal when travel is canceled
    var showTravelCanceledModal by remember { mutableStateOf(false) }
    var cancellationReason by remember { mutableStateOf("timeout") } // "timeout" or "penalties"
    
    // showRepairNeededModal: Controls visibility of the repair needed modal when ship runs out of travels
    var showRepairNeededModal by remember { mutableStateOf(false) }
    
    // pendingRepairModal: Flag to track if repair modal should be shown after all other screens are done
    var pendingRepairModal by remember { mutableStateOf(false) }
    
    // showBoostSelectionBottomSheet: Controls visibility of the boost selection bottom sheet
    var showBoostSelectionBottomSheet by remember { mutableStateOf(false) }
    
    // Scanner screen states
    var showScannerProgress by remember { mutableStateOf(false) }
    var showScannerResults by remember { mutableStateOf(false) }
    
    // Experimental fuel removal confirmation modal
    var showExperimentalFuelRemoveModal by remember { mutableStateOf(false) }
    
    // Unstable cargo cancellation modal
    var showUnstableCargoCanceledModal by remember { mutableStateOf(false) }

    // Flight control motivational quotes for launch start toast
    val flightControlQuotes = listOf(
        "Go do something awesome.",
        "A focused pilot is a capable pilot.",
        "Set your course and engage.",
        "The galaxy rewards disciplined pilots.",
        "Time to focus. The mission awaits.",
        "One session at a time.",
        "Stay sharp out there, pilot.",
        "Every mission counts.",
        "Remember why you started this mission.",
        "Your future self will appreciate this session.",
        "This is the part where you actually work.",
        "Focus engaged."
    )
    
    // Notify parent when bottom sheet visibility changes
    LaunchedEffect(showBoostSelectionBottomSheet) {
        onBoostSelectionBottomSheetVisibilityChange(showBoostSelectionBottomSheet)
    }
    
    // showRewardsScreen: Controls visibility of the rewards screen
    var showRewardsScreen by remember { mutableStateOf(false) }
    
    // travelMinutes: The duration of the completed travel (for rewards calculation)
    var travelMinutes by remember { mutableStateOf(0) }
    
    // travelPenaltyCount: The penalty count for the completed travel (captured before reset)
    var travelPenaltyCount by remember { mutableStateOf(0) }
    
    // travelEquippedItem: The equipped item at the time of travel completion (captured before consumption)
    var travelEquippedItem by remember { mutableStateOf<String?>(null) }
    
    // travelHasUnstableCargoPenalty: Whether unstable cargo penalty occurred during travel
    var travelHasUnstableCargoPenalty by remember { mutableStateOf(false) }
    
    // travelEmergencyModulatorUsed: Whether emergency modulator was used during travel (captured before consumption)
    var travelEmergencyModulatorUsed by remember { mutableStateOf(false) }
    
    // Track if travel was cancelled (to avoid showing modal if cancelled)
    var wasTravelCancelled by remember { mutableStateOf(false) }
    
    // Track travel session start time to calculate elapsed focus time
    var travelStartTime by remember { mutableStateOf(0L) } // System time in milliseconds
    
    // Track actual travel duration in seconds (may be reduced by experimental fuel)
    var actualTravelDurationSeconds by remember { mutableStateOf(0) }
    
    // TODO: REMOVE TESTING CODE - Track if in test mode (10 seconds)
    var isTestMode by remember { mutableStateOf(false) }
    
    // Track app lifecycle to recalculate timer when app resumes
    var appLifecycleState by remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    
    // Periodic timer trigger: Updates every second to force recalculation
    // This ensures the timer continues even when app is in background
    var timerTrigger by remember { mutableStateOf(0L) }
    LaunchedEffect(isTraveling) {
        if (isTraveling) {
            while (isTraveling) {
                delay(1000) // Update every second
                timerTrigger = System.currentTimeMillis() // Trigger recomposition
            }
        }
    }
    
    // Maintenance timer: Updates every second to check maintenance status
    // This ensures maintenance time continues even when app is in background
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000) // Update every second
            timerTrigger = System.currentTimeMillis() // Trigger recomposition for maintenance too
            
            // Check if maintenance is complete for current ship
            // Get remaining time directly - if it's <= 0, maintenance should be completed
            val remainingSeconds = com.example.fargalaxy.data.GameStateRepository.getRemainingMaintenanceTime(currentShip.id)
            val hasMaintenanceData = com.example.fargalaxy.data.GameStateRepository.hasMaintenanceData(currentShip.id)
            
            // If remaining time is 0 or negative, and maintenance data exists, complete maintenance
            // This handles the case where time expired but completeMaintenance wasn't called yet
            if (remainingSeconds <= 0 && hasMaintenanceData) {
                // Maintenance complete - reset consumed travels and clear maintenance data
                com.example.fargalaxy.data.GameStateRepository.completeMaintenance(currentShip.id)
            }
        }
    }
    
    // Calculate remaining time based on elapsed time (works even when app is in background)
    // This derived state recalculates whenever travelStartTime or timerTrigger changes
    // Uses actualTravelDurationSeconds which accounts for experimental fuel reduction
    val calculatedRemainingSeconds = derivedStateOf {
        if (isTraveling && travelStartTime > 0 && actualTravelDurationSeconds > 0) {
            val travelDurationMillis = actualTravelDurationSeconds * 1000L
            val elapsedMillis = System.currentTimeMillis() - travelStartTime
            ((travelDurationMillis - elapsedMillis) / 1000).toInt().coerceAtLeast(0)
        } else {
            remainingSeconds
        }
    }
    
    // Update remainingSeconds from calculated value (ensures it stays in sync)
    // This LaunchedEffect ensures the timer continues even when app is in background
    LaunchedEffect(calculatedRemainingSeconds.value, isTraveling, timerTrigger) {
        if (isTraveling && travelStartTime > 0) {
            remainingSeconds = calculatedRemainingSeconds.value
            
            // Check if travel completed
            if (remainingSeconds <= 0 && !wasTravelCancelled) {
                // Capture penalty count before resetting
                travelPenaltyCount = com.example.fargalaxy.data.PenaltyTracker.getPenaltyCount()
                isTraveling = false
                travelMinutes = if (isTestMode) {
                    0
                } else {
                    selectedMinutes
                }
                // Capture equipment info BEFORE consumption (for RewardsScreen display)
                val equippedItem = com.example.fargalaxy.data.EquipmentRepository.getEquippedItem()
                travelEquippedItem = equippedItem
                travelHasUnstableCargoPenalty = com.example.fargalaxy.data.EquipmentUsageRepository.hasUnstableCargoPenalty()
                travelEmergencyModulatorUsed = com.example.fargalaxy.data.EquipmentUsageRepository.isEmergencyModulatorUsed()
                
                // Handle equipment consumption
                when (equippedItem) {
                    "emergency_modulator" -> {
                        // Consumed after 1 travel - remove from inventory and unequip
                        com.example.fargalaxy.data.InventoryRepository.removeItem("emergency_modulator", 1)
                        com.example.fargalaxy.data.EquipmentRepository.unequipItem()
                        com.example.fargalaxy.data.EquipmentUsageRepository.resetUsage()
                    }
                    "unstable_cargo" -> {
                        // Consumed after 1 travel - remove from inventory and unequip
                        com.example.fargalaxy.data.InventoryRepository.removeItem("unstable_cargo", 1)
                        com.example.fargalaxy.data.EquipmentRepository.unequipItem()
                        com.example.fargalaxy.data.EquipmentUsageRepository.resetUsage()
                    }
                    "experimental_fuel" -> {
                        // Decrement remaining travels
                        val remaining = com.example.fargalaxy.data.EquipmentUsageRepository.decrementExperimentalFuel()
                        if (remaining <= 0) {
                            // All travels consumed - unequip
                            com.example.fargalaxy.data.EquipmentRepository.unequipItem()
                            com.example.fargalaxy.data.EquipmentUsageRepository.resetUsage()
                        }
                    }
                }
                
                // Consume one travel after trip completes
                com.example.fargalaxy.data.GameStateRepository.consumeTravel(currentShip.id)
                
                // Check if all travels are consumed - if so, start maintenance
                val durability = getDurabilityValue(currentShip.id)
                val consumedTravels = com.example.fargalaxy.data.GameStateRepository.getConsumedTravels(currentShip.id)
                if (consumedTravels >= durability) {
                    val wasAlreadyInMaintenance = com.example.fargalaxy.data.GameStateRepository.isShipInMaintenance(currentShip.id)
                    com.example.fargalaxy.data.GameStateRepository.startMaintenance(currentShip.id)
                    // Mark repair modal as pending - will show after all other screens (success modal, rewards, unlocks)
                    if (!wasAlreadyInMaintenance) {
                        pendingRepairModal = true
                    }
                }
                
                showTravelSuccessModal = true
                com.example.fargalaxy.data.PenaltyTracker.stopTracking()
                penaltyCount = 0
            }
        }
    }
    
    // Observe app lifecycle to detect when app resumes and trigger timer recalculation
    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            appLifecycleState = event
            // When app resumes and we're traveling, trigger timer recalculation
            // This ensures the timer continues even if it was paused in background
            if (event == Lifecycle.Event.ON_START && isTraveling && travelStartTime > 0) {
                timerTrigger = System.currentTimeMillis() // Force recalculation
            }
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(observer)
        onDispose {
            ProcessLifecycleOwner.get().lifecycle.removeObserver(observer)
        }
    }
    
    // Track unlocked ships/locations before session starts (to detect newly unlocked items)
    var unlockedShipsBeforeSession by remember { mutableStateOf<Set<String>>(emptySet()) }
    var unlockedLocationsBeforeSession by remember { mutableStateOf<Set<String>>(emptySet()) }
    
    // Track newly unlocked ships/locations during this session
    var newlyUnlockedShips by remember { mutableStateOf<List<String>>(emptyList()) }
    var newlyDiscoveredLocations by remember { mutableStateOf<List<String>>(emptyList()) }
    
    // Track which unlock/discovery screen to show
    var showShipUnlockedScreen by remember { mutableStateOf(false) }
    var showLocationDiscoveredScreen by remember { mutableStateOf(false) }
    var currentShipUnlockedIndex by remember { mutableStateOf(0) }
    var currentLocationDiscoveredIndex by remember { mutableStateOf(0) }
    
    // Notify parent about idle state changes
    LaunchedEffect(isPreparingLaunch, isTraveling) {
        val isIdle = !isPreparingLaunch && !isTraveling
        isIdleCallback(isIdle)
    }
    
    // Notify parent about rewards screen visibility
    LaunchedEffect(showRewardsScreen) {
        onRewardsScreenVisibilityChange(showRewardsScreen)
    }
    
    // Notify parent about ship unlocked screen visibility
    LaunchedEffect(showShipUnlockedScreen) {
        onShipUnlockedScreenVisibilityChange(showShipUnlockedScreen)
    }
    
    // Notify parent about location discovered screen visibility
    LaunchedEffect(showLocationDiscoveredScreen) {
        onLocationDiscoveredScreenVisibilityChange(showLocationDiscoveredScreen)
    }
    
    // Handler: Increment selectedMinutes by 5, clamped to maximum of 60
    // Only works when not traveling (buttons are disabled during travel)
    fun onIncrement() {
        if (!isTraveling) {
            // TODO: REMOVE TESTING CODE - Exit test mode when incrementing
            if (isTestMode) {
                isTestMode = false
                selectedMinutes = 5
            } else {
            selectedMinutes = (selectedMinutes + 5).coerceAtMost(60)
            }
        }
    }

    // Handler: Decrement selectedMinutes by 5, clamped to minimum of 5
    // Only works when not traveling (buttons are disabled during travel)
    // TODO: REMOVE TESTING CODE - Allow going below 5 to enter test mode (10 seconds)
    fun onDecrement() {
        if (!isTraveling) {
            if (selectedMinutes == 5 && !isTestMode) {
                // At 5 minutes, go to test mode (10 seconds)
                isTestMode = true
                selectedMinutes = 5 // Keep at 5 for display, but will use 10 seconds
            } else if (isTestMode) {
                // Already in test mode, go back to 5 minutes
                isTestMode = false
                selectedMinutes = 5
            } else {
            selectedMinutes = (selectedMinutes - 5).coerceAtLeast(5)
            }
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
            wasTravelCancelled = false // Reset cancelled flag
            
            // Store current unlocked ships/locations before session starts
            val allShips = com.example.fargalaxy.data.ShipRepository.getAllShips()
            unlockedShipsBeforeSession = allShips
                .filter { com.example.fargalaxy.data.GameStateRepository.isShipUnlocked(it.id) }
                .map { it.id }
                .toSet()
            
            val allLocations = com.example.fargalaxy.data.LocationRepository.getAllLocations()
            unlockedLocationsBeforeSession = allLocations
                .filter { com.example.fargalaxy.data.GameStateRepository.isLocationUnlocked(it.id) }
                .map { it.id }
                .toSet()
        } else {
            // Stop travel: Cancel countdown
            isTraveling = false
            isInitialRingAppearance = false // Reset for next launch
            wasTravelCancelled = true // Mark as cancelled
            
            // Handle equipment consumption for manually stopped travel
            val equippedItem = com.example.fargalaxy.data.EquipmentRepository.getEquippedItem()
            when (equippedItem) {
                "emergency_modulator" -> {
                    // Consumed after 1 travel - remove from inventory and unequip
                    com.example.fargalaxy.data.InventoryRepository.removeItem("emergency_modulator", 1)
                    com.example.fargalaxy.data.EquipmentRepository.unequipItem()
                    com.example.fargalaxy.data.EquipmentUsageRepository.resetUsage()
                }
                "unstable_cargo" -> {
                    // Consumed after 1 travel - remove from inventory and unequip
                    com.example.fargalaxy.data.InventoryRepository.removeItem("unstable_cargo", 1)
                    com.example.fargalaxy.data.EquipmentRepository.unequipItem()
                    com.example.fargalaxy.data.EquipmentUsageRepository.resetUsage()
                }
                "experimental_fuel" -> {
                    // Decrement remaining travels
                    val remaining = com.example.fargalaxy.data.EquipmentUsageRepository.decrementExperimentalFuel()
                    if (remaining <= 0) {
                        // All travels consumed - unequip
                        com.example.fargalaxy.data.EquipmentRepository.unequipItem()
                        com.example.fargalaxy.data.EquipmentUsageRepository.resetUsage()
                    }
                }
            }
            
            // Consume one travel after trip is manually cancelled
            com.example.fargalaxy.data.GameStateRepository.consumeTravel(currentShip.id)
            
            // Check if all travels are consumed - if so, start maintenance
            val durability = getDurabilityValue(currentShip.id)
            val consumedTravels = com.example.fargalaxy.data.GameStateRepository.getConsumedTravels(currentShip.id)
            if (consumedTravels >= durability) {
                val wasAlreadyInMaintenance = com.example.fargalaxy.data.GameStateRepository.isShipInMaintenance(currentShip.id)
                com.example.fargalaxy.data.GameStateRepository.startMaintenance(currentShip.id)
                // Mark repair modal as pending - will show after all other screens (success modal, rewards, unlocks)
                // For manual cancellation, show directly since there's no cancellation modal
                if (!wasAlreadyInMaintenance) {
                    pendingRepairModal = true
                    // Show repairs modal directly since manual cancellation doesn't show a cancellation modal
                    // LaunchedEffect will handle it, but we can also trigger it here as a fallback
                }
            }
            
            // Stop penalty tracking when travel is cancelled
            com.example.fargalaxy.data.PenaltyTracker.stopTracking()
            penaltyCount = 0 // Reset penalty count
            
            // Calculate elapsed focus time when cancelled and add it
            if (travelStartTime > 0) {
                val elapsedMillis = System.currentTimeMillis() - travelStartTime
                val elapsedMinutes = (elapsedMillis / 60_000).toInt() // Convert to minutes
                
                // Add focus time (always, even when cancelled)
                if (elapsedMinutes > 0) {
                    com.example.fargalaxy.data.UserDataRepository.addFocusTime(elapsedMinutes)
                    // Sync unlocked ships based on new focus time
                    com.example.fargalaxy.data.GameStateRepository.syncUnlockedShipsFromFocusTime()
                }
                
                // Reset start time and travel duration
                travelStartTime = 0L
                actualTravelDurationSeconds = 0
            }
        }
    }
    
    // Initialize travel start time and penalty tracking when travel begins
    // This LaunchedEffect only sets up the initial state
    LaunchedEffect(isTraveling, currentShip.id) {
        if (isTraveling && travelStartTime == 0L) {
            wasTravelCancelled = false // Reset cancelled flag when travel starts
            travelStartTime = System.currentTimeMillis() // Record start time
            
            // Capture ship ID for use in callback
            val shipIdForCallback = currentShip.id
            
            // Start penalty tracking when travel begins
            // Set up callback for trip cancellation (if user is away for >20 seconds or has 5+ penalties)
            com.example.fargalaxy.data.PenaltyTracker.onTripCancelled = { reason ->
                // Cancel the trip
                wasTravelCancelled = true
            isTraveling = false
                cancellationReason = reason // Store the cancellation reason
                com.example.fargalaxy.data.PenaltyTracker.stopTracking()
                penaltyCount = 0
                
                // Calculate elapsed focus time and add it (even when cancelled)
                if (travelStartTime > 0) {
                    val elapsedMillis = System.currentTimeMillis() - travelStartTime
                    val elapsedMinutes = (elapsedMillis / 60_000).toInt()
                    val focusTimeMinutes = if (elapsedMillis > 0 && elapsedMinutes == 0) {
                        1
                    } else {
                        elapsedMinutes
                    }
                    if (focusTimeMinutes > 0) {
                        com.example.fargalaxy.data.UserDataRepository.addFocusTime(focusTimeMinutes)
                        com.example.fargalaxy.data.GameStateRepository.syncUnlockedShipsFromFocusTime()
                        com.example.fargalaxy.data.GameStateRepository.syncUnlockedLocationsFromFocusTime()
                    }
                    travelStartTime = 0L
                }
                
                // Handle equipment consumption for trip cancellation
                val equippedItem = com.example.fargalaxy.data.EquipmentRepository.getEquippedItem()
                var willShowCancellationModal = false
                when (equippedItem) {
                    "emergency_modulator" -> {
                        // Consumed after 1 travel - remove from inventory and unequip
                        com.example.fargalaxy.data.InventoryRepository.removeItem("emergency_modulator", 1)
                        com.example.fargalaxy.data.EquipmentRepository.unequipItem()
                        com.example.fargalaxy.data.EquipmentUsageRepository.resetUsage()
                        // Show cancellation modal even with emergency modulator
                        showTravelCanceledModal = true
                        willShowCancellationModal = true
                    }
                    "unstable_cargo" -> {
                        // Consumed after 1 travel - remove from inventory and unequip
                        com.example.fargalaxy.data.InventoryRepository.removeItem("unstable_cargo", 1)
                        com.example.fargalaxy.data.EquipmentRepository.unequipItem()
                        com.example.fargalaxy.data.EquipmentUsageRepository.resetUsage()
                        // Show unstable cargo cancellation modal if penalty occurred
                        if (reason == "unstable_cargo") {
                            showUnstableCargoCanceledModal = true
                        } else {
                            showTravelCanceledModal = true
                        }
                        willShowCancellationModal = true
                    }
                    "experimental_fuel" -> {
                        // Decrement remaining travels
                        val remaining = com.example.fargalaxy.data.EquipmentUsageRepository.decrementExperimentalFuel()
                        if (remaining <= 0) {
                            // All travels consumed - unequip
                            com.example.fargalaxy.data.EquipmentRepository.unequipItem()
                            com.example.fargalaxy.data.EquipmentUsageRepository.resetUsage()
                        }
                        // Show cancellation modal
                        showTravelCanceledModal = true
                        willShowCancellationModal = true
                    }
                    else -> {
                        // No equipment - show regular cancellation modal
                        showTravelCanceledModal = true
                        willShowCancellationModal = true
                    }
                }
                
                // Consume one travel after trip is cancelled
                com.example.fargalaxy.data.GameStateRepository.consumeTravel(shipIdForCallback)
                
                // Check if all travels are consumed - if so, start maintenance
                val durability = getDurabilityValue(shipIdForCallback)
                val consumedTravels = com.example.fargalaxy.data.GameStateRepository.getConsumedTravels(shipIdForCallback)
                if (consumedTravels >= durability) {
                    val wasAlreadyInMaintenance = com.example.fargalaxy.data.GameStateRepository.isShipInMaintenance(shipIdForCallback)
                    com.example.fargalaxy.data.GameStateRepository.startMaintenance(shipIdForCallback)
                    // Mark repair modal as pending - will show after cancelled modal is closed, or directly if no modal is shown
                    if (!wasAlreadyInMaintenance) {
                        pendingRepairModal = true
                        // If no cancellation modal will be shown, show repairs modal directly
                        if (!willShowCancellationModal) {
                            showRepairNeededModal = true
                            pendingRepairModal = false
                        }
                    }
                }
            }
            com.example.fargalaxy.data.PenaltyTracker.startTracking()
        } else if (!isTraveling && travelStartTime > 0L) {
            // Travel ended - stop penalty tracking and reset
            com.example.fargalaxy.data.PenaltyTracker.stopTracking()
            penaltyCount = 0 // Reset penalty count
            
            // Calculate elapsed focus time and add it to counters (regardless of completion)
            // This happens when travel ends (either completed or cancelled)
            if (travelStartTime > 0) {
                // For successful completions, use selectedMinutes (already set in the completion handler)
                // For cancelled travels, calculate actual elapsed time
                val focusTimeMinutes = if (!wasTravelCancelled && travelMinutes > 0) {
                    // Travel completed successfully - use the selected minutes
                    travelMinutes
                } else {
                    // Travel was cancelled - calculate actual elapsed time
                    val elapsedMillis = System.currentTimeMillis() - travelStartTime
                    val elapsedMinutes = (elapsedMillis / 60_000).toInt() // Convert to minutes
                    // Minimum 1 minute if any time was spent (even if less than 1 minute)
                    if (elapsedMillis > 0 && elapsedMinutes == 0) {
                        1 // Less than 1 minute but some time was spent
                    } else {
                        elapsedMinutes
                    }
                }
                
                if (focusTimeMinutes > 0) {
                    com.example.fargalaxy.data.UserDataRepository.addFocusTime(focusTimeMinutes)
                    // Sync unlocked ships and locations based on new focus time
                    com.example.fargalaxy.data.GameStateRepository.syncUnlockedShipsFromFocusTime()
                    com.example.fargalaxy.data.GameStateRepository.syncUnlockedLocationsFromFocusTime()
                    
                    // Detect newly unlocked ships and locations
                    val allShips = com.example.fargalaxy.data.ShipRepository.getAllShips()
                    val unlockedShipsAfter = allShips
                        .filter { com.example.fargalaxy.data.GameStateRepository.isShipUnlocked(it.id) }
                        .map { it.id }
                        .toSet()
                    newlyUnlockedShips = (unlockedShipsAfter - unlockedShipsBeforeSession).toList()
                    
                    val allLocations = com.example.fargalaxy.data.LocationRepository.getAllLocations()
                    val unlockedLocationsAfter = allLocations
                        .filter { com.example.fargalaxy.data.GameStateRepository.isLocationUnlocked(it.id) }
                        .map { it.id }
                        .toSet()
                    newlyDiscoveredLocations = (unlockedLocationsAfter - unlockedLocationsBeforeSession).toList()
                }
                
                // Reset start time and travel duration
                travelStartTime = 0L
                actualTravelDurationSeconds = 0
            }
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
    
    // Update penalty count periodically while traveling (uses timerTrigger to update even in background)
    // Also watch for emergency modulator usage to update immediately
    val isEmergencyModulatorUsedForUpdate = com.example.fargalaxy.data.EquipmentUsageRepository.isEmergencyModulatorUsed()
    LaunchedEffect(isTraveling, timerTrigger, isEmergencyModulatorUsedForUpdate) {
        if (isTraveling) {
            penaltyCount = com.example.fargalaxy.data.PenaltyTracker.getPenaltyCount()
        } else {
            // Reset penalty count when travel stops
            penaltyCount = 0
        }
    }
    
    // Launch preparation countdown: 3-second countdown before actual travel begins
    // Decrements launchCountdown every second (3, 2, 1)
    // Get context for MediaPlayer
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // After countdown completes, automatically starts travel if not cancelled
    LaunchedEffect(isPreparingLaunch) {
        if (isPreparingLaunch) {
            launchCountdown = 3 // Reset to 3
            while (isPreparingLaunch && launchCountdown > 0) {
                // Play beep sound for current countdown number (3, 2, or 1)
                val beepPlayer = MediaPlayer.create(context, R.raw.beep)
                beepPlayer?.let { player ->
                    try {
                        player.setVolume(1f, 1f)
                        player.start()
                        
                        // Play for 1 second (duration the number is on screen)
                        delay(1000)
                        
                        player.stop()
                        player.release()
                    } catch (e: Exception) {
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
                
                if (isPreparingLaunch) { // Check if still preparing (not cancelled)
                    launchCountdown--
                }
            }
            // After countdown completes, play starship sound in parallel and start travel if still preparing
            if (isPreparingLaunch) {
                // Start starship sound in parallel (don't wait for it to finish)
                val starshipPlayer = MediaPlayer.create(context, R.raw.starship)
                starshipPlayer?.let { player ->
                    try {
                        player.setVolume(1f, 1f)
                        player.start()
                        
                        // Play in background - launch a coroutine to handle cleanup after sound finishes
                        coroutineScope.launch {
                            // Get duration in milliseconds
                            val duration = player.duration
                            if (duration > 0) {
                                delay(duration.toLong())
                            }
                            
                            // Clean up after sound finishes
                            try {
                                if (player.isPlaying) {
                                    player.stop()
                                }
                                player.release()
                            } catch (e: Exception) {
                                // Ignore cleanup errors
                            }
                        }
                    } catch (e: Exception) {
                        // If starting fails, try to release
                        try {
                            player.release()
                        } catch (e2: Exception) {
                            // Ignore release errors
                        }
                    }
                }
                
                // Show flight control motivational toast when travel actually starts
                val randomQuote = flightControlQuotes.random()
                onShowToast(randomQuote)

                // Start travel immediately (don't wait for sound)
                isPreparingLaunch = false
                isTraveling = true
                // TODO: REMOVE TESTING CODE - Use 10 seconds in test mode
                // Apply 10% reduction for experimental fuel if equipped
                val equippedItem = com.example.fargalaxy.data.EquipmentRepository.getEquippedItem()
                val baseSeconds = if (isTestMode) 10 else selectedMinutes * 60
                actualTravelDurationSeconds = if (equippedItem == "experimental_fuel") {
                    (baseSeconds * 0.9f).toInt() // 10% reduction
                } else {
                    baseSeconds
                }
                remainingSeconds = actualTravelDurationSeconds
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
            // Reset ring visibility when travel starts
            isRingVisible = true
            
            // Increment session ID for this travel session
            val currentSessionId = System.currentTimeMillis()
            travelSessionId = currentSessionId

            // Launch coroutine to show speed effect after 3 seconds
            val job = speedEffectScope.launch {
                delay(3000) // Wait 3 seconds
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
            // Reset ring visibility when travel stops
            isRingVisible = true
        }
    }
    
    // Main container Box that fills the entire screen
    // Note: Background and noise are handled by MainScreen (static layers)
    Box(modifier = modifier.fillMaxSize()) {
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
        
        // Durability arc indicator: Curved segments along an arc representing remaining travels.
        // Positioned aligned with the radar circle (640dp square, centered).
        // The middle of the arc is aligned with the vertical center (pointing right).
        // Only visible when idle (not traveling, not preparing) and not in maintenance.
        if (!isTraveling && !isPreparingLaunch) {
            val durability = getDurabilityValue(currentShip.id)
            // Use timerTrigger to ensure reactive updates when maintenance completes
            val consumedTravels = remember(timerTrigger, currentShip.id) {
                com.example.fargalaxy.data.GameStateRepository.getConsumedTravels(currentShip.id)
            }
            val isInMaintenance = remember(timerTrigger, currentShip.id) {
                com.example.fargalaxy.data.GameStateRepository.isShipInMaintenance(currentShip.id)
            }
            
            // Only show durability arc if not in maintenance
            // When maintenance completes, consumedTravels resets to 0, and all segments show full opacity
            if (!isInMaintenance) {
                DurabilityArcIndicator(
                    durability = durability,
                    consumedTravels = consumedTravels, // Reactive: updates when maintenance completes
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(300.dp) // Arc diameter
                )
            }
            
            // Maintenance arc indicator: Single continuous arc representing maintenance time remaining.
            // Positioned aligned with the radar circle (640dp square, centered).
            // The middle of the arc is aligned with the vertical center (pointing left, opposite of durability arc).
            // Only visible when idle and in maintenance.
            // Use timerTrigger to ensure updates even when app is in background
            val remainingMaintenanceSeconds = remember(timerTrigger) {
                com.example.fargalaxy.data.GameStateRepository.getRemainingMaintenanceTime(currentShip.id)
            }
            
            if (isInMaintenance && remainingMaintenanceSeconds > 0) {
                val maintenanceMinutes = getMaintenanceTime(currentShip.id)
                
                // Show maintenance arc
                MaintenanceArcIndicator(
                    remainingSeconds = remainingMaintenanceSeconds,
                    totalMinutes = maintenanceMinutes,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(300.dp) // Arc diameter
                )
            }
        }

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

        // Tilt-based movement for ship and impulse during travel
        val context = LocalContext.current
        val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager }
        val accelerometer = remember { sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
        val coroutineScope = rememberCoroutineScope()
        
        // Target offsets based on tilt (will be animated to)
        var targetVerticalOffset by remember { mutableStateOf(0f) } // in dp
        var targetHorizontalOffset by remember { mutableStateOf(0f) } // in dp
        
        // Track if we're in the last 10 seconds (need to return to center)
        val isLast10Seconds = remainingSeconds <= 10 && isTraveling
        
        // Sensor listener to detect device tilt
        // Capture remainingSeconds to check in the last 10 seconds
        var currentRemainingSeconds by remember { mutableStateOf(remainingSeconds) }
        
        // Update currentRemainingSeconds when remainingSeconds changes
        LaunchedEffect(remainingSeconds) {
            currentRemainingSeconds = remainingSeconds
        }
        
        DisposableEffect(isTraveling, accelerometer, sensorManager) {
            var listener: SensorEventListener? = null
            
            if (isTraveling && accelerometer != null && sensorManager != null) {
                listener = object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent?) {
                        if (event == null) return
                        
                        // Don't respond to tilt in the last 10 seconds - ship should stay centered
                        // Ignore all sensor input if we're in the last 10 seconds
                        if (currentRemainingSeconds <= 10) {
                            return // Don't update offsets, ship will be forced to center by LaunchedEffect
                        }
                        
                        // Get accelerometer values (x, y, z)
                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]
                        
                        // Calculate tilt sensitivity
                        // For a 10-degree tilt: sin(10°) ≈ 0.174, so acceleration component ≈ 0.174 * 9.8 ≈ 1.7 m/s²
                        // We want this to give us good movement (maybe 15-18dp)
                        // So sensitivity = 15 / 1.7 ≈ 8.8, we'll use 9 for good responsiveness
                        val sensitivity = 9f
                        
                        // Calculate tilt in horizontal direction only
                        // X axis: In portrait mode, when you tilt LEFT, X becomes negative
                        //        When you tilt RIGHT, X becomes positive
                        //        We want: tilt left → ship moves left (negative offset)
                        //                 tilt right → ship moves right (positive offset)
                        //        So we use -x to flip the direction
                        val tiltX = -x * sensitivity // Negate X so left tilt = left movement, right tilt = right movement
                        
                        // Check if device is relatively level horizontally (tilt is minimal)
                        // Use a threshold of ~0.2 m/s² which corresponds to ~2dp movement
                        val levelThreshold = 0.2f // m/s²
                        
                        // Update state on main thread to ensure recomposition
                        val newHorizontal = if (abs(x) < levelThreshold) {
                            0f
                        } else {
                            tiltX.coerceIn(-20f, 20f) // Max movement reduced to 20dp
                        }
                        
                        // Vertical movement is disabled - always keep at center
                        val newVertical = 0f
                        
                        // Only update if values changed to avoid unnecessary recompositions
                        if (abs(targetHorizontalOffset - newHorizontal) > 0.1f || abs(targetVerticalOffset - newVertical) > 0.1f) {
                            targetHorizontalOffset = newHorizontal
                            targetVerticalOffset = newVertical
                        }
                    }
                    
                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                        // Not needed for this use case
                    }
                }
                
                // Register sensor listener
                sensorManager.registerListener(
                    listener,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_GAME // Use GAME delay for faster updates (~50Hz)
                )
            } else {
                targetVerticalOffset = 0f
                targetHorizontalOffset = 0f
            }
            
            onDispose {
                listener?.let {
                    sensorManager?.unregisterListener(it)
                }
            }
        }
        
        // Force center in last 10 seconds
        LaunchedEffect(isLast10Seconds) {
            if (isLast10Seconds) {
                targetVerticalOffset = 0f
                targetHorizontalOffset = 0f
            }
        }
        
        // Animate to target position smoothly over 4 seconds
        val verticalOffset = animateDpAsState(
            targetValue = targetVerticalOffset.dp,
            animationSpec = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            label = "vertical_offset"
        ).value
        
        val horizontalOffset = animateDpAsState(
            targetValue = targetHorizontalOffset.dp,
            animationSpec = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            label = "horizontal_offset"
        ).value
        
        // Calculate impulse opacity: 100% -> 60% (2s) -> 100% (2s), repeating every 4 seconds
        // Keep the opacity animation separate from tilt movement
        var opacityAnimationStartTime by remember { mutableStateOf(0L) }
        var opacityProgress by remember { mutableStateOf(0f) }
        
        LaunchedEffect(isTraveling) {
            if (isTraveling) {
                opacityAnimationStartTime = System.currentTimeMillis()
                opacityProgress = 0f
            } else {
                opacityProgress = 0f
            }
        }
        
        LaunchedEffect(isTraveling, timerTrigger) {
            if (isTraveling) {
                while (isTraveling) {
                    val elapsed = System.currentTimeMillis() - opacityAnimationStartTime
                    val cycleDuration = 4000L // 4 seconds
                    opacityProgress = ((elapsed % cycleDuration).toFloat() / cycleDuration.toFloat())
                    delay(16) // Update ~60 times per second
                }
            } else {
                opacityProgress = 0f
            }
        }
        
        val impulseOpacity = remember(opacityProgress) {
            when {
                opacityProgress < 0.5f -> {
                    // 0-2s: 100% -> 60%
                    val t = opacityProgress / 0.5f
                    1f - 0.4f * t
                }
                else -> {
                    // 2-4s: 60% -> 100%
                    val t = (opacityProgress - 0.5f) / 0.5f
                    0.6f + 0.4f * t
                }
            }
        }

        // Impulse/thrust effect layer: Engine thrust effect that appears when traveling.
        // Positioned above the countdown ring but below the ship image.
        // Animates from 0% to 100% width over 3 seconds when travel starts.
        // Vertically centered and offset horizontally based on ship.
        // Uses the impulse image corresponding to the current ship with ship-specific sizes and offset.
        if (isTraveling) {
            ImpulseLayer(
                isTraveling = isTraveling,
                impulseResId = getImpulseImageResId(currentShip.id),
                impulseWidth = getImpulseWidth(currentShip.id),
                impulseHeight = getImpulseHeight(currentShip.id),
                horizontalOffset = getImpulseHorizontalOffset(currentShip.id),
                animatedOpacity = impulseOpacity,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = horizontalOffset, y = verticalOffset)
            )
        }

        // Spaceship layer: Centered spaceship image with ship-specific height.
        // Positioned at the vertical center to align with the radar animation and countdown ring.
        // Appears above the countdown ring and impulse layer.
        // Uses the ship image corresponding to the current ship with ship-specific height.
        // Tappable during travel to toggle countdown ring visibility.
        // For ship14, includes a lightning effect animation on top of the ship.
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(getGalaxyShipHeight(currentShip.id))
                .then(
                    if (isTraveling) {
                        Modifier
                            .offset(x = horizontalOffset, y = verticalOffset)
                            .clickable { 
                                // Toggle ring visibility when tapping ship during travel
                                isRingVisible = !isRingVisible
                            }
                    } else {
                        Modifier
                    }
                )
        ) {
            Image(
                painter = painterResource(id = getGalaxyShipImageResId(currentShip.id)),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(getGalaxyShipHeight(currentShip.id)),
                contentScale = ContentScale.Fit
            )
            
            // Lightning effect layer: Only for ship14 (Force of nature)
            // Same dimensions, positioning, and scaling behavior as ship image
            // Positioned on top of the ship image
            // Works for both idle and traveling states
            if (currentShip.id == "force_of_nature") {
                val lightningComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ship14lightningmainscreen))
                LottieAnimation(
                    composition = lightningComposition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(getGalaxyShipHeight(currentShip.id)),
                    contentScale = ContentScale.Fit
                )
            }
            
            // Solar flare effect layer: Only for ship15 (Dying Star)
            // Same dimensions, positioning, and scaling behavior as ship image
            // Positioned on top of the ship image
            // Works for both idle and traveling states
            if (currentShip.id == "dying_star") {
                val solarFlareComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.shipsolarflare2))
                LottieAnimation(
                    composition = solarFlareComposition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(getGalaxyShipHeight(currentShip.id)),
                    contentScale = ContentScale.Fit
                )
            }
        }

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
                .then(
                    if (isTraveling) {
                        Modifier.wrapContentHeight() // Allow content to expand when showing penalty counter
                    } else {
                        Modifier.height(51.dp) // Fixed height for other states
                    }
                ),
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
                    // Show "In travel" label and penalty counter when traveling
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        // "In travel" label
                    // Uses same font style as TimeLabel (Exo2 Regular, 24.sp, white color)
                    Text(
                        text = "In travel",
                        fontFamily = Exo2,
                        fontSize = 24.sp,
                        lineHeight = 24.sp,
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center
                    )
                        
                        // 24dp spacing below "In travel" label
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Penalty counter: SVG icon + label
                        PenaltyCounter(
                            penaltyCount = penaltyCount,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                else -> {
                    // Show navigation controls when idle - buttons only (indicator is in MainScreen)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(51.dp)
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CareerButton(onClick = onCareerClick)
                        // Indicator is shown in MainScreen (static), so we leave space for it
                        Box(modifier = Modifier.width(8.dp)) // Spacer to maintain layout
                        CollectionButton(onClick = onCollectionClick)
                    }
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
            // Hidden when ship is in maintenance and launch button is locked.
            // Use timerTrigger to ensure reactive updates when maintenance completes
            val isInMaintenanceForControls = remember(timerTrigger, currentShip.id) {
                com.example.fargalaxy.data.GameStateRepository.isShipInMaintenance(currentShip.id)
            }
            
            val durability = getDurabilityValue(currentShip.id)
            val consumedTravels = remember(timerTrigger, currentShip.id) {
                com.example.fargalaxy.data.GameStateRepository.getConsumedTravels(currentShip.id)
            }
            val availableTravels = durability - consumedTravels
            // Disable launch button when no travels available OR when ship is in maintenance
            val isLaunchDisabled = !isTraveling && !isPreparingLaunch && (availableTravels <= 0 || isInMaintenanceForControls)
            
            if (!(isInMaintenanceForControls && isLaunchDisabled)) {
                TimeControlsBar(
                    selectedMinutes = selectedMinutes,
                    remainingSeconds = remainingSeconds,
                    isTraveling = isTraveling,
                    isPreparingLaunch = isPreparingLaunch,
                    launchCountdown = launchCountdown,
                    isTestMode = isTestMode, // TODO: REMOVE TESTING CODE
                    onMinusClick = ::onDecrement,
                    onPlusClick = ::onIncrement
                )
            }
            
            // Launch button: Toggles between "LAUNCH", "CANCEL" (when preparing), and "STOP TRAVEL" (when traveling).
            // Disabled when no travels are available (shows "NOT AVAILABLE" with lock icon).
            // Calls onLaunchToggle handler to start preparation, cancel, or stop the countdown.
            LaunchButton(
                onClick = ::onLaunchToggle,
                isTraveling = isTraveling,
                isPreparingLaunch = isPreparingLaunch,
                isDisabled = isLaunchDisabled
            )
        }

        // Travel Success Modal: Shown when travel completes without cancellation
        if (showTravelSuccessModal && !showRewardsScreen) {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            
            // Play victory sound when modal appears
            LaunchedEffect(showTravelSuccessModal) {
                if (showTravelSuccessModal) {
                    val victoryPlayer = MediaPlayer.create(context, R.raw.victory)
                    victoryPlayer?.let { player ->
                        try {
                            player.setVolume(1f, 1f)
                            player.start()
                            
                            // Play in background - launch a coroutine to handle cleanup after sound finishes
                            coroutineScope.launch {
                                // Get duration in milliseconds
                                val duration = player.duration
                                if (duration > 0) {
                                    delay(duration.toLong())
                                }
                                
                                // Clean up after sound finishes
                                try {
                                    if (player.isPlaying) {
                                        player.stop()
                                    }
                                    player.release()
                                } catch (e: Exception) {
                                    // Ignore cleanup errors
                                }
                            }
                        } catch (e: Exception) {
                            // If starting fails, try to release
                            try {
                                player.release()
                            } catch (e2: Exception) {
                                // Ignore release errors
                            }
                        }
                    }
                }
            }
            
            TravelSuccessModal(
                onContinueClick = {
                    playMouseClickSound(context, coroutineScope)
                    showTravelSuccessModal = false
                    showRewardsScreen = true
                }
            )
        }
        
        // Watch for cancellation modal closing to show repairs modal if pending
        // Also handles case when trip is manually cancelled (no cancellation modal shown)
        LaunchedEffect(showTravelCanceledModal, showUnstableCargoCanceledModal, pendingRepairModal, showRewardsScreen, showShipUnlockedScreen, showLocationDiscoveredScreen, showTravelSuccessModal) {
            // When both cancellation modals are closed (or never shown) and repair is pending, show repairs modal
            if (!showTravelCanceledModal && !showUnstableCargoCanceledModal && pendingRepairModal) {
                // Only show if no other critical screens are showing
                if (!showRewardsScreen && !showShipUnlockedScreen && !showLocationDiscoveredScreen && !showTravelSuccessModal) {
                    // Small delay to ensure cancellation modal is fully closed (if one was shown)
                    // For manual cancellation, this delay is minimal and won't hurt
                    delay(150)
                    // Double-check conditions after delay (in case state changed)
                    if (pendingRepairModal && !showTravelCanceledModal && !showUnstableCargoCanceledModal && 
                        !showRewardsScreen && !showShipUnlockedScreen && !showLocationDiscoveredScreen && !showTravelSuccessModal) {
                        showRepairNeededModal = true
                        pendingRepairModal = false
                    }
                }
            }
        }
        
        // Travel Canceled Modal: Shown when trip is canceled (due to being away for >20 seconds or 5+ penalties)
        if (showTravelCanceledModal) {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            TravelCanceledModal(
                cancellationReason = cancellationReason,
                onContinueClick = {
                    playMouseClickSound(context, coroutineScope)
                    showTravelCanceledModal = false
                }
            )
        }
        
        // Unstable Cargo Canceled Modal: Shown when unstable cargo penalty occurs
        if (showUnstableCargoCanceledModal) {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            UnstableCargoCanceledModal(
                onContinueClick = {
                    playMouseClickSound(context, coroutineScope)
                    showUnstableCargoCanceledModal = false
                }
            )
        }
        
        // Experimental Fuel Remove Modal: Shown when user tries to remove experimental fuel with remaining travels
        if (showExperimentalFuelRemoveModal) {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            val remainingTravels = com.example.fargalaxy.data.EquipmentUsageRepository.getExperimentalFuelRemaining()
            ExperimentalFuelRemoveModal(
                remainingTravels = remainingTravels,
                onRemoveClick = {
                    playMouseClickSound(context, coroutineScope)
                    com.example.fargalaxy.data.EquipmentRepository.unequipItem()
                    com.example.fargalaxy.data.EquipmentUsageRepository.resetUsage()
                    showExperimentalFuelRemoveModal = false
                },
                onCancelClick = {
                    playMouseClickSound(context, coroutineScope)
                    showExperimentalFuelRemoveModal = false
                }
            )
        }

        // ADD / REPAIR buttons: Positioned on top of everything, centered on screen with offset
        // Only visible when idle (not traveling, not preparing) and no screens/modals are showing
        // TODO: Adjust offset values below to reposition the buttons manually
        // Current offset: 96dp right/left, 96dp down from center
        if (!isTraveling && !isPreparingLaunch && !showTravelSuccessModal && !showTravelCanceledModal && !showRewardsScreen && !showShipUnlockedScreen && !showLocationDiscoveredScreen && !showRepairNeededModal && !showExperimentalFuelRemoveModal && !showUnstableCargoCanceledModal && !showBoostSelectionBottomSheet) {
            val isInMaintenance = com.example.fargalaxy.data.GameStateRepository.isShipInMaintenance(currentShip.id)
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()

            // ADD button on the right side (always visible when idle)
            Box(
                modifier = Modifier
                    .align(Alignment.Center) // Center on screen
                    .offset(
                        x = 96.dp, // Horizontal offset to the right
                        y = 96.dp  // Vertical offset downwards
                    )
            ) {
                AddButton(
                    onClick = {
                        playMouseClickSound(context, coroutineScope)
                        showBoostSelectionBottomSheet = true
                    },
                    onCloseClick = {
                        playMouseClickSound(context, coroutineScope)
                        // Check if experimental fuel has remaining travels - show confirmation modal
                        val equippedItem = com.example.fargalaxy.data.EquipmentRepository.getEquippedItem()
                        if (equippedItem == "experimental_fuel") {
                            val remainingTravels = com.example.fargalaxy.data.EquipmentUsageRepository.getExperimentalFuelRemaining()
                            if (remainingTravels == 3) {
                                // Unused (3 trips left) - safely return to inventory without modal
                                com.example.fargalaxy.data.EquipmentRepository.unequipItem()
                                com.example.fargalaxy.data.EquipmentUsageRepository.resetUsage()
                                // Return to inventory
                                com.example.fargalaxy.data.InventoryRepository.addItem("experimental_fuel", 1)
                            } else if (remainingTravels > 0) {
                                // Partially used - show removal confirmation modal
                                showExperimentalFuelRemoveModal = true
                            } else {
                                // No remaining travels, safe to unequip
                                com.example.fargalaxy.data.EquipmentRepository.unequipItem()
                                com.example.fargalaxy.data.EquipmentUsageRepository.resetUsage()
                            }
                        } else {
                            // Other equipment - unequip directly
                            com.example.fargalaxy.data.EquipmentRepository.unequipItem()
                            com.example.fargalaxy.data.EquipmentUsageRepository.resetUsage()
                        }
                    },
                    refreshTrigger = showBoostSelectionBottomSheet // Refresh when bottom sheet visibility changes
                )
            }

            // REPAIR button on the left side (only visible when ship is in maintenance)
            if (isInMaintenance) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center) // Center on screen
                        .offset(
                            x = (-96).dp, // Horizontal offset to the left
                            y = 96.dp     // Vertical offset downwards
                        )
                ) {
                    val context = LocalContext.current
                    val coroutineScope = rememberCoroutineScope()
                    // Use proportional repair cost based on remaining maintenance time
                    val repairCost = remember(timerTrigger) {
                        com.example.fargalaxy.data.GameStateRepository.getShipRepairCostProportional(currentShip.id)
                    }
                    RepairButton(
                        onClick = {
                            playMouseClickSound(context, coroutineScope)
                            // Show repairs needed modal with current time remaining and proportional cost
                            showRepairNeededModal = true
                        }
                    )
                }
            }
        }
        
        // Maintenance time counter: Positioned 300dp below center, centered horizontally
        // Shows remaining maintenance time in minutes
        // Rendered on top of everything to ensure visibility
        // Hidden when screens/modals are showing
        if (!isTraveling && !isPreparingLaunch && !showTravelSuccessModal && !showTravelCanceledModal && !showRewardsScreen && !showShipUnlockedScreen && !showLocationDiscoveredScreen && !showRepairNeededModal && !showExperimentalFuelRemoveModal && !showUnstableCargoCanceledModal && !showBoostSelectionBottomSheet) {
            val isInMaintenance = com.example.fargalaxy.data.GameStateRepository.isShipInMaintenance(currentShip.id)
            if (isInMaintenance) {
                val remainingMaintenanceSeconds = remember(timerTrigger) {
                    com.example.fargalaxy.data.GameStateRepository.getRemainingMaintenanceTime(currentShip.id)
                }
                if (remainingMaintenanceSeconds > 0) {
                    val remainingMinutes = (remainingMaintenanceSeconds + 59) / 60 // Round up to nearest minute
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = 160.dp) // 160dp below center (moved up by additional 100dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "$remainingMinutes MINS REMAINING",
                            fontFamily = Exo2,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W400, // Regular
                            color = Color(0xFFF87F7F), // Red color #F87F7F
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }

        // Note: Noise overlay is handled by MainScreen (static layer)
        
        // Rewards Screen: Shown after modal continue is clicked
        // Rendered after buttons/counter to appear on top of them
        if (showRewardsScreen && !showShipUnlockedScreen && !showLocationDiscoveredScreen) {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            RewardsScreen(
                travelMinutes = travelMinutes,
                penaltyCount = travelPenaltyCount,
                equippedItemAtTravelTime = travelEquippedItem,
                hasUnstableCargoPenalty = travelHasUnstableCargoPenalty,
                emergencyModulatorUsedAtTravelTime = travelEmergencyModulatorUsed,
                onContinueClick = {
                    playMouseClickSound(context, coroutineScope)
                    showRewardsScreen = false
                    // After rewards screen, show ship unlock screens first, then location discovery screens
                    if (newlyUnlockedShips.isNotEmpty()) {
                        showShipUnlockedScreen = true
                        currentShipUnlockedIndex = 0
                    } else if (newlyDiscoveredLocations.isNotEmpty()) {
                        showLocationDiscoveredScreen = true
                        currentLocationDiscoveredIndex = 0
                    } else {
                        // No unlock screens, check if repair modal is pending
                        if (pendingRepairModal) {
                            showRepairNeededModal = true
                            pendingRepairModal = false
                        }
                    }
                },
                currentShip = currentShip
            )
        }
        
        // Ship Unlocked Screen: Shown after rewards screen if ships were unlocked
        // Rendered after buttons/counter to appear on top of them
        // Show each ship sequentially - keep showing until all ships have been displayed
        if (showShipUnlockedScreen && newlyUnlockedShips.isNotEmpty() && currentShipUnlockedIndex < newlyUnlockedShips.size) {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            ShipUnlockedScreen(
                shipId = newlyUnlockedShips[currentShipUnlockedIndex],
                onContinueClick = {
                    playMouseClickSound(context, coroutineScope)
                    // Move to next ship
                    val nextIndex = currentShipUnlockedIndex + 1
                    if (nextIndex >= newlyUnlockedShips.size) {
                        // All ships shown, hide ship unlock screen
                        showShipUnlockedScreen = false
                        currentShipUnlockedIndex = 0 // Reset for next session
                        // After all ships, show location discovery screens if any
                        if (newlyDiscoveredLocations.isNotEmpty()) {
                            showLocationDiscoveredScreen = true
                            currentLocationDiscoveredIndex = 0
                        } else {
                            // No more locations, check if repair modal is pending
                            if (pendingRepairModal) {
                                showRepairNeededModal = true
                                pendingRepairModal = false
                            }
                        }
                    } else {
                        // Show next ship
                        currentShipUnlockedIndex = nextIndex
                    }
                }
            )
        }
        
        // Location Discovered Screen: Shown after ship unlock screens (or after rewards if no ships)
        // Rendered after buttons/counter to appear on top of them
        // Show each location sequentially - keep showing until all locations have been displayed
        if (showLocationDiscoveredScreen && newlyDiscoveredLocations.isNotEmpty() && currentLocationDiscoveredIndex < newlyDiscoveredLocations.size) {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            LocationDiscoveredScreen(
                locationId = newlyDiscoveredLocations[currentLocationDiscoveredIndex],
                onContinueClick = {
                    playMouseClickSound(context, coroutineScope)
                    // Move to next location
                    val nextIndex = currentLocationDiscoveredIndex + 1
                    if (nextIndex >= newlyDiscoveredLocations.size) {
                        // All locations shown, hide location discovered screen
                        showLocationDiscoveredScreen = false
                        currentLocationDiscoveredIndex = 0 // Reset for next session
                        // Reset for next session
                        newlyUnlockedShips = emptyList()
                        newlyDiscoveredLocations = emptyList()
                        // All unlock screens done, check if repair modal is pending
                        if (pendingRepairModal) {
                            showRepairNeededModal = true
                            pendingRepairModal = false
                        }
                    } else {
                        // Show next location
                        currentLocationDiscoveredIndex = nextIndex
                    }
                }
            )
        }
        
        // Boost Selection Bottom Sheet: Shown when ADD button is tapped
        // Rendered on top of everything except modals
        if (showBoostSelectionBottomSheet && !showTravelSuccessModal && !showTravelCanceledModal && !showRepairNeededModal) {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            
            // Handle back button press to close bottom sheet
            BackHandler(enabled = true) {
                playMouseClickSound(context, coroutineScope)
                showBoostSelectionBottomSheet = false
            }
            
            BoostSelectionBottomSheet(
                onDismiss = {
                    playMouseClickSound(context, coroutineScope)
                    showBoostSelectionBottomSheet = false
                },
                currentShip = currentShip,
                onShowToast = onShowToast,
                onShowScannerProgress = {
                    playMouseClickSound(context, coroutineScope)
                    showBoostSelectionBottomSheet = false // Close bottom sheet
                    showScannerProgress = true // Show first intermediate screen
                },
                onShowExperimentalFuelRemoveModal = {
                    playMouseClickSound(context, coroutineScope)
                    showExperimentalFuelRemoveModal = true
                }
            )
        }
        
        // Scanner Progress Screen: First intermediate screen (shows scanning animation)
        if (showScannerProgress) {
            ScannerProgressScreen(
                onComplete = {
                    showScannerProgress = false
                    showScannerResults = true
                }
            )
        }
        
        // Scanner Results Screen: Second intermediate screen (shows results)
        if (showScannerResults) {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            ScannerResultsScreen(
                onContinueClick = {
                    playMouseClickSound(context, coroutineScope)
                    // Consume one scanner and reveal environment
                    com.example.fargalaxy.data.InventoryRepository.removeItem("deep_space_scanner", 1)
                    com.example.fargalaxy.data.FlightEnvironmentRepository.markScannerUsedForCurrentEnvironment()
                    onShowToast("Flight environment revealed for the day")
                    showScannerResults = false
                }
            )
        }
        
        // Repair Needed Modal: Shown when ship runs out of travels and enters maintenance
        // Rendered last to ensure it appears on top of everything
        // Only show when no other critical screens are visible (can show on top of bottom sheet and other modals)
        if (showRepairNeededModal && !showRewardsScreen && !showShipUnlockedScreen && !showLocationDiscoveredScreen && !showTravelSuccessModal && !showTravelCanceledModal && !showUnstableCargoCanceledModal) {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            // Use proportional repair cost based on remaining maintenance time
            val repairCost = remember(timerTrigger) {
                com.example.fargalaxy.data.GameStateRepository.getShipRepairCostProportional(currentShip.id)
            }
            val maintenanceMinutes = getMaintenanceTime(currentShip.id)
            
            RepairNeededModal(
                repairCost = repairCost,
                maintenanceMinutes = maintenanceMinutes,
                onRepairClick = {
                    playMouseClickSound(context, coroutineScope)
                    // Check if user has enough credits
                    val currentCredits = com.example.fargalaxy.data.UserDataRepository.userCredits
                    if (currentCredits >= repairCost) {
                        // Deduct credits
                        com.example.fargalaxy.data.UserDataRepository.userCredits = currentCredits - repairCost
                        // Complete maintenance
                        com.example.fargalaxy.data.GameStateRepository.completeMaintenance(currentShip.id)
                        showRepairNeededModal = false
                    } else {
                        // TODO: Show error message if not enough credits
                        // For now, still complete maintenance (testing mode)
                        com.example.fargalaxy.data.GameStateRepository.completeMaintenance(currentShip.id)
                        showRepairNeededModal = false
                    }
                },
                onWaitClick = {
                    playMouseClickSound(context, coroutineScope)
                    // Just close the modal - maintenance will continue automatically
                    showRepairNeededModal = false
                }
            )
        }
    }
}

/**
 * RepairButton composable - displays the "REPAIR" button with secondary style (no icon).
 * 
 * Uses the same visual format as AddButton but with:
 * - Transparent background with white border
 * - White text
 * - Rounded corners (80dp radius)
 * - Fixed size: 88dp width, 32dp height
 * - 16sp font size, regular weight
 * - No icon, just "REPAIR" text
 * 
 * @param onClick Callback when button is clicked
 * @param modifier Modifier for the button
 */
@Composable
private fun RepairButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(88.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(80.dp))
            .background(Color(0xFF242736)) // Fill color #242736
            .border(
                width = 1.dp,
                color = Color(0xFFFFFFFF), // White border
                shape = RoundedCornerShape(80.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // "REPAIR" text: 16sp, regular weight, no icon
        Text(
            text = "REPAIR",
            fontFamily = Exo2,
            fontSize = 16.sp,
            color = Color(0xFFFFFFFF), // White text
            textAlign = TextAlign.Center
        )
    }
}

/**
 * AddButton composable - displays the "ADD" button with secondary style and miniplus icon.
 * When an equipment item is equipped, shows the equipped state with item image, "ADDED" label, and close button.
 *
 * Uses the same visual format as ViewButton but with:
 * - Transparent background with white border
 * - White text
 * - Rounded corners (80dp radius)
 * - Default size: 88dp width, 32dp height
 * - Equipped size: max 120dp width, 40dp height
 * - 16sp font size, regular weight
 * - miniplus icon (12dp) to the left of "ADD" text with 8dp spacing (default state)
 * - Item image (32x32) to the left of "ADDED" text, close button (16x16) to the right (equipped state)
 *
 * @param onClick Callback when button is clicked (opens inventory)
 * @param onCloseClick Callback when close button is clicked (unequips item)
 * @param refreshTrigger Boolean trigger to refresh equipment state
 * @param modifier Modifier for the button
 */
@Composable
private fun AddButton(
    onClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    refreshTrigger: Boolean = false, // Trigger to refresh equipment state
    modifier: Modifier = Modifier
) {
    // Use mutableStateOf to track equipped item and make it reactive
    var equippedItemId by remember {
        mutableStateOf(com.example.fargalaxy.data.EquipmentRepository.getEquippedItem())
    }
    
    // Update equipped item when refresh is triggered
    LaunchedEffect(refreshTrigger) {
        equippedItemId = com.example.fargalaxy.data.EquipmentRepository.getEquippedItem()
    }
    
    // Get image resource ID for equipped item
    val equippedImageResId = when (equippedItemId) {
        "emergency_modulator" -> R.drawable.modulatorselection
        "unstable_cargo" -> R.drawable.cargoselection
        "experimental_fuel" -> R.drawable.fuelselection
        else -> null
    }
    
    if (equippedItemId != null && equippedImageResId != null) {
        // Equipped state: 40dp height, max 120dp width, item image, "ADDED" label, close button
        Box(
            modifier = modifier
                .height(40.dp)
                .widthIn(max = 120.dp)
                .wrapContentWidth()
                .clip(RoundedCornerShape(80.dp))
                .background(Color(0xFF242736)) // Fill color #242736
                .border(
                    width = 1.dp,
                    color = Color(0xFFFFFFFF), // White border
                    shape = RoundedCornerShape(80.dp)
                )
                .clickable(onClick = onClick), // Clicking anywhere opens inventory
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 4.dp, end = 8.dp) // 4dp left, 8dp right padding
            ) {
                // Item image: 32x32
                Image(
                    painter = painterResource(id = equippedImageResId),
                    contentDescription = "Equipped item",
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
                
                // 0dp spacing between image and label
                Spacer(modifier = Modifier.width(0.dp))
                
                // "ADDED" text: 16sp, regular weight
                Text(
                    text = "ADDED",
                    fontFamily = Exo2,
                    fontSize = 16.sp,
                    color = Color(0xFFFFFFFF), // White text
                    textAlign = TextAlign.Center
                )
                
                // 8dp spacing between label and close button
                Spacer(modifier = Modifier.width(8.dp))
                
                // Close button: 16x16, tappable to unequip
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clickable(
                            onClick = {
                                // Update local state immediately
                                equippedItemId = null
                                // Call the callback to unequip
                                onCloseClick()
                            },
                            indication = null, // Remove ripple effect
                            interactionSource = remember { MutableInteractionSource() }
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.closebuttonsmall),
                        contentDescription = "Remove equipment",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    } else {
        // Default state: 88dp width, 32dp height, miniplus icon + "ADD" text
        Box(
            modifier = modifier
                .width(88.dp)
                .height(32.dp)
                .clip(RoundedCornerShape(80.dp))
                .background(Color(0xFF242736)) // Fill color #242736
                .border(
                    width = 1.dp,
                    color = Color(0xFFFFFFFF), // White border
                    shape = RoundedCornerShape(80.dp)
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp) // Internal padding for icon and text
            ) {
                // miniplus icon: 12dp size, 8dp spacing to the right of text
                Image(
                    painter = painterResource(id = R.drawable.miniplus),
                    contentDescription = "Add",
                    modifier = Modifier.size(12.dp),
                    contentScale = ContentScale.Fit
                )
                
                // 8dp spacing between icon and text
                Spacer(modifier = Modifier.width(8.dp))
                
                // "ADD" text: 16sp, regular weight
                Text(
                    text = "ADD",
                    fontFamily = Exo2,
                    fontSize = 16.sp,
                    color = Color(0xFFFFFFFF), // White text
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Preview composable for GalaxyScreen - used for design preview in Android Studio.
 * Displays the screen with default state values (25 minutes selected, not traveling).
 */
@Preview(showBackground = true)
@Composable
private fun GalaxyScreenPreview() {
    GalaxyScreen(
        currentShip = com.example.fargalaxy.data.ShipRepository.getCurrentShip()
    )
}

