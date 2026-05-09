package org.worklog.app.core.util

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual class AppActions {

    actual fun openDialer(phoneNumber: String) {
        openUrl("tel:$phoneNumber")
    }

    actual fun openMessageCompose(phoneNumber: String) {
        openUrl("sms:$phoneNumber")
    }

    private fun openUrl(urlString: String) {
        val url = NSURL.URLWithString(urlString) ?: return
        val app = UIApplication.sharedApplication

        if (app.canOpenURL(url)) {
            app.openURL(url, options = emptyMap<Any?, Any?>()) { success ->
                if (!success) {
                    println("Failed to open URL: $urlString")
                }
            }
        } else {
            // This is where the Simulator currently hits
            println("Device cannot open URL: $urlString (Likely Simulator or iPad)")
        }
    }
}