package org.worklog.app.domain.usecase.profile

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.data.model.request.AddressRequest
import org.worklog.app.domain.repository.UserRepository

class UpdateAddressUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: Int, request: AddressRequest): ResultWrapper<String> {
        return userRepository.updateAddress(id, request)
    }
}
