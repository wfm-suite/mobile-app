package org.worklog.app.presentation.screen.home

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
    val selectedMonth: Int = 1,
    val selectedYear: Int = 2024,
    val hasCurrentRota: Boolean = false,
    val monthlyRotas: List<Rota> = emptyList(),
    val rotaStartDate: String = "",
    val rotaEndDate: String = ""
)