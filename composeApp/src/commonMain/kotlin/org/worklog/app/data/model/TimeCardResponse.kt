package org.worklog.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimeCardResponse(
    @SerialName("type")
    val type: String,
    @SerialName("month")
    val month: Int,
    @SerialName("year")
    val year: Int,
    @SerialName("month_name")
    val monthName: String,
    @SerialName("period_start")
    val periodStart: String,
    @SerialName("period_end")
    val periodEnd: String,
    @SerialName("total_work_days")
    val totalWorkDays: Int,
    @SerialName("total_worked_hours")
    val totalWorkedHours: String,
    @SerialName("days")
    val days: List<TimeCardDayDto>
)

@Serializable
data class TimeCardDayDto(
    @SerialName("date")
    val date: String,
    @SerialName("check_in")
    val checkIn: String?,
    @SerialName("check_out")
    val checkOut: String?,
    @SerialName("worked_hours")
    val workedHours: String,
    @SerialName("break_count")
    val breakCount: Int,
    @SerialName("break_minutes")
    val breakMinutes: Int
)
