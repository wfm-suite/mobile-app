package org.worklog.app.presentation.screen.home

import org.worklog.app.domain.model.IncomingSwap
import org.worklog.app.domain.model.Rota
import org.worklog.app.domain.model.UserInfo

data class HomeUiState(
    val userInfo: UserInfo? = null,
    val rotas: List<Rota> = emptyList(),
    val openRotas: List<Rota> = emptyList(),
    val currentRota: Rota? = null,
    val greetingText: String = "",
    val currentDate: String = "",
    val isShiftStarted: Boolean = false,
    val isShiftEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val isShiftToggling: Boolean = false,
    val message: String? = null,
    val latitude: String = "51.5079111",
    val longitude: String = "-0.0903026",
    // True when the user's current GPS position is within the office radius.
    // Drives the Gemini glow on the Start Shift / No Shift button.
    val isNearOffice: Boolean = false,
    val selectedMonth: Int = 1,
    val selectedYear: Int = 2024,
    val hasCurrentRota: Boolean = false,
    val monthlyRotas: List<Rota> = emptyList(),
    val rotaStartDate: String = "",
    val rotaEndDate: String = "",
    // Last date with a real published shift. Days after this are treated as
    // "rota not yet published" and rendered as "—" instead of "No Shift".
    val lastPublishedDate: String = "",
    val incomingSwaps: List<IncomingSwap> = emptyList(),
    val respondingSwapId: Int? = null,
    // Rota.id of the row currently being cancelled (Cancel Request button on
    // a pending outgoing swap/handover). Drives the per-row spinner.
    val cancellingRotaId: Int? = null
)