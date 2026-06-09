package org.worklog.app.domain.usecase.profile

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.repository.UserRepository

class ChangePasswordUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): ResultWrapper<String> {
        return userRepository.changePassword(currentPassword, newPassword, confirmPassword)
    }
}
