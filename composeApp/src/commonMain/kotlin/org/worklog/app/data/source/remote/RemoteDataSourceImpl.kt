package org.worklog.app.data.source.remote

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.data.model.BaseResponse
import org.worklog.app.data.model.CurrentRotaResponse
import org.worklog.app.data.model.EmployeeListResponse
import org.worklog.app.data.model.HolidayRequest
import org.worklog.app.data.model.LeaveResponse
import org.worklog.app.data.model.LoginResponse
import org.worklog.app.data.model.MonthlyRotaResponse
import org.worklog.app.data.model.OpenRotaResponse
import org.worklog.app.data.model.RotaHanoverRequest
import org.worklog.app.data.model.RotaResponse
import org.worklog.app.data.model.TimeCardResponse
import org.worklog.app.data.model.UpcomingEmployeeRotaResponse
import org.worklog.app.data.model.UpcomingRotaResponse
import org.worklog.app.data.model.UpdateProfileRequest
import org.worklog.app.data.model.WeeklyRotaResponse
import kotlin.time.Clock

import org.worklog.app.data.model.ShiftRequest
import org.worklog.app.data.model.SwapRequest

class RemoteDataSourceImpl(
    val apiService: ApiService
) : RemoteDataSource {

    private val baseUrl = "https://mobile-api.gbspares.com/api/app"

    override suspend fun login(
        email: String,
        password: String
    ): ResultWrapper<BaseResponse<LoginResponse>> {
        return apiService.postForm<BaseResponse<LoginResponse>>(
            endpoint = "${baseUrl}/login",
            formData = mapOf("email" to email, "password" to password)
        )
    }

    override suspend fun sendOtp(phone: String): ResultWrapper<BaseResponse<Unit>> {
        return apiService.post<BaseResponse<Unit>>(
            endpoint = "${baseUrl}/auth/otp/send",
            body = mapOf("phone" to phone)
        )
    }

    override suspend fun resendOtp(phone: String): ResultWrapper<BaseResponse<Unit>> {
        return apiService.post<BaseResponse<Unit>>(
            endpoint = "${baseUrl}/auth/otp/resend",
            body = mapOf("phone" to phone)
        )
    }

    override suspend fun verifyOtp(phone: String, otp: String): ResultWrapper<BaseResponse<LoginResponse>> {
        return apiService.post<BaseResponse<LoginResponse>>(
            endpoint = "${baseUrl}/auth/otp/verify",
            body = mapOf("phone" to phone, "otp" to otp)
        )
    }

    override suspend fun forgotPassword(email: String): ResultWrapper<BaseResponse<Unit>> {
        val formData = mapOf(
            "email" to email
        )
        return apiService.postForm<BaseResponse<Unit>>(
            endpoint = "${baseUrl}/forgot-password",
            formData = formData
        )
    }

    override suspend fun resetPassword(
        email: String,
        token: String,
        password: String,
        confirmPassword: String
    ): ResultWrapper<BaseResponse<Unit>> {
        val formData = mapOf(
            "email" to email,
            "token" to token,
            "password" to password,
            "password_confirmation" to confirmPassword
        )
        return apiService.postForm<BaseResponse<Unit>>(
            endpoint = "${baseUrl}/reset-password",
            formData = formData
        )
    }

    override suspend fun loadUserProfile(): ResultWrapper<BaseResponse<LoginResponse>> {
        return apiService.get<BaseResponse<LoginResponse>>(
            endpoint = "${baseUrl}/profile",
        )
    }

    override suspend fun updateUserProfile(userProfileRequest: UpdateProfileRequest): ResultWrapper<BaseResponse<LoginResponse>> {
        return apiService.post<BaseResponse<LoginResponse>>(
            endpoint = "${baseUrl}/profile/update",
            body = userProfileRequest
        )
    }

    override suspend fun uploadProfileImage(imageBytes: ByteArray): ResultWrapper<BaseResponse<Unit>> {
        return apiService.postMultipart<BaseResponse<Unit>>(
            endpoint = "${baseUrl}/profile/upload-profile-picture",
            imageBytes = imageBytes,
            fileName = "${Clock.System.now().toEpochMilliseconds()}_profile_image.jpg"
        )
    }

    override suspend fun getAuthUserUpcomingRota(): ResultWrapper<BaseResponse<UpcomingRotaResponse>> {
        return apiService.get<BaseResponse<UpcomingRotaResponse>>(
            endpoint = "${baseUrl}/rota/upcoming",
        )
    }

    override suspend fun getAuthUserCurrentRota(): ResultWrapper<BaseResponse<CurrentRotaResponse>> {
        return apiService.get<BaseResponse<CurrentRotaResponse>>(
            endpoint = "${baseUrl}/rota/current",
        )
    }

    override suspend fun getAuthUserMonthlyRota(): ResultWrapper<BaseResponse<RotaResponse>> {
        return apiService.get<BaseResponse<RotaResponse>>(
            endpoint = "${baseUrl}/rota/auth-user",
        )
    }

    override suspend fun getAuthUserMonthlyRotaByMonthYear(
        month: Int,
        year: Int
    ): ResultWrapper<BaseResponse<RotaResponse>> {
        val formData = mapOf(
            "mode" to "monthly",
            "month" to month.toString(),
            "year" to year.toString()
        )
        return apiService.postForm<BaseResponse<RotaResponse>>(
            endpoint = "${baseUrl}/rota/month/auth-user",
            formData = formData
        )
    }

    override suspend fun getAuthUserRotaLastNDays(days: Int): ResultWrapper<BaseResponse<RotaResponse>> {
        val formData = mapOf(
            "mode" to "custom",
            "days" to days.toString()
        )
        return apiService.postForm<BaseResponse<RotaResponse>>(
            endpoint = "${baseUrl}/rota/month/auth-user",
            formData = formData
        )
    }

    override suspend fun getRotaByUserId(userId: Int): ResultWrapper<BaseResponse<RotaResponse>> {
        val formData = mapOf(
            "employee_id" to userId
        )
        return apiService.post<BaseResponse<RotaResponse>>(
            endpoint = "${baseUrl}/rota/user",
            body = formData
        )
    }

    override suspend fun getAllUsersWeeklyRota(): ResultWrapper<BaseResponse<WeeklyRotaResponse>> {
        return apiService.get<BaseResponse<WeeklyRotaResponse>>(
            endpoint = "${baseUrl}/rota/week/all"
        )
    }

    override suspend fun getAllUsersMonthlyRota(): ResultWrapper<BaseResponse<MonthlyRotaResponse>> {
        return apiService.get<BaseResponse<MonthlyRotaResponse>>(
            endpoint = "${baseUrl}/rota/month/all"
        )
    }

    override suspend fun getAllUsersMonthlyRotaByMonthYear(
        month: Int,
        year: Int
    ): ResultWrapper<BaseResponse<MonthlyRotaResponse>> {
        return apiService.get<BaseResponse<MonthlyRotaResponse>>(
            endpoint = "${baseUrl}/rota/month/all",
            parameters = mapOf(
                "month" to month.toString(),
                "year" to year.toString()
            )
        )
    }

    override suspend fun getUpcomingRotasExceptAuthUser(): ResultWrapper<BaseResponse<UpcomingEmployeeRotaResponse>> {
        return apiService.get<BaseResponse<UpcomingEmployeeRotaResponse>>(
            endpoint = "${baseUrl}/rota/upcoming-except-auth-user",
        )
    }

    override suspend fun getLeaveDetails(): ResultWrapper<BaseResponse<LeaveResponse>> {
        return apiService.get<BaseResponse<LeaveResponse>>(
            endpoint = "${baseUrl}/leave/details"
        )
    }

    override suspend fun requestHoliday(
        reason: String,
        dates: List<String>
    ): ResultWrapper<BaseResponse<String>> {
        val holidayRequest = HolidayRequest(
            reason = reason,
            dates = dates
        )
        return apiService.post<BaseResponse<String>>(
            endpoint = "${baseUrl}/leave/holiday/request",
            body = holidayRequest
        )
    }

    override suspend fun startShift(
        employeeId: String,
        latitude: String,
        longitude: String
    ): ResultWrapper<BaseResponse<Unit>> {
        val shiftRequest = ShiftRequest(
            employeeId = employeeId,
            latitude = latitude,
            longitude = longitude
        )
        return apiService.post<BaseResponse<Unit>>(
            endpoint = "${baseUrl}/shift/checkin",
            body = shiftRequest
        )
    }

    override suspend fun endShift(
        employeeId: String,
        latitude: String,
        longitude: String
    ): ResultWrapper<BaseResponse<Unit>> {
        val shiftRequest = ShiftRequest(
            employeeId = employeeId,
            latitude = latitude,
            longitude = longitude
        )
        return apiService.post<BaseResponse<Unit>>(
            endpoint = "${baseUrl}/shift/checkout",
            body = shiftRequest
        )
    }

    override suspend fun getAllEmployees(): ResultWrapper<BaseResponse<EmployeeListResponse>> {
        return apiService.get<BaseResponse<EmployeeListResponse>>(
            endpoint = "${baseUrl}/employee-list"
        )
    }

    override suspend fun rotaSwapRequest(
        myRotaId: Int,
        requestedRotaId: Int
    ): ResultWrapper<BaseResponse<Unit>> {
        val swapRequest = SwapRequest(
            myRotaId = myRotaId,
            requestedRotaId = requestedRotaId
        )
        return apiService.post<BaseResponse<Unit>>(
            endpoint = "${baseUrl}/swap/swap-request",
            body = swapRequest
        )
    }

    override suspend fun rotaHanoverRequest(
        rotaId: Int,
    ): ResultWrapper<BaseResponse<Unit>> {
        val hanoverRequest = RotaHanoverRequest(
            rotaId = rotaId
        )
        return apiService.post<BaseResponse<Unit>>(
            endpoint = "${baseUrl}/handover/handover-request",
            body = hanoverRequest
        )
    }

    override suspend fun getUpcomingOpenRota(): ResultWrapper<BaseResponse<OpenRotaResponse>> {
        return apiService.get<BaseResponse<OpenRotaResponse>>(
            endpoint = "${baseUrl}/rota/upcoming-open-rota"
        )
    }

    override suspend fun getMonthlyTimeCard(
        month: Int,
        year: Int
    ): ResultWrapper<BaseResponse<TimeCardResponse>> {
        val formData = mapOf(
            "month" to month.toString(),
            "year" to year.toString()
        )
        return apiService.postForm<BaseResponse<TimeCardResponse>>(
            endpoint = "${baseUrl}/timecard/all",
            formData = formData
        )
    }

}