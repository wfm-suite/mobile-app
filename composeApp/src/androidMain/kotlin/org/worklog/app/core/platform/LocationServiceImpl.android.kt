package org.worklog.app.core.platform

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationServiceImpl(private val context: Context) : LocationService {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LocationData? =
        suspendCancellableCoroutine { continuation ->
            val cts = CancellationTokenSource()
            fusedLocationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                .addOnSuccessListener { location ->
                    continuation.resume(
                        location?.let { LocationData(it.latitude, it.longitude) }
                    )
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
            continuation.invokeOnCancellation { cts.cancel() }
        }
}
