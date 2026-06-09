package org.worklog.app.domain.model

data class AppNotification(
    val id: Int,
    val title: String,
    val body: String,
    val type: String, // "leave", "swap", "shift", "general"
    val isRead: Boolean,
    val readAt: String?,
    val createdAt: String
)
