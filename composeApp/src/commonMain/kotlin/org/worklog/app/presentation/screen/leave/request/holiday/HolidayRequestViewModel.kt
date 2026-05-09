package org.worklog.app.presentation.screen.leave.request.holiday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.usecase.leave.GetLeaveDetailsUseCase
import org.worklog.app.domain.usecase.leave.SubmitHolidayRequestUseCase
import org.worklog.app.domain.usecase.rota.GetAuthUserRotaUseCase

class HolidayRequestViewModel(
    private val getAuthUserRotaUseCase: GetAuthUserRotaUseCase,
    private val requestHolidayUseCase: SubmitHolidayRequestUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HolidayRequestUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadEmployeeRota()
    }

    fun setAccruedHoliday(accruedHoliday: Int) {
        _uiState.update { it.copy(accruedHoliday = accruedHoliday) }
    }

    fun loadEmployeeRota() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = getAuthUserRotaUseCase.getMonthlyRota()
            if (result is ResultWrapper.Success) {
                _uiState.update { it.copy(rotas = result.data, isLoading = false) }
            } else if (result is ResultWrapper.Error) {
                _uiState.update { it.copy(errorMessage = result.message, isLoading = false) }
            }
        }
    }

    fun onDateToggle(date: String) {
        _uiState.update { state ->
            val isSelected = date in state.selectedDates
            //val hasRotaForDate = state.rotas.any { it.fullDate == date }

            val updatedDates = when {
                isSelected -> state.selectedDates - date
                state.selectedDates.size < uiState.value.accruedHoliday -> state.selectedDates + date
                else -> state.selectedDates
            }

            state.copy(selectedDates = updatedDates)
        }
    }

    fun onCommentChange(comment: String) {
        _uiState.update {
            it.copy(
                comment = comment
            )
        }
    }

    fun submitHolidayRequest() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = requestHolidayUseCase(
                reason = uiState.value.comment,
                dates = uiState.value.selectedDates.toList()
            )
            when (result) {
                is ResultWrapper.Success -> {
                    _uiState.update {
                        it.copy(
                            successMessage = result.data,
                            isLoading = false
                        )
                    }
                }

                is ResultWrapper.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message, isLoading = false) }
                }

                is ResultWrapper.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }

            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}