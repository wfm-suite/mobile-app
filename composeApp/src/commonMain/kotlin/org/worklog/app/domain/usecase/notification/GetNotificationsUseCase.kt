package org.worklog.app.domain.usecase.notification

import org.worklog.app.domain.repository.NotificationRepository

class GetNotificationsUseCase(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke() = repository.getNotifications()
}
