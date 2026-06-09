package org.worklog.app.data.repository

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.data.mapper.toDomainModel
import org.worklog.app.data.mapper.toEmployeeRota
import org.worklog.app.data.source.remote.RemoteDataSource
import org.worklog.app.data.util.handleApiResponse
import org.worklog.app.data.util.handleSuccessResponse
import org.worklog.app.domain.model.EmployeeRota
import org.worklog.app.domain.model.IncomingSwap
import org.worklog.app.domain.model.MyHandover
import org.worklog.app.domain.model.Rota
import org.worklog.app.domain.repository.RotaRepository

class RotaRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
) : RotaRepository {

    private val rotaByUserIdCache = mutableMapOf<Int, List<Rota>>()
    private var allUsersWeeklyRotaCache: List<EmployeeRota>? = null
    private var allUsersMonthlyRotaCache: List<EmployeeRota>? = null
    private val allUsersMonthlyByYearCache = mutableMapOf<Pair<Int, Int>, List<EmployeeRota>>()
    private var upcomingRotasExceptAuthUserCache: List<EmployeeRota>? = null
    private var upcomingOpenRotaCache: List<Rota>? = null
    private val authUserLastNDaysRotaCache = mutableMapOf<Int, List<Rota>>()
    private var myHandoversCache: List<MyHandover>? = null
    private var incomingSwapsCache: List<IncomingSwap>? = null

    private fun invalidateAll() {
        rotaByUserIdCache.clear()
        allUsersWeeklyRotaCache = null
        allUsersMonthlyRotaCache = null
        allUsersMonthlyByYearCache.clear()
        upcomingRotasExceptAuthUserCache = null
        upcomingOpenRotaCache = null
        authUserLastNDaysRotaCache.clear()
        myHandoversCache = null
        incomingSwapsCache = null
    }

    override suspend fun getRotaByUserId(userId: Int, forceRefresh: Boolean): ResultWrapper<List<Rota>> {
        if (!forceRefresh) rotaByUserIdCache[userId]?.let { return ResultWrapper.Success(it) }
        return handleApiResponse(
            call = { remoteDataSource.getRotaByUserId(userId) },
            mapper = { response ->
                response.rotas.map { it.toDomainModel("User Designation") }
            }
        ).also { if (it is ResultWrapper.Success) rotaByUserIdCache[userId] = it.data }
    }

    override suspend fun getAllUsersWeeklyRota(forceRefresh: Boolean): ResultWrapper<List<EmployeeRota>> {
        if (!forceRefresh) allUsersWeeklyRotaCache?.let { return ResultWrapper.Success(it) }
        return handleApiResponse(
            call = remoteDataSource::getAllUsersWeeklyRota,
            mapper = { response ->
                response.rotas.flatMap { employeeRota ->
                    val emp = employeeRota.employee ?: return@flatMap emptyList<EmployeeRota>()
                    employeeRota.rotas.map { rota ->
                        rota.toEmployeeRota(emp)
                    }
                }
            }
        ).also { if (it is ResultWrapper.Success) allUsersWeeklyRotaCache = it.data }
    }

    override suspend fun getAllUsersMonthlyRota(forceRefresh: Boolean): ResultWrapper<List<EmployeeRota>> {
        if (!forceRefresh) allUsersMonthlyRotaCache?.let { return ResultWrapper.Success(it) }
        return handleApiResponse(
            call = remoteDataSource::getAllUsersMonthlyRota,
            mapper = { response ->
                response.rotas.flatMap { employeeRota ->
                    val emp = employeeRota.employee ?: return@flatMap emptyList<EmployeeRota>()
                    employeeRota.rotas.map { rota ->
                        rota.toEmployeeRota(emp)
                    }
                }
            }
        ).also { if (it is ResultWrapper.Success) allUsersMonthlyRotaCache = it.data }
    }

    override suspend fun getAllUsersMonthlyRotaByMonthYear(
        month: Int,
        year: Int,
        forceRefresh: Boolean
    ): ResultWrapper<List<EmployeeRota>> {
        val key = month to year
        if (!forceRefresh) allUsersMonthlyByYearCache[key]?.let { return ResultWrapper.Success(it) }
        return handleApiResponse(
            call = { remoteDataSource.getAllUsersMonthlyRotaByMonthYear(month, year) },
            mapper = { response ->
                response.rotas.flatMap { employeeRota ->
                    val emp = employeeRota.employee ?: return@flatMap emptyList<EmployeeRota>()
                    employeeRota.rotas.map { rota ->
                        rota.toEmployeeRota(emp)
                    }
                }
            }
        ).also { if (it is ResultWrapper.Success) allUsersMonthlyByYearCache[key] = it.data }
    }

    override suspend fun getUpcomingRotasExceptAuthUser(forceRefresh: Boolean): ResultWrapper<List<EmployeeRota>> {
        if (!forceRefresh) upcomingRotasExceptAuthUserCache?.let { return ResultWrapper.Success(it) }
        return handleApiResponse(
            call = remoteDataSource::getUpcomingRotasExceptAuthUser,
            mapper = { response ->
                response.upcomingRotas?.flatMap { employeeRota ->
                    val emp = employeeRota.employee ?: return@flatMap emptyList<EmployeeRota>()
                    employeeRota.rotas.map { rota ->
                        rota.toEmployeeRota(emp)
                    }
                } ?: emptyList()
            }
        ).also { if (it is ResultWrapper.Success) upcomingRotasExceptAuthUserCache = it.data }
    }

    override suspend fun rotaSwapRequest(
        myRotaId: Int,
        requestedRotaId: Int
    ): ResultWrapper<String> {
        return handleSuccessResponse(
            call = {
                remoteDataSource.rotaSwapRequest(
                    myRotaId = myRotaId,
                    requestedRotaId = requestedRotaId
                )
            }
        ).also { if (it is ResultWrapper.Success) invalidateAll() }
    }

    override suspend fun getIncomingSwaps(forceRefresh: Boolean): ResultWrapper<List<IncomingSwap>> {
        if (!forceRefresh) incomingSwapsCache?.let { return ResultWrapper.Success(it) }
        return handleApiResponse(
            call = remoteDataSource::getIncomingSwaps,
            mapper = { response -> response.swaps.mapNotNull { it.toDomainModel() } }
        ).also { if (it is ResultWrapper.Success) incomingSwapsCache = it.data }
    }

    override suspend fun respondToSwap(swapId: Int, action: String): ResultWrapper<String> {
        return handleSuccessResponse(
            call = { remoteDataSource.respondToSwap(swapId = swapId, action = action) }
        ).also { if (it is ResultWrapper.Success) invalidateAll() }
    }

    override suspend fun cancelSwap(swapId: Int): ResultWrapper<String> {
        return handleSuccessResponse(
            call = { remoteDataSource.cancelSwap(swapId = swapId) }
        ).also { if (it is ResultWrapper.Success) invalidateAll() }
    }

    override suspend fun rotaHanoverRequest(
        rotaId: Int,
    ): ResultWrapper<String> {
        return handleSuccessResponse(
            call = {
                remoteDataSource.rotaHanoverRequest(
                    rotaId = rotaId
                )
            },
        ).also { if (it is ResultWrapper.Success) invalidateAll() }
    }

    override suspend fun cancelHandover(handoverId: Int): ResultWrapper<String> {
        return handleSuccessResponse(
            call = { remoteDataSource.cancelHandover(handoverId = handoverId) }
        ).also { if (it is ResultWrapper.Success) invalidateAll() }
    }

    override suspend fun getMyHandovers(forceRefresh: Boolean): ResultWrapper<List<MyHandover>> {
        if (!forceRefresh) myHandoversCache?.let { return ResultWrapper.Success(it) }
        return handleApiResponse(
            call = remoteDataSource::getMyHandovers,
            mapper = { response ->
                response.handovers.mapNotNull { it.toDomainModel() }
            }
        ).also { if (it is ResultWrapper.Success) myHandoversCache = it.data }
    }

    override suspend fun getUpcomingOpenRota(forceRefresh: Boolean): ResultWrapper<List<Rota>> {
        if (!forceRefresh) upcomingOpenRotaCache?.let { return ResultWrapper.Success(it) }
        return handleApiResponse(
            call = remoteDataSource::getUpcomingOpenRota,
            mapper = { response ->
                response.rotas.map { it.toDomainModel("Open Rota") }
            }
        ).also { if (it is ResultWrapper.Success) upcomingOpenRotaCache = it.data }
    }

    override suspend fun getAuthUserRotaLastNDays(days: Int, forceRefresh: Boolean): ResultWrapper<List<Rota>> {
        if (!forceRefresh) authUserLastNDaysRotaCache[days]?.let { return ResultWrapper.Success(it) }
        return handleApiResponse(
            call = { remoteDataSource.getAuthUserRotaLastNDays(days) },
            mapper = { response ->
                response.rotas.map { it.toDomainModel("Your Designation") }
            }
        ).also { if (it is ResultWrapper.Success) authUserLastNDaysRotaCache[days] = it.data }
    }
}
