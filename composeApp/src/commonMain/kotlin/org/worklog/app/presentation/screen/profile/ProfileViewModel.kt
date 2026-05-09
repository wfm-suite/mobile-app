package org.worklog.app.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.usecase.user.AuthenticationUseCase
import org.worklog.app.domain.usecase.user.UserProfileUseCase

class ProfileViewModel(
    private val getUserProfileUseCase: UserProfileUseCase,
    private val authenticationUseCase: AuthenticationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun refreshData() {
        getUserProfile()
    }

    fun getUserProfile() {
        viewModelScope.launch {
            getUserProfileUseCase.getUserProfile.collect { result ->
                if (result is ResultWrapper.Success) {
                    _uiState.update { it.copy(userInfo = result.data) }
                } else if (result is ResultWrapper.Error) {
                    _uiState.update { it.copy(message = result.message) }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = authenticationUseCase.logout()) {
                is ResultWrapper.Success -> {
                    _uiState.update { it.copy(isLoading = false, isLoggedOut = true) }
                }

                is ResultWrapper.Error -> {
                    _uiState.update { it.copy(isLoading = false, message = result.message) }
                }

                else -> {}
            }
        }
    }
}