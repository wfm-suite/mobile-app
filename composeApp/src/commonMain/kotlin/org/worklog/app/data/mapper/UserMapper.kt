package org.worklog.app.data.mapper

import org.worklog.app.data.model.EmployeeDto
import org.worklog.app.data.model.UpdateProfileRequest
import org.worklog.app.data.model.UserDto
import org.worklog.app.domain.model.EmployeeInfo
import org.worklog.app.domain.model.UserInfo

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
        branchName = branchName ?: "",
        branchAddress = branchAddress ?: "",
        floor = floor ?: "",
        department = department ?: "",
        designation = designation ?: "",
        phoneNumber = phone ?: "",
        gender = gender ?: "",
        dateOfBirth = dateOfBirth ?: "",
        maritalStatus = maritalStatus ?: ""
    )
}

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
    maritalStatus = maritalStatus
)