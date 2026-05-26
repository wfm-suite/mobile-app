package org.worklog.app.domain.usecase.rota

import org.worklog.app.domain.repository.RotaRepository

class EmployeeRotaUseCase(
    private val repository: RotaRepository
) {
    suspend fun getAllUsersWeeklyRota(forceRefresh: Boolean = false) =
        repository.getAllUsersWeeklyRota(forceRefresh)

    suspend fun getAllUsersMonthlyRota(forceRefresh: Boolean = false) =
        repository.getAllUsersMonthlyRota(forceRefresh)

    suspend fun getAllUsersMonthlyRotaByMonthYear(month: Int, year: Int, forceRefresh: Boolean = false) =
        repository.getAllUsersMonthlyRotaByMonthYear(month, year, forceRefresh)

    suspend fun getUpcomingRotasExceptAuthUser(forceRefresh: Boolean = false) =
        repository.getUpcomingRotasExceptAuthUser(forceRefresh)
}
