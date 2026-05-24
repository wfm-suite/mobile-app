package org.worklog.app.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.worklog.app.domain.model.Rota
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.map_image
import worklog.composeapp.generated.resources.start_shift
import worklog.composeapp.generated.resources.stop_shift

@Composable
fun CurrentShiftContent(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isShiftToggling: Boolean = false,
    isShiftStarted: Boolean = false,
    isLoading: Boolean = false,
    hasCurrentRota: Boolean = true,
    currentRota: Rota? = null,
    branchName: String = "",
    onStartShiftClick: () -> Unit = {},
    onLocateMeClick: () -> Unit = {}
) {
    if (isLoading) {
        CustomCard(
            modifier = modifier,
            innerPadding = PaddingValues(0.dp)
        ) {
            Column(modifier = Modifier.padding(dimens.innerHorizontalPadding)) {
                ShimmerBox(height = 140.dp, cornerRadius = dimens.cornerRadius)
                Spacer(modifier = Modifier.height(12.dp))
                ShimmerBox(modifier = Modifier.padding(horizontal = 40.dp), height = 20.dp, cornerRadius = 4.dp)
                Spacer(modifier = Modifier.height(12.dp))
                ShimmerBox(height = 48.dp, cornerRadius = dimens.cornerRadius)
            }
        }
    } else if (!hasCurrentRota || currentRota == null) {
        CustomCard(
            modifier = modifier,
            innerPadding = PaddingValues(16.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                textAlign = TextAlign.Center,
                text = "No shift today",
                style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary)
            )
        }
    } else {
        CustomCard(
            modifier = modifier,
            innerPadding = PaddingValues(0.dp),
            cornerRadius = 24.dp
        ) {
            // Map Section
            Box(modifier = Modifier.fillMaxWidth().height(180.dp).padding(12.dp)) {
                Image(
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                    painter = painterResource(Res.drawable.map_image),
                    contentDescription = "Shift Location",
                    contentScale = ContentScale.Crop
                )
                
                IconButton(
                    onClick = onLocateMeClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Locate Me",
                        tint = Color(0xFF007991),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Time Range
                if (currentRota.shiftStartTime.isNotBlank()) {
                    Text(
                        text = "${currentRota.shiftStartTime} - ${currentRota.shiftEndTime}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF005467),
                            fontSize = 18.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Location Line
                val floorPart = if (currentRota.floorName.isNotBlank()) " (${currentRota.floorName})" else ""
                Text(
                    text = "$branchName$floorPart - ${currentRota.designation}",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF5E8B9A),
                        fontSize = 15.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                // Action Button
                PrimaryButton(
                    isLoading = isShiftToggling,
                    enabled = isEnabled,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    containerColor = if (isShiftStarted) Color(0xFFE53935) else Color(0xFF007991),
                    label = if (isShiftStarted) stringResource(Res.string.stop_shift) else stringResource(Res.string.start_shift),
                    onClick = onStartShiftClick
                )
            }
        }
    }
}
