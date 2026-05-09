package org.worklog.app.domain.usecase.user

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.model.UserInfo
import org.worklog.app.domain.repository.UserRepository

class UpdateUserProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userInfo: UserInfo
    ): ResultWrapper<UserInfo> {
        return userRepository.updateUserProfile(
            userInfo = userInfo
        )
    }
}