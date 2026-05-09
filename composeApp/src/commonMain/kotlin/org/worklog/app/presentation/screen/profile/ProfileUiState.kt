package org.worklog.app.presentation.screen.profile

import org.worklog.app.domain.model.UserInfo

data class ProfileUiState(
    val userInfo: UserInfo? = null,
    val isLoading: Boolean = false,
    val message: String? = null,
    val isLoggedOut: Boolean = false
)