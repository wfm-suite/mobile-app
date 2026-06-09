package org.worklog.app.domain.model

data class UserAddress(
    val id: Int,
    val addressType: String, // "home", "correspondence", "other"
    val addressLine1: String,
    val addressLine2: String? = null,
    val city: String,
    val county: String? = null,
    val postcode: String,
    val country: String = "UK",
    val isPrimary: Boolean = false
)
