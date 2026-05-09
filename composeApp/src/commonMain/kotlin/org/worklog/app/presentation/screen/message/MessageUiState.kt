package org.worklog.app.presentation.screen.message

import org.worklog.app.domain.model.EmployeeInfo

data class MessageUiState(
    val employees: List<EmployeeInfo> = emptyList(),
    val filteredEmployees: List<EmployeeInfo> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val message: String = ""
)