package org.worklog.app.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.usecase.preference.FirstLaunchUseCase
import org.worklog.app.domain.usecase.user.UserProfileUseCase
import org.worklog.app.presentation.navigation.ScreenRoute

class AppViewModel(
    private val userProfileUseCase: UserProfileUseCase,
    private val firstLaunchUseCase: FirstLaunchUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
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