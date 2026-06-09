package org.worklog.app.presentation.screen.notification

import org.worklog.app.domain.model.AppNotification

data class NotificationUiState(
    val isLoading: Boolean = false,
    val notifications: List<AppNotification> = emptyList(),
    val unreadCount: Int = 0,
    val errorMessage: String? = null
)
