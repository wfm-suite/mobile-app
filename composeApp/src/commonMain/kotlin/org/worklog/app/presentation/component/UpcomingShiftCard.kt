package org.worklog.app.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
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
import org.worklog.app.domain.model.IncomingSwap
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

// "Today" is marked only with a red outline — the fill still follows the shift category.
private val TodayBorder = Color(0xFFD32F2F)
private val TodayBorderWidth = 2.dp

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
    lastPublishedDate: String = "",
    incomingSwap: IncomingSwap? = null,
    isResponding: Boolean = false,
    isCancelling: Boolean = false,
    onClick: (Rota) -> Unit = {},
    onAcceptSwap: (Int) -> Unit = {},
    onDenySwap: (Int) -> Unit = {},
    onCancelRequest: (Rota) -> Unit = {}
) {
    // Outgoing pending request from this user: render the Figma cancel-row
    // (Node 2318-3719) in place of the normal shift row. Incoming swap
    // requests still take precedence below.
    if (incomingSwap == null &&
        shift.status == RotaStatus.PENDING &&
        shift.requestId > 0
    ) {
        PendingRequestRow(
            shift = shift,
            isCancelling = isCancelling,
            onCancel = { onCancelRequest(shift) }
        )
        return
    }

    // A row is "unpublished" if it's an OFF/no-shift past the last known
    // published date — the rota hasn't been released for that day yet.
    val isUnpublished = lastPublishedDate.isNotEmpty() &&
        shift.fullDate > lastPublishedDate &&
        (shift.shiftStatus.equals("off", ignoreCase = true) ||
            shift.shortCode.equals("OFF", ignoreCase = true)) &&
        !shift.isLeave
    val category = categorize(shift)

    // Background color for the details bar (Right side) — always follows the shift category.
    val rowBg = when (category) {
        ShiftCategory.NIGHT -> BgNight
        ShiftCategory.OFF   -> BgOff
        ShiftCategory.LEAVE -> BgLeave
        ShiftCategory.DAY   -> Color(0xFFCFE6F1) // Default Figma Light Blue
    }

    // Determine text/icon color for the details bar
    val contentColor = if (category == ShiftCategory.NIGHT) Color.White else FigmaTextDark

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
        ShiftCategory.OFF -> when {
            isUnpublished -> "—"
            shift.status == RotaStatus.ACCEPTED && shift.shiftLabel.isNotBlank() -> shift.shiftLabel
            else -> "No Shift"
        }
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

    val hasSwapRequest = incomingSwap != null

    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
    // Figma row: 50dp height, gap 12dp between badge and detail
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Date Badge — 50x50dp, corner 12dp.
        // Fill always follows the shift category; today is marked with a red border only.
        val badgeBg = badgeBgForCategory
        val isDarkBadge = (category == ShiftCategory.NIGHT)
        val badgeTextColor = if (isDarkBadge) Color.White else FigmaTextDark

        Column(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(badgeBg)
                .border(
                    width = if (isToday) TodayBorderWidth else 1.dp,
                    color = if (isToday) TodayBorder else FigmaPrimary,
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

        // Shift Detail Container — flex 1, height 50dp, corner 12dp.
        // Today gets a red outline; non-today rows have no border.
        Row(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(rowBg)
                .then(
                    if (isToday) Modifier.border(
                        width = TodayBorderWidth,
                        color = TodayBorder,
                        shape = RoundedCornerShape(12.dp)
                    ) else Modifier
                )
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

            // Incoming swap request badge takes precedence over the existing
            // handover/swap status badge.
            if (hasSwapRequest) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(FigmaHandoverBg)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Swap by\n${incomingSwap?.requesterName.orEmpty()}",
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
            } else if (shift.status == RotaStatus.PENDING || shift.status == RotaStatus.ACCEPTED) {
                val typeLabel = if (shift.requestType == "swap") "Swap" else "Handover"
                val stateLabel = if (shift.status == RotaStatus.ACCEPTED) "Accepted" else "Requested"
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(FigmaHandoverBg)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "$typeLabel\n$stateLabel",
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

    if (hasSwapRequest && incomingSwap != null) {
        val offeredTime = listOfNotNull(
            incomingSwap.offeredRota.date,
            incomingSwap.offeredRota.shiftStart?.let { s ->
                incomingSwap.offeredRota.shiftEnd?.let { e -> "$s - $e" }
            }
        ).joinToString(" · ")
        val offeredBranch = incomingSwap.offeredRota.branch
        val offeredLine = listOfNotNull(
            offeredTime.takeIf { it.isNotBlank() },
            offeredBranch?.takeIf { it.isNotBlank() }
        ).joinToString(" • ")
        if (offeredLine.isNotBlank()) {
            Text(
                text = "Their shift: $offeredLine",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Normal,
                    color = FigmaHandoverText,
                    fontSize = 11.sp,
                    letterSpacing = 0.4.sp
                ),
                modifier = Modifier.padding(start = 62.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 62.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SwapActionButton(
                label = "Deny",
                bg = Color(0xFFBA1A1A),
                fg = Color.White,
                isLoading = isResponding,
                enabled = !isResponding,
                modifier = Modifier.weight(1f),
                onClick = { onDenySwap(incomingSwap.id) }
            )
            SwapActionButton(
                label = "Accept",
                bg = FigmaPrimary,
                fg = Color.White,
                isLoading = isResponding,
                enabled = !isResponding,
                modifier = Modifier.weight(1f),
                onClick = { onAcceptSwap(incomingSwap.id) }
            )
        }
    }
    }
}

/**
 * Pending outgoing handover/swap row — Figma Node 2318-3719.
 * 360x50 capsule, light-blue background, avatar + time/name + red Cancel.
 * Renders in place of the normal UpcomingShiftCard when the user has a
 * PENDING outgoing request on this shift.
 */
@Composable
private fun PendingRequestRow(
    shift: Rota,
    isCancelling: Boolean,
    onCancel: () -> Unit,
    isToday: Boolean = false
) {
    val time = if (shift.shiftStartTime.isNotBlank())
        "${shift.shiftStartTime} - ${shift.shiftEndTime}"
    else "Shift"
    val typeLabel = if (shift.requestType == "swap") "Swap" else "Handover"
    // For swaps we have a real recipient name; handovers go to whoever picks
    // them up, so fall back to a short status line.
    val subtitle = when {
        shift.requestType == "handover" && shift.recipientName.isBlank() -> "Handover · Pending approval"
        shift.requestType == "swap" && shift.recipientName.isBlank() -> "Swap · Pending"
        else -> "${shift.recipientName} · $typeLabel pending"
    }

    // Outer row keeps the same date-badge + detail-capsule shape as the
    // regular UpcomingShiftCard so the day stays anchored on the left.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Date badge — matches the standard 50x50 badge so the user can still
        // tell which day this pending request belongs to.
        Column(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(BgDay)
                .border(
                    width = if (isToday) TodayBorderWidth else 1.dp,
                    color = if (isToday) TodayBorder else FigmaPrimary,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = shift.date,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
                    color = FigmaTextDark,
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = shift.dayName.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Normal,
                    color = FigmaTextDark,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center
            )
        }

        // Detail capsule — the Figma 2318:3719 row content (avatar + name + cancel)
    Row(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFCFE6F1))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Avatar (36x36) — Figma: dark teal 1dp border, image fill.
        // Falls back to a tinted initial when no image is available.
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(Color(0xFFE0F1F7))
                .border(
                    width = 1.dp,
                    color = FigmaPrimary,
                    shape = RoundedCornerShape(percent = 50)
                ),
            contentAlignment = Alignment.Center
        ) {
            val initial = when {
                shift.requestType == "handover" && shift.recipientName.isBlank() -> "H"
                shift.requestType == "swap" && shift.recipientName.isBlank() -> "S"
                else -> shift.recipientName.firstOrNull()?.uppercase() ?: "?"
            }
            Text(
                text = initial,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = FigmaPrimary,
                    fontSize = 14.sp
                )
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = time,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
                    color = FigmaTextDark,
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp
                ),
                maxLines = 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Normal,
                    color = FigmaTextDark,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp
                ),
                maxLines = 1
            )
        }

        // Cancel Request button — Figma: 104x24, #BA1A1A, 6dp radius
        Box(
            modifier = Modifier
                .height(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFBA1A1A))
                .clickable(enabled = !isCancelling) { onCancel() }
                .padding(horizontal = 9.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isCancelling) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 1.5.dp,
                    modifier = Modifier.size(12.dp)
                )
            } else {
                Text(
                    text = "Cancel Request",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
    }
}

@Composable
private fun SwapActionButton(
    label: String,
    bg: Color,
    fg: Color,
    isLoading: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = fg,
                strokeWidth = 2.dp,
                modifier = Modifier.size(16.dp)
            )
        } else {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = fg,
                    fontSize = 14.sp,
                    letterSpacing = 0.4.sp
                )
            )
        }
    }
}
