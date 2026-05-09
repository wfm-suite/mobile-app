package org.worklog.app.presentation.screen.home

import org.worklog.app.domain.model.Rota
import org.worklog.app.domain.model.UserInfo

data class HomeUiState(
    val userInfo: UserInfo? = null,
    val rotas: List<Rota> = emptyList(),
    val currentRota: Rota? = null,
    val greetingText: String = "",
    val currentDate: String = "",
    val isShiftStarted: Boolean = false,
    val isShiftEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val isShiftToggling: Boolean = false,
    val message: String? = null
)