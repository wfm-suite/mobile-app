package org.worklog.app.core.util

expect class AppActions() {
    fun openDialer(phoneNumber: String)

    fun openMessageCompose(phoneNumber: String)

    fun openMap(latitude: String, longitude: String)
}
