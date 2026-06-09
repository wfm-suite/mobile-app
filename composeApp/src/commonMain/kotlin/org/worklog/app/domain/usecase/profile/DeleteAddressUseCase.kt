package org.worklog.app.domain.usecase.profile

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.repository.UserRepository

class DeleteAddressUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: Int): ResultWrapper<String> {
        return userRepository.deleteAddress(id)
    }
}
