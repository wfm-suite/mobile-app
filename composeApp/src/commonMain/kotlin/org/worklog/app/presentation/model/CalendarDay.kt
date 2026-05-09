package org.worklog.app.presentation.model

data class CalendarDay(
    val fullDate: String,        // yyyy-MM-dd
    val dayNumber: String,       // 1..31
    val dayName: String? = null, // MON, TUE (weekly only)
    val shift: String,           // remarks / OFF
    val isCurrentMonth: Boolean = true
)
