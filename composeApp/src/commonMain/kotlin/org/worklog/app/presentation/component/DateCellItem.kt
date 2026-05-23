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
        return MaterialTheme.colorScheme.surfaceBright to MaterialTheme.colorScheme.outline
    }
    if (isSelected) {
        return MaterialTheme.colorScheme.tertiaryFixed to MaterialTheme.colorScheme.onBackground
    }
    val upper = shift.uppercase().trim()
    if (upper == "OFF" || upper.isBlank()) {
        return MaterialTheme.colorScheme.surfaceDim to MaterialTheme.colorScheme.onBackground
    }
    // Dynamic colour — any code from the database gets a consistent colour
    // derived from the code string itself, so new codes work automatically.
    val bg = shiftCodeBackground(upper)
    return bg to Color.White
}

private val shiftColorPalette = listOf(
    Color(0xFF1565C0),  // blue        – e.g. D (Day)
    Color(0xFF1A237E),  // dark navy   – e.g. N (Night)
    Color(0xFF2E7D32),  // green       – e.g. L (Long)
    Color(0xFF00695C),  // teal        – e.g. L1D
    Color(0xFFE65100),  // deep orange – e.g. LD
    Color(0xFF6A1B9A),  // purple      – e.g. NS
    Color(0xFF0277BD),  // light blue
    Color(0xFF558B2F),  // olive
    Color(0xFFC62828),  // red
    Color(0xFF00838F),  // cyan
)

private fun shiftCodeBackground(code: String): Color =
    shiftColorPalette[kotlin.math.abs(code.hashCode()) % shiftColorPalette.size]

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