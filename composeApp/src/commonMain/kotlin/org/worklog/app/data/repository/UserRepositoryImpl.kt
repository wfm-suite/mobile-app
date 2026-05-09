package org.worklog.app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.data.mapper.toDomain
import org.worklog.app.data.mapper.toDomainModel
import org.worklog.app.data.mapper.toUpdateProfileRequest
import org.worklog.app.data.source.local.PreferenceDataSource
import org.worklog.app.data.source.remote.RemoteDataSource
import org.worklog.app.data.util.handleApiResponse
import org.worklog.app.data.util.handleNullableApiResponse
import org.worklog.app.data.util.handleSuccessResponse
import org.worklog.app.domain.model.EmployeeInfo
import org.worklog.app.domain.model.LeaveSummary
import org.worklog.app.domain.model.Rota
import org.worklog.app.domain.model.UserInfo
import org.worklog.app.domain.repository.UserRepository

class UserRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val preferenceDataSource: PreferenceDataSource,
) : UserRepository {

    private val _userProfile = MutableStateFlow<ResultWrapper<UserInfo>>(ResultWrapper.Loading)
    override val userProfile: Flow<ResultWrapper<UserInfo>> = _userProfile

    override suspend fun loadUserProfile(): ResultWrapper<UserInfo> {
        return handleApiResponse(
            call = { remoteDataSource.loadUserProfile() },
            mapper = { it.user.toDomainModel() }
        ).also { result ->
            _userProfile.value = result
        }
    }

    override suspend fun updateUserProfile(userInfo: UserInfo): ResultWrapper<UserInfo> {
        return handleApiResponse(
            call = {
                remoteDataSource.updateUserProfile(
                    userProfileRequest = userInfo.toUpdateProfileRequest()
                )
            },
            mapper = { it.user.toDomainModel() }
        ).also {
            loadUserProfile()
        }
    }

    override suspend fun uploadProfileImage(imageBytes: ByteArray): ResultWrapper<String> {
        return handleSuccessResponse(
            call = { remoteDataSource.uploadProfileImage(imageBytes) },
        ).also {
            loadUserProfile()
        }
    }

    override suspend fun login(
        username: String,
        password: String
    ): ResultWrapper<UserInfo> {
        return handleApiResponse(
            call = {
                val result = remoteDataSource.login(username, password)
                if (result is ResultWrapper.Success) {
                    result.data.data?.token?.let { preferenceDataSource.saveAuthToken(it) }
                }
                result
            },
            mapper = { it.user.toDomainModel() }
        )
    }

    override suspend fun forgotPassword(email: String): ResultWrapper<String> {
        return handleSuccessResponse {
            remoteDataSource.forgotPassword(email)
        }
    }

    override suspend fun resetPassword(
        email: String,
        token: String,
        password: String,
        confirmPassword: String
    ): ResultWrapper<String> {
        return handleSuccessResponse {
            remoteDataSource.resetPassword(email, token, password, confirmPassword)
        }
    }

    override suspend fun logout(): ResultWrapper<Unit> {
        preferenceDataSource.saveAuthToken("")
        _userProfile.value = ResultWrapper.Loading
        return ResultWrapper.Success(Unit)
    }

    override suspend fun getAuthUserMonthlyRota(): ResultWrapper<List<Rota>> {
        val user = (_userProfile.value as? ResultWrapper.Success)?.data
        return handleApiResponse(
            call = { remoteDataSource.getAuthUserMonthlyRota() },
            mapper = { it.rotas.map { it.toDomainModel(user?.designation ?: "Your Designation") } }
        )
    }

    override suspend fun getCurrentRota(): ResultWrapper<Rota?> {
        val user = (_userProfile.value as? ResultWrapper.Success)?.data
        return handleNullableApiResponse(
            call = { remoteDataSource.getAuthUserCurrentRota() },
            mapper = { it.currentRota?.toDomainModel(user?.designation ?: "Your Designation") }
        )
    }

    override suspend fun getUpcomingRotas(): ResultWrapper<List<Rota>> {
        val user = (_userProfile.value as? ResultWrapper.Success)?.data
        return handleApiResponse(
            call = { remoteDataSource.getAuthUserUpcomingRota() },
            mapper = {
                it.upcomingRotas?.map {
                    it.toDomainModel(
                        user?.designation ?: "Your Designation"
                    )
                } ?: emptyList()
            }
        )
    }

    override suspend fun getLeaveSummary(): ResultWrapper<LeaveSummary> {
        return handleApiResponse(
            call = { remoteDataSource.getLeaveDetails() },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun requestHoliday(
        reason: String,
        dates: List<String>
    ): ResultWrapper<String> {
        return when (val result = remoteDataSource.requestHoliday(reason, dates)) {
            is ResultWrapper.Success -> ResultWrapper.Success(result.data.message)
            is ResultWrapper.Error -> result
            is ResultWrapper.Loading -> result
        }
    }

    override suspend fun startShift(
        employeeId: String,
        latitude: String,
        longitude: String
    ): ResultWrapper<String> {
        return handleSuccessResponse(
            call = { remoteDataSource.startShift(employeeId, latitude, longitude) },
        )
    }

    override suspend fun endShift(
        employeeId: String,
        latitude: String,
        longitude: String
    ): ResultWrapper<String> {
        return handleSuccessResponse(
            call = { remoteDataSource.endShift(employeeId, latitude, longitude) },
        )
    }


    override suspend fun getAllEmployees(): ResultWrapper<List<EmployeeInfo>> {
        return handleApiResponse(
            call = remoteDataSource::getAllEmployees,
            mapper = { response ->
                response.employees.map { it.toDomain() }
            }
        )
    }
}