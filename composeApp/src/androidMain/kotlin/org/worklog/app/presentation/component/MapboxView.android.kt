package org.worklog.app.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.locationcomponent.location
import org.worklog.app.BuildConfig

@Composable
actual fun MapboxView(
    modifier: Modifier,
    latitude: Double,
    longitude: Double,
    zoom: Double,
    onMapLoaded: () -> Unit
) {
    // Ensure Mapbox access token is set before any Mapbox component is initialized
    if (MapboxOptions.accessToken.isBlank()) {
        MapboxOptions.accessToken = BuildConfig.MAPBOX_ACCESS_TOKEN
    }

    val viewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(longitude, latitude))
            zoom(zoom)
            pitch(0.0)
            bearing(0.0)
        }
    }

    // Automatically follow user location on start (centers location)
    LaunchedEffect(Unit) {
        viewportState.transitionToFollowPuckState()
    }

    Box(modifier = modifier) {
        MapboxMap(
            modifier = Modifier.matchParentSize(),
            mapViewportState = viewportState,
            style = { MapStyle(style = Style.STANDARD) },
        ) {
            MapEffect(Unit) { mapView ->
                mapView.mapboxMap.subscribeMapLoaded {
                    onMapLoaded()
                }
                mapView.location.updateSettings {
                    enabled = true // Must be enabled to follow the puck
                    pulsingEnabled = false
                }
            }
        }

        // Arrow button (Locate Me) - Aligned with the shift info row (nudged up further)
        IconButton(
            onClick = {
                viewportState.transitionToFollowPuckState()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 90.dp)
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.9f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.NearMe,
                contentDescription = "Locate Me",
                tint = Color(0xFF007B99),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
