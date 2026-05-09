package org.worklog.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeaveResponse(
    val summary: LeaveSummaryDto,
    val history: List<LeaveHistoryDto> = emptyList()
)

@Serializable
data class LeaveSummaryDto(
    @SerialName("allowance_year")
    val allowanceYear: String,
    @SerialName("accrued_holidays")
    val accruedHolidays: Float,
    @SerialName("total_allowance")
    val totalAllowance: Float,
    @SerialName("days_taken")
    val daysTaken: Float,
    @SerialName("days_remaining")
    val daysRemaining: Float,
    @SerialName("days_pending")
    val pendingLeave: Float
)

@Serializable
data class LeaveHistoryDto(
    val id: Int,
    @SerialName("leave_type_id")
    val leaveTypeId: String,
    @SerialName("requested_from_date")
    val requestedFromDate: String,
    @SerialName("requested_to_date")
    val requestedToDate: String,
    @SerialName("requested_total_day")
    val requestedTotalDay: String,
    @SerialName("approved_by_id")
    val approvedById: String?,
    @SerialName("approved_total_day")
    val approvedTotalDay: String?,
    @SerialName("created_at")
    val createdAt: String,
    val status: String,
    val comments: String?,
    @SerialName("leave_type")
    val leaveType: LeaveTypeDto
)

@Serializable
data class LeaveTypeDto(
    val id: Int,
    val name: String,
    val description: String?,
    @SerialName("is_active")
    val isActive: String,
    val others: String? = null,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)