package org.worklog.app.presentation.screen.swap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.model.EmployeeRota
import org.worklog.app.domain.model.Rota
import org.worklog.app.domain.usecase.rota.EmployeeRotaUseCase
import org.worklog.app.domain.usecase.rota.RotaSwapHandoverUseCase
import org.worklog.app.domain.usecase.user.UserProfileUseCase

class RotaSwapViewModel(
    private val employeeRotaUseCase: EmployeeRotaUseCase,
    private val swapHandoverUseCase: RotaSwapHandoverUseCase,
    private val userProfileUseCase: UserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RotaSwapUiState())
    val uiState: StateFlow<RotaSwapUiState> = _uiState.asStateFlow()

    init {
        observeUserProfile()
        loadEmployeeRotas()
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            userProfileUseCase.getUserProfile.collect { result ->
                if (result is ResultWrapper.Success) {
                    _uiState.update { it.copy(userInfo = result.data) }
                } else if (result is ResultWrapper.Error) {
                    _uiState.update { it.copy(message = result.message) }
                }
            }
        }
    }

    private fun loadEmployeeRotas() {
        viewModelScope.launch {
            when (val result = employeeRotaUseCase.getUpcomingRotasExceptAuthUser()) {
                is ResultWrapper.Success -> {
                    _uiState.update {
                        it.copy(
                            displayRotas = result.data,
                            baseRotas = result.data,
                            isLoading = false
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

    fun onRotaActionClick(rotaAction: RotaSwapAction) {
        _uiState.update { it.copy(rotaAction = rotaAction) }
    }

    fun onCalendarToggle() {
        _uiState.update { it.copy(isCalendarExpanded = !it.isCalendarExpanded) }
    }

    fun setUserRota(userRota: Rota) {
        _uiState.update { it.copy(userRota = userRota) }
    }

    fun onRotaSelected(rota: EmployeeRota) {
        _uiState.update { it.copy(selectedRota = rota) }
    }

    fun onRequestSwap() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSwapRequesting = true, message = null) }

            val result = swapHandoverUseCase.rotaSwapRequest(
                myRotaId = uiState.value.userRota?.id ?: 0,
                requestedRotaId = uiState.value.selectedRota?.rota?.id ?: 0
            )

            _uiState.update { state ->
                when (result) {
                    is ResultWrapper.Success -> {
                        state.copy(
                            isSwapRequesting = false,
                            isRequestSent = true,
                            message = "Rota swap request sent"
                        )
                    }

                    is ResultWrapper.Error -> {
                        state.copy(
                            isSwapRequesting = false,
                            message = result.message
                        )
                    }

                    else -> state
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun onRequestHandover() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSwapRequesting = true, message = null) }

            val result = swapHandoverUseCase.rotaHanoverRequest(
                rotaId = uiState.value.userRota?.id ?: 0
            )

            _uiState.update { state ->
                when (result) {
                    is ResultWrapper.Success -> {
                        state.copy(
                            isSwapRequesting = false,
                            isRequestSent = true,
                            message = "Rota handover request sent"
                        )
                    }

                    is ResultWrapper.Error -> {
                        state.copy(
                            isSwapRequesting = false,
                            message = result.message
                        )
                    }

                    else -> state
                }
            }
        }
    }
}