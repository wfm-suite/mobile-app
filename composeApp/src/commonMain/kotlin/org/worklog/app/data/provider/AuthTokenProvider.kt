package org.worklog.app.data.provider

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.worklog.app.domain.repository.PreferenceRepository

enum class SessionEvent { ForcedLogout }

class AuthTokenProvider(
    private val preferenceRepository: PreferenceRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _tokenState = MutableStateFlow("")
    private val _refreshState = MutableStateFlow("")
    private val _sessionEvents = MutableSharedFlow<SessionEvent>(extraBufferCapacity = 1)

    val sessionEvents: SharedFlow<SessionEvent> = _sessionEvents.asSharedFlow()

    init {
        runBlocking {
            val token = preferenceRepository.getAuthToken().first()
            val refresh = preferenceRepository.getRefreshToken().first()
            println("AuthTokenProvider: Initializing (blocking) - Access: ${token.take(10)}..., Refresh: ${refresh.take(10)}...")
            _tokenState.value = token
            _refreshState.value = refresh
        }
        
        // Also keep observing changes
        scope.launch {
            preferenceRepository.getAuthToken().collect { _tokenState.value = it }
        }
        scope.launch {
            preferenceRepository.getRefreshToken().collect { _refreshState.value = it }
        }
    }

    fun getTokenOrEmpty(): String = _tokenState.value
    fun getRefreshTokenOrEmpty(): String = _refreshState.value

    // Update in-memory immediately + persist so future cold starts pick it up.
    fun setToken(token: String) {
        _tokenState.value = token
        scope.launch { preferenceRepository.saveAuthToken(token) }
    }

    fun setRefreshToken(token: String) {
        _refreshState.value = token
        scope.launch { preferenceRepository.saveRefreshToken(token) }
    }

    fun setTokens(access: String, refresh: String) {
        _tokenState.value = access
        _refreshState.value = refresh
        scope.launch {
            preferenceRepository.saveAuthToken(access)
            preferenceRepository.saveRefreshToken(refresh)
        }
    }

    fun clearToken() {
        _tokenState.value = ""
        _refreshState.value = ""
        scope.launch {
            preferenceRepository.saveAuthToken("")
            preferenceRepository.saveRefreshToken("")
        }
    }

    // Called by the HTTP interceptor when refresh fails — the UI layer should
    // observe sessionEvents and navigate to Login.
    fun emitForcedLogout() {
        _sessionEvents.tryEmit(SessionEvent.ForcedLogout)
    }
}
