package org.worklog.app.domain.usecase.rota

import kotlinx.coroutines.flow.Flow
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.repository.PreferenceRepository
import org.worklog.app.domain.repository.UserRepository

class ToggleShiftUseCase(
    private val userRepository: UserRepository,
    private val preferenceRepository: PreferenceRepository
) {

    suspend fun startShift(
        employeeId: String,
        latitude: String,
        longitude: String
    ): ResultWrapper<String> {
        return userRepository.startShift(
            employeeId = employeeId,
            latitude = latitude,
            longitude = longitude
        )
    }

    suspend fun endShift(
        employeeId: String,
        latitude: String,
        longitude: String
    ): ResultWrapper<String> {
        return userRepository.endShift(
            employeeId = employeeId,
            latitude = latitude,
            longitude = longitude
        )
    }

    fun observeCurrentShiftStatus(id: String): Flow<Boolean> {
        return preferenceRepository.observeCurrentShiftStatus(id)
    }

    suspend fun updateCurrentShiftStatus(id: String, isShifting: Boolean) {
        preferenceRepository.updateCurrentShiftStatus(id, isShifting)
    }
}