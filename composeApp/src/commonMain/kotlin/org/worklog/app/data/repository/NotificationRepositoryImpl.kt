package org.worklog.app.data.repository

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.data.mapper.toDomainModel
import org.worklog.app.data.source.remote.RemoteDataSource
import org.worklog.app.data.util.handleApiResponse
import org.worklog.app.data.util.handleSuccessResponse
import org.worklog.app.domain.model.AppNotification
import org.worklog.app.domain.repository.NotificationRepository

class NotificationRepositoryImpl(
    private val remoteDataSource: RemoteDataSource
) : NotificationRepository {

    override suspend fun getNotifications(): ResultWrapper<List<AppNotification>> {
        return handleApiResponse(
            call = { remoteDataSource.getNotifications() },
            mapper = { response ->
                response.notifications.map { it.toDomainModel() }
            }
        )
    }

    override suspend fun getUnreadCount(): ResultWrapper<Int> {
        return handleApiResponse(
            call = { remoteDataSource.getUnreadCount() },
            mapper = { it.unreadCount }
        )
    }

    override suspend fun markRead(id: Int): ResultWrapper<String> {
        return handleSuccessResponse { remoteDataSource.markNotificationRead(id) }
    }

    override suspend fun markAllRead(): ResultWrapper<String> {
        return handleSuccessResponse { remoteDataSource.markAllNotificationsRead() }
    }

    override suspend fun deleteNotification(id: Int): ResultWrapper<String> {
        return handleSuccessResponse { remoteDataSource.deleteNotification(id) }
    }

    override suspend fun saveDeviceToken(token: String): ResultWrapper<String> {
        return handleSuccessResponse { remoteDataSource.saveDeviceToken(token) }
    }
}
