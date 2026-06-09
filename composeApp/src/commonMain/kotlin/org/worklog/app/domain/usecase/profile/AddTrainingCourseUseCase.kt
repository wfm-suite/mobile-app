package org.worklog.app.domain.usecase.profile

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.data.model.request.TrainingCourseRequest
import org.worklog.app.domain.repository.UserRepository

class AddTrainingCourseUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(request: TrainingCourseRequest): ResultWrapper<String> {
        return userRepository.addTrainingCourse(request)
    }
}
