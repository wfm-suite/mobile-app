package org.worklog.app.presentation.screen.leave.screen

import org.worklog.app.domain.model.LeaveSummary

data class LeaveUiState(
    val isLoading: Boolean = false,
    val leaveSummary: LeaveSummary? = null,
    val error: String? = null
)
