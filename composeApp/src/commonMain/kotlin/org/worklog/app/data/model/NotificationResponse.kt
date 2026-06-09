package org.worklog.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppNotificationDto(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("body") val body: String,
    @SerialName("type") val type: String = "general",
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("read_at") val readAt: String? = null,
    @SerialName("created_at") val createdAt: String = ""
)

@Serializable
data class NotificationsResponse(
    @SerialName("notifications") val notifications: List<AppNotificationDto>,
    @SerialName("unread_count") val unreadCount: Int
)

@Serializable
data class UnreadCountResponse(
    @SerialName("unread_count") val unreadCount: Int
)
