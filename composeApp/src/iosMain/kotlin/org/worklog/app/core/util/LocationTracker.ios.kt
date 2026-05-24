package org.worklog.app.core.util

import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import kotlinx.cinterop.useContents
import kotlinx.cinterop.ExperimentalForeignApi

class IosLocationTracker : LocationTracker {
    @OptIn(ExperimentalForeignApi::class)
    override suspend fun getCurrentLocation(): Pair<Double, Double>? {
        val manager = CLLocationManager()
        val status = manager.authorizationStatus
        
        if (status == kCLAuthorizationStatusAuthorizedAlways || status == kCLAuthorizationStatusAuthorizedWhenInUse) {
            val location = manager.location
            if (location != null) {
                return location.coordinate.useContents {
                    Pair(latitude, longitude)
                }
            }
        }
        return null
    }
}
