package org.worklog.app.domain.usecase.preference

import org.worklog.app.domain.repository.PreferenceRepository

class AuthTokenUseCase(
    private val repository: PreferenceRepository
) {
    operator fun invoke() = repository.getAuthToken()
    suspend operator fun invoke(token: String) = repository.saveAuthToken(token)
}