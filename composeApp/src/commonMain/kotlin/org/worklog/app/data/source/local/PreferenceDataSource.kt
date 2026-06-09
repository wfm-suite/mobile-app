package org.worklog.app.data.source.local

import kotlinx.coroutines.flow.Flow

interface PreferenceDataSource {
    fun isFirstLaunch(): Flow<Boolean>
    suspend fun updateFirstLaunch()
    fun getAuthToken(): Flow<String>
    suspend fun saveAuthToken(token: String)
    fun getRefreshToken(): Flow<String>
    suspend fun saveRefreshToken(token: String)

    fun observeCurrentShiftStatus(id: String): Flow<Boolean>
    suspend fun updateCurrentShiftStatus(id: String, isShifting: Boolean)
}