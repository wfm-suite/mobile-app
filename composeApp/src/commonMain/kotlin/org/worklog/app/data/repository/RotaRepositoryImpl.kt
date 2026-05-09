package org.worklog.app.data.repository

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.data.mapper.toDomainModel
import org.worklog.app.data.mapper.toEmployeeRota
import org.worklog.app.data.source.remote.RemoteDataSource
import org.worklog.app.data.util.handleApiResponse
import org.worklog.app.data.util.handleSuccessResponse
import org.worklog.app.domain.model.EmployeeRota
import org.worklog.app.domain.model.Rota
import org.worklog.app.domain.repository.RotaRepository

class RotaRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
) : RotaRepository {

    override suspend fun getRotaByUserId(userId: Int): ResultWrapper<List<Rota>> {
        return handleApiResponse(
            call = { remoteDataSource.getRotaByUserId(userId) },
            mapper = { response ->
                response.rotas.map { it.toDomainModel("User Designation") }
            }
        )
    }

    override suspend fun getAllUsersWeeklyRota(): ResultWrapper<List<EmployeeRota>> {
        return handleApiResponse(
            call = remoteDataSource::getAllUsersWeeklyRota,
            mapper = { response ->
                response.rotas.flatMap { employeeRota ->
                    employeeRota.rotas.map { rota ->
                        rota.toEmployeeRota(employeeRota.employee)
                    }
                }
            }
        )
    }

    override suspend fun getAllUsersMonthlyRota(): ResultWrapper<List<EmployeeRota>> {
        return handleApiResponse(
            call = remoteDataSource::getAllUsersMonthlyRota,
            mapper = { response ->
                response.rotas.flatMap { employeeRota ->
                    employeeRota.rotas.map { rota ->
                        rota.toEmployeeRota(employeeRota.employee)
                    }
                }
            }
        )
    }

    override suspend fun getUpcomingRotasExceptAuthUser(): ResultWrapper<List<EmployeeRota>> {
        return handleApiResponse(
            call = remoteDataSource::getUpcomingRotasExceptAuthUser,
            mapper = { response ->
                response.upcomingRotas?.flatMap { employeeRota ->
                    employeeRota.rotas.map { rota ->
                        rota.toEmployeeRota(employeeRota.employee)
                    }
                } ?: emptyList()
            }
        )
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
        )
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
        )
    }
}