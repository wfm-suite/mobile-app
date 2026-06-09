package org.worklog.app.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.worklog.app.core.notification.RefreshEvents
import org.worklog.app.core.util.AppActions
import org.worklog.app.core.util.LocationTracker
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.model.MyHandover
import org.worklog.app.domain.model.Rota
import org.worklog.app.domain.model.RotaStatus
import org.worklog.app.domain.usecase.rota.RotaSwapHandoverUseCase
import org.worklog.app.domain.usecase.rota.ToggleShiftUseCase
import org.worklog.app.domain.usecase.user.GetRotaUseCase
import org.worklog.app.domain.usecase.user.UserProfileUseCase

class HomeViewModel(
    private val userProfileUseCase: UserProfileUseCase,
    private val rotaUseCase: GetRotaUseCase,
    private val toggleShiftUseCase: ToggleShiftUseCase,
    private val swapHandoverUseCase: RotaSwapHandoverUseCase,
    private val appActions: AppActions,
    private val locationTracker: LocationTracker,
    private val refreshEvents: RefreshEvents
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        _uiState.update { it.copy(selectedMonth = now.month.number, selectedYear = now.year) }
        updateGreetingAndDate()
        observeCurrentShiftStatus()
        observeUserProfile()
        loadUserRota()
        loadHomeShifts()
        loadIncomingSwaps()
        observeRefreshEvents()
    }

    // FCM push (handover_accepted, swap_accepted etc.) → refresh Home's data.
    private fun observeRefreshEvents() {
        viewModelScope.launch {
            refreshEvents.events
                .filter {
                    it == RefreshEvents.Topics.ROTAS ||
                            it == RefreshEvents.Topics.SWAPS ||
                            it == RefreshEvents.Topics.HANDOVERS ||
                            it == RefreshEvents.Topics.LEAVES
                }
                .collect {
                    loadUserRota(forceRefresh = true)
                    loadHomeShifts(forceRefresh = true)
                    loadIncomingSwaps(forceRefresh = true)
                }
        }
    }

    fun refreshData() {
        updateGreetingAndDate()
        loadUserRota(forceRefresh = true)
        loadHomeShifts(forceRefresh = true)
        loadIncomingSwaps(forceRefresh = true)
    }

    fun loadIncomingSwaps(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            when (val result = swapHandoverUseCase.getIncomingSwaps(forceRefresh)) {
                is ResultWrapper.Success ->
                    _uiState.update { it.copy(incomingSwaps = result.data) }
                is ResultWrapper.Error ->
                    _uiState.update { it.copy(message = result.message) }
                else -> Unit
            }
        }
    }

    fun acceptSwap(swapId: Int) = respondSwap(swapId, accept = true)
    fun denySwap(swapId: Int) = respondSwap(swapId, accept = false)

    /**
     * Cancels the user's own pending swap or handover request on a shift row.
     * The button only renders for PENDING outgoing requests, so requestType
     * determines which endpoint to hit.
     */
    fun cancelRequest(rotaId: Int, requestId: Int, requestType: String) {
        if (requestId <= 0) return
        viewModelScope.launch {
            _uiState.update { it.copy(cancellingRotaId = rotaId) }
            val result = when (requestType) {
                "swap" -> swapHandoverUseCase.cancelSwap(requestId)
                "handover" -> swapHandoverUseCase.cancelHandover(requestId)
                else -> null
            }
            when (result) {
                is ResultWrapper.Success -> {
                    _uiState.update {
                        it.copy(
                            cancellingRotaId = null,
                            message = "Request cancelled."
                        )
                    }
                    loadHomeShifts(forceRefresh = true)
                }
                is ResultWrapper.Error ->
                    _uiState.update { it.copy(cancellingRotaId = null, message = result.message) }
                else ->
                    _uiState.update { it.copy(cancellingRotaId = null) }
            }
        }
    }

    private fun respondSwap(swapId: Int, accept: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(respondingSwapId = swapId) }
            val result = if (accept) swapHandoverUseCase.acceptSwap(swapId)
            else swapHandoverUseCase.denySwap(swapId)
            when (result) {
                is ResultWrapper.Success -> {
                    _uiState.update {
                        it.copy(
                            respondingSwapId = null,
                            incomingSwaps = it.incomingSwaps.filterNot { s -> s.id == swapId },
                            message = if (accept) "Swap accepted — awaiting admin approval." else "Swap request declined."
                        )
                    }
                    loadHomeShifts(forceRefresh = true)
                }
                is ResultWrapper.Error ->
                    _uiState.update { it.copy(respondingSwapId = null, message = result.message) }
                else ->
                    _uiState.update { it.copy(respondingSwapId = null) }
            }
        }
    }

    fun loadHomeShifts(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            // Only show shimmer when we have no data to display
            if (_uiState.value.monthlyRotas.isEmpty()) {
                _uiState.update { it.copy(isLoading = true) }
            }
            val result = rotaUseCase.getLastNDaysRota(35, forceRefresh)
            if (result is ResultWrapper.Success) {
                val homeRotas = result.data

                // The Home rota endpoint doesn't tag shifts with handover/swap
                // status, but the upcoming-shifts endpoint does. Overlay that
                // status onto the matching Home rows (and add any upcoming shift
                // the Home window happens to miss).
                val upcoming = (rotaUseCase.getUpcomingRotas(forceRefresh) as? ResultWrapper.Success)
                    ?.data ?: emptyList()
                val upcomingById = upcoming.associateBy { it.id }
                val withStatus = homeRotas.map { r ->
                    val u = upcomingById[r.id]
                    if (u != null && u.status != RotaStatus.NOTHING)
                        r.copy(
                            status = u.status,
                            requestType = u.requestType,
                            requestId = u.requestId,
                            recipientName = u.recipientName,
                            recipientAvatarUrl = u.recipientAvatarUrl
                        )
                    else r
                }
                val homeIds = homeRotas.map { it.id }.toSet()
                val rotas = withStatus + upcoming.filter { it.id !in homeIds }

                // Accepted handovers null out the rota's employee_id, so those
                // days vanish from the rota list. Re-add them as OFF rows so the
                // user still sees "Handover accepted" on that day.
                val handoversResult = rotaUseCase.getMyHandovers(forceRefresh)
                val acceptedHandovers = (handoversResult as? ResultWrapper.Success)?.data
                    ?.filter { it.status == RotaStatus.ACCEPTED }
                    ?: emptyList()

                val existingDates = rotas.map { it.fullDate }.toSet()
                val handoverRows = acceptedHandovers
                    .filter { it.rotaDate !in existingDates }
                    .map { it.toAcceptedHandoverOffRota() }

                val today = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

                // Home shows today + future only — past shifts are hidden here.
                // Future leaves are excluded too: holidays belong on the Leave
                // page, not in My Shifts. Today's leave (if any) is kept so the
                // anchor row reflects reality.
                val futureAndToday = (rotas + handoverRows)
                    .filter { it.fullDate >= today }
                    .filterNot { it.isLeave && it.fullDate > today }
                    .sortedBy { it.fullDate }

                // Guarantee today is the first row, even when no rota exists for
                // today (rest day, or unpublished): drop in a "No Shift" anchor.
                val finalList = if (futureAndToday.any { it.fullDate == today }) {
                    futureAndToday
                } else {
                    listOf(todayPlaceholderRota(today)) + futureAndToday
                }

                // Heuristic publication horizon: the last day in the window
                // that has a real shift (not OFF, not leave). Days after this
                // that are OFF are treated as "rota not yet published" and the
                // card renders "—" instead of "No Shift". Imperfect — a real
                // OFF day after the last working shift would also show "—" —
                // but it matches the common weekly-publish pattern.
                val lastPublishedDate = finalList
                    .filter {
                        !it.isLeave &&
                        !it.shiftStatus.equals("off", ignoreCase = true) &&
                        !it.shortCode.equals("OFF", ignoreCase = true)
                    }
                    .maxByOrNull { it.fullDate }?.fullDate.orEmpty()

                _uiState.update {
                    it.copy(
                        monthlyRotas = finalList,
                        rotaStartDate = finalList.firstOrNull()?.fullDate ?: "",
                        rotaEndDate = finalList.lastOrNull()?.fullDate ?: "",
                        lastPublishedDate = lastPublishedDate,
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
                    _uiState.update { state ->
                        state.copy(
                            userInfo = result.data,
                            // Office coords land here for the first time → re-evaluate
                            // proximity using whatever lat/lon we already have.
                            isNearOffice = computeIsNearOffice(
                                userLat = state.latitude.toDoubleOrNull(),
                                userLon = state.longitude.toDoubleOrNull(),
                                info = result.data,
                            )
                        )
                    }
                } else if (result is ResultWrapper.Error) {
                    _uiState.update { it.copy(message = result.message) }
                }
            }
        }
    }

    fun loadUserRota(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (_uiState.value.currentRota == null) {
                _uiState.update { it.copy(isLoading = true) }
            }
            val currentRotaResult = rotaUseCase.getCurrentRota(forceRefresh)

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

    fun showError(msg: String) {
        _uiState.update { it.copy(message = msg) }
    }

    // "No Shift" anchor row for today when there's no real rota — keeps today
    // pinned at the top of the Home list. Negative id => non-clickable.
    private fun todayPlaceholderRota(today: String): Rota {
        val d = LocalDate.parse(today)
        val dayName = d.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
        return Rota(
            id = Int.MIN_VALUE,
            fullDate = today,
            date = d.day.toString(),
            dayName = dayName,
            shiftStartTime = "",
            shiftEndTime = "",
            breakStartTime = "",
            breakEndTime = "",
            totalHours = "",
            location = "",
            remarks = "",
            shiftType = "",
            shiftStatus = "off",
            status = RotaStatus.NOTHING,
            requestType = "",
            designation = "",
            startTimeEnabled = false,
            floorName = "",
            shiftLabel = "",
            shortCode = "OFF",
            isLeave = false
        )
    }

    private fun MyHandover.toAcceptedHandoverOffRota(): Rota {
        val d = LocalDate.parse(rotaDate)
        val dayName = d.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
        val time = approvedAt.takeIf { it.length >= 16 }?.substring(11, 16)
        val note = if (time != null) "Handover accepted · $time" else "Handover accepted"
        return Rota(
            id = -id, // negative → unique LazyColumn key + non-clickable
            fullDate = rotaDate,
            date = d.day.toString(),
            dayName = dayName,
            shiftStartTime = "",
            shiftEndTime = "",
            breakStartTime = "",
            breakEndTime = "",
            totalHours = "",
            location = "",
            remarks = "",
            shiftType = "",
            shiftStatus = "off",
            status = RotaStatus.ACCEPTED,
            requestType = "handover",
            designation = "",
            startTimeEnabled = false,
            floorName = "",
            shiftLabel = note,
            shortCode = "OFF",
            isLeave = false
        )
    }

    fun updateLocation(latitude: String, longitude: String) {
        _uiState.update {
            it.copy(
                latitude = latitude,
                longitude = longitude,
                isNearOffice = computeIsNearOffice(
                    userLat = latitude.toDoubleOrNull(),
                    userLon = longitude.toDoubleOrNull(),
                    info = it.userInfo,
                )
            )
        }
    }

    fun openMap() {
        viewModelScope.launch {
            val location = locationTracker.getCurrentLocation()
            if (location != null) {
                updateLocation(location.first.toString(), location.second.toString())
                appActions.openMap(location.first.toString(), location.second.toString())
            } else {
                appActions.openMap(_uiState.value.latitude, _uiState.value.longitude)
            }
        }
    }

    // Returns true when the user is within NEAR_OFFICE_RADIUS_M of the office.
    // The office coords come from the user's company profile (admin-configured
    // via dashboard). When either side is missing we keep the prior value as
    // false rather than guessing — better than glowing at random locations.
    private fun computeIsNearOffice(
        userLat: Double?,
        userLon: Double?,
        info: org.worklog.app.domain.model.UserInfo?,
    ): Boolean {
        val officeLat = info?.companyLatitude ?: return false
        val officeLon = info.companyLongitude ?: return false
        if (userLat == null || userLon == null) return false
        return haversineMeters(userLat, userLon, officeLat, officeLon) <= NEAR_OFFICE_RADIUS_M
    }

    private fun haversineMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6_371_000.0
        val dLat = (lat2 - lat1) * kotlin.math.PI / 180.0
        val dLon = (lon2 - lon1) * kotlin.math.PI / 180.0
        val l1 = lat1 * kotlin.math.PI / 180.0
        val l2 = lat2 * kotlin.math.PI / 180.0
        val a = kotlin.math.sin(dLat / 2).let { it * it } +
                kotlin.math.cos(l1) * kotlin.math.cos(l2) *
                kotlin.math.sin(dLon / 2).let { it * it }
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return r * c
    }

    companion object {
        // Considered "at the office" within this radius (metres). Wider than
        // strictly needed to absorb GPS jitter.
        private const val NEAR_OFFICE_RADIUS_M = 300.0
    }
}
