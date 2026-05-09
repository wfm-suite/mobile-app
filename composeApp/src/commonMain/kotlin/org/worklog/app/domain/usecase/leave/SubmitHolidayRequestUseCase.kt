package org.worklog.app.domain.usecase.leave

import org.worklog.app.domain.repository.UserRepository

class SubmitHolidayRequestUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        reason: String,
        dates: List<String>
    ) = userRepository.requestHoliday(reason, dates)
}