package org.worklog.app.presentation.component

import androidx.compose.runtime.Composable

@Composable
expect fun PlatformDatePicker(
    show: Boolean,
    initialDate: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
)