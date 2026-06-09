package org.worklog.app.data.source.remote

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.data.model.BaseResponse
import org.worklog.app.data.model.CurrentRotaResponse
import org.worklog.app.data.model.EmployeeListResponse
import org.worklog.app.data.model.LeaveResponse
import org.worklog.app.data.model.LoginResponse
import org.worklog.app.data.model.IncomingSwapsResponse
import org.worklog.app.data.model.MyHandoversResponse
import org.worklog.app.data.model.MonthlyRotaResponse
import org.worklog.app.data.model.OpenRotaResponse
import org.worklog.app.data.model.RotaResponse
import org.worklog.app.data.model.TimeCardResponse
import org.worklog.app.data.model.UpcomingEmployeeRotaResponse
import org.worklog.app.data.model.UpcomingRotaResponse
import org.worklog.app.data.model.UpdateProfileRequest
import org.worklog.app.data.model.WeeklyRotaResponse
import org.worklog.app.data.model.request.AddressRequest
import org.worklog.app.data.model.NotificationsResponse
import org.worklog.app.data.model.UnreadCountResponse
import org.worklog.app.data.model.request.ChangePasswordRequest
import org.worklog.app.data.model.request.EmergencyContactRequest
import org.worklog.app.data.model.request.ResignationRequest
import org.worklog.app.data.model.request.TrainingCourseRequest

interface RemoteDataSource {

    suspend fun login(email: String, password: String): ResultWrapper<BaseResponse<LoginResponse>>
    suspend fun refreshToken(refreshToken: String): ResultWrapper<BaseResponse<org.worklog.app.data.model.RefreshTokenResponse>>
    suspend fun sendOtp(phone: String): ResultWrapper<BaseResponse<Unit>>
    suspend fun resendOtp(phone: String): ResultWrapper<BaseResponse<Unit>>
    suspend fun verifyOtp(phone: String, otp: String): ResultWrapper<BaseResponse<LoginResponse>>
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
    suspend fun getAuthUserMonthlyRotaByMonthYear(
        month: Int,
        year: Int
    ): ResultWrapper<BaseResponse<RotaResponse>>
    suspend fun getAuthUserRotaLastNDays(days: Int): ResultWrapper<BaseResponse<RotaResponse>>
    suspend fun getRotaByUserId(userId: Int): ResultWrapper<BaseResponse<RotaResponse>>
    suspend fun getAllUsersWeeklyRota(): ResultWrapper<BaseResponse<WeeklyRotaResponse>>
    suspend fun getAllUsersMonthlyRota(): ResultWrapper<BaseResponse<MonthlyRotaResponse>>
    suspend fun getAllUsersMonthlyRotaByMonthYear(
        month: Int,
        year: Int
    ): ResultWrapper<BaseResponse<MonthlyRotaResponse>>
    suspend fun getUpcomingRotasExceptAuthUser(): ResultWrapper<BaseResponse<UpcomingEmployeeRotaResponse>>
    suspend fun getLeaveDetails(): ResultWrapper<BaseResponse<LeaveResponse>>
    suspend fun getBlockedLeaveDates(): ResultWrapper<BaseResponse<org.worklog.app.data.model.BlockedDatesResponse>>
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

    suspend fun getIncomingSwaps(): ResultWrapper<BaseResponse<IncomingSwapsResponse>>

    suspend fun respondToSwap(
        swapId: Int,
        action: String
    ): ResultWrapper<BaseResponse<Unit>>

    suspend fun cancelSwap(swapId: Int): ResultWrapper<BaseResponse<Unit>>

    suspend fun rotaHanoverRequest(
        rotaId: Int,
    ): ResultWrapper<BaseResponse<Unit>>

    suspend fun getMyHandovers(): ResultWrapper<BaseResponse<MyHandoversResponse>>

    suspend fun cancelHandover(handoverId: Int): ResultWrapper<BaseResponse<Unit>>

    suspend fun getUpcomingOpenRota(): ResultWrapper<BaseResponse<OpenRotaResponse>>

    suspend fun getMonthlyTimeCard(
        month: Int,
        year: Int
    ): ResultWrapper<BaseResponse<TimeCardResponse>>

    // Profile: Addresses
    suspend fun addAddress(request: AddressRequest): ResultWrapper<BaseResponse<Unit>>
    suspend fun updateAddress(id: Int, request: AddressRequest): ResultWrapper<BaseResponse<Unit>>
    suspend fun deleteAddress(id: Int): ResultWrapper<BaseResponse<Unit>>

    // Profile: Emergency Contacts
    suspend fun addEmergencyContact(request: EmergencyContactRequest): ResultWrapper<BaseResponse<Unit>>
    suspend fun updateEmergencyContact(id: Int, request: EmergencyContactRequest): ResultWrapper<BaseResponse<Unit>>
    suspend fun deleteEmergencyContact(id: Int): ResultWrapper<BaseResponse<Unit>>

    // Profile: Training Courses
    suspend fun addTrainingCourse(request: TrainingCourseRequest): ResultWrapper<BaseResponse<Unit>>
    suspend fun updateTrainingCourse(id: Int, request: TrainingCourseRequest): ResultWrapper<BaseResponse<Unit>>
    suspend fun deleteTrainingCourse(id: Int): ResultWrapper<BaseResponse<Unit>>

    // Profile: Resignation
    suspend fun submitResignation(request: ResignationRequest): ResultWrapper<BaseResponse<Unit>>

    // Profile: Change Password
    suspend fun changePassword(request: ChangePasswordRequest): ResultWrapper<BaseResponse<Unit>>

    // Notifications
    suspend fun getNotifications(): ResultWrapper<BaseResponse<NotificationsResponse>>
    suspend fun getUnreadCount(): ResultWrapper<BaseResponse<UnreadCountResponse>>
    suspend fun markNotificationRead(id: Int): ResultWrapper<BaseResponse<Unit>>
    suspend fun markAllNotificationsRead(): ResultWrapper<BaseResponse<Unit>>
    suspend fun deleteNotification(id: Int): ResultWrapper<BaseResponse<Unit>>
    suspend fun saveDeviceToken(token: String): ResultWrapper<BaseResponse<Unit>>
}