package org.worklog.app.domain.usecase.user

import org.worklog.app.domain.repository.RotaRepository
import org.worklog.app.domain.repository.UserRepository

class GetRotaUseCase(
    private val userRepository: UserRepository,
    private val rotaRepository: RotaRepository
) {
    suspend fun getCurrentRota(forceRefresh: Boolean = false) =
        userRepository.getCurrentRota(forceRefresh)

    suspend fun getUpcomingRotas(forceRefresh: Boolean = false) =
        userRepository.getUpcomingRotas(forceRefresh)

    suspend fun getMonthlyRota(month: Int, year: Int, forceRefresh: Boolean = false) =
        userRepository.getAuthUserMonthlyRotaByMonthYear(month, year, forceRefresh)

    suspend fun getLastNDaysRota(days: Int, forceRefresh: Boolean = false) =
        userRepository.getAuthUserRotaLastNDays(days, forceRefresh)

    suspend fun getUpcomingOpenRota(forceRefresh: Boolean = false) =
        rotaRepository.getUpcomingOpenRota(forceRefresh)

    suspend fun getMyHandovers(forceRefresh: Boolean = false) =
        rotaRepository.getMyHandovers(forceRefresh)
}
