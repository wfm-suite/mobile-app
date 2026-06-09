package org.worklog.app.domain.usecase.profile

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.data.model.request.EmergencyContactRequest
import org.worklog.app.domain.repository.UserRepository

class AddEmergencyContactUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(request: EmergencyContactRequest): ResultWrapper<String> {
        return userRepository.addEmergencyContact(request)
    }
}
