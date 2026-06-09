package org.worklog.app.presentation.app

import org.worklog.app.presentation.navigation.ScreenRoute

data class AppUiState(
    val isLoading: Boolean = false,
    val showHomeShimmer: Boolean = false,
    val error: String? = null,
    val initialScreen: ScreenRoute? = null,
    val notificationCount: Int = 0
)
