package org.worklog.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("access_token")
    val accessToken: String? = null,
    val token: String? = null,
    val user: UserDto
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
    @SerialName("branch_name")
    val branchName: String? = "",
    @SerialName("branch_address")
    val branchAddress: String? = "",
    val floor: String? = "",
    val designation: String? = "",
    val department: String? = null,
    val roles: List<String>? = emptyList()
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
    @SerialName("employee_name")
    val employeeName: String,
    val employee: EmployeeDto,
    val rotas: List<RotaDto>
)

@Serializable
data class ShiftRequest(
    @SerialName("employee_id")
    val employeeId: String,
    val latitude: String,
    val longitude: String
)