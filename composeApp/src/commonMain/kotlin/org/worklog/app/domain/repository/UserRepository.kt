package org.worklog.app.domain.repository

import kotlinx.coroutines.flow.Flow
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.model.EmployeeInfo
import org.worklog.app.domain.model.LeaveSummary
import org.worklog.app.domain.model.Rota
import org.worklog.app.domain.model.UserInfo

interface UserRepository {
    val userProfile: Flow<ResultWrapper<UserInfo>>
    suspend fun login(username: String, password: String): ResultWrapper<UserInfo>
    suspend fun forgotPassword(email: String): ResultWrapper<String>
    suspend fun resetPassword(
        email: String,
        token: String,
        password: String,
        confirmPassword: String
    ): ResultWrapper<String>

    suspend fun logout(): ResultWrapper<Unit>
    suspend fun loadUserProfile(): ResultWrapper<UserInfo>
    suspend fun updateUserProfile(userInfo: UserInfo): ResultWrapper<UserInfo>
    suspend fun uploadProfileImage(imageBytes: ByteArray): ResultWrapper<String>
    suspend fun getAuthUserMonthlyRota(): ResultWrapper<List<Rota>>
    suspend fun getCurrentRota(): ResultWrapper<Rota?>
    suspend fun getUpcomingRotas(): ResultWrapper<List<Rota>>
    suspend fun getLeaveSummary(): ResultWrapper<LeaveSummary>

    suspend fun requestHoliday(
        reason: String,
        dates: List<String>
    ): ResultWrapper<String>

    suspend fun startShift(
        employeeId: String,
        latitude: String,
        longitude: String
    ): ResultWrapper<String>

    suspend fun endShift(
        employeeId: String,
        latitude: String,
        longitude: String
    ): ResultWrapper<String>


    suspend fun getAllEmployees(): ResultWrapper<List<EmployeeInfo>>
}