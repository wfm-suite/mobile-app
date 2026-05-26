package org.worklog.app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.data.mapper.toDomain
import org.worklog.app.data.mapper.toDomainModel
import org.worklog.app.data.mapper.toUpdateProfileRequest
import org.worklog.app.data.provider.AuthTokenProvider
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
    private val authTokenProvider: AuthTokenProvider,
) : UserRepository {

    private val _userProfile = MutableStateFlow<ResultWrapper<UserInfo>>(ResultWrapper.Loading)
    override val userProfile: Flow<ResultWrapper<UserInfo>> = _userProfile

        // In-memory caches. Repositories are Koin singletons, so caches outlive ViewModels
    // and survive bottom-nav tab switches. Mutations (shift toggle, logout) invalidate.
    private var currentRotaCache: Rota? = null
    private var currentRotaCacheLoaded: Boolean = false
    private val monthlyRotaCache = mutableMapOf<Pair<Int, Int>, List<Rota>>()
    private val lastNDaysRotaCache = mutableMapOf<Int, List<Rota>>()
    private var authMonthlyRotaCache: List<Rota>? = null
    private var upcomingRotaCache: List<Rota>? = null
    private var leaveSummaryCache: LeaveSummary? = null
    private var allEmployeesCache: List<EmployeeInfo>? = null

    private fun invalidateRotaCaches() {
        currentRotaCache = null
        currentRotaCacheLoaded = false
        monthlyRotaCache.clear()
        lastNDaysRotaCache.clear()
        authMonthlyRotaCache = null
        upcomingRotaCache = null
    }

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
                    val token = result.data.data?.accessToken ?: result.data.data?.token
                    token?.let {
                        authTokenProvider.setToken(it)
                        preferenceDataSource.saveAuthToken(it)
                    }
                }
                result
            },
            mapper = { it.user.toDomainModel() }
        )
    }

    override suspend fun sendOtp(phone: String): ResultWrapper<String> {
        return handleSuccessResponse { remoteDataSource.sendOtp(phone) }
    }

    override suspend fun resendOtp(phone: String): ResultWrapper<String> {
        return handleSuccessResponse { remoteDataSource.resendOtp(phone) }
    }

    override suspend fun verifyOtp(phone: String, otp: String): ResultWrapper<UserInfo> {
        return handleApiResponse(
            call = {
                val result = remoteDataSource.verifyOtp(phone, otp)
                if (result is ResultWrapper.Success) {
                    val token = result.data.data?.accessToken ?: result.data.data?.token
                    token?.let {
                        authTokenProvider.setToken(it)
                        preferenceDataSource.saveAuthToken(it)
                    }
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
        authTokenProvider.clearToken()
        preferenceDataSource.saveAuthToken("")
        _userProfile.value = ResultWrapper.Loading
        invalidateRotaCaches()
        leaveSummaryCache = null
        allEmployeesCache = null
        return ResultWrapper.Success(Unit)
    }

    override suspend fun getAuthUserMonthlyRota(forceRefresh: Boolean): ResultWrapper<List<Rota>> {
        if (!forceRefresh) authMonthlyRotaCache?.let { return ResultWrapper.Success(it) }
        val user = (_userProfile.value as? ResultWrapper.Success)?.data
        return handleApiResponse(
            call = { remoteDataSource.getAuthUserMonthlyRota() },
            mapper = { it.rotas.map { it.toDomainModel(user?.designation ?: "Your Designation") } }
        ).also { if (it is ResultWrapper.Success) authMonthlyRotaCache = it.data }
    }

    override suspend fun getAuthUserMonthlyRotaByMonthYear(
        month: Int,
        year: Int,
        forceRefresh: Boolean
    ): ResultWrapper<List<Rota>> {
        val key = month to year
        if (!forceRefresh) monthlyRotaCache[key]?.let { return ResultWrapper.Success(it) }
        val user = (_userProfile.value as? ResultWrapper.Success)?.data
        return handleApiResponse(
            call = { remoteDataSource.getAuthUserMonthlyRotaByMonthYear(month, year) },
            mapper = { it.rotas.map { it.toDomainModel(user?.designation ?: "Your Designation") } }
        ).also { if (it is ResultWrapper.Success) monthlyRotaCache[key] = it.data }
    }

    override suspend fun getAuthUserRotaLastNDays(days: Int, forceRefresh: Boolean): ResultWrapper<List<Rota>> {
        if (!forceRefresh) lastNDaysRotaCache[days]?.let { return ResultWrapper.Success(it) }
        val user = (_userProfile.value as? ResultWrapper.Success)?.data
        return handleApiResponse(
            call = { remoteDataSource.getAuthUserRotaLastNDays(days) },
            mapper = { it.rotas.map { it.toDomainModel(user?.designation ?: "Your Designation") } }
        ).also { if (it is ResultWrapper.Success) lastNDaysRotaCache[days] = it.data }
    }

    override suspend fun getCurrentRota(forceRefresh: Boolean): ResultWrapper<Rota?> {
        if (!forceRefresh && currentRotaCacheLoaded) {
            return ResultWrapper.Success(currentRotaCache)
        }
        val user = (_userProfile.value as? ResultWrapper.Success)?.data
        return handleNullableApiResponse(
            call = { remoteDataSource.getAuthUserCurrentRota() },
            mapper = { it.currentRota?.toDomainModel(user?.designation ?: "Your Designation") }
        ).also {
            if (it is ResultWrapper.Success) {
                currentRotaCache = it.data
                currentRotaCacheLoaded = true
            }
        }
    }

    override suspend fun getUpcomingRotas(forceRefresh: Boolean): ResultWrapper<List<Rota>> {
        if (!forceRefresh) upcomingRotaCache?.let { return ResultWrapper.Success(it) }
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
        ).also { if (it is ResultWrapper.Success) upcomingRotaCache = it.data }
    }

    override suspend fun getLeaveSummary(forceRefresh: Boolean): ResultWrapper<LeaveSummary> {
        if (!forceRefresh) leaveSummaryCache?.let { return ResultWrapper.Success(it) }
        return handleApiResponse(
            call = { remoteDataSource.getLeaveDetails() },
            mapper = { it.toDomain() }
        ).also { if (it is ResultWrapper.Success) leaveSummaryCache = it.data }
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
        ).also { if (it is ResultWrapper.Success) invalidateRotaCaches() }
    }

    override suspend fun endShift(
        employeeId: String,
        latitude: String,
        longitude: String
    ): ResultWrapper<String> {
        return handleSuccessResponse(
            call = { remoteDataSource.endShift(employeeId, latitude, longitude) },
        ).also { if (it is ResultWrapper.Success) invalidateRotaCaches() }
    }


    override suspend fun getAllEmployees(forceRefresh: Boolean): ResultWrapper<List<EmployeeInfo>> {
        if (!forceRefresh) allEmployeesCache?.let { return ResultWrapper.Success(it) }
        return handleApiResponse(
            call = remoteDataSource::getAllEmployees,
            mapper = { response ->
                response.employees.map { it.toDomain() }
            }
        ).also { if (it is ResultWrapper.Success) allEmployeesCache = it.data }
    }
}