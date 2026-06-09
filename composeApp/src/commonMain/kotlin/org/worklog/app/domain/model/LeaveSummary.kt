package org.worklog.app.domain.model

data class LeaveSummary(
    val allowanceYear: String,
    val remainingLeave: Float,
    val accruedHolidays: Float,
    val totalAllowance: Float,
    val pendingLeave: Float,         // = "Requested" — submitted, not yet approved
    val approvedLeave: Float = 0f,   // approved by manager but not yet taken
    val daysTaken: Float,            // already past dates that were taken
    val history: List<LeaveHistory> = emptyList()
)

data class LeaveHistory(
    val id: Int,
    val leaveType: LeaveType,
    val requestedFromDate: String,
    val requestedToDate: String,
    val requestedTotalDay: String,
    val status: String,
    val comments: String?,
    val createdAt: String
)

data class LeaveType(
    val id: Int,
    val name: String,
    val description: String?
)