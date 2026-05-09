package org.worklog.app.presentation.screen.login

import org.worklog.app.domain.model.UserInfo

data class LoginUiState(
    val isLoading: Boolean = false,
    val userInfo: UserInfo? = null,
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)
