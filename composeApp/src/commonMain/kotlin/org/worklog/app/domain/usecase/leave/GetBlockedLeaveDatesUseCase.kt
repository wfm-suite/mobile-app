package org.worklog.app.domain.usecase.leave

import org.worklog.app.domain.repository.UserRepository

class GetBlockedLeaveDatesUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false) =
        repository.getBlockedLeaveDates(forceRefresh)
}
