package org.worklog.app.presentation.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.usecase.user.AuthenticationUseCase

class LoginViewModel(
    private val authenticationUseCase: AuthenticationUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun sendOtp() {
        val phone = _uiState.value.phone
        val phoneError = validatePhone(phone)
        if (phoneError != null) {
            _uiState.update { it.copy(phoneError = phoneError, error = phoneError) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authenticationUseCase.sendOtp(phone)) {
                is ResultWrapper.Success -> {
                    _uiState.update { it.copy(isLoading = false, step = LoginStep.OTP, error = null) }
                }
                is ResultWrapper.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is ResultWrapper.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun verifyOtp() {
        val phone = _uiState.value.phone
        val otp = _uiState.value.otp
        val otpError = validateOtp(otp)
        if (otpError != null) {
            _uiState.update { it.copy(otpError = otpError, error = otpError) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authenticationUseCase.verifyOtp(phone, otp)) {
                is ResultWrapper.Success -> {
                    _uiState.update {
                        it.copy(userInfo = result.data, isLoggedIn = true, isLoading = false, error = null)
                    }
                }
                is ResultWrapper.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is ResultWrapper.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun onPhoneChange(phone: String) {
        _uiState.update { it.copy(phone = phone, phoneError = null) }
    }

    fun onOtpChange(otp: String) {
        _uiState.update { it.copy(otp = otp, otpError = null) }
    }

    fun goBackToPhone() {
        _uiState.update { it.copy(step = LoginStep.PHONE, otp = "", otpError = null, error = null) }
    }

    private fun validatePhone(phone: String): String? {
        return when {
            phone.isBlank() -> "Phone number is required"
            phone.length < 10 -> "Enter a valid phone number"
            else -> null
        }
    }

    private fun validateOtp(otp: String): String? {
        return when {
            otp.isBlank() -> "OTP is required"
            otp.length < 4 -> "Enter a valid OTP"
            else -> null
        }
    }

    // -- email login (commented out, restore if needed) --
    // fun loginUser() { ... }
    // private fun validateEmail(email: String): String? { ... }
    // private fun validatePassword(password: String): String? { ... }
    // fun onEmailChange(email: String) { ... }
    // fun onPasswordChange(password: String) { ... }
}
