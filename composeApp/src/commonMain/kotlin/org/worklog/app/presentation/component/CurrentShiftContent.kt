package org.worklog.app.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.worklog.app.domain.model.Rota
import org.worklog.app.presentation.theme.poppinsFontFamily

// M3 tokens from Figma spec (node 2129:16725)
private val ColorTeal = Color(0xFF007B99)                // primary
private val ColorOnPrimary = Color(0xFFFFFFFF)           // on-primary
private val ColorPrimaryContainer = Color(0xFFF2FCFF)    // primary-container (unused here, kept for parity)
private val ColorOnPrimaryContainer = Color(0xFF004D61)  // on-primary-container — time text
private val ColorSecondary = Color(0xFF2B3133)           // secondary — role text
private val ColorTertiaryContainer = Color(0xFF9DF0FB)   // tertiary-container — banner accent
private val ColorSurface = Color(0xFFFFFFFF)             // surface — card bg
private val ColorSurfaceDim = Color(0xFFD5DBDD)          // surface-dim — card border
private val ColorError = Color(0xFFBA1A1A)               // error — End Shift bg

@Composable
fun CurrentShiftContent(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isShiftToggling: Boolean = false,
    isShiftStarted: Boolean = false,
    isLoading: Boolean = false,
    hasCurrentRota: Boolean = true,
    isNearOffice: Boolean = false,
    userName: String = "",
    currentRota: Rota? = null,
    onStartShiftClick: () -> Unit = {},
    onLocateMeClick: (String, String) -> Unit = { _, _ -> }
) {
    if (isLoading) {
        ShimmerBox(modifier = modifier.fillMaxWidth(), height = 280.dp, cornerRadius = 18.dp)
        return
    }

    val poppins = poppinsFontFamily()

    val initials = remember(userName) {
        val names = userName.split(" ").filter { it.isNotBlank() }
        when {
            names.size >= 2 -> {
                val firstInitial = names.first().firstOrNull()?.uppercaseChar() ?: ""
                val lastInitial = names.last().firstOrNull()?.uppercaseChar() ?: ""
                "$firstInitial$lastInitial"
            }
            names.size == 1 -> {
                names.first().take(2).uppercase()
            }
            else -> "MH"
        }
    }

    val typographyTitleMedium = TextStyle(
        fontFamily = poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    )
    val typographyBodySmall = TextStyle(
        fontFamily = poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .border(0.5.dp, ColorSurfaceDim, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            MapboxView(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        onLocateMeClick("51.5079111", "-0.0903026")
                    },
                latitude = 51.5079111,
                longitude = -0.0903026,
                zoom = 14.5,
                onMapLoaded = {}
            )

            MapPinOverlay(
                isActive = isShiftStarted,
                poppins = poppins,
                initials = initials,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 60.dp)
            )

            if (isShiftStarted) {
                MapBannerOverlay(poppins = poppins)
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                ColorSurface.copy(alpha = 0.7f),
                                ColorSurface.copy(alpha = 0.95f),
                                ColorSurface
                            )
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val timeRange = if (hasCurrentRota && currentRota != null && currentRota.shiftStartTime.isNotBlank())
                    "${currentRota.shiftStartTime} — ${currentRota.shiftEndTime}"
                else "09.40 pm — 04.30 am"

                val floorSuffix = if (hasCurrentRota && currentRota != null && currentRota.floorName.isNotBlank())
                    " · ${currentRota.floorName}"
                else " · Barista"

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "$timeRange$floorSuffix",
                        style = typographyTitleMedium.copy(color = ColorOnPrimaryContainer),
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = userName,
                            style = typographyBodySmall.copy(color = ColorSecondary),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }
                }

                when {
                    isShiftStarted -> FigmaPrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isShiftToggling,
                        isLoading = isShiftToggling,
                        isDestructive = true,
                        label = "End Shift",
                        onClick = onStartShiftClick
                    )
                    else -> GeminiAnimatedButton(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isShiftToggling,
                        glow = isNearOffice,
                        isLoading = isShiftToggling,
                        primaryText = if (hasWorkingShift(hasCurrentRota, currentRota)) "Start Shift" else "No Shift",
                        secondaryText = if (hasWorkingShift(hasCurrentRota, currentRota)) {
                            buildString {
                                append("${currentRota?.shiftStartTime} - ${currentRota?.shiftEndTime}")
                                if (currentRota?.floorName?.isNotBlank() == true) {
                                    append(" : ${currentRota.floorName}")
                                }
                            }
                        } else null,
                        onClick = if (hasWorkingShift(hasCurrentRota, currentRota)) onStartShiftClick else ({})
                    )
                }
            }
        }
    }
}

private fun hasWorkingShift(hasCurrentRota: Boolean, currentRota: Rota?): Boolean {
    return hasCurrentRota && currentRota != null &&
            currentRota.shiftStartTime.isNotBlank() &&
            !currentRota.isLeave &&
            !currentRota.shortCode.equals("OFF", ignoreCase = true) &&
            !currentRota.shortCode.equals("AL", ignoreCase = true)
}

@Composable
private fun GeminiAnimatedButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    glow: Boolean,
    isLoading: Boolean,
    primaryText: String,
    secondaryText: String? = null,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "GeminiTransition")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )

    // App-coloured glow palette — built from M3 tokens so the border picks up
    // the brand identity instead of the Google rainbow. The cyan tertiary
    // container reads as a bright glow against the solid teal fill.
    val glowColors = listOf(
        ColorTertiaryContainer,    // #9DF0FB — bright cyan glow
        ColorOnPrimary,            // #FFFFFF — hot spot
        ColorTertiaryContainer,    // #9DF0FB
        ColorTeal,                 // #007B99 — drops into base teal
        ColorTertiaryContainer     // wraps back to cyan
    )

    val isAnimated = (enabled || glow) && !isLoading

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            // Solid app primary fill — no transparency, no hollow centre.
            .background(ColorTeal)
            .then(
                if (isAnimated) {
                    // Border-only glow: the sweep gradient rotates around the
                    // outline. The interior keeps its solid teal fill — no
                    // overlay, no inner highlight, no transparency.
                    Modifier.border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(
                            colors = glowColors,
                            // rotating the gradient stops gives the orbit
                            // effect without painting over the fill.
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else Modifier
            )
            .clickable(enabled = enabled && !isLoading, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = ColorOnPrimary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = primaryText,
                    style = TextStyle(
                        fontWeight = FontWeight.ExtraBold,
                        color = ColorOnPrimary,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp
                    )
                )
                if (!secondaryText.isNullOrBlank()) {
                    Text(
                        text = secondaryText,
                        style = TextStyle(
                            color = ColorOnPrimary.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun FigmaPrimaryButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    isLoading: Boolean,
    isDestructive: Boolean,
    label: String,
    onClick: () -> Unit
) {
    val bg = if (isDestructive) ColorError else ColorTeal
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg.copy(alpha = if (enabled) 1f else 0.5f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = ColorOnPrimary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                text = label,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = ColorOnPrimary,
                    fontSize = 16.sp,
                    letterSpacing = 0.15.sp
                )
            )
        }
    }
}

@Composable
private fun MapPinOverlay(
    isActive: Boolean,
    poppins: FontFamily,
    initials: String,
    modifier: Modifier = Modifier
) {
    val pinColor = if (isActive) ColorOnPrimaryContainer else ColorTeal

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (isActive) {
            val infiniteTransition = rememberInfiniteTransition()

            val ring1Scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 2.5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
            val ring1Alpha by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )

            val ring2Scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 2.5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                    initialStartOffset = StartOffset(650)
                )
            )
            val ring2Alpha by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                    initialStartOffset = StartOffset(650)
                )
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .graphicsLayer {
                        this.scaleX = ring1Scale
                        this.scaleY = ring1Scale
                        this.alpha = ring1Alpha
                    }
                    .border(2.dp, ColorTeal, CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .graphicsLayer {
                        this.scaleX = ring2Scale
                        this.scaleY = ring2Scale
                        this.alpha = ring2Alpha
                    }
                    .border(2.dp, ColorTeal, CircleShape)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(pinColor, CircleShape)
                    .border(3.dp, ColorOnPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = ColorOnPrimary,
                    fontFamily = poppins,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Canvas(modifier = Modifier.size(10.dp, 6.dp)) {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width / 2, size.height)
                    close()
                }
                drawPath(path, color = ColorOnPrimary)
            }
        }
    }
}

@Composable
private fun MapBannerOverlay(poppins: FontFamily) {
    val infiniteTransition = rememberInfiniteTransition()
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(ColorTeal.copy(alpha = 0.92f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .alpha(blinkAlpha)
                        .background(ColorTertiaryContainer, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Shift running",
                    color = ColorOnPrimary,
                    fontFamily = poppins,
                    fontSize = 11.sp
                )
            }
            Text(
                text = "01:24:10",
                color = ColorTertiaryContainer,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
        }
    }
}
