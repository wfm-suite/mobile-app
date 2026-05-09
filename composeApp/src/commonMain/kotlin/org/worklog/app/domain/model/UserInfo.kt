package org.worklog.app.domain.model

data class UserInfo(
    val id: String,
    val firstName: String,
    val lastName: String,
    val displayName: String,
    val profilePicture: String = "",
    val gender: String,
    val dateOfBirth: String,
    val maritalStatus: String,
    val email: String? = null,
    val phoneNumber: String,
    val companyName: String,
    val companyAddress: String,
    val branchName: String,
    val branchAddress: String,
    val floor: String,
    val department: String,
    val designation: String
)