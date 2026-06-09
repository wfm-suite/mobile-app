package org.worklog.app.domain.repository

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.model.EmployeeRota
import org.worklog.app.domain.model.IncomingSwap
import org.worklog.app.domain.model.MyHandover
import org.worklog.app.domain.model.Rota

interface RotaRepository {
    suspend fun getRotaByUserId(userId: Int, forceRefresh: Boolean = false): ResultWrapper<List<Rota>>
    suspend fun getAllUsersWeeklyRota(forceRefresh: Boolean = false): ResultWrapper<List<EmployeeRota>>
    suspend fun getAllUsersMonthlyRota(forceRefresh: Boolean = false): ResultWrapper<List<EmployeeRota>>
    suspend fun getAllUsersMonthlyRotaByMonthYear(
        month: Int,
        year: Int,
        forceRefresh: Boolean = false
    ): ResultWrapper<List<EmployeeRota>>
    suspend fun getUpcomingRotasExceptAuthUser(forceRefresh: Boolean = false): ResultWrapper<List<EmployeeRota>>

    suspend fun rotaSwapRequest(
        myRotaId: Int,
        requestedRotaId: Int
    ): ResultWrapper<String>

    suspend fun getIncomingSwaps(forceRefresh: Boolean = false): ResultWrapper<List<IncomingSwap>>

    suspend fun respondToSwap(swapId: Int, action: String): ResultWrapper<String>

    suspend fun cancelSwap(swapId: Int): ResultWrapper<String>

    suspend fun rotaHanoverRequest(
        rotaId: Int,
    ): ResultWrapper<String>

    suspend fun getMyHandovers(forceRefresh: Boolean = false): ResultWrapper<List<MyHandover>>

    suspend fun cancelHandover(handoverId: Int): ResultWrapper<String>

    suspend fun getUpcomingOpenRota(forceRefresh: Boolean = false): ResultWrapper<List<Rota>>

    suspend fun getAuthUserRotaLastNDays(days: Int, forceRefresh: Boolean = false): ResultWrapper<List<Rota>>

}