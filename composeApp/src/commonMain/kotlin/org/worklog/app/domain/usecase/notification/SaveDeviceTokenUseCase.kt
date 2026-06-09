package org.worklog.app.domain.usecase.notification

import org.worklog.app.domain.repository.NotificationRepository

class SaveDeviceTokenUseCase(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(token: String) = repository.saveDeviceToken(token)
}
