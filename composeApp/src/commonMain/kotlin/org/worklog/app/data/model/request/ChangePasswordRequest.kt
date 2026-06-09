package org.worklog.app.data.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    @SerialName("current_password") val currentPassword: String,
    @SerialName("password") val newPassword: String,
    @SerialName("password_confirmation") val confirmPassword: String
)
