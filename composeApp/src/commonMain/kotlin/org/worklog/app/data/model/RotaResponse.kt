package org.worklog.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RotaResponse(
    @SerialName("employee_id")
    val employeeId: Int? = null,
    val rotas: List<RotaDto> = emptyList(),
    val month: RotaDurationDto? = null
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
    val shiftStartTime: String? = null,
    @SerialName("shift_end_time")
    val shiftEndTime: String? = null,
    @SerialName("break_start_time")
    val breakStartTime: String? = null,
    @SerialName("break_end_time")
    val breakEndTime: String? = null,
    @SerialName("total_hours")
    val totalHours: String?,
    val location: String?,
    val remarks: String?,
    @SerialName("request_status")
    val requestStatus: String? = null,
    @SerialName("request_type")
    val requestType: String? = null,
    @SerialName("request_id")
    val requestId: Int? = null,
    @SerialName("request_recipient")
    val requestRecipient: RequestRecipientDto? = null,
    @SerialName("shift_type")
    val shiftType: String? = null,
    @SerialName("shift_status")
    val shiftStatus: String? = null,
    val designation: String? = null,
    val department: String? = null,
    @SerialName("start_time_enabled")
    val startTimeEnabled: Boolean = false,
    @SerialName("floor_name")
    val floorName: String? = null,
    @SerialName("shift_label")
    val shiftLabel: String? = null,
    @SerialName("short_code")
    val shortCode: String? = null,
    @SerialName("is_leave")
    val isLeave: Boolean = false
)

@Serializable
data class RequestRecipientDto(
    val id: Int? = null,
    val name: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null
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

@Serializable
data class OpenRotaResponse(
    @Serializable
    val rotas: List<RotaDto>
)

@Serializable
data class IncomingSwapsResponse(
    val swaps: List<IncomingSwapDto> = emptyList()
)

@Serializable
data class IncomingSwapDto(
    val id: Int,
    @SerialName("requested_at")
    val requestedAt: String? = null,
    val requester: SwapRequesterDto? = null,
    @SerialName("my_rota")
    val myRota: SwapRotaSummaryDto? = null,
    @SerialName("offered_rota")
    val offeredRota: SwapRotaSummaryDto? = null
)

@Serializable
data class SwapRequesterDto(
    val id: Int? = null,
    val name: String? = null
)

@Serializable
data class SwapRotaSummaryDto(
    val id: Int? = null,
    val date: String? = null,
    @SerialName("shift_start")
    val shiftStart: String? = null,
    @SerialName("shift_end")
    val shiftEnd: String? = null,
    @SerialName("shift_type")
    val shiftType: String? = null,
    val branch: String? = null
)

@Serializable
data class SwapRespondRequest(
    val action: String
)
