package org.worklog.app.data.mapper

import org.worklog.app.data.model.EmployeeDto
import org.worklog.app.data.model.UpdateProfileRequest
import org.worklog.app.data.model.UserAddressDto
import org.worklog.app.data.model.UserDto
import org.worklog.app.data.model.UserEmergencyContactDto
import org.worklog.app.data.model.UserResignationRequestDto
import org.worklog.app.data.model.UserTrainingCourseDto
import org.worklog.app.domain.model.EmployeeInfo
import org.worklog.app.domain.model.UserAddress
import org.worklog.app.domain.model.UserEmergencyContact
import org.worklog.app.domain.model.UserInfo
import org.worklog.app.domain.model.UserResignationRequest
import org.worklog.app.domain.model.UserTrainingCourse

fun UserDto.toDomainModel(): UserInfo {
    return UserInfo(
        id = id.toString(),
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        displayName = displayName ?: "",
        profilePicture = profilePicture ?: "",
        email = email ?: "",
        companyName = companyName ?: "",
        companyAddress = companyAddress ?: "",
        companyLatitude = companyLatitude,
        companyLongitude = companyLongitude,
        branchName = branchName ?: "",
        branchAddress = branchAddress ?: "",
        floor = floor ?: "",
        department = department ?: "",
        designation = designation ?: "",
        phoneNumber = phone ?: "",
        gender = gender ?: "",
        dateOfBirth = dateOfBirth ?: "",
        maritalStatus = maritalStatus ?: "",
        // RTW / Documents
        niNumber = niNumber,
        passportNumber = passportNumber,
        passportExpiry = passportExpiry,
        visaNumber = visaNumber,
        visaExpiry = visaExpiry,
        rtwStatus = rtwStatus,
        rtwExpiry = rtwExpiry,
        // Equality / Diversity / Inclusion
        ethnicity = ethnicity,
        nationality = nationality,
        disability = disability,
        religion = religion,
        sexualOrientation = sexualOrientation,
        // Job Status
        employeeStatus = employeeStatus,
        employmentType = employmentType,
        contractStartDate = contractStartDate,
        contractEndDate = contractEndDate,
        // Vetting
        dbsCheckDate = dbsCheckDate,
        dbsCertificateNumber = dbsCertificateNumber,
        reference1Name = reference1Name,
        reference1Contact = reference1Contact,
        reference2Name = reference2Name,
        reference2Contact = reference2Contact,
        // Next of Kin
        nextOfKinName = nextOfKinName,
        nextOfKinRelationship = nextOfKinRelationship,
        nextOfKinPhone = nextOfKinPhone,
        // Declaration
        declarationSigned = declarationSigned,
        declarationSignedAt = declarationSignedAt,
        // Bank Details
        bankAccName = bankAccName,
        bankAccNumber = bankAccNumber,
        bankRoutingNumber = bankRoutingNumber,
        bankAddress = bankAddress,
        // Nested collections
        addresses = addresses.map { it.toDomainModel() },
        emergencyContacts = emergencyContacts.map { it.toDomainModel() },
        trainingCourses = trainingCourses.map { it.toDomainModel() },
        resignationRequests = resignationRequests.map { it.toDomainModel() }
    )
}

fun UserAddressDto.toDomainModel() = UserAddress(
    id = id,
    addressType = addressType,
    addressLine1 = addressLine1,
    addressLine2 = addressLine2,
    city = city,
    county = county,
    postcode = postcode,
    country = country,
    isPrimary = isPrimary
)

fun UserEmergencyContactDto.toDomainModel() = UserEmergencyContact(
    id = id,
    name = name,
    relationship = relationship,
    phone = phone,
    email = email,
    address = address,
    isPrimary = isPrimary
)

fun UserTrainingCourseDto.toDomainModel() = UserTrainingCourse(
    id = id,
    courseName = courseName,
    provider = provider,
    completedDate = completedDate,
    expiryDate = expiryDate,
    certificateNumber = certificateNumber,
    status = status
)

fun UserResignationRequestDto.toDomainModel() = UserResignationRequest(
    id = id,
    lastWorkingDay = lastWorkingDay,
    reason = reason,
    status = status,
    submittedAt = submittedAt
)

fun EmployeeDto.toDomain() = EmployeeInfo(
    id = id ?: 0,
    firstName = firstName ?: "",
    lastName = lastName ?: "",
    displayName = displayName ?: "",
    profilePicture = imageUrl ?: "",
    designation = designation ?: "",
    email = email ?: "",
    phone = phone ?: "",
    floorId = floorId?.toString() ?: "",
    floorName = floorName ?: "",
    departmentId = departmentId?.toString() ?: "",
    departmentName = departmentName ?: "",
)

fun UserInfo.toUpdateProfileRequest() = UpdateProfileRequest(
    firstName = firstName,
    lastName = lastName,
    displayName = displayName,
    email = email ?: "",
    phone = phoneNumber,
    gender = gender,
    dateOfBirth = dateOfBirth,
    maritalStatus = maritalStatus,
    // Contact Details (next of kin)
    nextOfKinName = nextOfKinName,
    nextOfKinRelationship = nextOfKinRelationship,
    nextOfKinPhone = nextOfKinPhone,
    // Bank Details
    bankAccName = bankAccName,
    bankAccNumber = bankAccNumber,
    bankRoutingNumber = bankRoutingNumber,
    bankAddress = bankAddress,
    // Employee Declaration
    declarationSigned = declarationSigned,
    declarationSignedAt = declarationSignedAt,
    // RTW / Documents
    niNumber = niNumber,
    passportNumber = passportNumber,
    passportExpiry = passportExpiry,
    visaNumber = visaNumber,
    visaExpiry = visaExpiry,
    rtwStatus = rtwStatus,
    rtwExpiry = rtwExpiry,
    // Equality / Diversity / Inclusion
    ethnicity = ethnicity,
    nationality = nationality,
    disability = disability,
    religion = religion,
    sexualOrientation = sexualOrientation,
    // Vetting
    dbsCheckDate = dbsCheckDate,
    dbsCertificateNumber = dbsCertificateNumber,
    reference1Name = reference1Name,
    reference1Contact = reference1Contact,
    reference2Name = reference2Name,
    reference2Contact = reference2Contact
)