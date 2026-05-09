package org.worklog.app.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    currentRota: Rota? = null,
    onStartShiftClick: () -> Unit = {}
) {
    if (isLoading) {
        CustomCard(
            modifier = modifier,
            innerPadding = PaddingValues(dimens.innerHorizontalPadding)
        ) {
            ShimmerBox(
                height = 140.dp,
                cornerRadius = dimens.cornerRadius
            )
            Spacer(modifier = Modifier.height(12.dp))
            ShimmerBox(
                modifier = Modifier.padding(horizontal = 40.dp),
                height = 20.dp,
                cornerRadius = 4.dp
            )
            Spacer(modifier = Modifier.height(5.dp))
            ShimmerBox(
                modifier = Modifier.padding(horizontal = 60.dp),
                height = 16.dp,
                cornerRadius = 4.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
            ShimmerBox(
                height = 48.dp,
                cornerRadius = dimens.cornerRadius
            )
        }
    } else {
        currentRota?.let {
            CustomCard(
                modifier = modifier,
                innerPadding = PaddingValues(dimens.innerHorizontalPadding)
            ) {
                Image(
                    modifier = Modifier.fillMaxWidth()
                        .clip(
                            shape = RoundedCornerShape(
                                topStart = dimens.cornerRadius,
                                topEnd = dimens.cornerRadius
                            )
                        ).height(140.dp),
                    painter = painterResource(Res.drawable.map_image),
                    contentDescription = "Office Location",
                    contentScale = ContentScale.FillBounds
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "${currentRota.shiftStartTime} - ${currentRota.shiftEndTime}",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = currentRota.location,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                PrimaryButton(
                    isLoading = isShiftToggling,
                    enabled = isEnabled,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = if (isShiftStarted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    label = if (isShiftStarted) stringResource(Res.string.stop_shift) else stringResource(Res.string.start_shift),
                    onClick = onStartShiftClick
                )
            }
        } ?: run {
            CustomCard(
                modifier = modifier,
                innerPadding = PaddingValues(dimens.innerHorizontalPadding)
            ) {
                Text(
                    modifier = Modifier.fillMaxSize()
                        .padding(dimens.verticalPadding),
                    textAlign = TextAlign.Center,
                    text = "No Shift Today",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}