package org.worklog.app.domain.usecase.user

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.model.UserInfo
import org.worklog.app.domain.repository.UserRepository

class UploadProfileImageUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        imageBytes: ByteArray
    ): ResultWrapper<String> {
        return userRepository.uploadProfileImage(
            imageBytes = imageBytes
        )
    }
}