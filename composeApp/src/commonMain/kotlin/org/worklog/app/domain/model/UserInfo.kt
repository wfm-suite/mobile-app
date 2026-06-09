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
    // Office geo-coords. Set by admin via dashboard. Null when unset.
    val companyLatitude: Double? = null,
    val companyLongitude: Double? = null,
    val branchName: String,
    val branchAddress: String,
    val floor: String,
    val department: String,
    val designation: String,
    // RTW / Documents
    val niNumber: String? = null,
    val passportNumber: String? = null,
    val passportExpiry: String? = null,
    val visaNumber: String? = null,
    val visaExpiry: String? = null,
    val rtwStatus: String? = null,
    val rtwExpiry: String? = null,
    // Equality / Diversity / Inclusion
    val ethnicity: String? = null,
    val nationality: String? = null,
    val disability: String? = null,
    val religion: String? = null,
    val sexualOrientation: String? = null,
    // Job Status
    val employeeStatus: String? = null,
    val employmentType: String? = null,
    val contractStartDate: String? = null,
    val contractEndDate: String? = null,
    // Vetting
    val dbsCheckDate: String? = null,
    val dbsCertificateNumber: String? = null,
    val reference1Name: String? = null,
    val reference1Contact: String? = null,
    val reference2Name: String? = null,
    val reference2Contact: String? = null,
    // Contact Details (next of kin)
    val nextOfKinName: String? = null,
    val nextOfKinRelationship: String? = null,
    val nextOfKinPhone: String? = null,
    // Employee Declaration
    val declarationSigned: Boolean = false,
    val declarationSignedAt: String? = null,
    // Bank Details
    val bankAccName: String? = null,
    val bankAccNumber: String? = null,
    val bankRoutingNumber: String? = null,
    val bankAddress: String? = null,
    // Nested collections
    val addresses: List<UserAddress> = emptyList(),
    val emergencyContacts: List<UserEmergencyContact> = emptyList(),
    val trainingCourses: List<UserTrainingCourse> = emptyList(),
    val resignationRequests: List<UserResignationRequest> = emptyList()
)