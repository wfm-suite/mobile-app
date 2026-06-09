package org.worklog.app.data.mapper

import org.worklog.app.data.model.AppNotificationDto
import org.worklog.app.domain.model.AppNotification

fun AppNotificationDto.toDomainModel() = AppNotification(
    id = id,
    title = title,
    body = body,
    type = type,
    isRead = isRead,
    readAt = readAt,
    createdAt = createdAt
)
