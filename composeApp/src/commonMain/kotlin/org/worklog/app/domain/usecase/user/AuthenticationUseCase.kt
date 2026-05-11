package org.worklog.app.domain.usecase.user

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.model.UserInfo
import org.worklog.app.domain.repository.UserRepository

class AuthenticationUseCase(
    private val userRepository: UserRepository
) {
    suspend fun sendOtp(phone: String): ResultWrapper<String> {
        return userRepository.sendOtp(phone)
    }

    suspend fun verifyOtp(phone: String, otp: String): ResultWrapper<UserInfo> {
        return userRepository.verifyOtp(phone, otp)
    }

    suspend fun logout(): ResultWrapper<Unit> {
        return userRepository.logout()
    }

    // -- email login (commented out, restore if needed) --
    // suspend fun login(username: String, password: String): ResultWrapper<UserInfo> {
    //     return userRepository.login(username, password)
    // }
}
