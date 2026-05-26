package org.worklog.app.presentation.screen.leave.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.usecase.leave.GetLeaveDetailsUseCase

class LeaveViewModel(
    private val getLeaveDetailsUseCase: GetLeaveDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaveUiState())
    val uiState: StateFlow<LeaveUiState> = _uiState.asStateFlow()

    init {
        getLeaveDetails(forceRefresh = false)
    }

    fun refreshData() {
        getLeaveDetails(forceRefresh = true)
    }

    private fun getLeaveDetails(forceRefresh: Boolean) {
        if (_uiState.value.leaveSummary == null) {
            _uiState.update { it.copy(isLoading = true) }
        }
        viewModelScope.launch {
            when (val result = getLeaveDetailsUseCase(forceRefresh)) {
                is ResultWrapper.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            leaveSummary = result.data
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
