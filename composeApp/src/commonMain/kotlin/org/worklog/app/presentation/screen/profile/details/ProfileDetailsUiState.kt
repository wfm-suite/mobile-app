package org.worklog.app.presentation.screen.profile.details

data class ProfileDetailsUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,

    // Personal Details
    val firstName: String = "",
    val lastName: String = "",
    val displayName: String = "",
    val email: String = "",
    val phone: String = "",
    val gender: String = "",
    val dateOfBirth: String = "",
    val maritalStatus: String = "",
    val profilePicture: String? = null,

    // Assignment Summary (read-only from server)
    val companyName: String = "",
    val companyAddress: String = "",
    val branchName: String = "",
    val branchAddress: String = "",
    val floor: String = "",
    val department: String = "",
    val designation: String = "",

    // Contact Details
    val nextOfKinName: String = "",
    val nextOfKinRelationship: String = "",
    val nextOfKinPhone: String = "",

    // Addresses
    val addresses: List<UserAddressUiState> = emptyList(),

    // Emergency Contacts
    val emergencyContacts: List<EmergencyContactUiState> = emptyList(),

    // Bank Details
    val bankAccName: String = "",
    val bankAccNumber: String = "",
    val bankRoutingNumber: String = "",
    val bankAddress: String = "",

    // Employee Declaration
    val declarationSigned: Boolean = false,
    val declarationSignedAt: String? = null,

    // Job Status (read-only)
    val employeeStatus: String = "",
    val employmentType: String = "",
    val contractStartDate: String = "",
    val contractEndDate: String = "",

    // RTW Details
    val niNumber: String = "",
    val passportNumber: String = "",
    val passportExpiry: String = "",
    val visaNumber: String = "",
    val visaExpiry: String = "",
    val rtwStatus: String = "",
    val rtwExpiry: String = "",

    // Equality / Diversity / Inclusion
    val ethnicity: String = "",
    val nationality: String = "",
    val disability: String = "",
    val religion: String = "",
    val sexualOrientation: String = "",

    // Training Courses
    val trainingCourses: List<TrainingCourseUiState> = emptyList(),

    // Vetting Details
    val dbsCheckDate: String = "",
    val dbsCertificateNumber: String = "",
    val reference1Name: String = "",
    val reference1Contact: String = "",
    val reference2Name: String = "",
    val reference2Contact: String = "",

    // Password Change
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",

    // Resignation
    val resignationLastWorkingDay: String = "",
    val resignationReason: String = "",
    val existingResignation: UserResignationUiState? = null,
)

data class UserAddressUiState(
    val id: Int = 0,
    val addressType: String = "home",
    val addressLine1: String = "",
    val addressLine2: String = "",
    val city: String = "",
    val county: String = "",
    val postcode: String = "",
    val country: String = "UK",
    val isPrimary: Boolean = false
)

data class EmergencyContactUiState(
    val id: Int = 0,
    val name: String = "",
    val relationship: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val isPrimary: Boolean = false
)

data class TrainingCourseUiState(
    val id: Int = 0,
    val courseName: String = "",
    val provider: String = "",
    val completedDate: String = "",
    val expiryDate: String = "",
    val certificateNumber: String = "",
    val status: String = "completed"
)

data class UserResignationUiState(
    val id: Int = 0,
    val lastWorkingDay: String = "",
    val reason: String = "",
    val status: String = "pending",
    val submittedAt: String = ""
)
