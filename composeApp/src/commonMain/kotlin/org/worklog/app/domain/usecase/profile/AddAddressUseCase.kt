package org.worklog.app.domain.usecase.profile

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.data.model.request.AddressRequest
import org.worklog.app.domain.repository.UserRepository

class AddAddressUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(request: AddressRequest): ResultWrapper<String> {
        return userRepository.addAddress(request)
    }
}
