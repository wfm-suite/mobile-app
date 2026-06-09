package org.worklog.app.presentation.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private var countdownJob: Job? = null

    fun switchLoginMethod(method: LoginMethod) {
        _uiState.update {
            it.copy(
                loginMethod = method,
                error = null,
                otpSent = false,
                otp = "",
                phoneError = null,
                otpError = null,
                emailError = null,
                passwordError = null
            )
        }
    }

    // --- Email login ---
    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun loginWithEmail() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        val emailError = if (email.isBlank()) "Email is required"
            else if (!Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matches(email)) "Invalid email"
            else null
        val passwordError = if (password.isBlank()) "Password is required"
            else if (password.length < 6) "Password must be at least 6 characters"
            else null

        if (emailError != null || passwordError != null) {
            _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authenticationUseCase.login(email, password)) {
                is ResultWrapper.Success -> _uiState.update { it.copy(isLoading = false, userInfo = result.data) }
                is ResultWrapper.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                is ResultWrapper.Loading -> Unit
            }
        }
    }

    // --- Phone OTP login ---
    fun onPhoneChange(phone: String) {
        _uiState.update { it.copy(phone = phone, phoneError = null) }
    }

    fun onOtpChange(otp: String) {
        if (otp.length <= 6) _uiState.update { it.copy(otp = otp, otpError = null) }
    }

    fun sendOtp() {
        val rawPhone = _uiState.value.phone.trim()
        if (rawPhone.isBlank()) {
            _uiState.update { it.copy(phoneError = "Phone number is required") }
            return
        }

        val phone = normalizePhone(rawPhone)
        if (phone == null) {
            _uiState.update { it.copy(phoneError = "Enter a valid phone number") }
            return
        }

        // Persist normalized number so resend/verify reuse it
        _uiState.update { it.copy(phone = phone) }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authenticationUseCase.sendOtp(phone)) {
                is ResultWrapper.Success -> {
                    _uiState.update { it.copy(isLoading = false, otpSent = true, otp = "") }
                    startResendCountdown()
                }
                is ResultWrapper.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                is ResultWrapper.Loading -> Unit
            }
        }
    }

    // Convert user input to E.164.
    // Examples: "07424 978944" → "+447424978944", "00447424978944" → "+447424978944",
    //           "+447424978944" → unchanged, "447424978944" → "+447424978944".
    // Returns null if the result isn't a plausible international number (8–15 digits).
    private fun normalizePhone(raw: String): String? {
        val cleaned = raw.filter { it.isDigit() || it == '+' }
        val withPlus = when {
            cleaned.startsWith("+") -> cleaned
            cleaned.startsWith("00") -> "+" + cleaned.drop(2)
            // UK national format: leading "0" → assume UK (+44)
            cleaned.startsWith("0") -> "+44" + cleaned.drop(1)
            cleaned.isNotEmpty() -> "+$cleaned"
            else -> return null
        }
        val digits = withPlus.drop(1)
        if (digits.length !in 8..15 || !digits.all { it.isDigit() }) return null
        return withPlus
    }

    fun verifyOtp() {
        val otp = _uiState.value.otp.trim()
        if (otp.length != 6) {
            _uiState.update { it.copy(otpError = "Enter the 6-digit OTP") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authenticationUseCase.verifyOtp(_uiState.value.phone.trim(), otp)) {
                is ResultWrapper.Success -> _uiState.update { it.copy(isLoading = false, userInfo = result.data) }
                is ResultWrapper.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                is ResultWrapper.Loading -> Unit
            }
        }
    }

    fun resendOtp() {
        if (_uiState.value.resendCountdown > 0) return
        val phone = _uiState.value.phone.trim()
        _uiState.update { it.copy(otp = "") }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authenticationUseCase.resendOtp(phone)) {
                is ResultWrapper.Success -> {
                    _uiState.update { it.copy(isLoading = false, error = "OTP Resent Successfully") }
                    startResendCountdown()
                }
                is ResultWrapper.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                is ResultWrapper.Loading -> Unit
            }
        }
    }

    fun goBackToPhone() {
        countdownJob?.cancel()
        _uiState.update { it.copy(otpSent = false, otp = "", otpError = null, resendCountdown = 0) }
    }

    private fun startResendCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (i in 60 downTo 1) {
                _uiState.update { it.copy(resendCountdown = i) }
                delay(1000)
            }
            _uiState.update { it.copy(resendCountdown = 0) }
        }
    }
}
