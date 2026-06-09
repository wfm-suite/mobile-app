package org.worklog.app.domain.usecase.notification

import org.worklog.app.domain.repository.NotificationRepository

class DeleteNotificationUseCase(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(id: Int) = repository.deleteNotification(id)
}
