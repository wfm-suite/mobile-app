package org.worklog.app.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.worklog.app.domain.model.Rota
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.map_image
import worklog.composeapp.generated.resources.start_shift
import worklog.composeapp.generated.resources.stop_shift

// Figma tokens (Node 2129:16725)
private val FigmaCardBg = Color(0xFFF2FCFF)
private val FigmaPrimary = Color(0xFF007B99)
private val FigmaOnPrimaryContainer = Color(0xFF004D61)
private val FigmaError = Color(0xFFBA1A1A)

@Composable
fun CurrentShiftContent(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isShiftToggling: Boolean = false,
    isShiftStarted: Boolean = false,
    isLoading: Boolean = false,
    hasCurrentRota: Boolean = true,
    currentRota: Rota? = null,
    onStartShiftClick: () -> Unit = {},
    onLocateMeClick: (String, String) -> Unit = { _, _ -> }
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = FigmaCardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    ShimmerBox(height = 173.dp, cornerRadius = 16.dp)
                    Spacer(modifier = Modifier.height(12.dp))
                    ShimmerBox(
                        modifier = Modifier.padding(horizontal = 40.dp),
                        height = 20.dp,
                        cornerRadius = 4.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ShimmerBox(height = 42.dp, cornerRadius = 16.dp)
                }
                !hasCurrentRota || currentRota == null -> {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        textAlign = TextAlign.Center,
                        text = "No shift today",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                else -> {
                    // Everything is now inside this Box which contains the Map
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp) // Taller height to fit map and info
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        MapboxView(
                            modifier = Modifier.fillMaxSize().clickable {
                                onLocateMeClick("51.5079111", "-0.0903026")
                            },
                            latitude = 51.5079111,
                            longitude = -0.0903026
                        )

                        // Bottom Overlay: Only the Button (with time included)
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.4f)
                                        )
                                    )
                                )
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val timeRange = if (currentRota.shiftStartTime.isNotBlank()) "  •  ${currentRota.shiftStartTime} - ${currentRota.shiftEndTime}" else ""
                            
                            FigmaPrimaryButton(
                                modifier = Modifier.fillMaxWidth(),
                                enabled = isEnabled && !isShiftToggling,
                                isLoading = isShiftToggling,
                                isDestructive = isShiftStarted,
                                label = if (isShiftStarted) stringResource(Res.string.stop_shift)
                                else "${stringResource(Res.string.start_shift)}$timeRange",
                                onClick = onStartShiftClick
                            )
                        }
                    }
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
    val bg = if (isDestructive) FigmaError else FigmaPrimary
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bg.copy(alpha = if (enabled) 1f else 0.5f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp,
                    letterSpacing = 0.15.sp
                )
            )
        }
    }
}
