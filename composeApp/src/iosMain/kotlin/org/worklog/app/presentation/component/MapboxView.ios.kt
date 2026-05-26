package org.worklog.app.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
actual fun MapboxView(
    modifier: Modifier,
    latitude: Double,
    longitude: Double,
    zoom: Double,
    onMapLoaded: () -> Unit
) {
    LaunchedEffect(Unit) {
        onMapLoaded()
    }

    Box(
        modifier = modifier.background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text("Map View (iOS Placeholder)")
    }
}
