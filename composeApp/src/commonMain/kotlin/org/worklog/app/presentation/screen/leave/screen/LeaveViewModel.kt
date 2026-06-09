package org.worklog.app.presentation.screen.leave.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.worklog.app.core.notification.RefreshEvents
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.usecase.leave.GetLeaveDetailsUseCase

class LeaveViewModel(
    private val getLeaveDetailsUseCase: GetLeaveDetailsUseCase,
    private val refreshEvents: RefreshEvents
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaveUiState())
    val uiState: StateFlow<LeaveUiState> = _uiState.asStateFlow()

    init {
        // Always fetch fresh on screen open — admin actions (approve/reject)
        // can change leave status server-side at any time, and the singleton
        // repo's in-memory cache won't know about them.
        getLeaveDetails(forceRefresh = true)

        // FCM push (holiday_approved / holiday_rejected) → immediate refresh.
        viewModelScope.launch {
            refreshEvents.events
                .filter { it == RefreshEvents.Topics.LEAVES }
                .collect { refreshData() }
        }
    }

    fun refreshData() {
        getLeaveDetails(forceRefresh = true)
    }

    fun onPullToRefresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            when (val result = getLeaveDetailsUseCase(true)) {
                is ResultWrapper.Success -> {
                    val summary = result.data
                    val sortedHistory = summary.history.sortedByDescending { it.createdAt }
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            leaveSummary = summary.copy(history = sortedHistory)
                        )
                    }
                }

                is ResultWrapper.Error -> _uiState.update {
                    it.copy(isRefreshing = false, error = result.message)
                }

                ResultWrapper.Loading -> {}
            }
        }
    }

    private fun getLeaveDetails(forceRefresh: Boolean) {
        if (_uiState.value.leaveSummary == null) {
            _uiState.update { it.copy(isLoading = true) }
        }
        viewModelScope.launch {
            when (val result = getLeaveDetailsUseCase(forceRefresh)) {
                is ResultWrapper.Success -> {
                    val summary = result.data
                    val sortedHistory = summary.history.sortedByDescending { it.createdAt }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            leaveSummary = summary.copy(history = sortedHistory)
                        )
                    }
                }

                is ResultWrapper.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }

                ResultWrapper.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}
