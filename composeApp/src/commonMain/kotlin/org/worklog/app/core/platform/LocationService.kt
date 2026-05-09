package org.worklog.app.core.platform

data class LocationData(val latitude: Double, val longitude: Double)

interface LocationService {
    suspend fun getCurrentLocation(): LocationData?
}
