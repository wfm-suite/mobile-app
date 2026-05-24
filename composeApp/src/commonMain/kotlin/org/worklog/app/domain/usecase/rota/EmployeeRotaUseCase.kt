package org.worklog.app.domain.usecase.rota

import org.worklog.app.domain.repository.RotaRepository

class EmployeeRotaUseCase(
    private val repository: RotaRepository
) {
    suspend fun getAllUsersWeeklyRota() = repository.getAllUsersWeeklyRota()
    suspend fun getAllUsersMonthlyRota() = repository.getAllUsersMonthlyRota()
    suspend fun getAllUsersMonthlyRotaByMonthYear(month: Int, year: Int) = 
        repository.getAllUsersMonthlyRotaByMonthYear(month, year)

    suspend fun getUpcomingRotasExceptAuthUser() = repository.getUpcomingRotasExceptAuthUser()
}