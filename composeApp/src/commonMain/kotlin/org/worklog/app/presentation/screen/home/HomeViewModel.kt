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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.worklog.app.core.platform.LocationService
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.usecase.rota.ToggleShiftUseCase
import org.worklog.app.domain.usecase.user.GetRotaUseCase
import org.worklog.app.domain.usecase.user.UserProfileUseCase
import kotlin.time.Clock

class HomeViewModel(
    private val userProfileUseCase: UserProfileUseCase,
    private val rotaUseCase: GetRotaUseCase,
    private val toggleShiftUseCase: ToggleShiftUseCase,
    private val locationService: LocationService
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        updateGreetingAndDate()
        observeCurrentShiftStatus()
    }

    fun refreshData() {
        updateGreetingAndDate()
        getUserProfile()
        loadUserRota()
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
        val month = datetime.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
        val date = "$dayOfWeek, ${datetime.day} $month ${datetime.year}"

        _uiState.update {
            it.copy(
                greetingText = greeting,
                currentDate = date
            )
        }
    }

    fun getUserProfile() {
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
            _uiState.update { it.copy(isLoading = _uiState.value.rotas.isEmpty()) }
            val result = rotaUseCase.getUpcomingRotas()
            val currentRota = rotaUseCase.getCurrentRota()

            if (result is ResultWrapper.Success) {
                _uiState.update { it.copy(rotas = result.data) }
            } else if (result is ResultWrapper.Error) {
                _uiState.update { it.copy(message = result.message) }
            }

            if (currentRota is ResultWrapper.Success) {
                _uiState.update {
                    it.copy(
                        currentRota = currentRota.data,
                        isShiftEnabled = currentRota.data?.startTimeEnabled == true
                    )
                }
            } else if (currentRota is ResultWrapper.Error) {
                _uiState.update { it.copy(message = currentRota.message) }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun toggleShift() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val isShiftStarted = currentState.isShiftStarted

            _uiState.update { it.copy(isShiftToggling = true) }

            val location = locationService.getCurrentLocation()
            val latitude = location?.latitude?.toString() ?: ""
            val longitude = location?.longitude?.toString() ?: ""

            val result = if (!isShiftStarted) {
                toggleShiftUseCase.startShift(
                    employeeId = currentState.userInfo?.id ?: "",
                    latitude = latitude,
                    longitude = longitude
                )
            } else {
                toggleShiftUseCase.endShift(
                    employeeId = currentState.userInfo?.id ?: "",
                    latitude = latitude,
                    longitude = longitude
                )
            }

            _uiState.update { state ->
                when (result) {
                    is ResultWrapper.Success -> {
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
}
