package org.worklog.app.data.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddressRequest(
    @SerialName("address_type") val addressType: String,
    @SerialName("address_line1") val addressLine1: String,
    @SerialName("address_line2") val addressLine2: String? = null,
    @SerialName("city") val city: String,
    @SerialName("county") val county: String? = null,
    @SerialName("postcode") val postcode: String,
    @SerialName("country") val country: String = "UK",
    @SerialName("is_primary") val isPrimary: Boolean = false
)
