package org.worklog.app.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.worklog.app.domain.model.RotaStatus
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.ic_user

// Figma tokens (Node 2305:767)
private val MeBorderColor = androidx.compose.ui.graphics.Color(0xFF2B3133)   // dark left bar = ME
private val OtherBorderColor = androidx.compose.ui.graphics.Color(0xFFFFFFFF) // white left bar = teammate

@Composable
fun TeamShiftCard(
    modifier: Modifier = Modifier,
    shift: String = "No Shift",
    floorName: String = "",
    name: String = "Md Hasan (You)",
    profileImage: String = "",
    status: RotaStatus = RotaStatus.NOTHING,
    rightIcon: DrawableResource? = null,
    isCurrentUser: Boolean = false,
    isCancelling: Boolean = false,
    onCancelClick: (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    val shiftLine = if (floorName.isNotBlank()) "$shift • $floorName" else shift
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(dimens.cornerRadius))
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(dimens.cornerRadius)
            )
            // Figma: 3dp left border — dark for current user (ME marker), white for others
            .border(
                width = 3.dp,
                color = if (isCurrentUser) MeBorderColor else OtherBorderColor,
                shape = RoundedCornerShape(dimens.cornerRadius)
            )
            .clickable { onClick() }
            .padding(
                vertical = 6.dp,
                horizontal = 12.dp
            )
    ) {
        // Figma: 36×36 circular avatar with #007B99 border
        AsyncImage(
            modifier = Modifier.size(36.dp)
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(Res.drawable.ic_user),
            error = painterResource(Res.drawable.ic_user),
            model = profileImage,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(dimens.innerVerticalPadding))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = shiftLine,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall
            )
        }

        if (isCurrentUser && status == RotaStatus.PENDING && onCancelClick != null) {
            CancelRequestChip(
                isLoading = isCancelling,
                onClick = onCancelClick
            )
        } else if (status != RotaStatus.NOTHING) {
            val (contentColor, containerColor) = when (status) {
                RotaStatus.PENDING -> MaterialTheme.colorScheme.onTertiaryContainer to MaterialTheme.colorScheme.tertiaryContainer
                RotaStatus.ACCEPTED -> MaterialTheme.colorScheme.onPrimaryContainer to MaterialTheme.colorScheme.primaryContainer
                RotaStatus.REJECTED -> MaterialTheme.colorScheme.onErrorContainer to MaterialTheme.colorScheme.error
            }
            val statusText = if (status == RotaStatus.PENDING && isCurrentUser) "Handover Requested" else status.status
            Text(
                modifier = Modifier.clip(RoundedCornerShape(dimens.cornerRadiusSmall))
                    .background(containerColor).padding(4.dp),
                text = statusText,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = contentColor
                )
            )
        }

        rightIcon?.let {
            Icon(
                modifier = Modifier.size(dimens.smallIconSize),
                painter = painterResource(rightIcon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun CancelRequestChip(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(dimens.cornerRadiusSmall))
            .background(MaterialTheme.colorScheme.error)
            .clickable(enabled = !isLoading) { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(14.dp),
                color = MaterialTheme.colorScheme.onError,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "Cancel Request",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onError
                )
            )
        }
    }
}

@Composable
@Preview
private fun Preview() {
    TeamShiftCard()
}