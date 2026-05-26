package org.worklog.app.domain.usecase.rota

import org.worklog.app.domain.repository.UserRepository

class GetAuthUserRotaUseCase(
    private val repository: UserRepository
) {
    suspend fun getMonthlyRota(forceRefresh: Boolean = false) =
        repository.getAuthUserMonthlyRota(forceRefresh)

    suspend fun getLastNDaysRota(days: Int = 40, forceRefresh: Boolean = false) =
        repository.getAuthUserRotaLastNDays(days, forceRefresh)
}
