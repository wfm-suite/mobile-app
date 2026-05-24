package org.worklog.app.core.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@Composable
actual fun rememberOpenMapAction(onLocationReceived: (String, String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val appActions = remember { AppActions() }
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) fetchAndOpen(fusedClient, appActions, onLocationReceived)
    }

    return {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            fetchAndOpen(fusedClient, appActions, onLocationReceived)
        } else {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}

@SuppressLint("MissingPermission")
private fun fetchAndOpen(
    client: FusedLocationProviderClient, 
    appActions: AppActions,
    onLocationReceived: (String, String) -> Unit
) {
    client.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            val lat = location.latitude.toString()
            val lon = location.longitude.toString()
            onLocationReceived(lat, lon)
            appActions.openMap(lat, lon)
        }
    }
}
