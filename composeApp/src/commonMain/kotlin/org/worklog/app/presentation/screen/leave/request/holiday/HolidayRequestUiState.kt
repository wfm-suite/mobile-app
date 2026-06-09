package org.worklog.app.presentation.screen.leave.request.holiday

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.worklog.app.domain.model.Rota
import kotlin.time.Clock

data class HolidayRequestUiState(
    val selectedMonth: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val rotas: List<Rota> = emptyList(),
    val selectedDates: Set<String> = emptySet(),
    // Dates already covered by a pending or approved leave (yyyy-MM-dd).
    val blockedDates: Set<String> = emptySet(),
    val comment: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val accruedHoliday: Int = 0
)