package org.worklog.app.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.ic_app_banner
import worklog.composeapp.generated.resources.ic_notification

@Composable
fun TopbarWithLogo(
    onNotificationClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), // Touching the absolute left border
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .size(80.dp), // Square size for the trees-only logo
                painter = painterResource(Res.drawable.ic_app_banner),
                contentDescription = "Forest Trees Logo",
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                modifier = Modifier
                    .clip(RoundedCornerShape(dimens.cornerRadiusSmall))
                    .size(dimens.vectorImageSize),
                onClick = onNotificationClick,
            ) {
                Icon(
                    modifier = Modifier.padding(5.dp),
                    painter = painterResource(Res.drawable.ic_notification),
                    contentDescription = "Notification",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun AppTopbarWithBack(
    modifier: Modifier = Modifier,
    title: String = "WorkLog",
    showNotification: Boolean = true,
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(
            bottomStart = dimens.cornerRadiusMedium,
            bottomEnd = dimens.cornerRadiusMedium
        )
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(
                    start = dimens.innerHorizontalPadding,
                    end = dimens.innerHorizontalPadding,
                    bottom = dimens.innerVerticalPadding,
                    top = 8.dp
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(dimens.mediumIconSize)
                    )
                }
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            if (showNotification) {
                IconButton(
                    onClick = onNotificationClick
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_notification),
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(dimens.smallIconSize)
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun TopbarPreview() {
    TopbarWithLogo()
}