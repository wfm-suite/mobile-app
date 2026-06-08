package org.worklog.app.presentation.screen.shift

import org.worklog.app.domain.model.EmployeeRota
import org.worklog.app.domain.model.IncomingSwap
import org.worklog.app.domain.model.Rota
import org.worklog.app.domain.model.UserInfo

data class ShiftUIState(
    val displayRotas: List<Rota> = emptyList(),
    val baseRotas: List<Rota> = emptyList(),
    val employeeRota: EmployeeRota? = null,
    val userInfo: UserInfo? = null,
    val selectedRota: Rota? = null,
    val selectedDate: String? = null,
    val isCalendarExpanded: Boolean = false,
    val isLoading: Boolean = false,
    val message: String? = null,
    val isSwapRequesting: Boolean = false,
    val isRequestSent: Boolean = false,
    // Incoming swap — someone sent a swap request targeting this rota
    val incomingSwap: IncomingSwap? = null,
    val isLoadingIncomingSwap: Boolean = false,
    val isRespondingSwap: Boolean = false,
)
