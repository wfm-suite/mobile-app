package org.worklog.app.presentation.screen.leave.request.holiday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.worklog.app.core.notification.RefreshEvents
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.usecase.leave.GetBlockedLeaveDatesUseCase
import org.worklog.app.domain.usecase.leave.SubmitHolidayRequestUseCase
import org.worklog.app.domain.usecase.rota.GetAuthUserRotaUseCase
import kotlin.time.Clock

class HolidayRequestViewModel(
    private val getAuthUserRotaUseCase: GetAuthUserRotaUseCase,
    private val requestHolidayUseCase: SubmitHolidayRequestUseCase,
    private val getBlockedLeaveDatesUseCase: GetBlockedLeaveDatesUseCase,
    private val refreshEvents: RefreshEvents
) : ViewModel() {
    private val _uiState = MutableStateFlow(HolidayRequestUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadEmployeeRota()
        loadBlockedDates()
        observeRefreshEvents()
    }

    // FCM push (holiday_approved etc.) → refresh the blocked dates list + rota calendar
    private fun observeRefreshEvents() {
        viewModelScope.launch {
            refreshEvents.events
                .filter { it == RefreshEvents.Topics.LEAVES }
                .collect {
                    loadBlockedDates()
                    loadEmployeeRota()
                }
        }
    }

    private fun loadBlockedDates() {
        viewModelScope.launch {
            // Pending leaves are NOT reflected in the rota (only approved
            // leaves are, via short_code='AL'). Fetch the server's canonical
            // "you've already booked these days" list so the picker can
            // disable them in addition to the rota-driven AL/BAL check.
            val result = getBlockedLeaveDatesUseCase(forceRefresh = true)
            if (result is ResultWrapper.Success) {
                _uiState.update { it.copy(blockedDates = result.data) }
            }
        }
    }

    fun setAccruedHoliday(accruedHoliday: Int) {
        _uiState.update { it.copy(accruedHoliday = accruedHoliday) }
    }

    fun loadEmployeeRota() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Always force-refresh so the calendar shows the latest approved/pending leave
            val result = getAuthUserRotaUseCase.getMonthlyRota(forceRefresh = true)
            if (result is ResultWrapper.Success) {
                _uiState.update { it.copy(rotas = result.data, isLoading = false) }
            } else if (result is ResultWrapper.Error) {
                _uiState.update { it.copy(errorMessage = result.message, isLoading = false) }
            }
        }
    }

    fun onDateToggle(date: String) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val parsed = runCatching { LocalDate.parse(date) }.getOrNull()

        // Block past dates
        if (parsed != null && parsed < today) {
            _uiState.update { it.copy(errorMessage = "You cannot select a past date as holiday") }
            return
        }

        _uiState.update { state ->
            // Block in two ways:
            //   (1) server says this date already has a pending/approved leave
            //       (covers pending requests which aren't yet in the rota), and
            //   (2) rota row already marks the day as AL/BAL/leave (covers
            //       approved holidays that have been written into the rota).
            val isBlockedByServer = date in state.blockedDates

            val existingRota = state.rotas.find { it.fullDate == date }
            val isAlreadyLeave = existingRota != null &&
                (existingRota.isLeave ||
                    existingRota.shortCode.uppercase().let { code ->
                        code == "AL" || code == "A/L" || code == "BAL"
                    })

            if (isBlockedByServer || isAlreadyLeave) {
                return@update state.copy(
                    errorMessage = "This date already has a holiday — you cannot request it again"
                )
            }

            val isSelected = date in state.selectedDates
            when {
                // Deselect
                isSelected -> state.copy(selectedDates = state.selectedDates - date)

                // Add date — still within allowance
                state.selectedDates.size < state.accruedHoliday ->
                    state.copy(selectedDates = state.selectedDates + date)

                // Exceeded allowance
                else -> state.copy(
                    errorMessage = "You can only request ${state.accruedHoliday} holiday day(s)"
                )
            }
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
                    // Server just flipped the requested days to A/L on the
                    // rota. Nudge Home / My Team / Leave to reload so the
                    // user sees the change without leaving the app.
                    refreshEvents.emit(RefreshEvents.Topics.ROTAS)
                    refreshEvents.emit(RefreshEvents.Topics.LEAVES)
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