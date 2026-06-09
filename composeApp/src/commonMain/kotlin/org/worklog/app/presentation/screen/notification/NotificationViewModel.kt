package org.worklog.app.presentation.screen.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.worklog.app.core.notification.RefreshEvents
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.usecase.notification.DeleteNotificationUseCase
import org.worklog.app.domain.usecase.notification.GetNotificationsUseCase
import org.worklog.app.domain.usecase.notification.GetUnreadCountUseCase
import org.worklog.app.domain.usecase.notification.MarkAllReadUseCase
import org.worklog.app.domain.usecase.notification.MarkNotificationReadUseCase

class NotificationViewModel(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markReadUseCase: MarkNotificationReadUseCase,
    private val markAllReadUseCase: MarkAllReadUseCase,
    private val deleteUseCase: DeleteNotificationUseCase,
    private val getUnreadCountUseCase: GetUnreadCountUseCase,
    private val refreshEvents: RefreshEvents
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadNotifications()
        observeRefreshEvents()
    }

    private fun observeRefreshEvents() {
        viewModelScope.launch {
            refreshEvents.events
                .filter { it == RefreshEvents.Topics.NOTIFICATIONS }
                .collect { loadNotifications() }
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getNotificationsUseCase()) {
                is ResultWrapper.Success -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        notifications = result.data,
                        unreadCount = result.data.count { n -> !n.isRead }
                    )
                }
                is ResultWrapper.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
                else -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun markRead(id: Int) {
        viewModelScope.launch {
            markReadUseCase(id)
            _uiState.update { state ->
                state.copy(
                    notifications = state.notifications.map {
                        if (it.id == id) it.copy(isRead = true) else it
                    },
                    unreadCount = state.notifications.count { !it.isRead && it.id != id }
                )
            }
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            markAllReadUseCase()
            _uiState.update { state ->
                state.copy(
                    notifications = state.notifications.map { it.copy(isRead = true) },
                    unreadCount = 0
                )
            }
        }
    }

    fun deleteNotification(id: Int) {
        viewModelScope.launch {
            deleteUseCase(id)
            _uiState.update { state ->
                state.copy(notifications = state.notifications.filter { it.id != id })
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
}
