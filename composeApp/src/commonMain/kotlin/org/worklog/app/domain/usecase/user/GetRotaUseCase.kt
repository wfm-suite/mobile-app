package org.worklog.app.domain.usecase.user

import org.worklog.app.domain.repository.UserRepository

class GetRotaUseCase(
    private val userRepository: UserRepository
) {
    suspend fun getCurrentRota() = userRepository.getCurrentRota()

    suspend fun getUpcomingRotas() = userRepository.getUpcomingRotas()
}