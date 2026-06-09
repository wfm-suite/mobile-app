package org.worklog.app.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.ic_forest_logo
import worklog.composeapp.generated.resources.ic_notification

@Composable
fun TopbarWithLogo(
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    notificationBadge: Int = 0
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), // Touching the absolute left border
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBack) {
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
            }
            Image(
                modifier = Modifier
                    .padding(start = 12.dp, top = 4.dp, bottom = 4.dp)
                    .size(48.dp), 
                painter = painterResource(Res.drawable.ic_forest_logo),
                contentDescription = "Forest Healthcare Logo",
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.weight(1f))
            Box {
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
                if (notificationBadge > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = 4.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE53935))
                            .padding(1.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (notificationBadge > 9) "9+" else notificationBadge.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontSize = 8.sp
                        )
                    }
                }
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
    onNotificationClick: () -> Unit = {},
    actions: @Composable (() -> Unit)? = null
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

            if (actions != null) {
                actions()
            } else if (showNotification) {
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