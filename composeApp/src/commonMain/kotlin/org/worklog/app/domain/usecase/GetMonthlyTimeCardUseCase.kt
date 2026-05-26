package org.worklog.app.domain.usecase

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.model.TimeCard
import org.worklog.app.domain.repository.TimeCardRepository

class GetMonthlyTimeCardUseCase(
    private val timeCardRepository: TimeCardRepository
) {
    suspend operator fun invoke(month: Int, year: Int, forceRefresh: Boolean = false): ResultWrapper<TimeCard> {
        return timeCardRepository.getMonthlyTimeCard(month, year, forceRefresh)
    }
}
