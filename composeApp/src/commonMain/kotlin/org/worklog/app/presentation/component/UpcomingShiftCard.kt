package org.worklog.app.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.worklog.app.domain.model.Rota
import org.worklog.app.domain.model.RotaStatus
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.ic_next

@Composable
fun UpcomingShiftCard(
    shift: Rota,
    onClick: (Rota) -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable { onClick(shift) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = 50.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(dimens.cornerRadius)
                ).border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(dimens.cornerRadius)
                ).padding(
                    vertical = 4.dp,
                    horizontal = 6.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = shift.date,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = shift.dayName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Row(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(dimens.cornerRadius)
                ).padding(
                    vertical = 7.dp,
                    horizontal = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${shift.shiftStartTime} - ${shift.shiftEndTime}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
                Text(
                    text = "${shift.location}, ${shift.designation}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                )
            }

            if (shift.status != RotaStatus.NOTHING) {
                val (contentColor, containerColor) = when (shift.status) {
                    RotaStatus.PENDING -> MaterialTheme.colorScheme.onTertiaryContainer to MaterialTheme.colorScheme.tertiaryContainer
                    RotaStatus.ACCEPTED -> MaterialTheme.colorScheme.onPrimaryContainer to MaterialTheme.colorScheme.primaryContainer
                    RotaStatus.REJECTED -> MaterialTheme.colorScheme.onErrorContainer to MaterialTheme.colorScheme.error
                }
                Text(
                    modifier = Modifier.clip(RoundedCornerShape(dimens.cornerRadiusSmall))
                        .background(containerColor).padding(4.dp),
                    text = shift.status.status,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = contentColor
                    )
                )
            }
            Icon(
                modifier = Modifier.size(dimens.smallIconSize),
                painter = painterResource(Res.drawable.ic_next),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}