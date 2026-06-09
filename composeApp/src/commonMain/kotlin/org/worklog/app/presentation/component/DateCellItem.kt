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

// Figma: selected cell border is #00E1FF (cyan) — matches M3/sys/light/on-background in design file
private val FigmaSelectedBorder = Color(0xFF00E1FF)
// Today's cell is marked with a red outline so it's instantly identifiable.
private val TodayBorder = Color(0xFFD32F2F)

@Composable
fun DateCellItem(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isSelected: Boolean = false,
    isToday: Boolean = false,
    dayName: String? = "Mon",
    date: String = "1",
    shift: String = "Off",
    isRotaPublished: Boolean = true,
    onClick: () -> Unit = {}
) {
    // Selection only changes the BORDER (→ cyan), not the cell background — matches Figma exactly
    val (containerColor, contentColor) = shiftColors(shift, isEnabled)

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
                    width = if (isToday && !isSelected) 2.dp else 1.dp,
                    // Priority: disabled → muted, selected (user action) → cyan,
                    // today → red, otherwise → primary teal.
                    color = when {
                        !isEnabled -> MaterialTheme.colorScheme.surfaceDim
                        isSelected -> FigmaSelectedBorder
                        isToday -> TodayBorder
                        else -> MaterialTheme.colorScheme.primary
                    },
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

// Per-code palette for the calendar cell background. Each shift code gets a
// distinct tint so the calendar doubles as a legend (LD, N, OFF, A/L, BAL).
private val BgLD    = Color(0xFFFFFFFF) // Long Day      – white
private val BgN     = Color(0xFF2B3133) // Night         – near-black
private val BgOff   = Color(0xFFD5DBDD) // Off (scheduled) – grey
private val BgAL    = Color(0xFFB8EAFF) // Annual Leave  – light blue
private val BgBAL   = Color(0xFFFFD6E7) // Birthday      – soft pink
private val BgEmpty = Color(0xFFF5FAFE) // No rota row   – almost white (placeholder day)
private val FgEmpty = Color(0xFF9CA3A8) // Muted grey text inside the "-" badge

@Composable
private fun shiftColors(
    shift: String,
    isEnabled: Boolean,
): Pair<Color, Color> {
    if (!isEnabled) {
        return MaterialTheme.colorScheme.surfaceBright to
                MaterialTheme.colorScheme.outline
    }
    val onDark = Color.White
    val onLight = MaterialTheme.colorScheme.onSurface
    return when (shift.uppercase()) {
        "N"          -> BgN to onDark
        "LD"         -> BgLD to onLight
        "A/L", "AL"  -> BgAL to onLight
        "BAL"        -> BgBAL to onLight
        "-", ""      -> BgEmpty to FgEmpty   // no rota for this day – render dash in muted grey
        else         -> BgOff to onLight
    }
}

@Preview(showBackground = true)
@Composable
private fun DateCellItemPreview() {
    WorkLogTheme {
        DateCellItem(
            dayName = "Mon",
            date = "1",
            shift = "N"
        )
    }
}