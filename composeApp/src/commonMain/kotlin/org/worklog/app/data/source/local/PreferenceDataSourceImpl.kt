package org.worklog.app.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PreferenceDataSourceImpl(
    private val dataStore: DataStore<Preferences>
) : PreferenceDataSource {

    private object Keys {
        val API_KEY = stringPreferencesKey("api_key")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val LANGUAGE = stringPreferencesKey("language")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        val CURRENT_SHIFT_ID = stringPreferencesKey("current_shift_id")
    }

    private object Defaults {
        const val LANGUAGE = "en"
        const val FIRST_LAUNCH = true
        const val CURRENT_SHIFT_ID = ""
    }

    // --- Helpers ---
    private suspend fun <T> read(
        key: Preferences.Key<T>,
        default: T? = null
    ): T? = dataStore.data.first()[key] ?: default

    private fun <T> readFlow(
        key: Preferences.Key<T>,
        default: T
    ): Flow<T> = dataStore.data.map { it[key] ?: default }

    private suspend fun <T> write(
        key: Preferences.Key<T>,
        value: T
    ) {
        dataStore.edit { it[key] = value }
    }

    override fun isFirstLaunch(): Flow<Boolean> {
        return readFlow(Keys.FIRST_LAUNCH, Defaults.FIRST_LAUNCH)
    }

    override suspend fun updateFirstLaunch() {
        write(Keys.FIRST_LAUNCH, false)
    }

    // --- Public API ---

    override fun getAuthToken(): Flow<String> =
        readFlow(Keys.API_KEY, "")

    override suspend fun saveAuthToken(token: String) =
        write(Keys.API_KEY, token)

    override fun getRefreshToken(): Flow<String> =
        readFlow(Keys.REFRESH_TOKEN, "")

    override suspend fun saveRefreshToken(token: String) =
        write(Keys.REFRESH_TOKEN, token)

    override fun observeCurrentShiftStatus(id: String): Flow<Boolean> {
        return readFlow(Keys.CURRENT_SHIFT_ID, Defaults.CURRENT_SHIFT_ID).map {
            it == id
        }
    }

    override suspend fun updateCurrentShiftStatus(id: String, isShifting: Boolean) {
        if (isShifting) {
            write(Keys.CURRENT_SHIFT_ID, id)
        } else {
            write(Keys.CURRENT_SHIFT_ID, Defaults.CURRENT_SHIFT_ID)
        }
    }
}