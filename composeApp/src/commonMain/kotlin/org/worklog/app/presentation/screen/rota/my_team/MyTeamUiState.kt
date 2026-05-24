package org.worklog.app.presentation.screen.rota.my_team

import org.worklog.app.domain.model.EmployeeRota
import org.worklog.app.domain.model.UserInfo

data class MyTeamUiState(
    val weeklyRotas: List<EmployeeRota> = emptyList(),
    val monthlyRotas: List<EmployeeRota> = emptyList(),
    val displayRotas: List<EmployeeRota> = emptyList(),
    val userInfo: UserInfo? = null,
    val isCalendarExpanded: Boolean = false,
    val selectedDate: String? = null,
    val selectedMonth: Int? = null,
    val selectedYear: Int? = null,
    val isLoading: Boolean = false,
    val message: String? = null,
    val shiftTypes: List<String> = listOf("All", "Day", "Night"),
    val selectedShiftStatus: String? = "All",
    val floorNames: List<String> = listOf("All"),
    val selectedFloorName: String? = "All"
)
