package org.worklog.app.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun TeamShiftCard(
    modifier: Modifier = Modifier,
    shift: String = "No Shift",
    name: String = "Md Hasan (You)",
    profileImage: String = "",
    status: RotaStatus = RotaStatus.NOTHING,
    rightIcon: DrawableResource? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(dimens.cornerRadius))
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(dimens.cornerRadius)
            )
            .clickable { onClick() }
            .padding(
                vertical = 6.dp,
                horizontal = 12.dp
            )
    ) {
        AsyncImage(
            modifier = Modifier.size(40.dp)
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
                text = shift,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall
            )
        }

        if (status != RotaStatus.NOTHING) {
            val (contentColor, containerColor) = when (status) {
                RotaStatus.PENDING -> MaterialTheme.colorScheme.onTertiaryContainer to MaterialTheme.colorScheme.tertiaryContainer
                RotaStatus.ACCEPTED -> MaterialTheme.colorScheme.onPrimaryContainer to MaterialTheme.colorScheme.primaryContainer
                RotaStatus.REJECTED -> MaterialTheme.colorScheme.onErrorContainer to MaterialTheme.colorScheme.error
            }
            Text(
                modifier = Modifier.clip(RoundedCornerShape(dimens.cornerRadiusSmall))
                    .background(containerColor).padding(4.dp),
                text = status.status,
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
@Preview
private fun Preview() {
    TeamShiftCard()
}