package org.worklog.app.data.mapper

import org.worklog.app.data.model.LeaveHistoryDto
import org.worklog.app.data.model.LeaveResponse
import org.worklog.app.data.model.LeaveTypeDto
import org.worklog.app.domain.model.LeaveHistory
import org.worklog.app.domain.model.LeaveSummary
import org.worklog.app.domain.model.LeaveType

fun LeaveResponse.toDomain(): LeaveSummary {
    return LeaveSummary(
        allowanceYear = summary.allowanceYear,
        remainingLeave = summary.daysRemaining,
        accruedHolidays = summary.accruedHolidays,
        totalAllowance = summary.totalAllowance,
        daysTaken = summary.daysTaken,
        pendingLeave = summary.pendingLeave,
        approvedLeave = summary.approvedLeave,
        history = history.map { it.toDomain() }
    )
}

fun LeaveHistoryDto.toDomain(): LeaveHistory {
    return LeaveHistory(
        id = id,
        leaveType = leaveType.toDomain(),
        requestedFromDate = requestedFromDate,
        requestedToDate = requestedToDate,
        requestedTotalDay = requestedTotalDay,
        status = status,
        comments = comments,
        createdAt = createdAt
    )
}

fun LeaveTypeDto.toDomain(): LeaveType {
    return LeaveType(
        id = id,
        name = name,
        description = description
    )
}