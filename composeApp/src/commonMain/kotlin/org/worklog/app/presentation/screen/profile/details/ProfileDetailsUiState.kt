package org.worklog.app.presentation.screen.profile.details

import org.worklog.app.domain.model.UserInfo

data class ProfileDetailsUiState(
    val userInfo: UserInfo? = null,
    val isLoading: Boolean = false,
    val message: String? = null,
)