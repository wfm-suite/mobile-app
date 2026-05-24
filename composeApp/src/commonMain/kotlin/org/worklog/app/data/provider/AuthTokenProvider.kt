package org.worklog.app.data.provider

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.worklog.app.domain.repository.PreferenceRepository

class AuthTokenProvider(
    preferenceRepository: PreferenceRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _tokenState = MutableStateFlow("")

    init {
        scope.launch {
            preferenceRepository.getAuthToken().collect { _tokenState.value = it }
        }
    }

    fun getTokenOrEmpty(): String = _tokenState.value

    // Immediately update in-memory token so subsequent requests use it without
    // waiting for the DataStore → Flow → StateFlow propagation cycle.
    fun setToken(token: String) { _tokenState.value = token }

    fun clearToken() { _tokenState.value = "" }
}
