package org.worklog.app.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.worklog.app.core.util.AppActions
import org.worklog.app.core.util.LocationTracker
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.usecase.rota.ToggleShiftUseCase
import org.worklog.app.domain.usecase.user.GetRotaUseCase
import org.worklog.app.domain.usecase.user.UserProfileUseCase

class HomeViewModel(
    private val userProfileUseCase: UserProfileUseCase,
    private val rotaUseCase: GetRotaUseCase,
    private val toggleShiftUseCase: ToggleShiftUseCase,
    private val appActions: AppActions,
    private val locationTracker: LocationTracker
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        _uiState.update { it.copy(selectedMonth = now.month.number, selectedYear = now.year) }
        updateGreetingAndDate()
        observeCurrentShiftStatus()
        observeUserProfile()
    }

    fun refreshData() {
        updateGreetingAndDate()
        loadUserRota()
        loadHomeShifts()
    }

    fun loadHomeShifts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            var result = rotaUseCase.getLastNDaysRota(40)
            if (result !is ResultWrapper.Success || (result as ResultWrapper.Success).data.isEmpty()) {
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                result = rotaUseCase.getMonthlyRota(now.month.number, now.year)
            }
            if (result is ResultWrapper.Success) {
                val rotas = result.data
                _uiState.update {
                    it.copy(
                        monthlyRotas = rotas,
                        rotaStartDate = rotas.firstOrNull()?.fullDate ?: "",
                        rotaEndDate = rotas.lastOrNull()?.fullDate ?: "",
                        isLoading = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        message = (result as? ResultWrapper.Error)?.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeCurrentShiftStatus() {
        viewModelScope.launch {
            _uiState.map { it.currentRota?.id }
                .distinctUntilChanged()
                .filterNotNull()
                .flatMapLatest { rotaId ->
                    toggleShiftUseCase.observeCurrentShiftStatus(rotaId.toString())
                }
                .collect { isShiftStarted ->
                    _uiState.update { it.copy(isShiftStarted = isShiftStarted) }
                }
        }
    }

    private fun updateGreetingAndDate() {
        val currentMoment = Clock.System.now()
        val datetime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())

        val greeting = when (datetime.hour) {
            in 0..4 -> "Good Night"
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..20 -> "Good Evening"
            else -> "Good Night"
        }

        val dayOfWeek =
            datetime.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
        val monthStr = datetime.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
        val date = "$dayOfWeek, ${datetime.day} $monthStr ${datetime.year}"

        _uiState.update {
            it.copy(
                greetingText = greeting,
                currentDate = date
            )
        }
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

    fun loadUserRota() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val currentRotaResult = rotaUseCase.getCurrentRota()
            
            if (currentRotaResult is ResultWrapper.Success) {
                _uiState.update {
                    it.copy(
                        currentRota = currentRotaResult.data,
                        hasCurrentRota = currentRotaResult.data != null,
                        isShiftEnabled = currentRotaResult.data?.startTimeEnabled == true,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        message = (currentRotaResult as? ResultWrapper.Error)?.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun loadMonthlyRota(month: Int, year: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedMonth = month, selectedYear = year) }
            val result = rotaUseCase.getMonthlyRota(month, year)
            if (result is ResultWrapper.Success) {
                val rotas = result.data
                _uiState.update {
                    it.copy(
                        monthlyRotas = rotas,
                        rotaStartDate = rotas.firstOrNull()?.fullDate ?: "",
                        rotaEndDate = rotas.lastOrNull()?.fullDate ?: "",
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(message = (result as ResultWrapper.Error).message, isLoading = false) }
            }
        }
    }

    fun nextMonth() {
        var month = _uiState.value.selectedMonth + 1
        var year = _uiState.value.selectedYear
        if (month > 12) {
            month = 1
            year++
        }
        loadMonthlyRota(month, year)
    }

    fun previousMonth() {
        var month = _uiState.value.selectedMonth - 1
        var year = _uiState.value.selectedYear
        if (month < 1) {
            month = 12
            year--
        }
        loadMonthlyRota(month, year)
    }

    fun goToToday() {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        loadMonthlyRota(now.month.number, now.year)
    }

    fun toggleShift() {
        viewModelScope.launch {

            val currentState = _uiState.value
            val isShiftStarted = currentState.isShiftStarted

            _uiState.update { it.copy(isShiftToggling = true) }

            val result = if (!isShiftStarted) {
                println("Start Shift")
                toggleShiftUseCase.startShift(
                    employeeId = currentState.userInfo?.id ?: "",
                    latitude = currentState.latitude,
                    longitude = currentState.longitude
                )
            } else {
                toggleShiftUseCase.endShift(
                    employeeId = currentState.userInfo?.id ?: "",
                    latitude = currentState.latitude,
                    longitude = currentState.longitude
                )
            }

            _uiState.update { state ->
                when (result) {
                    is ResultWrapper.Success -> {
                        println("Shift Toggled: $isShiftStarted, ${result.data}")
                        toggleShiftUseCase.updateCurrentShiftStatus(
                            currentState.currentRota?.id?.toString() ?: "",
                            !isShiftStarted
                        )

                        state.copy(
                            isShiftStarted = !isShiftStarted,
                            isShiftToggling = false
                        )
                    }

                    is ResultWrapper.Error -> {
                        println("Shift Toggled: $isShiftStarted, ${result.message}")
                        state.copy(
                            message = result.message,
                            isShiftToggling = false
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

    fun updateLocation(latitude: String, longitude: String) {
        _uiState.update { it.copy(latitude = latitude, longitude = longitude) }
    }

    fun openMap() {
        viewModelScope.launch {
            val location = locationTracker.getCurrentLocation()
            if (location != null) {
                _uiState.update { it.copy(latitude = location.first.toString(), longitude = location.second.toString()) }
                appActions.openMap(location.first.toString(), location.second.toString())
            } else {
                appActions.openMap(_uiState.value.latitude, _uiState.value.longitude)
            }
        }
    }
}
