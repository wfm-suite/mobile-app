package org.worklog.app.presentation.screen.rota.time_card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.todayIn
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.model.MonthYear
import org.worklog.app.domain.model.TimeCard
import org.worklog.app.domain.usecase.GetMonthlyTimeCardUseCase
import kotlin.time.Clock

data class TimeCardUiState(
    val isLoading: Boolean = false,
    val timeCard: TimeCard? = null,
    val availableMonths: List<MonthYear> = emptyList(),
    val selectedMonthYear: MonthYear? = null,
    val message: String? = null
)

class TimeCardViewModel(
    private val getMonthlyTimeCardUseCase: GetMonthlyTimeCardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimeCardUiState())
    val uiState: StateFlow<TimeCardUiState> = _uiState.asStateFlow()

    init {
        initializeMonths()
        loadTimeCard()
    }

    private fun initializeMonths() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val currentMonth = today.month.number
        val currentYear = today.year

        val months = mutableListOf<MonthYear>()
        
        // Generate last 12 months
        for (i in 0 until 12) {
            var month = currentMonth - i
            var year = currentYear
            
            if (month <= 0) {
                month += 12
                year -= 1
            }
            
            val monthName = getMonthName(month)
            months.add(MonthYear(month, year, "$monthName $year"))
        }

        _uiState.update {
            it.copy(
                availableMonths = months,
                selectedMonthYear = months.first()
            )
        }
    }

    private fun getMonthName(month: Int): String {
        return when (month) {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"
            else -> ""
        }
    }

    fun loadTimeCard() {
        val selectedMonthYear = _uiState.value.selectedMonthYear ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = getMonthlyTimeCardUseCase(
                month = selectedMonthYear.month,
                year = selectedMonthYear.year
            )) {
                is ResultWrapper.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            timeCard = result.data
                        )
                    }
                }
                is ResultWrapper.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            message = result.message
                        )
                    }
                }

                else -> {}
            }
        }
    }

    fun onMonthYearSelected(monthYear: MonthYear) {
        _uiState.update { it.copy(selectedMonthYear = monthYear) }
        loadTimeCard()
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
