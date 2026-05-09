package org.worklog.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Rota(
    val id: Int,
    val fullDate: String, // 2025-12-08
    val date: String, // 1/2/3
    val dayName: String, // MON, TUE, WED, etc
    val shiftStartTime: String,
    val shiftEndTime: String,
    val breakStartTime: String,
    val breakEndTime: String,
    val totalHours: String,
    val location: String,
    val remarks: String,
    val shiftType: String,
    val shiftStatus: String,
    val status: RotaStatus,
    val designation: String,
    val startTimeEnabled: Boolean,
    val floorName: String
)
