package org.worklog.app.presentation.component

import androidx.compose.material3.*
import androidx.compose.runtime.*
import java.time.*
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun PlatformDatePicker(
    show: Boolean,
    initialDate: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    val zone = remember { ZoneOffset.UTC }
    val formatter = remember { DateTimeFormatter.ISO_LOCAL_DATE }

    val todayMillis = remember {
        LocalDate.now(zone)
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()
    }

    val initialMillis = remember(initialDate) {
        runCatching {
            LocalDate.parse(initialDate, formatter)
                .atStartOfDay(zone)
                .toInstant()
                .toEpochMilli()
        }.getOrElse { todayMillis }
    }

    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= todayMillis
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = state.selectedDateMillis ?: todayMillis
                    val date = Instant.ofEpochMilli(millis)
                        .atZone(zone)
                        .toLocalDate()

                    onConfirm(date.format(formatter))
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = state,
            showModeToggle = false
        )
    }
}
