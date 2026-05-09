package org.worklog.app.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.worklog.app.presentation.theme.WorkLogTheme
import org.worklog.app.presentation.theme.dimens

@Composable
fun DateCellItem(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isSelected: Boolean = false,
    dayName: String? = "Mon",
    date: String = "1",
    shift: String = "Off",
    isRotaPublished: Boolean = true,
    onClick: () -> Unit = {}
) {
    val (containerColor, contentColor) = shiftColors(shift, isEnabled, isSelected)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        dayName?.let {
            Text(
                text = dayName,
                style = MaterialTheme.typography.labelSmall
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 3.dp)
                .clip(RoundedCornerShape(dimens.cornerRadius))
                .background(containerColor)
                .clickable { onClick() }
                .border(
                    width = 1.dp,
                    color = if (!isEnabled) MaterialTheme.colorScheme.surfaceDim else if (isSelected) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(dimens.cornerRadius)
                ).padding(
                    vertical = 5.dp,
                    horizontal = 8.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date,
                maxLines = 1,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = contentColor
                )
            )
            Text(
                text = if (isRotaPublished) shift else "-",
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = contentColor
                )
            )
        }
    }
}

@Composable
private fun shiftColors(
    shift: String,
    isEnabled: Boolean,
    isSelected: Boolean
): Pair<Color, Color> {
    if (!isEnabled) {
        return MaterialTheme.colorScheme.surfaceBright to
                MaterialTheme.colorScheme.outline
    }
    if (isSelected) {
        return MaterialTheme.colorScheme.tertiaryFixed to MaterialTheme.colorScheme.onBackground
    }
    return when (shift) {
        "NS" -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        "LD" -> MaterialTheme.colorScheme.onPrimary to MaterialTheme.colorScheme.onBackground
        else -> MaterialTheme.colorScheme.surfaceDim to MaterialTheme.colorScheme.onBackground
    }
}

@Preview(showBackground = true)
@Composable
private fun DateCellItemPreview() {
    WorkLogTheme {
        DateCellItem(
            dayName = "Mon",
            date = "1",
            shift = "NS"
        )
    }
}