package org.worklog.app.data.repository

import kotlinx.coroutines.flow.Flow
import org.worklog.app.data.source.local.PreferenceDataSource
import org.worklog.app.domain.repository.PreferenceRepository

class PreferenceRepositoryImpl(
    private val dataSource: PreferenceDataSource
) : PreferenceRepository {

    override fun isFirstLaunch(): Flow<Boolean> {
        return dataSource.isFirstLaunch()
    }

    override suspend fun updateFirstLaunch() {
        dataSource.updateFirstLaunch()
    }

    override fun getAuthToken(): Flow<String> =
        dataSource.getAuthToken()

    override suspend fun saveAuthToken(token: String) =
        dataSource.saveAuthToken(token)

    override fun observeCurrentShiftStatus(id: String): Flow<Boolean> {
        return dataSource.observeCurrentShiftStatus(id)
    }

    override suspend fun updateCurrentShiftStatus(id: String, isShifting: Boolean) {
        dataSource.updateCurrentShiftStatus(id, isShifting)
    }
}