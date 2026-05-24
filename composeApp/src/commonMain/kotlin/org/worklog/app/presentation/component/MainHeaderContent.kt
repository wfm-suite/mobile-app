package org.worklog.app.presentation.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainHeaderContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    greetingText: String = "",
    userName: String = "",
    date: String = "",
    onNotificationClick: () -> Unit = {},
    onMapClick: () -> Unit = {}, // Keep for compatibility but image shows top right notification
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            if (isLoading) {
                ShimmerBox(
                    height = 24.dp,
                    width = 150.dp,
                    cornerRadius = 4.dp,
                    shimmerColors = listOf(Color.White.copy(0.3f), Color.White.copy(0.1f), Color.White.copy(0.3f))
                )
            } else {
                Text(
                    text = "$greetingText, $userName",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                if (date.isNotBlank()) {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                    )
                }
            }
        }
        
        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
