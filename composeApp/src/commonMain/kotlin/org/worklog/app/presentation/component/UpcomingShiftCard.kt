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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.worklog.app.domain.model.Rota
import org.worklog.app.domain.model.RotaStatus

@Composable
fun UpcomingShiftCard(
    shift: Rota,
    isToday: Boolean = false,
    onClick: (Rota) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE8F5F8))
            .clickable { if (shift.id > 0) onClick(shift) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Date Block
        Column(
            modifier = Modifier
                .size(width = 54.dp, height = 54.dp)
                .background(
                    color = if (isToday) Color(0xFF2D3E3F) else Color.White,
                    shape = RoundedCornerShape(10.dp)
                )
                .border(
                    width = if (isToday) 0.dp else 1.dp,
                    color = Color(0xFFD1D5DB),
                    shape = RoundedCornerShape(10.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = shift.date,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isToday) Color.White else Color(0xFF1F2937),
                    fontSize = 18.sp
                )
            )
            Text(
                text = shift.dayName.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    color = if (isToday) Color.White.copy(alpha = 0.7f) else Color(0xFF6B7280)
                )
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Shift Details
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val timeLabel = if (shift.shiftStartTime.isNotBlank()) 
                    "${shift.shiftStartTime} - ${shift.shiftEndTime}" 
                    else "No Shift"
                
                Text(
                    text = "$timeLabel ${shift.shiftLabel}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F2937),
                        fontSize = 15.sp
                    )
                )
            }
            
            Text(
                text = "${shift.location}, ${shift.designation}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF6B7280),
                    fontSize = 12.sp
                ),
                maxLines = 1
            )
        }

        // Optional Handover Badge (Matching the Cyan/Teal color in image)
        if (shift.status == RotaStatus.PENDING) {
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFB2EBF2).copy(alpha = 0.7f))
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Pending\nHandover",
                    lineHeight = 10.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00838F),
                        fontSize = 8.sp
                    )
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(20.dp)
        )
    }
}
