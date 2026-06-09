package org.worklog.app.domain.repository

import kotlinx.coroutines.flow.Flow
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.data.model.request.AddressRequest
import org.worklog.app.data.model.request.EmergencyContactRequest
import org.worklog.app.data.model.request.ResignationRequest
import org.worklog.app.data.model.request.TrainingCourseRequest
import org.worklog.app.domain.model.EmployeeInfo
import org.worklog.app.domain.model.LeaveSummary
import org.worklog.app.domain.model.Rota
import org.worklog.app.domain.model.UserInfo

interface UserRepository {
    val userProfile: Flow<ResultWrapper<UserInfo>>
    suspend fun login(username: String, password: String): ResultWrapper<UserInfo>
    suspend fun sendOtp(phone: String): ResultWrapper<String>
    suspend fun resendOtp(phone: String): ResultWrapper<String>
    suspend fun verifyOtp(phone: String, otp: String): ResultWrapper<UserInfo>
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
    suspend fun getAuthUserMonthlyRota(forceRefresh: Boolean = false): ResultWrapper<List<Rota>>
    suspend fun getAuthUserMonthlyRotaByMonthYear(
        month: Int,
        year: Int,
        forceRefresh: Boolean = false
    ): ResultWrapper<List<Rota>>
    suspend fun getAuthUserRotaLastNDays(days: Int, forceRefresh: Boolean = false): ResultWrapper<List<Rota>>
    suspend fun getCurrentRota(forceRefresh: Boolean = false): ResultWrapper<Rota?>
    suspend fun getUpcomingRotas(forceRefresh: Boolean = false): ResultWrapper<List<Rota>>
    suspend fun getLeaveSummary(forceRefresh: Boolean = false): ResultWrapper<LeaveSummary>

    suspend fun getBlockedLeaveDates(forceRefresh: Boolean = false): ResultWrapper<Set<String>>

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


    suspend fun getAllEmployees(forceRefresh: Boolean = false): ResultWrapper<List<EmployeeInfo>>

    // Addresses
    suspend fun addAddress(request: AddressRequest): ResultWrapper<String>
    suspend fun updateAddress(id: Int, request: AddressRequest): ResultWrapper<String>
    suspend fun deleteAddress(id: Int): ResultWrapper<String>

    // Emergency Contacts
    suspend fun addEmergencyContact(request: EmergencyContactRequest): ResultWrapper<String>
    suspend fun updateEmergencyContact(id: Int, request: EmergencyContactRequest): ResultWrapper<String>
    suspend fun deleteEmergencyContact(id: Int): ResultWrapper<String>

    // Training Courses
    suspend fun addTrainingCourse(request: TrainingCourseRequest): ResultWrapper<String>
    suspend fun updateTrainingCourse(id: Int, request: TrainingCourseRequest): ResultWrapper<String>
    suspend fun deleteTrainingCourse(id: Int): ResultWrapper<String>

    // Resignation
    suspend fun submitResignation(request: ResignationRequest): ResultWrapper<String>

    // Change Password
    suspend fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String): ResultWrapper<String>
}