package org.worklog.app.domain.usecase.rota

import org.worklog.app.domain.repository.UserRepository

class GetAuthUserRotaUseCase(
    private val repository: UserRepository
) {
    suspend fun getMonthlyRota() = repository.getAuthUserMonthlyRota()
}