package org.worklog.app.domain.usecase.notification

import org.worklog.app.domain.repository.NotificationRepository

class GetUnreadCountUseCase(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke() = repository.getUnreadCount()
}
