package org.worklog.app.domain.usecase.leave

import org.worklog.app.domain.repository.UserRepository

class GetLeaveDetailsUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false) =
        repository.getLeaveSummary(forceRefresh)
}
