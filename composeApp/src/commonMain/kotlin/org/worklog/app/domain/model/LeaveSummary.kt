package org.worklog.app.domain.model

data class LeaveSummary(
    val allowanceYear: String,
    val remainingLeave: Float,
    val accruedHolidays: Float,
    val totalAllowance: Float,
    val pendingLeave: Float,
    val daysTaken: Float,
    val history: List<LeaveHistory> = emptyList()
)

data class LeaveHistory(
    val id: Int,
    val leaveType: LeaveType,
    val requestedFromDate: String,
    val requestedToDate: String,
    val requestedTotalDay: String,
    val status: String,
    val comments: String?
)

data class LeaveType(
    val id: Int,
    val name: String,
    val description: String?
)