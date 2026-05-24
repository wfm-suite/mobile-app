package org.worklog.app.presentation.screen.login

import org.worklog.app.domain.model.UserInfo

enum class LoginMethod { PHONE, EMAIL }

data class LoginUiState(
    val isLoading: Boolean = false,
    val userInfo: UserInfo? = null,
    val loginMethod: LoginMethod = LoginMethod.PHONE,
    // Phone OTP
    val phone: String = "",
    val otp: String = "",
    val phoneError: String? = null,
    val otpError: String? = null,
    val otpSent: Boolean = false,
    val resendCountdown: Int = 0,
    // Email password
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val error: String? = null
)
