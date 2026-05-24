package org.worklog.app.data.mapper

import org.worklog.app.data.model.TimeCardDayDto
import org.worklog.app.data.model.TimeCardResponse
import org.worklog.app.domain.model.TimeCard
import org.worklog.app.domain.model.TimeCardDay

fun TimeCardResponse.toDomainModel(): TimeCard {
    return TimeCard(
        type = type,
        month = month,
        year = year,
        monthName = monthName,
        periodStart = periodStart,
        periodEnd = periodEnd,
        totalWorkDays = totalWorkDays,
        totalWorkedHours = totalWorkedHours,
        days = days.map { it.toDomainModel() }
    )
}

fun TimeCardDayDto.toDomainModel(): TimeCardDay {
    return TimeCardDay(
        date = date,
        checkIn = checkIn,
        checkOut = checkOut,
        workedHours = workedHours,
        breakCount = breakCount,
        breakMinutes = breakMinutes
    )
}
