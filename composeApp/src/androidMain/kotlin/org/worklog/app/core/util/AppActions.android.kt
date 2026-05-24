package org.worklog.app.core.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


actual class AppActions : KoinComponent {

    private val context: Context by inject()

    actual fun openDialer(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:$phoneNumber".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    actual fun openMessageCompose(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "smsto:$phoneNumber".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    actual fun openMap(latitude: String, longitude: String) {
        val gmmIntentUri = "geo:$latitude,$longitude?q=$latitude,$longitude".toUri()
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(mapIntent)
    }
}