package org.worklog.app.presentation.screen.rota.my_team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.model.UserInfo
import org.worklog.app.domain.usecase.rota.EmployeeRotaUseCase
import org.worklog.app.domain.usecase.user.UserProfileUseCase
import kotlin.time.Clock

class MyTeamViewModel(
    private val employeeRotaUseCase: EmployeeRotaUseCase,
    private val userProfileUseCase: UserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyTeamUiState())
    val uiState: StateFlow<MyTeamUiState> = _uiState.asStateFlow()

    init {
        getCurrentDate()
        observeUserProfile()
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

            val weeklyResult = weeklyDeferred.await()
            val monthlyResult = monthlyDeferred.await()

            _uiState.update { state ->
                when {
                    weeklyResult is ResultWrapper.Success &&
                            monthlyResult is ResultWrapper.Success -> {

                        val allRotas =
                            (weeklyResult.data + monthlyResult.data).distinctBy { it.rota.id }

                        val uniqueShiftTypes = listOf("All") + allRotas
                            .map {
                                if (it.rota.shiftStatus.startsWith("N", ignoreCase = true)) "Night" else "Day"
                            }
                            .distinct()

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

                        state.copy(
                            weeklyRotas = weeklyResult.data,
                            monthlyRotas = monthlyResult.data,
                            shiftTypes = uniqueShiftTypes,
                            floorNames = uniqueFloorNames,
                            selectedDate = selectedDate,
                            selectedFloorName = todayRota?.rota?.floorName ?: "All",
                            selectedShiftStatus = if (todayRota == null) "All" else if (todayRota.rota.shiftStatus.startsWith("N", ignoreCase = true)) "Night" else "Day",
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

    private fun filterRotas() {
        _uiState.update { state ->
            val sourceRotas = if (state.isCalendarExpanded) {
                state.monthlyRotas
            } else {
                state.weeklyRotas
            }

            var filteredRotas = sourceRotas

            state.selectedDate?.let { selectedDate ->
                filteredRotas = filteredRotas.filter { it.rota.fullDate == selectedDate }
            }

            state.selectedShiftStatus?.let { selectedShiftStatus ->
                if (selectedShiftStatus != "All") {
                    filteredRotas = filteredRotas.filter {
                        val shiftCategory = if (it.rota.shiftStatus.startsWith("N", ignoreCase = true)) "Night" else "Day"
                        shiftCategory == selectedShiftStatus
                    }
                }
            }

            state.selectedFloorName?.let { selectedFloorName ->
                if (selectedFloorName != "All") {
                    filteredRotas = filteredRotas.filter { it.rota.floorName == selectedFloorName }
                }
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
        _uiState.update { state ->
            val newSelectedDate = if (state.selectedDate == date) null else date
            state.copy(selectedDate = newSelectedDate)
        }
        filterRotas()
    }

    fun showError(message: String) {
        _uiState.update { it.copy(message = message) }
    }
}
