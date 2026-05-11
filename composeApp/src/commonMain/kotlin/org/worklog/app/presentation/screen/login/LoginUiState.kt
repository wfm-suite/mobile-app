package org.worklog.app.presentation.screen.login

import org.worklog.app.domain.model.UserInfo

enum class LoginStep { PHONE, OTP }

data class LoginUiState(
    val isLoading: Boolean = false,
    val userInfo: UserInfo? = null,
    val step: LoginStep = LoginStep.PHONE,
    val phone: String = "",
    val otp: String = "",
    val phoneError: String? = null,
    val otpError: String? = null,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    // -- email login (commented out, restore if needed) --
    // val email: String = "",
    // val password: String = "",
    // val emailError: String? = null,
    // val passwordError: String? = null,
)
