package org.worklog.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class HolidayRequest(
    val reason: String?,
    val dates: List<String>
)