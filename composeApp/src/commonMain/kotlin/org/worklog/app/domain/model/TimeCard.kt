package org.worklog.app.domain.model

data class TimeCard(
    val type: String,
    val month: Int,
    val year: Int,
    val monthName: String,
    val periodStart: String,
    val periodEnd: String,
    val totalWorkDays: Int,
    val totalWorkedHours: String,
    val days: List<TimeCardDay>
)

data class TimeCardDay(
    val date: String,
    val checkIn: String?,
    val checkOut: String?,
    val workedHours: String,
    val breakCount: Int,
    val breakMinutes: Int
)

data class MonthYear(
    val month: Int,
    val year: Int,
    val displayName: String
)
