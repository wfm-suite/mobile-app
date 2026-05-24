package org.worklog.app.core.util

interface LocationTracker {
    suspend fun getCurrentLocation(): Pair<Double, Double>?
}
