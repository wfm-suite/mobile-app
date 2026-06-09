package org.worklog.app.domain.usecase.profile

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.repository.UserRepository

class DeleteTrainingCourseUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: Int): ResultWrapper<String> {
        return userRepository.deleteTrainingCourse(id)
    }
}
