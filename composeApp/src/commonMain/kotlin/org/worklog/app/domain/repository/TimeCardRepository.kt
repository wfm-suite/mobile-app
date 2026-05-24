package org.worklog.app.domain.repository

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.model.TimeCard

interface TimeCardRepository {
    suspend fun getMonthlyTimeCard(month: Int, year: Int): ResultWrapper<TimeCard>
}
