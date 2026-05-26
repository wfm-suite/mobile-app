package org.worklog.app.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.worklog.app.domain.model.Rota
import org.worklog.app.domain.model.RotaStatus

// Figma tokens (Node 2129:16725)
private val FigmaPrimary = Color(0xFF007B99)
private val FigmaHandoverBg = Color(0xFF9DF0FB)
private val FigmaHandoverText = Color(0xFF004F56)
private val FigmaTextDark = Color(0xFF000000)

// Row background colors per category (keep in one place)
private val BgDay   = Color(0xFFFFFFFF)  // White
private val BgNight = Color(0xFF000000)  // Black
private val BgOff   = Color(0xFFE9EFF1)  // Grey
private val BgLeave = Color(0xFFB8EAFF)  // primaryContainer
private val BgTodayHighlight = Color(0xFFC8E6C9) // Unique highlight for Today (Light Green)

private enum class ShiftCategory { DAY, NIGHT, OFF, LEAVE }

private fun categorize(shift: Rota): ShiftCategory {
    // 1. shift.isLeave == true -> LEAVE
    if (shift.isLeave) return ShiftCategory.LEAVE
    
    // 2. shift.shiftStatus == "off" OR shift.shortCode == "OFF" -> OFF
    if (shift.shiftStatus.equals("off", ignoreCase = true) || 
        shift.shortCode.equals("OFF", ignoreCase = true)) {
        return ShiftCategory.OFF
    }
    
    // 3. shift.shortCode starts with "N" OR shift.shiftType contains "night"/"evening" -> NIGHT
    val code = shift.shortCode.uppercase()
    if (code.startsWith("N") || 
        shift.shiftType.contains("night", ignoreCase = true) || 
        shift.shiftType.contains("evening", ignoreCase = true)) {
        return ShiftCategory.NIGHT
    }
    
    // 4. else -> DAY
    return ShiftCategory.DAY
}

@Composable
fun UpcomingShiftCard(
    shift: Rota,
    userFloor: String = "",
    isToday: Boolean = false,
    onClick: (Rota) -> Unit = {}
) {
    val category = categorize(shift)

    // Background color for the details bar (Right side)
    // Now it changes based on category/today status
    val rowBg = when {
        isToday -> BgTodayHighlight
        category == ShiftCategory.NIGHT -> BgNight
        category == ShiftCategory.OFF -> BgOff
        category == ShiftCategory.LEAVE -> BgLeave
        else -> Color(0xFFCFE6F1) // Default Figma Light Blue
    }

    // Determine text/icon color for the details bar
    val contentColor = if (category == ShiftCategory.NIGHT && !isToday) Color.White else FigmaTextDark

    // Date badge bg per category
    val badgeBgForCategory = when (category) {
        ShiftCategory.NIGHT -> BgNight
        ShiftCategory.OFF   -> BgOff
        ShiftCategory.LEAVE -> BgLeave
        ShiftCategory.DAY   -> BgDay
    }

    // Title text per category. OFF and LEAVE rows show ONLY their label —
    // no location, no designation. Day/Night rows show the time range + floor.
    val titleText: String = when (category) {
        ShiftCategory.OFF -> "No Shift"
        ShiftCategory.LEAVE -> when (shift.shortCode.uppercase()) {
            "AL"  -> "✈️ Annual Leave"
            "BAL" -> "🎂 Birthday Leave"
            else  -> shift.shiftLabel.ifBlank { "Leave" }
        }
        else -> {
            val time = if (shift.shiftStartTime.isNotBlank())
                "${shift.shiftStartTime} - ${shift.shiftEndTime}"
            else "No Shift"
            val floor = if (shift.floorName.isNotBlank()) " • ${shift.floorName}" else ""
            "$time$floor"
        }
    }

    // Day/Night rows show location + designation. OFF and LEAVE hide them.
    val showSubtitle = category == ShiftCategory.DAY || category == ShiftCategory.NIGHT

    // Figma row: 50dp height, gap 12dp between badge and detail
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Date Badge — 50x50dp, corner 12dp, border 1dp #007B99
        // Today gets the unique highlight; otherwise uses the category badge color.
        val badgeBg = if (isToday) BgTodayHighlight else badgeBgForCategory
        val isDarkBadge = (category == ShiftCategory.NIGHT && !isToday)
        val badgeTextColor = if (isDarkBadge) Color.White else FigmaTextDark

        Column(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(badgeBg)
                .border(
                    width = if (isToday) 2.dp else 1.dp,
                    color = FigmaPrimary,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Day number: Poppins Regular 16sp
            Text(
                text = shift.date,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
                    color = badgeTextColor,
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center
            )
            // Day name: Poppins Regular 10sp
            Text(
                text = shift.dayName.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Normal,
                    color = badgeTextColor,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center
            )
        }

        // Shift Detail Container — flex 1, height 50dp, corner 12dp
        Row(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(rowBg)
                .clickable(enabled = shift.id > 0) { onClick(shift) }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text column: title (always) + subtitle (day/night only)
            Column(modifier = Modifier.weight(1f)) {
                // Title: time range for shifts, "No Shift" for off, "✈️ Annual Leave" etc for leaves
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                        color = contentColor,
                        fontSize = 16.sp,
                        letterSpacing = 0.5.sp
                    ),
                    maxLines = 1
                )
                // Subtitle: location + designation — only on day/night shifts
                if (showSubtitle) {
                    Text(
                        text = "${shift.location}, ${shift.designation}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Normal,
                            color = contentColor,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        ),
                        maxLines = 1
                    )
                }
            }

            // Pending Handover badge
            if (shift.status == RotaStatus.PENDING) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(FigmaHandoverBg)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Pending\nHandover",
                        textAlign = TextAlign.Center,
                        lineHeight = 12.sp,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = FigmaHandoverText,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }

            // Right arrow
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
