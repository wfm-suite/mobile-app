package org.worklog.app.domain.repository

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.model.EmployeeRota
import org.worklog.app.domain.model.Rota

interface RotaRepository {
    suspend fun getRotaByUserId(userId: Int): ResultWrapper<List<Rota>>
    suspend fun getAllUsersWeeklyRota(): ResultWrapper<List<EmployeeRota>>
    suspend fun getAllUsersMonthlyRota(): ResultWrapper<List<EmployeeRota>>
    suspend fun getUpcomingRotasExceptAuthUser(): ResultWrapper<List<EmployeeRota>>

    suspend fun rotaSwapRequest(
        myRotaId: Int,
        requestedRotaId: Int
    ): ResultWrapper<String>

    suspend fun rotaHanoverRequest(
        rotaId: Int,
    ): ResultWrapper<String>

}