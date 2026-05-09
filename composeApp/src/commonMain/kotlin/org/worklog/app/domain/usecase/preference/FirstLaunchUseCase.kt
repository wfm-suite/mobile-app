package org.worklog.app.domain.usecase.preference

import kotlinx.coroutines.flow.Flow
import org.worklog.app.domain.repository.PreferenceRepository

class FirstLaunchUseCase(
    private val preferenceRepository: PreferenceRepository
) {
    fun isFirstLaunch(): Flow<Boolean> {
        return preferenceRepository.isFirstLaunch()
    }
    suspend fun updateFirstLaunch() {
        preferenceRepository.updateFirstLaunch()
    }
}