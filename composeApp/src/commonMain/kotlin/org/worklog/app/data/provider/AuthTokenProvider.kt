package org.worklog.app.data.provider

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.worklog.app.domain.repository.PreferenceRepository

class AuthTokenProvider(
    private val preferenceRepository: PreferenceRepository
) {
    @Volatile
    private var token: String = ""

    init {
        // Load token synchronously on first access to prevent race condition,
        // then keep it updated in the background.
        runBlocking {
            token = preferenceRepository.getAuthToken().first()
        }
        CoroutineScope(Dispatchers.Default).launch {
            preferenceRepository.getAuthToken().collect {
                token = it
            }
        }
    }

    fun getTokenOrEmpty(): String = token
}
