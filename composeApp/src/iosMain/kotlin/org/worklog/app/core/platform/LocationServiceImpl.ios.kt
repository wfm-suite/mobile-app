package org.worklog.app.core.platform

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLDistanceFilterNone
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.darwin.NSObject
import kotlin.coroutines.resume

class LocationServiceImpl : LocationService {

    override suspend fun getCurrentLocation(): LocationData? =
        suspendCancellableCoroutine { continuation ->
            val manager = CLLocationManager()
            manager.desiredAccuracy = kCLLocationAccuracyBest
            manager.distanceFilter = kCLDistanceFilterNone

            val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManager(
                    manager: CLLocationManager,
                    didUpdateLocations: List<*>
                ) {
                    val location = manager.location
                    manager.stopUpdatingLocation()
                    continuation.resume(
                        location?.let {
                            LocationData(it.coordinate.latitude, it.coordinate.longitude)
                        }
                    )
                }

                override fun locationManager(
                    manager: CLLocationManager,
                    didFailWithError: platform.Foundation.NSError
                ) {
                    manager.stopUpdatingLocation()
                    continuation.resume(null)
                }
            }

            manager.delegate = delegate
            manager.requestWhenInUseAuthorization()
            manager.startUpdatingLocation()

            continuation.invokeOnCancellation { manager.stopUpdatingLocation() }
        }
}
