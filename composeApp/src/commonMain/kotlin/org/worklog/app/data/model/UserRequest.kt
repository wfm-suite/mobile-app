package org.worklog.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("display_name")
    val displayName: String,
    val email: String,
    val phone: String,
    val gender: String,
    @SerialName("date_of_birth")
    val dateOfBirth: String,
    @SerialName("marital_status")
    val maritalStatus: String
)
