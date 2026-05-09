package org.worklog.app.presentation.screen.password_reset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.usecase.user.PasswordResetUseCase

class PasswordResetViewModel(
    private val passwordResetUseCase: PasswordResetUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PasswordResetUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailError = null
            )
        }
    }

    fun onTokenChange(token: String) {
        _uiState.update {
            it.copy(
                token = token,
                tokenError = null
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordError = null
            )
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = null
            )
        }
    }

    fun onSendEmail() {
        val email = _uiState.value.email

        val emailError = validateEmail(email)

        // if validation fails, update UI and return
        if (emailError != null) {
            _uiState.update {
                it.copy(
                    emailError = emailError,
                    message = emailError,
                    isLoading = false
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = passwordResetUseCase.sendEmail(_uiState.value.email)) {
                is ResultWrapper.Success -> {
                    _uiState.update {
                        it.copy(
                            hasEmailSent = true,
                            isLoading = false,
                            message = "Email sent successfully please check your inbox"
                        )
                    }
                }

                is ResultWrapper.Error -> {
                    _uiState.update {
                        it.copy(message = result.message, isLoading = false)
                    }
                }

                is ResultWrapper.Loading -> {

                }
            }
        }
    }

    fun onResetPassword() {
        val state = _uiState.value

        val tokenError = validateToken(state.token)
        val passwordError = validatePassword(state.password)
        val confirmPasswordError = validateConfirmPassword(
            password = state.password,
            confirmPassword = state.confirmPassword
        )
        val errorMessage = tokenError ?: passwordError ?: confirmPasswordError

        // Stop if validation fails
        if (errorMessage != null) {
            _uiState.update {
                it.copy(
                    tokenError = tokenError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError,
                    message = errorMessage
                )
            }
            return
        }

        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true) }

            val result = passwordResetUseCase.resetPassword(
                email = state.email,
                token = state.token,
                password = state.password,
                confirmPassword = state.confirmPassword
            )

            _uiState.update { current ->
                when (result) {
                    is ResultWrapper.Success -> current.copy(
                        isLoading = false,
                        hasPasswordReset = true,
                        message = "Password reset successfully"
                    )

                    is ResultWrapper.Error -> current.copy(
                        isLoading = false,
                        message = result.message
                    )

                    else -> current.copy(isLoading = false)
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matches(email) -> "Invalid email"
            else -> null
        }
    }

    private fun validateToken(token: String): String? {
        return when {
            token.isBlank() -> "Code is required"
            token.length !in 6..10 -> "Code must be between 6 and 10 characters"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length !in 8..20 -> "Password must be between 8 and 20 characters"
            else -> null
        }
    }

    private fun validateConfirmPassword(
        password: String,
        confirmPassword: String
    ): String? {
        return when {
            confirmPassword.isBlank() -> "Confirm password is required"
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }
    }
}