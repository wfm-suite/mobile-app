package org.worklog.app.data.mapper

import kotlinx.datetime.LocalDate
import org.worklog.app.data.model.EmployeeDto
import org.worklog.app.data.model.RotaDto
import org.worklog.app.domain.model.EmployeeRota
import org.worklog.app.domain.model.Rota
import org.worklog.app.domain.model.RotaStatus

fun RotaDto.toDomainModel(designation: String): Rota {
    val localDate = LocalDate.parse(date)
    val dayName = localDate.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    val dayOnly = localDate.day.toString()
    return Rota(
        id = id,
        fullDate = date,
        date = dayOnly,
        dayName = dayName,
        shiftStartTime = shiftStartTime ?: "",
        shiftEndTime = shiftEndTime ?: "",
        breakStartTime = breakStartTime ?: "",
        breakEndTime = breakEndTime ?: "",
        totalHours = totalHours ?: "",
        location = location ?: "",
        remarks = remarks?.toShortShiftName() ?: "",
        shiftType = shiftType?.toShortShiftName() ?: "",
        shiftStatus = shiftStatus ?: "",
        status = swapStatus.toRotaStatus(),
        designation = designation,
        startTimeEnabled = startTimeEnabled,
        floorName = floorName ?: "",
        shiftLabel = shiftLabel ?: remarks ?: "",
        shortCode = shortCode ?: remarks ?: "",
        isLeave = isLeave
    )
}

fun RotaDto.toEmployeeRota(employee: EmployeeDto): EmployeeRota {
    return EmployeeRota(
        employee = employee.toDomain(),
        rota = this.toDomainModel(employee.designation ?: "")
    )
}

fun String.toShortShiftName(): String {
    return when (this.lowercase()) {
        "regular shift", "day" -> "LD"
        "night shift", "evening" -> "N"
        else -> this
    }
}

fun String?.toRotaStatus(): RotaStatus {
    return when (this?.lowercase()) {
        "1" -> RotaStatus.PENDING
        "2" -> RotaStatus.ACCEPTED
        "3" -> RotaStatus.REJECTED
        else -> RotaStatus.NOTHING
    }
}