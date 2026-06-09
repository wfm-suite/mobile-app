package org.worklog.app.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.worklog.app.core.notification.RefreshEvents
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.data.provider.AuthTokenProvider
import org.worklog.app.data.provider.SessionEvent
import org.worklog.app.domain.usecase.notification.GetNotificationsUseCase
import org.worklog.app.domain.usecase.preference.FirstLaunchUseCase
import org.worklog.app.domain.usecase.user.UserProfileUseCase
import org.worklog.app.presentation.navigation.ScreenRoute

class AppViewModel(
    private val userProfileUseCase: UserProfileUseCase,
    private val firstLaunchUseCase: FirstLaunchUseCase,
    private val authTokenProvider: AuthTokenProvider,
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val refreshEvents: RefreshEvents
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    val sessionEvents = authTokenProvider.sessionEvents

    init {
        loadInitialData()
        observeRefreshEvents()
        observeSessionEvents()
    }

    private fun observeSessionEvents() {
        viewModelScope.launch {
            sessionEvents.collect { event ->
                if (event == SessionEvent.ForcedLogout) {
                    _uiState.update { it.copy(notificationCount = 0) }
                }
            }
        }
    }

    private fun observeRefreshEvents() {
        viewModelScope.launch {
            refreshEvents.events
                .filter { it == RefreshEvents.Topics.NOTIFICATIONS }
                .collect { loadUnreadCount() }
        }
    }

    private fun loadUnreadCount() {
        viewModelScope.launch {
            when (val result = getNotificationsUseCase()) {
                is ResultWrapper.Success -> {
                    // User requested "total number of notification" instead of unread count
                    _uiState.update { it.copy(notificationCount = result.data.size) }
                }

                else -> Unit
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            firstLaunchUseCase.isFirstLaunch().take(1).collect { firstLaunch ->
                if (firstLaunch) {
                    _uiState.update {
                        it.copy(
                            initialScreen = ScreenRoute.Onboarding,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(showHomeShimmer = true) }
                    loadUserProfile()
                    loadUnreadCount()
                }
            }
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            userProfileUseCase.getUserProfile
                .collect { result ->
                    when (result) {
                        is ResultWrapper.Success -> {
                            _uiState.update {
                                it.copy(
                                    initialScreen = ScreenRoute.Main,
                                    isLoading = false,
                                    showHomeShimmer = false
                                )
                            }
                        }

                        is ResultWrapper.Error -> {
                            _uiState.update {
                                it.copy(
                                    initialScreen = ScreenRoute.Login,
                                    isLoading = false,
                                    showHomeShimmer = false
                                )
                            }
                        }

                        is ResultWrapper.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }
        }
    }

}