package org.worklog.app.presentation.screen.password_reset

data class PasswordResetUiState(
    val email: String = "",
    val token: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val emailError: String? = null,
    val tokenError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val message: String? = null,
    val hasEmailSent: Boolean = false,
    val hasPasswordReset: Boolean = false,
)