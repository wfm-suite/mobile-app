package org.worklog.app.data.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResignationRequest(
    @SerialName("last_working_day") val lastWorkingDay: String,
    @SerialName("reason") val reason: String? = null
)
