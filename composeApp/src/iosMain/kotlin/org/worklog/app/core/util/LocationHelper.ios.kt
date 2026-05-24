package org.worklog.app.core.util

import androidx.compose.runtime.Composable
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import kotlinx.cinterop.useContents
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberOpenMapAction(onLocationReceived: (String, String) -> Unit): () -> Unit {
    return {
        val manager = CLLocationManager()
        val appActions = AppActions()

        when (manager.authorizationStatus) {
            kCLAuthorizationStatusNotDetermined -> {
                manager.requestWhenInUseAuthorization()
            }
            kCLAuthorizationStatusAuthorizedAlways,
            kCLAuthorizationStatusAuthorizedWhenInUse -> {
                val location = manager.location
                if (location != null) {
                    val lat = location.coordinate.useContents { latitude }.toString()
                    val lng = location.coordinate.useContents { longitude }.toString()
                    onLocationReceived(lat, lng)
                    appActions.openMap(lat, lng)
                }
            }
            else -> {}
        }
    }
}
