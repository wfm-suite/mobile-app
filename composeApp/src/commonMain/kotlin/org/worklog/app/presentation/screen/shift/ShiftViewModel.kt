package org.worklog.app.presentation.screen.shift

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.worklog.app.core.notification.RefreshEvents
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
    private val rotaSwapHandoverUseCase: RotaSwapHandoverUseCase,
    private val refreshEvents: RefreshEvents
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShiftUIState())
    val uiState: StateFlow<ShiftUIState> = _uiState.asStateFlow()

    init {
        observeUserProfile()
        loadEmployeeRotas()
        setInitialDate()
        observeRefreshEvents()
    }

    private fun observeRefreshEvents() {
        viewModelScope.launch {
            refreshEvents.events
                .filter { it == RefreshEvents.Topics.ROTAS || it == RefreshEvents.Topics.SWAPS }
                .collect {
                    loadEmployeeRotas(forceRefresh = true)
                    loadIncomingSwaps(forceRefresh = true)
                }
        }
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
        loadIncomingSwaps()
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

    private fun loadEmployeeRotas(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }
            val result = rotaUseCase.getUpcomingRotas(forceRefresh)
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
                    is ResultWrapper.Error -> state.copy(message = result.message, isLoading = false)
                    else -> state.copy(isLoading = false)
                }
            }
        }
    }

    // Load incoming swap requests targeted at this employee's rota
    private fun loadIncomingSwaps(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingIncomingSwap = true) }
            val result = rotaSwapHandoverUseCase.getIncomingSwaps(forceRefresh)
            _uiState.update { state ->
                when (result) {
                    is ResultWrapper.Success -> {
                        // Find swap targeting the currently viewed rota
                        val currentRotaId = state.employeeRota?.rota?.id
                        val matching = result.data.firstOrNull {
                            it.myRota.rotaId == currentRotaId
                        }
                        state.copy(incomingSwap = matching, isLoadingIncomingSwap = false)
                    }
                    else -> state.copy(isLoadingIncomingSwap = false)
                }
            }
        }
    }

    fun onDateSelected(date: String) {
        _uiState.update { state ->
            val filteredRotas = state.baseRotas.filter { it.fullDate == date }
            state.copy(selectedDate = date, displayRotas = filteredRotas)
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
                    refreshEvents.emit(RefreshEvents.Topics.ROTAS)
                    refreshEvents.emit(RefreshEvents.Topics.SWAPS)
                    _uiState.update {
                        it.copy(isSwapRequesting = false, isRequestSent = true, message = "Swap request sent")
                    }
                }
                is ResultWrapper.Error -> _uiState.update {
                    it.copy(isSwapRequesting = false, message = response.message)
                }
                else -> {}
            }
        }
    }

    fun onAcceptSwap() {
        val swapId = _uiState.value.incomingSwap?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isRespondingSwap = true, message = null) }
            val result = rotaSwapHandoverUseCase.acceptSwap(swapId)
            _uiState.update { state ->
                when (result) {
                    is ResultWrapper.Success -> {
                        refreshEvents.emit(RefreshEvents.Topics.ROTAS)
                        refreshEvents.emit(RefreshEvents.Topics.SWAPS)
                        state.copy(isRespondingSwap = false, isRequestSent = true, message = "Swap accepted!")
                    }
                    is ResultWrapper.Error -> state.copy(isRespondingSwap = false, message = result.message)
                    else -> state.copy(isRespondingSwap = false)
                }
            }
        }
    }

    fun onDenySwap() {
        val swapId = _uiState.value.incomingSwap?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isRespondingSwap = true, message = null) }
            val result = rotaSwapHandoverUseCase.denySwap(swapId)
            _uiState.update { state ->
                when (result) {
                    is ResultWrapper.Success -> {
                        refreshEvents.emit(RefreshEvents.Topics.ROTAS)
                        refreshEvents.emit(RefreshEvents.Topics.SWAPS)
                        state.copy(isRespondingSwap = false, isRequestSent = true, message = "Swap request denied")
                    }
                    is ResultWrapper.Error -> state.copy(isRespondingSwap = false, message = result.message)
                    else -> state.copy(isRespondingSwap = false)
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
