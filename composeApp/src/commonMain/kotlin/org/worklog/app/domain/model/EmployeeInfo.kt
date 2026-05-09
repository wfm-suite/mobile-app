package org.worklog.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeInfo(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val displayName: String,
    val profilePicture: String,
    val designation: String,
    val email: String,
    val phone: String,
    val floorId: String,
    val floorName: String,
    val departmentId: String,
    val departmentName: String
)
