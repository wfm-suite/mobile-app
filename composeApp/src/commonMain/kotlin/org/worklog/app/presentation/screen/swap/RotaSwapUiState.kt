package org.worklog.app.presentation.screen.swap

import org.worklog.app.domain.model.EmployeeRota
import org.worklog.app.domain.model.Rota
import org.worklog.app.domain.model.UserInfo

data class RotaSwapUiState(
    val userInfo: UserInfo? = null,
    val displayRotas: List<EmployeeRota> = emptyList(),
    val baseRotas: List<EmployeeRota> = emptyList(),
    val selectedRota: EmployeeRota? = null,
    val rotaAction: RotaSwapAction? = null,
    val isCalendarExpanded: Boolean = false,
    val userRota: Rota? = null,
    val isLoading: Boolean = true,
    val isSwapRequesting: Boolean = false,
    val isCancelling: Boolean = false,
    val isRequestSent: Boolean = false,
    val message: String? = null
)

enum class RotaSwapAction {
    SWAP,
    HANDOVER
}