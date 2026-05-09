package org.worklog.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RotaResponse(
    @SerialName("employee_id")
    val employeeId: Int,
    val rotas: List<RotaDto>
)

@Serializable
data class UpcomingRotaResponse(
    @SerialName("has_upcoming_rotas")
    val hasUpcomingRotas: Boolean,
    @SerialName("upcoming_rotas")
    val upcomingRotas: List<RotaDto>? = null
)

@Serializable
data class CurrentRotaResponse(
    @SerialName("has_current_rota")
    val hasCurrentRota: Boolean,
    @SerialName("current_rota")
    val currentRota: RotaDto? = null
)

@Serializable
data class MonthlyRotaResponse(
    val month: RotaDurationDto,
    val rotas: List<EmployeeRotaDto>
)

@Serializable
data class WeeklyRotaResponse(
    val week: RotaDurationDto,
    val rotas: List<EmployeeRotaDto>
)

@Serializable
data class UpcomingEmployeeRotaResponse(
    @SerialName("has_upcoming_rotas")
    val hasUpcomingRotas: Boolean,
    @SerialName("upcoming_rotas")
    val upcomingRotas: List<EmployeeRotaDto>? = null
)

@Serializable
data class RotaDto(
    val id: Int,
    val date: String,
    @SerialName("shift_start_time")
    val shiftStartTime: String,
    @SerialName("shift_end_time")
    val shiftEndTime: String,
    @SerialName("break_start_time")
    val breakStartTime: String? = null,
    @SerialName("break_end_time")
    val breakEndTime: String? = null,
    @SerialName("total_hours")
    val totalHours: String?,
    val location: String?,
    val remarks: String?,
    @SerialName("swap_status")
    val swapStatus: String? = null,
    @SerialName("shift_type")
    val shiftType: String? = null,
    @SerialName("shift_status")
    val shiftStatus: String? = null,
    val designation: String? = null,
    val department: String? = null,
    @SerialName("start_time_enabled")
    val startTimeEnabled: Boolean = false,
    @SerialName("floor_name")
    val floorName: String? = null
)

@Serializable
data class RotaDurationDto(
    @SerialName("start_date")
    val startDate: String,
    @SerialName("end_date")
    val endDate: String,
)

@Serializable
data class SwapRequest(
    @SerialName("my_rota_id")
    val myRotaId: Int,
    @SerialName("requested_rota_id")
    val requestedRotaId: Int
)


@Serializable
data class RotaHanoverRequest(
    @SerialName("rota_id")
    val rotaId: Int
)
