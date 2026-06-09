package org.worklog.app.domain.repository

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.model.AppNotification

interface NotificationRepository {
    suspend fun getNotifications(): ResultWrapper<List<AppNotification>>
    suspend fun getUnreadCount(): ResultWrapper<Int>
    suspend fun markRead(id: Int): ResultWrapper<String>
    suspend fun markAllRead(): ResultWrapper<String>
    suspend fun deleteNotification(id: Int): ResultWrapper<String>
    suspend fun saveDeviceToken(token: String): ResultWrapper<String>
}
