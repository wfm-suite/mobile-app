package org.worklog.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    fun isFirstLaunch(): Flow<Boolean>
    suspend fun updateFirstLaunch()

    fun getAuthToken(): Flow<String>
    suspend fun saveAuthToken(token: String)
    fun getRefreshToken(): Flow<String>
    suspend fun saveRefreshToken(token: String)

    fun observeCurrentShiftStatus(id: String): Flow<Boolean>
    suspend fun updateCurrentShiftStatus(id: String, isShifting: Boolean)
}