package org.worklog.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MyHandover(
    val id: Int,
    val status: RotaStatus,
    val requestedAt: String,
    val approvedAt: String,   // raw "yyyy-MM-dd HH:mm:ss" or "" if not yet approved
    val note: String,
    val rotaId: Int,
    val rotaDate: String,     // yyyy-MM-dd
    val shiftStartTime: String,
    val shiftEndTime: String
)
