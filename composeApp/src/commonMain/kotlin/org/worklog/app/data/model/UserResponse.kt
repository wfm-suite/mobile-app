package org.worklog.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("access_token")
    val accessToken: String? = null,
    @SerialName("refresh_token")
    val refreshToken: String? = null,
    val token: String? = null,
    val user: UserDto
)

@Serializable
data class RefreshTokenRequest(
    @SerialName("refresh_token")
    val refreshToken: String
)

@Serializable
data class RefreshTokenResponse(
    @SerialName("access_token")
    val accessToken: String? = null,
    @SerialName("refresh_token")
    val refreshToken: String? = null
)

@Serializable
data class UserDto(
    val id: Int,
    @SerialName("first_name")
    val firstName: String?,
    @SerialName("last_name")
    val lastName: String?,
    @SerialName("display_name")
    val displayName: String?,
    @SerialName("profile_picture_url")
    val profilePicture: String? = "",
    val email: String? = "",
    @SerialName("company_name")
    val companyName: String? = "",
    val phone: String? = "",
    val gender: String? = "",
    @SerialName("date_of_birth")
    val dateOfBirth: String? = "",
    @SerialName("marital_status")
    val maritalStatus: String? = "",
    @SerialName("company_address")
    val companyAddress: String? = "",
    @SerialName("company_latitude")
    val companyLatitude: Double? = null,
    @SerialName("company_longitude")
    val companyLongitude: Double? = null,
    @SerialName("branch_name")
    val branchName: String? = "",
    @SerialName("branch_address")
    val branchAddress: String? = "",
    val floor: String? = "",
    val designation: String? = "",
    val department: String? = null,
    val roles: List<String>? = emptyList(),
    // RTW / Documents
    @SerialName("ni_number")
    val niNumber: String? = null,
    @SerialName("passport_number")
    val passportNumber: String? = null,
    @SerialName("passport_expiry")
    val passportExpiry: String? = null,
    @SerialName("visa_number")
    val visaNumber: String? = null,
    @SerialName("visa_expiry")
    val visaExpiry: String? = null,
    @SerialName("rtw_status")
    val rtwStatus: String? = null,
    @SerialName("rtw_expiry")
    val rtwExpiry: String? = null,
    // Equality / Diversity / Inclusion
    val ethnicity: String? = null,
    val nationality: String? = null,
    val disability: String? = null,
    val religion: String? = null,
    @SerialName("sexual_orientation")
    val sexualOrientation: String? = null,
    // Job Status
    @SerialName("employee_status")
    val employeeStatus: String? = null,
    @SerialName("employment_type")
    val employmentType: String? = null,
    @SerialName("contract_start_date")
    val contractStartDate: String? = null,
    @SerialName("contract_end_date")
    val contractEndDate: String? = null,
    // Vetting
    @SerialName("dbs_check_date")
    val dbsCheckDate: String? = null,
    @SerialName("dbs_certificate_number")
    val dbsCertificateNumber: String? = null,
    @SerialName("reference1_name")
    val reference1Name: String? = null,
    @SerialName("reference1_contact")
    val reference1Contact: String? = null,
    @SerialName("reference2_name")
    val reference2Name: String? = null,
    @SerialName("reference2_contact")
    val reference2Contact: String? = null,
    // Contact Details (next of kin)
    @SerialName("next_of_kin_name")
    val nextOfKinName: String? = null,
    @SerialName("next_of_kin_relationship")
    val nextOfKinRelationship: String? = null,
    @SerialName("next_of_kin_phone")
    val nextOfKinPhone: String? = null,
    // Employee Declaration
    @SerialName("declaration_signed")
    val declarationSigned: Boolean = false,
    @SerialName("declaration_signed_at")
    val declarationSignedAt: String? = null,
    // Bank Details
    @SerialName("bank_acc_name")
    val bankAccName: String? = null,
    @SerialName("bank_acc_number")
    val bankAccNumber: String? = null,
    @SerialName("bank_routing_number")
    val bankRoutingNumber: String? = null,
    @SerialName("bank_address")
    val bankAddress: String? = null,
    // Nested collections
    val addresses: List<UserAddressDto> = emptyList(),
    @SerialName("emergency_contacts")
    val emergencyContacts: List<UserEmergencyContactDto> = emptyList(),
    @SerialName("training_courses")
    val trainingCourses: List<UserTrainingCourseDto> = emptyList(),
    @SerialName("resignation_requests")
    val resignationRequests: List<UserResignationRequestDto> = emptyList()
)

@Serializable
data class EmployeeListResponse(
    val employees: List<EmployeeDto> = emptyList()
)

@Serializable
data class EmployeeDto(
    val id: Int?,
    @SerialName("first_name")
    val firstName: String?,
    @SerialName("last_name")
    val lastName: String?,
    @SerialName("display_name")
    val displayName: String?,
    @SerialName("image_url")
    val imageUrl: String? = "",
    val email: String? = "",
    val phone: String? = "",
    val designation: String? = "",
    @SerialName("floor_id")
    val floorId: Int? = 0,
    @SerialName("floor_name")
    val floorName: String? = "",
    @SerialName("department_id")
    val departmentId: Int? = 0,
    @SerialName("department")
    val departmentName: String? = "",

)

@Serializable
data class EmployeeRotaDto(
    @SerialName("employee_id")
    val employeeId: Int,
    // Server sends $user?->first_name → null if first_name is null or the user
    // record is missing. One non-null mismatch kills the whole response, so
    // these stay nullable and the mapper skips rows without an employee.
    @SerialName("employee_name")
    val employeeName: String? = null,
    val employee: EmployeeDto? = null,
    val rotas: List<RotaDto> = emptyList()
)

@Serializable
data class ShiftRequest(
    @SerialName("employee_id")
    val employeeId: String,
    val latitude: String,
    val longitude: String
)

@Serializable
data class UserAddressDto(
    val id: Int,
    @SerialName("address_type")
    val addressType: String,
    @SerialName("address_line1")
    val addressLine1: String,
    @SerialName("address_line2")
    val addressLine2: String? = null,
    val city: String,
    val county: String? = null,
    val postcode: String,
    val country: String = "UK",
    @SerialName("is_primary")
    val isPrimary: Boolean = false
)

@Serializable
data class UserEmergencyContactDto(
    val id: Int,
    val name: String,
    val relationship: String,
    val phone: String,
    val email: String? = null,
    val address: String? = null,
    @SerialName("is_primary")
    val isPrimary: Boolean = false
)

@Serializable
data class UserTrainingCourseDto(
    val id: Int,
    @SerialName("course_name")
    val courseName: String,
    val provider: String? = null,
    @SerialName("completed_date")
    val completedDate: String? = null,
    @SerialName("expiry_date")
    val expiryDate: String? = null,
    @SerialName("certificate_number")
    val certificateNumber: String? = null,
    val status: String = "completed"
)

@Serializable
data class UserResignationRequestDto(
    val id: Int,
    @SerialName("last_working_day")
    val lastWorkingDay: String,
    val reason: String? = null,
    val status: String = "pending",
    @SerialName("submitted_at")
    val submittedAt: String
)