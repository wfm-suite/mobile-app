package org.worklog.app.domain.model

data class UserEmergencyContact(
    val id: Int,
    val name: String,
    val relationship: String,
    val phone: String,
    val email: String? = null,
    val address: String? = null,
    val isPrimary: Boolean = false
)
