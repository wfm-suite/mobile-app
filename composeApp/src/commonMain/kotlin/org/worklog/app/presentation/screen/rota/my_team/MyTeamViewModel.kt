package org.worklog.app.presentation.screen.rota.my_team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.worklog.app.core.notification.RefreshEvents
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.model.UserInfo
import org.worklog.app.domain.model.EmployeeRota
import org.worklog.app.domain.usecase.rota.EmployeeRotaUseCase
import org.worklog.app.domain.usecase.rota.RotaSwapHandoverUseCase
import org.worklog.app.domain.usecase.user.UserProfileUseCase
import kotlin.time.Clock

class MyTeamViewModel(
    private val employeeRotaUseCase: EmployeeRotaUseCase,
    private val userProfileUseCase: UserProfileUseCase,
    private val swapHandoverUseCase: RotaSwapHandoverUseCase,
    private val refreshEvents: RefreshEvents
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyTeamUiState())
    val uiState: StateFlow<MyTeamUiState> = _uiState.asStateFlow()

    init {
        getCurrentDate()
        observeUserProfile()
        // FCM push (handover_accepted, swap_accepted etc.) → refresh team rota.
        viewModelScope.launch {
            refreshEvents.events
                .filter { it == RefreshEvents.Topics.ROTAS }
                .collect {
                    _uiState.value.userInfo?.let { loadEmployeeRotas(it, forceRefresh = true) }
                }
        }
    }

    fun refreshData() {
        getCurrentDate()
        _uiState.value.userInfo?.let { loadEmployeeRotas(it, forceRefresh = true) }
    }

    private fun getCurrentDate() {
        val currentDate = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
        
        _uiState.update { 
            it.copy(
                selectedDate = currentDate.toString(),
                selectedMonth = currentDate.month.number,
                selectedYear = currentDate.year
            ) 
        }
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            userProfileUseCase.getUserProfile.collect { result ->
                if (result is ResultWrapper.Success) {
                    println("\n\n User Profile: ${result.data}\n\n")
                    _uiState.update { it.copy(userInfo = result.data) }
                    loadEmployeeRotas(result.data) // Trigger loadEmployeeRotas after userInfo is available
                } else if (result is ResultWrapper.Error) {
                    _uiState.update { it.copy(message = result.message) }
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun loadEmployeeRotas(userInfo: UserInfo?, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val isEmpty = _uiState.value.weeklyRotas.isEmpty() && _uiState.value.monthlyRotas.isEmpty()
            if (isEmpty) {
                _uiState.update { it.copy(isLoading = true, message = null) }
            } else {
                _uiState.update { it.copy(message = null) }
            }

            val weeklyDeferred = async { employeeRotaUseCase.getAllUsersWeeklyRota(forceRefresh) }

            // Load CURRENT month
            val monthlyDeferred = async {
                val state = _uiState.value
                if (state.selectedMonth != null && state.selectedYear != null) {
                    employeeRotaUseCase.getAllUsersMonthlyRotaByMonthYear(
                        state.selectedMonth,
                        state.selectedYear,
                        forceRefresh
                    )
                } else {
                    employeeRotaUseCase.getAllUsersMonthlyRota(forceRefresh)
                }
            }

            // ALSO load NEXT month so the weekly strip can scroll across the
            // month boundary into rotas like Jun 1–6 from a May session.
            val nextMonthDeferred = async {
                val state = _uiState.value
                val m = state.selectedMonth
                val y = state.selectedYear
                if (m != null && y != null) {
                    val nextM = if (m == 12) 1 else m + 1
                    val nextY = if (m == 12) y + 1 else y
                    employeeRotaUseCase.getAllUsersMonthlyRotaByMonthYear(
                        nextM, nextY, forceRefresh
                    )
                } else {
                    null
                }
            }

            val weeklyResult = weeklyDeferred.await()
            val monthlyResult = monthlyDeferred.await()
            val nextMonthResult = nextMonthDeferred.await()
            val nextMonthData = (nextMonthResult as? ResultWrapper.Success)?.data ?: emptyList()

            _uiState.update { state ->
                when {
                    weeklyResult is ResultWrapper.Success &&
                            monthlyResult is ResultWrapper.Success -> {

                        val allRotas =
                            (weeklyResult.data + monthlyResult.data + nextMonthData)
                                .distinctBy { it.rota.id }

                        // Only Day / Night — no "All". Default chip selected
                        // based on the user's own shift for the selected date.
                        val shiftTypeOptions = listOf("Day", "Night")

                        val uniqueFloorNames = listOf("All") + allRotas
                            .map { it.rota.floorName }
                            .filter { it.isNotBlank() }
                            .distinct()

                        // Use the already selected date from state, don't override it
                        val selectedDate = state.selectedDate ?: Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                            .date
                            .toString()

                        val currentUserId = userInfo?.id.toString()
                        val todayRota = allRotas.firstOrNull {
                            it.rota.fullDate == selectedDate && it.employee.id.toString() == currentUserId
                        }

                        // Detect user's shift type using shortCode + shiftType
                        // (more reliable than the often-null shiftStatus field).
                        val userIsNight = todayRota?.rota?.let { r ->
                            val code = r.shortCode.uppercase()
                            code.startsWith("N") ||
                                    r.shiftType.contains("night", ignoreCase = true) ||
                                    r.shiftType.contains("evening", ignoreCase = true)
                        } == true

                        state.copy(
                            weeklyRotas = weeklyResult.data,
                            // monthlyRotas now spans current + next month so the
                            // weekly strip can cross the month boundary
                            monthlyRotas = (monthlyResult.data + nextMonthData)
                                .distinctBy { it.rota.id },
                            shiftTypes = shiftTypeOptions,
                            floorNames = uniqueFloorNames,
                            selectedDate = selectedDate,
                            selectedFloorName = todayRota?.rota?.floorName ?: "All",
                            // Auto-select: Night if I'm on N tonight, else Day.
                            // If I'm OFF / no rota today → fall back to Day.
                            selectedShiftStatus = if (userIsNight) "Night" else "Day",
                            isLoading = false
                        )
                    }

                    weeklyResult is ResultWrapper.Error -> {
                        state.copy(
                            message = weeklyResult.message,
                            isLoading = false
                        )
                    }

                    monthlyResult is ResultWrapper.Error -> {
                        state.copy(
                            message = monthlyResult.message,
                            isLoading = false
                        )
                    }

                    else -> state.copy(isLoading = false)
                }
            }
            filterRotas()
        }
    }

    fun onMonthYearSelected(month: Int, year: Int) {
        // Create the first day of the selected month
        val firstDayOfMonth = "$year-${month.toString().padStart(2, '0')}-01"
        
        _uiState.update { 
            it.copy(
                selectedMonth = month,
                selectedYear = year,
                selectedDate = firstDayOfMonth
            ) 
        }
        // Reload data with new month/year
        _uiState.value.userInfo?.let { userInfo ->
            loadEmployeeRotas(userInfo)
        }
    }

    // Weekly strip swiped into a different month → load that month's rota but
    // keep the user's selected date (unlike onMonthYearSelected, which jumps to
    // the 1st). Avoids the strip snapping while swiping across months.
    fun onWeekMonthChanged(month: Int, year: Int) {
        val state = _uiState.value
        if (state.selectedMonth == month && state.selectedYear == year) return

        _uiState.update { it.copy(selectedMonth = month, selectedYear = year) }
        state.userInfo?.let { loadEmployeeRotas(it) }
    }

    private fun filterRotas() {
        _uiState.update { state ->
            val sourceRotas = if (state.isCalendarExpanded) {
                state.monthlyRotas
            } else {
                state.weeklyRotas
            }

            var filteredRotas = sourceRotas

            // 1. Filter by selected date
            state.selectedDate?.let { selectedDate ->
                filteredRotas = filteredRotas.filter { it.rota.fullDate == selectedDate }
            }

            // 2-4. Team filter: only show people who are actually on shift on
            // the selected date and match the Day/Night and floor chips. The
            // same rules apply to "Me" — onDateSelected switches the Day/Night
            // chip to my own shift type so I appear when I have a real shift,
            // and stay hidden when I'm off.
            filteredRotas = filteredRotas.filter { row ->
                val r = row.rota

                // Working-shift check
                val noShiftTime = r.shiftStartTime.isBlank() || r.shiftEndTime.isBlank()
                val isOff = r.shiftStatus.equals("off", ignoreCase = true) ||
                            r.shortCode.equals("OFF", ignoreCase = true)
                val isLeaveRow = r.isLeave
                if (noShiftTime || isOff || isLeaveRow) return@filter false

                // Day/Night chip
                state.selectedShiftStatus?.let { selected ->
                    val code = r.shortCode.uppercase()
                    val isNight = code.startsWith("N") ||
                            r.shiftType.contains("night", ignoreCase = true) ||
                            r.shiftType.contains("evening", ignoreCase = true)
                    val shiftCategory = if (isNight) "Night" else "Day"
                    if (shiftCategory != selected) return@filter false
                }

                // Floor chip ("All" = no filter)
                state.selectedFloorName?.let { selectedFloorName ->
                    if (selectedFloorName != "All" && r.floorName != selectedFloorName) {
                        return@filter false
                    }
                }

                true
            }

            state.copy(displayRotas = filteredRotas)
        }
    }

    fun onShiftTypeSelected(shiftType: String) {
        _uiState.update { it.copy(selectedShiftStatus = shiftType) }
        filterRotas()
    }

    fun onFloorNameSelected(floorName: String) {
        _uiState.update { it.copy(selectedFloorName = floorName) }
        filterRotas()
    }

    fun onCalendarToggle() {
        _uiState.update {
            it.copy(isCalendarExpanded = !it.isCalendarExpanded)
        }
        filterRotas()
    }

    fun onDateSelected(date: String) {
        // Always pin the list to the tapped date — tapping the same cell again
        // should NOT clear the filter (otherwise the list collapses to every
        // employee across every date, which read as "wrong values").
        _uiState.update { state ->
            val currentUserId = state.userInfo?.id ?: ""
            // Detect user's shift for the tapped date
            val rotaOnDate = (state.weeklyRotas + state.monthlyRotas).firstOrNull {
                it.rota.fullDate == date && it.employee.id.toString() == currentUserId
            }
            
            val isNight = rotaOnDate?.rota?.let { r ->
                val code = r.shortCode.uppercase()
                code.startsWith("N") ||
                        r.shiftType.contains("night", ignoreCase = true) ||
                        r.shiftType.contains("evening", ignoreCase = true)
            } == true

            state.copy(
                selectedDate = date,
                // Auto-update chips to match MY shift on the selected date
                selectedShiftStatus = if (isNight) "Night" else "Day",
                selectedFloorName = rotaOnDate?.rota?.floorName?.takeIf { it.isNotBlank() } ?: "All"
            )
        }
        filterRotas()
    }

    fun showError(message: String) {
        _uiState.update { it.copy(message = message) }
    }

    fun onCancelRequest(employeeRota: EmployeeRota) {
        val rota = employeeRota.rota
        if (rota.requestId <= 0 || rota.requestType.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(cancellingRequestId = rota.requestId, message = null) }

            val result = when (rota.requestType) {
                "swap" -> swapHandoverUseCase.cancelSwap(rota.requestId)
                "handover" -> swapHandoverUseCase.cancelHandover(rota.requestId)
                else -> null
            }

            when (result) {
                is ResultWrapper.Success -> {
                    refreshEvents.emit(RefreshEvents.Topics.ROTAS)
                    refreshEvents.emit(
                        if (rota.requestType == "handover")
                            RefreshEvents.Topics.HANDOVERS
                        else RefreshEvents.Topics.SWAPS
                    )
                    val label = if (rota.requestType == "handover") "Handover" else "Swap"
                    _uiState.update {
                        it.copy(
                            cancellingRequestId = null,
                            message = "$label request cancelled"
                        )
                    }
                    _uiState.value.userInfo?.let { loadEmployeeRotas(it, forceRefresh = true) }
                }

                is ResultWrapper.Error -> _uiState.update {
                    it.copy(
                        cancellingRequestId = null,
                        message = result.message
                    )
                }

                else -> _uiState.update { it.copy(cancellingRequestId = null) }
            }
        }
    }
}
