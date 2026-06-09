package org.worklog.app.domain.usecase.notification

import org.worklog.app.domain.repository.NotificationRepository

class MarkNotificationReadUseCase(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(id: Int) = repository.markRead(id)
}
