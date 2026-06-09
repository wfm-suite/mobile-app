package org.worklog.app.data.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmergencyContactRequest(
    @SerialName("name") val name: String,
    @SerialName("relationship") val relationship: String,
    @SerialName("phone") val phone: String,
    @SerialName("email") val email: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("is_primary") val isPrimary: Boolean = false
)
