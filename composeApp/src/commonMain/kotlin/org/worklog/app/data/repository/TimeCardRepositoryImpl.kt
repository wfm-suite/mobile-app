package org.worklog.app.data.repository

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.data.mapper.toDomainModel
import org.worklog.app.data.source.remote.RemoteDataSource
import org.worklog.app.data.util.handleApiResponse
import org.worklog.app.domain.model.TimeCard
import org.worklog.app.domain.repository.TimeCardRepository

class TimeCardRepositoryImpl(
    private val remoteDataSource: RemoteDataSource
) : TimeCardRepository {

    private val timeCardCache = mutableMapOf<Pair<Int, Int>, TimeCard>()

    override suspend fun getMonthlyTimeCard(
        month: Int,
        year: Int,
        forceRefresh: Boolean
    ): ResultWrapper<TimeCard> {
        val key = month to year
        if (!forceRefresh) timeCardCache[key]?.let { return ResultWrapper.Success(it) }
        return handleApiResponse(
            call = { remoteDataSource.getMonthlyTimeCard(month, year) },
            mapper = { response -> response.toDomainModel() }
        ).also { if (it is ResultWrapper.Success) timeCardCache[key] = it.data }
    }
}
