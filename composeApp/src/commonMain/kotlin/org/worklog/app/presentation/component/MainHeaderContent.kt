package org.worklog.app.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.ic_notification

@Composable
fun MainHeaderContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    greetingText: String = "",
    userName: String = "",
    date: String = "",
    onNotificationClick: () -> Unit = {},
) {

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            if (isLoading) {
                val whiteShimmerColors = listOf(
                    Color.White.copy(alpha = 0.6f),
                    Color.White.copy(alpha = 0.2f),
                    Color.White.copy(alpha = 0.6f),
                )
                ShimmerBox(
                    height = 28.dp,
                    width = 200.dp,
                    cornerRadius = 4.dp,
                    shimmerColors = whiteShimmerColors
                )
                Spacer(modifier = Modifier.padding(2.dp))
                ShimmerBox(
                    height = 16.dp,
                    width = 120.dp,
                    cornerRadius = 4.dp,
                    shimmerColors = whiteShimmerColors
                )
            } else {
                Text(
                    text = "$greetingText, $userName",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
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
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}