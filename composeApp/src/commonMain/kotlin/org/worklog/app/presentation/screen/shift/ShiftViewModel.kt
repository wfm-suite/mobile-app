package org.worklog.app.presentation.screen.shift

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.model.EmployeeRota
import org.worklog.app.domain.model.Rota
import org.worklog.app.domain.usecase.rota.RotaSwapHandoverUseCase
import org.worklog.app.domain.usecase.user.GetRotaUseCase
import org.worklog.app.domain.usecase.user.UserProfileUseCase
import kotlin.time.Clock

class ShiftViewModel(
    private val rotaUseCase: GetRotaUseCase,
    private val userProfileUseCase: UserProfileUseCase,
    private val rotaSwapHandoverUseCase: RotaSwapHandoverUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShiftUIState())
    val uiState: StateFlow<ShiftUIState> = _uiState.asStateFlow()

    init {
        observeUserProfile()
        loadEmployeeRotas()
        setInitialDate()
    }

    private fun setInitialDate() {
        val currentDate = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
            .toString()
        onDateSelected(currentDate)
    }

    fun setEmployeeRota(employeeRota: EmployeeRota) {
        _uiState.update { it.copy(employeeRota = employeeRota) }
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            userProfileUseCase.getUserProfile.collect { result ->
                if (result is ResultWrapper.Success) {
                    println("\n\n User Profile: ${result.data}\n\n")
                    _uiState.update { it.copy(userInfo = result.data) }
                } else if (result is ResultWrapper.Error) {
                    _uiState.update { it.copy(message = result.message) }
                }
            }
        }
    }

    private fun loadEmployeeRotas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }

            val result = rotaUseCase.getUpcomingRotas()

            _uiState.update { state ->
                when (result) {
                    is ResultWrapper.Success -> {
                        val filteredRotas = state.selectedDate?.let { date ->
                            result.data.filter { it.fullDate == date }
                        } ?: result.data

                        state.copy(
                            baseRotas = result.data,
                            displayRotas = filteredRotas,
                            isLoading = false
                        )
                    }

                    is ResultWrapper.Error -> {
                        state.copy(
                            message = result.message,
                            isLoading = false
                        )
                    }

                    else -> state.copy(isLoading = false)
                }
            }
        }
    }

    fun onDateSelected(date: String) {
        _uiState.update { state ->
            val filteredRotas = state.baseRotas.filter { it.fullDate == date }
            state.copy(
                selectedDate = date,
                displayRotas = filteredRotas
            )
        }
    }

    fun onRotaSelected(rota: Rota) {
        _uiState.update { it.copy(selectedRota = rota) }
    }

    fun onCalendarToggle() {
        _uiState.update { it.copy(isCalendarExpanded = !it.isCalendarExpanded) }
    }

    fun onRequestSwap() {
        _uiState.update { it.copy(isSwapRequesting = true, message = null) }
        viewModelScope.launch {
            val state = _uiState.value
            val response = rotaSwapHandoverUseCase.rotaSwapRequest(
                myRotaId = state.selectedRota?.id ?: 0,
                requestedRotaId = state.employeeRota?.rota?.id ?: 0
            )
            when (response) {
                is ResultWrapper.Success -> {
                    _uiState.update {
                        it.copy(
                            isSwapRequesting = false,
                            isRequestSent = true,
                            message = "Rota swap request sent"
                        )
                    }
                }

                is ResultWrapper.Error -> {
                    _uiState.update {
                        it.copy(
                            isSwapRequesting = false,
                            message = response.message
                        )
                    }
                }

                else -> {}
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}