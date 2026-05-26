package org.worklog.app.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MapboxView(
    modifier: Modifier = Modifier,
    latitude: Double,
    longitude: Double,
    zoom: Double = 15.0,
    onMapLoaded: () -> Unit = {}
)
