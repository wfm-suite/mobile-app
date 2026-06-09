package org.worklog.app.domain.usecase.profile

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.data.model.request.ResignationRequest
import org.worklog.app.domain.repository.UserRepository

class SubmitResignationUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(request: ResignationRequest): ResultWrapper<String> {
        return userRepository.submitResignation(request)
    }
}
