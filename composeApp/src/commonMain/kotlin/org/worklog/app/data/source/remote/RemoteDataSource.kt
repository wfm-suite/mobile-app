package org.worklog.app.data.source.remote

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.data.model.BaseResponse
import org.worklog.app.data.model.CurrentRotaResponse
import org.worklog.app.data.model.EmployeeListResponse
import org.worklog.app.data.model.LeaveResponse
import org.worklog.app.data.model.LoginResponse
import org.worklog.app.data.model.MonthlyRotaResponse
import org.worklog.app.data.model.RotaResponse
import org.worklog.app.data.model.UpcomingEmployeeRotaResponse
import org.worklog.app.data.model.UpcomingRotaResponse
import org.worklog.app.data.model.UpdateProfileRequest
import org.worklog.app.data.model.WeeklyRotaResponse

interface RemoteDataSource {

    suspend fun login(email: String, password: String): ResultWrapper<BaseResponse<LoginResponse>>
    suspend fun forgotPassword(email: String): ResultWrapper<BaseResponse<Unit>>
    suspend fun resetPassword(
        email: String,
        token: String,
        password: String,
        confirmPassword: String
    ): ResultWrapper<BaseResponse<Unit>>

    suspend fun loadUserProfile(): ResultWrapper<BaseResponse<LoginResponse>>
    suspend fun updateUserProfile(
        userProfileRequest: UpdateProfileRequest
    ): ResultWrapper<BaseResponse<LoginResponse>>

    suspend fun uploadProfileImage(
        imageBytes: ByteArray
    ): ResultWrapper<BaseResponse<Unit>>

    suspend fun getAuthUserUpcomingRota(): ResultWrapper<BaseResponse<UpcomingRotaResponse>>
    suspend fun getAuthUserCurrentRota(): ResultWrapper<BaseResponse<CurrentRotaResponse>>
    suspend fun getAuthUserMonthlyRota(): ResultWrapper<BaseResponse<RotaResponse>>
    suspend fun getRotaByUserId(userId: Int): ResultWrapper<BaseResponse<RotaResponse>>
    suspend fun getAllUsersWeeklyRota(): ResultWrapper<BaseResponse<WeeklyRotaResponse>>
    suspend fun getAllUsersMonthlyRota(): ResultWrapper<BaseResponse<MonthlyRotaResponse>>
    suspend fun getUpcomingRotasExceptAuthUser(): ResultWrapper<BaseResponse<UpcomingEmployeeRotaResponse>>
    suspend fun getLeaveDetails(): ResultWrapper<BaseResponse<LeaveResponse>>
    suspend fun requestHoliday(
        reason: String,
        dates: List<String>
    ): ResultWrapper<BaseResponse<String>>

    suspend fun startShift(
        employeeId: String,
        latitude: String,
        longitude: String
    ): ResultWrapper<BaseResponse<Unit>>

    suspend fun endShift(
        employeeId: String,
        latitude: String,
        longitude: String
    ): ResultWrapper<BaseResponse<Unit>>

    suspend fun getAllEmployees(): ResultWrapper<BaseResponse<EmployeeListResponse>>

    suspend fun rotaSwapRequest(
        myRotaId: Int,
        requestedRotaId: Int
    ): ResultWrapper<BaseResponse<Unit>>

    suspend fun rotaHanoverRequest(
        rotaId: Int,
    ): ResultWrapper<BaseResponse<Unit>>
}