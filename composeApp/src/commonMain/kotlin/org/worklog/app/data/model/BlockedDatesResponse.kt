package org.worklog.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlockedDatesResponse(
    @SerialName("blocked_dates")
    val blockedDates: List<String> = emptyList()
)
