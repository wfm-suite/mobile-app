package org.worklog.app.domain.usecase.user

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.repository.UserRepository

class PasswordResetUseCase(
    private val userRepository: UserRepository
) {
    suspend fun resetPassword(
        email: String,
        token: String,
        password: String,
        confirmPassword: String
    ): ResultWrapper<String> {
        return userRepository.resetPassword(email,token, password, confirmPassword)
    }

    suspend fun sendEmail(email: String): ResultWrapper<String> {
        return userRepository.forgotPassword(email)
    }
}