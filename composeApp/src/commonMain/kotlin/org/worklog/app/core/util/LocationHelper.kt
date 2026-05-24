package org.worklog.app.core.util

import androidx.compose.runtime.Composable

@Composable
expect fun rememberOpenMapAction(onLocationReceived: (String, String) -> Unit = { _, _ -> }): () -> Unit
