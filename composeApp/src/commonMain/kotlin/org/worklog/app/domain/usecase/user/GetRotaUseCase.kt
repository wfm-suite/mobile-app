package org.worklog.app.domain.usecase.user

import org.worklog.app.domain.repository.RotaRepository
import org.worklog.app.domain.repository.UserRepository

class GetRotaUseCase(
    private val userRepository: UserRepository,
    private val rotaRepository: RotaRepository
) {
    suspend fun getCurrentRota() = userRepository.getCurrentRota()

    suspend fun getUpcomingRotas() = userRepository.getUpcomingRotas()

    suspend fun getMonthlyRota(month: Int, year: Int) = userRepository.getAuthUserMonthlyRotaByMonthYear(month, year)
    
    suspend fun getUpcomingOpenRota() = rotaRepository.getUpcomingOpenRota()
}